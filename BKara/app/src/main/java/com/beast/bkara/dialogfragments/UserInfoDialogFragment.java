package com.beast.bkara.dialogfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.beast.bkara.Controller;
import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.model.User;
import com.beast.bkara.util.ImageCaptureHandler;
import com.beast.bkara.util.ValidationUtil;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.util.imgur.ImageResponse;
import com.beast.bkara.util.imgur.ImageUpload;
import com.beast.bkara.util.imgur.ImgurService;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInfoDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInfoDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserInfoDialogFragment";

    private EditText username, email , firstName, lastName ,address;
    private CircularImageView avatar;
    Spinner country;
    Button editBtn;

    private ImageCaptureHandler mImageCaptureHandler;
    private ProgressDialog progressDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Controller controller;

    public UserInfoDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoDialogFragment newInstance(String param1, String param2) {
        UserInfoDialogFragment fragment = new UserInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Personal Information");

        controller = (Controller) getActivity().getApplicationContext();

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user_info_dialog, container, false);

        username = (EditText) v.findViewById(R.id.user_info_username);
        email = (EditText) v.findViewById(R.id.user_info_email);
        firstName = (EditText) v.findViewById(R.id.user_info_first_name);
        lastName = (EditText) v.findViewById(R.id.user_info_last_name);
        address = (EditText) v.findViewById(R.id.user_info_address);
        country = (Spinner) v.findViewById(R.id.user_info_country);
        country.setEnabled(false);
        country.setPrompt(getResources().getString(R.string.country_prompt));
        avatar = (CircularImageView) v.findViewById(R.id.user_info_avatar);

        mImageCaptureHandler = new ImageCaptureHandler(getActivity(),avatar);
        mImageCaptureHandler.setAlbumName(getString(R.string.bkara_album_avatar));
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(ImageCaptureHandler.ACTION_TAKE_PHOTO_B);
            }
        });

        populateUserInfoIntoViews();

        editBtn = (Button) v.findViewById(R.id.user_info_edit_button);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !toggleEditMode  && isUpdatedInfoValid() )
                    updateUser();
                toggleEditText(toggleEditMode);
            }
        });

        return v;
    }


    private boolean isUpdatedInfoValid() {
        boolean isValid = false;
        if( username.getText().toString().equalsIgnoreCase("") )
            username.setError("Please fill in 'UserName' field");
        else if( username.getText().toString().length() < 4 )
            username.setError("Username must be at least 4 characters");
        else if( email.getText().toString().equalsIgnoreCase("") )
            email.setError("Please fill in 'Email' field");
        else if ( !ValidationUtil.isEmailValid(email.getText().toString()) )
            email.setError("Email is not valid");
        else isValid = true;

        return isValid;
    }


    private void updateUser(){

        showProgress(true);

        final String[] imgLink = {null};

        if( mImageCaptureHandler.isAlreadyCaptured() ){

            ImageUpload imgUpload = new ImageUpload(new File(mImageCaptureHandler.getmCurrentPhotoPath()));
            ImgurService.getInstance().uploadFileToImgur(imgUpload, new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if( response.isSuccessful() ) {
                        imgLink[0] = response.body().data.link;
                        Log.i(TAG, "Post image successfully " + imgLink[0]);
                        updateUserContinue(imgLink[0]);
                    }
                    else showProgress(false);
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Log.i(TAG, "Post image to IMGUR failed " + t.getLocalizedMessage());
                    Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            });
        }
        else updateUserContinue(null);

    }


    private void updateUserContinue(String imgLink) {
        final User user = new User(controller.getCurrUser());
        user.setEmail(email.getText().toString());
        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setAddress(address.getText().toString());
        user.setCountry(country.getSelectedItem().toString());
        if( imgLink != null && !imgLink.isEmpty() )
            user.setAvatarLink(imgLink);

        BkaraService.getInstance().UpdateUser(user, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if( response.isSuccessful() ) {
                    Log.i("@BkaraService", "@UpdateUser @onResponse success");
                    Toast.makeText(getActivity(), "Update successfully", Toast.LENGTH_LONG).show();

                    // Notify MainActivity to update user info
                    mListener.onUpdateUserInfoSuccessfully(user);
                }
                else {
                    Toast.makeText(getActivity(), "Email has already taken !", Toast.LENGTH_LONG).show();

                    // Re-fill old user info
                    populateUserInfoIntoViews();
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("@BkaraService", "@UpdateUser @onResponse failed " + t.getLocalizedMessage()+ " " + t.getMessage());
                Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_LONG).show();

                // Re-fill old user info
                populateUserInfoIntoViews();

                showProgress(false);
            }
        });
    }

    /**
     * Shows the progress UI
     */
    private void showProgress(final boolean show) {
        if( show ) {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Updating info ...", true);
            progressDialog.setCancelable(true);
        }else if( progressDialog != null && progressDialog.isShowing() )
            progressDialog.dismiss();
    }


    private void populateUserInfoIntoViews() {

        if (!controller.isLogin()) {
            this.getDialog().dismiss();
            return;
        }

        User user =  controller.getCurrUser();
        username.setText(user.getUserName());
        email.setText(user.getEmail());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        address.setText(user.getAddress());

        if( user.getCountry() != null && !user.getCountry().isEmpty()) {
            String[] countriesArray = getResources().getStringArray(R.array.countries_array);
            int countryIndex;
            for (countryIndex = 0; countryIndex < countriesArray.length; countryIndex++)
                if (countriesArray[countryIndex].equals(user.getCountry())) {
                    country.setSelection(countryIndex);
                    break;
                }
        }

        if( user.getAvatarLink() != null && !user.getAvatarLink().isEmpty() )
            ImageLoader.getInstance().displayImage(user.getAvatarLink(), avatar);


    }


    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = mImageCaptureHandler.dispatchTakePictureIntent(actionCode);
        startActivityForResult(takePictureIntent, actionCode);
    }


    /**
     * Get result back from camera intent and set image to ImageView
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( mImageCaptureHandler != null )
            mImageCaptureHandler.onActivityResult(requestCode,resultCode,data);

    }


    /**
     * Some lifecycle callbacks so that the image can survive orientation change
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if( mImageCaptureHandler != null )
            mImageCaptureHandler.onSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    /**
     * Restore state which was called in @onStart
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if( mImageCaptureHandler != null )
            mImageCaptureHandler.onViewStateRestored(savedInstanceState);
    }


    boolean toggleEditMode = true;
    private void toggleEditText(boolean enabled) {
        email.setEnabled(enabled);
        firstName.setEnabled( enabled);
        lastName.setEnabled( enabled);
        address.setEnabled( enabled);
        country.setEnabled( enabled);

        toggleEditMode = !toggleEditMode;
        if( toggleEditMode )
            editBtn.setText("Edit");
        else editBtn.setText("Save");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onUpdateUserInfoSuccessfully(User user);
    }
}

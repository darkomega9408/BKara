package com.beast.bkara.dialogfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beast.bkara.R;
import com.beast.bkara.model.Record;
import com.beast.bkara.model.User;
import com.beast.bkara.util.SHAEncryptionService;
import com.beast.bkara.util.ValidationUtil;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.util.ImageCaptureHandler;
import com.beast.bkara.util.imgur.ImageResponse;
import com.beast.bkara.util.imgur.ImageUpload;
import com.beast.bkara.util.imgur.ImgurService;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignUpDialogFragment.OnSignUpDialogFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpDialogFragment extends DialogFragment {
    private final String TAG = "SignUpDialogFragment";

    private ImageCaptureHandler mImageCaptureHandler;
    private CircularImageView mImageView;
    private EditText userName,password , repassword, email;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSignUpDialogFragmentInteractionListener mListener;
    private ProgressDialog progressDialog;

    public SignUpDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpDialogFragment newInstance(String param1, String param2) {
        SignUpDialogFragment fragment = new SignUpDialogFragment();
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

        // Full screen dialog
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(getString(R.string.action_sign_up));

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up_dialog, container, false);

        // Find edit text
        userName = (EditText) v.findViewById(R.id.sign_up_username);
        password = (EditText) v.findViewById(R.id.sign_up_password);
        repassword = (EditText) v.findViewById(R.id.sign_up_re_password);
        email = (EditText) v.findViewById(R.id.sign_up_email);


        // In order to use @ImageCaptureHandler , you must initialize two things below here ( in @onCreate )
        mImageView = (CircularImageView) v.findViewById(R.id.sign_up_avatar);
        mImageCaptureHandler = new ImageCaptureHandler(getActivity(),mImageView);
        mImageCaptureHandler.setAlbumName(getString(R.string.bkara_album_avatar));

        ImageButton captureBtn = (ImageButton) v.findViewById(R.id.sign_up_capture);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(ImageCaptureHandler.ACTION_TAKE_PHOTO_B);
            }
        });
        // ~~

        v.findViewById(R.id.sign_up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isSignUpFormValid())
                    signUp();
            }
        });

        return v;
    }


    private boolean isSignUpFormValid() {
        boolean isValid = false;
        if( userName.getText().toString().equalsIgnoreCase("") )
            userName.setError("Please fill in 'UserName' field");
        else if( userName.getText().toString().length() < 4 )
            userName.setError("Username must be at least 4 characters");
        else if( email.getText().toString().equalsIgnoreCase("") )
            email.setError("Please fill in 'Email' field");
        else if ( !ValidationUtil.isEmailValid(email.getText().toString()) )
            email.setError("Email is not valid");
        else if( password.getText().toString().equalsIgnoreCase("") )
            password.setError("Please fill in 'Password' field");
        else if( password.getText().toString().length() < 4 )
            userName.setError("Password must be at least 4 characters");
        else if( repassword.getText().toString().equalsIgnoreCase("") )
            repassword.setError("Please re-type password");
        else if ( !password.getText().toString().equals(repassword.getText().toString()) )
            repassword.setError("Re-type password mismatch");
        else isValid = true;

        return isValid;
    }


    private void signUp() {
        showProgress(true);

        final String[] imgLink = {null};

        // New image is set in imageview -> upload to imgur server
        if( mImageCaptureHandler.isAlreadyCaptured() ){
            ImageUpload imgUpload = new ImageUpload(new File(mImageCaptureHandler.getmCurrentPhotoPath()));
            ImgurService.getInstance().uploadFileToImgur(imgUpload, new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if( response.isSuccessful() ) {
                        imgLink[0] = response.body().data.link;
                        Log.i(TAG, "Post image successfully " + imgLink[0]);
                        signUpContinue(imgLink[0]);
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
        else signUpContinue(null);


    }


    private void signUpContinue(String imgLink) {
        // Post new user info to our server
        final User user = new User();
        user.setUserName(userName.getText().toString());
        user.setPassword(SHAEncryptionService.getInstance().encrypt(password.getText().toString()));
        user.setEmail(email.getText().toString());
        if( imgLink != null )
            user.setAvatarLink(imgLink);
        ArrayList<Record> records = new ArrayList<>();
        Record record = new Record();
        record.setDate_created(new Date());
        records.add(record);
        user.setRecords(records);

        // Do sign up
         BkaraService.getInstance().signUp(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if( response.isSuccessful() ) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.sign_up_success_msg, Toast.LENGTH_LONG).show();
                    mListener.onSignUpSuccessfully(response.body());
                }else Toast.makeText(getActivity().getApplicationContext(), R.string.user_existed_error, Toast.LENGTH_LONG).show();

                showProgress(false);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }



    /**
     * As its name suggested
     * @param actionCode
     */
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




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpDialogFragmentInteractionListener) {
            mListener = (OnSignUpDialogFragmentInteractionListener) context;
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
     * Shows the progress UI
     */
    private void showProgress(final boolean show) {
        if( show ) {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Signing up ...", true);
            progressDialog.setCancelable(true);
        }else if( progressDialog != null && progressDialog.isShowing() )
            progressDialog.dismiss();
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
    public interface OnSignUpDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSignUpSuccessfully(User user);
    }


}

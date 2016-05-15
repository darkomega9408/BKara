package com.beast.bkara.dialogfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.beast.bkara.R;
import com.beast.bkara.model.User;
import com.beast.bkara.util.SHAEncryptionService;
import com.beast.bkara.util.bkararestful.BkaraService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginDialogFragment.OnLoginDialogFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginDialogFragment extends DialogFragment {
    private EditText userName , password;
    private CheckBox remember;
    private ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnLoginDialogFragmentInteractionListener mListener;

    public LoginDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginDialogFragment newInstance(String param1, String param2) {
        LoginDialogFragment fragment = new LoginDialogFragment();
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
        // retain this fragment
        //setRetainInstance(true);

        // Full screen dialog
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Login");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_dialog, container, false);

        userName = (EditText) v.findViewById(R.id.login_username);
        password = (EditText) v.findViewById(R.id.login_password);
        remember = (CheckBox) v.findViewById(R.id.login_remember);

        // Sign up button
        Button btnOpenSignUpForm = (Button) v.findViewById(R.id.sign_up_button);
        btnOpenSignUpForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOpenSignUpForm();
            }
        });

        // Login button
        v.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return v;
    }


    private void login(){

        showProgress(true);

        final User user = new User();
        user.setUserName(userName.getText().toString());
        user.setPassword(SHAEncryptionService.getInstance().encrypt(password.getText().toString()));

        BkaraService.getInstance().login(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(  response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Welcome "+ response.body().getUserName(), Toast.LENGTH_LONG).show();
                    mListener.onLoginSuccessfully(response.body(),remember.isChecked());
                }
                else Toast.makeText(getActivity().getApplicationContext(), R.string.user_not_existed_error, Toast.LENGTH_LONG).show();
                showProgress(false);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLoginDialogFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginDialogFragmentInteractionListener) {
            mListener = (OnLoginDialogFragmentInteractionListener) context;
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

    public void openSignUpForm() {
        mListener.onOpenSignUpForm();
    }


    /**
     * Shows the progress UI
     */
    private void showProgress(final boolean show) {
        if( show ) {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait...", "Signing in ...", true);
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
    public interface OnLoginDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginDialogFragmentInteraction(Uri uri);
        void onOpenSignUpForm();

        void onLoginSuccessfully(User user, boolean isRemembered);
    }
}

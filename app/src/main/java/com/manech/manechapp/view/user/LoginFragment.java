package com.manech.manechapp.view.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.User;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.helper.StringValidation;
import com.manech.manechapp.model.UserViewModel;


public class LoginFragment extends Fragment {
    private final StringValidation stringValidation = new StringValidation();
    private UserViewModel userViewModel;
    private EditText txtEmail ;
    private EditText txtPassword ;
    private TextView tvRegisterLink ;
    private Button btnLogin ;
    private boolean btnLoginClicked =false;

    private SecureSharedPref sharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = SecureSharedPref.getInstance(getContext(), getString(R.string.preference_file_key));

        userViewModel=((MainActivity)getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(btnLoginClicked) {
                    btnLoginClicked = false;
                    if(user!=null) {
                        sharedPref.putUUID(getString(R.string.current_user_id), user.getId());
                        ((MainActivity)getActivity()).hideProgressBar();
                        ((MainActivity)getActivity()).showDashboardFragment();
                        return;
                    }
                    Toast.makeText(getContext(), getString(R.string.wrong_password_email), Toast.LENGTH_LONG).show();
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        btnLoginClicked =false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ((MainActivity)getActivity()).setTitle(getString(R.string.login));

        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);
        tvRegisterLink = view.findViewById(R.id.btnNewAccount);
        btnLogin = view.findViewById(R.id.btnLogin);
        view.findViewById(R.id.btnLanguage).setOnClickListener(v ->
                ((MainActivity) getActivity()).showLanguageChooser());
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLoginClicked =true;
                // Get User Input
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();

                ((MainActivity)getActivity()).showProgressBar();

                userViewModel.authenticate(email, password);
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showRegisterFragment();
            }
        });

        return view;
    }
}
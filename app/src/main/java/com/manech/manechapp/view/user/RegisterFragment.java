package com.manech.manechapp.view.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.User;
import com.manech.manechapp.helper.StringValidation;
import com.manech.manechapp.model.UserViewModel;


public class RegisterFragment extends Fragment {
    private final StringValidation stringValidation = new StringValidation();
    private EditText txtPassword;
    private EditText txtConfirm;
    private EditText txtEmail;
    private Button btnRegister;
    private Button btnLogin;
    private UserViewModel userViewModel;

    private boolean btnRegisterClicked =false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setTitle(getString(R.string.new_login));

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtConfirm = view.findViewById(R.id.txtConfirm);
        txtEmail = view.findViewById(R.id.txtEmail);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnLogin =view.findViewById(R.id.btnLogin);
        view.findViewById(R.id.btnLanguage).setOnClickListener(v ->
                ((MainActivity) getActivity()).showLanguageChooser());

        btnRegisterClicked =false;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showLoginFragment();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegisterClicked =true;
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                final String confirm = txtConfirm.getText().toString();
                if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    Toast.makeText(getContext(),
                            getString(R.string.email_warning), Toast.LENGTH_LONG).show();
                } else if (!stringValidation.isValidPassword(password)) {
                    Toast.makeText(getContext(),
                            getString(R.string.password_warning), Toast.LENGTH_LONG).show();
                } else if (!confirm.equals(password)) {
                    Toast.makeText(getContext(),
                            getString(R.string.password_confirm_warning), Toast.LENGTH_LONG).show();
                } else {
                    userViewModel.userByEmail(email);
                }


            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel=((MainActivity)getActivity()).getViewModel(UserViewModel.class);

        userViewModel.getUserUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(btnRegisterClicked) {
                    btnRegisterClicked=false;
                    if (aBoolean) {
                        Toast.makeText(getContext(),
                                getString(R.string.user_created_success), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).logout();
                    } else {
                        Toast.makeText(getContext(),
                                getString(R.string.user_create_faild), Toast.LENGTH_SHORT).show();
                    }
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (btnRegisterClicked) {
                    if (user != null) {
                        Toast.makeText(getContext(),
                                getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();

                    } else {
                        ((MainActivity)getActivity()).showProgressBar();

                        final String email = txtEmail.getText().toString();
                        final String password = txtPassword.getText().toString();
                        userViewModel.insertUser(new User("", password, email));
                    }
                }
            }
        });
    }

}

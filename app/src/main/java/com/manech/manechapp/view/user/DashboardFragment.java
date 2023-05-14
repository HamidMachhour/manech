package com.manech.manechapp.view.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.User;
import com.manech.manechapp.model.UserViewModel;


public class DashboardFragment extends Fragment {

    private UserViewModel userViewModel;
    private boolean refreshInvoked=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel=((MainActivity)getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                if(refreshInvoked){
                    if(user==null){
                        ((MainActivity)getActivity()).logout();
                    }

                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked=false;
        refreshAction();
    }
    private void refreshAction() {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentUser(false,false,false,false);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((MainActivity)getActivity()).setTitle(getString(R.string.home));
        return view;

    }
}

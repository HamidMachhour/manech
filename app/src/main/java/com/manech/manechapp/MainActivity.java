package com.manech.manechapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.manech.manechapp.data.User;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.model.ViewModelFactory;
import com.manech.manechapp.service.ManechService;
import com.manech.manechapp.view.item.ItemsFragment;
import com.manech.manechapp.view.item.manage.ItemsManagerFragment;
import com.manech.manechapp.view.mstore.StoresFragment;
import com.manech.manechapp.view.order.OrdersFragment;
import com.manech.manechapp.view.order.sell.SellsFragment;
import com.manech.manechapp.view.user.DashboardFragment;
import com.manech.manechapp.view.user.LoginFragment;
import com.manech.manechapp.view.user.RegisterFragment;
import com.manech.manechapp.view.user.UserFragment;

import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private ViewModelFactory viewModelFactory;
    private UserViewModel userViewModel;
    private SecureSharedPref sharedPref;

    private TextView tvFragmentTitle;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    // private User currentUser;

    private boolean fetchUserInvoked = false;

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void showLoginFragment() {
        toolbar.setVisibility(View.GONE);
        initFragment(LoginFragment.class, getString(R.string.login_fragment));
    }

    public void showRegisterFragment() {
        toolbar.setVisibility(View.GONE);
        initFragment(RegisterFragment.class, getString(R.string.register_fragment));

    }

    public void showDashboardFragment() {
        toolbar.setVisibility(View.VISIBLE);
        initFragment(DashboardFragment.class, getString(R.string.dashboard_fragment));
        // TODO start service
        /*intent.setComponent(new ComponentName("com.manech.manechapp", "com.manech.manechapp.service.ManechService"));
        getApplicationContext().startService(intent);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        tvFragmentTitle = findViewById(R.id.tvFragmentTitle);
        toolbar = findViewById(R.id.menu_bar);
        toolbar.setVisibility(View.GONE);
        // set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = findViewById(R.id.progressBar);

        sharedPref = SecureSharedPref.getInstance(getApplicationContext(), getString(R.string.preference_file_key));

        // Instantiate the ViewModelFactory
        viewModelFactory = Injection.provideViewModelFactory(this);

        userViewModel = getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (fetchUserInvoked) {
                    fetchUserInvoked = false;
                    // Log.e("MANECH_TAG", "MainActivity");
                    if (user != null) {
                        showDashboardFragment();
                    } else {
                        showLoginFragment();
                    }
                }
            }
        });
        fetchUser();

    }

    private void fetchUser() {
        fetchUserInvoked = true;
        final UUID userId = sharedPref.getUUID(getString(R.string.current_user_id), null);
        userViewModel.fetchUser(userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                fetchUser();
                return true;

            case R.id.action_manage_stores:
                initFragment(StoresFragment.class, getString(R.string.manage_stores_edit_mode));
                return true;

            case R.id.action_buy_items:
                sharedPref.putInt(getString(R.string.items_view_mode), ItemsFragment.BUY_MODE);
                initFragment(ItemsFragment.class, getString(R.string.items_buy_mode));
                return true;
            case R.id.action_manage_items:
                initFragment(ItemsManagerFragment.class, getString(R.string.items_manage_mode));
                return true;
            case R.id.action_manage_account:
                initFragment(UserFragment.class, getString(R.string.manage_account_edit_mode));
                return true;
            case R.id.action_manage_orders:
                initFragment(OrdersFragment.class, getString(R.string.orderlist_view));
                return true;
            case R.id.action_manage_sells:
                initFragment(SellsFragment.class, getString(R.string.sellslist_view));
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public <T extends ViewModel> T getViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(this, viewModelFactory).get(viewModelClass);
    }

    public <T extends Fragment> void initFragment(Class<T> fragmentClass, String backStateName) {

        showProgressBar();

        FragmentManager manager = getSupportFragmentManager();
        final Fragment fragmentByTag = manager.findFragmentByTag(backStateName);

        if (fragmentByTag != null) {
            manager.popBackStack(backStateName, 0);
        } else { // fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            try {
                T fragment = fragmentClass.newInstance();
                ft.replace(R.id.main_frame_layout, fragment, backStateName);
                if (!backStateName.equals(getString(R.string.login_fragment)) &&
                        !backStateName.equals(getString(R.string.register_fragment)))
                    ft.addToBackStack(backStateName);
                ft.commit();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() > 1) {
            // If there are back-stack entries, leave the FragmentActivity
            // implementation take care of them.
            manager.popBackStack();

        } else {
            // Otherwise, ask user if he wants to leave
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Exit Manech?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            // MainActivity.super.onBackPressed();
                            logout();
                            finishAffinity();
                        }
                    }).create().show();
        }

    }

    public void logout() {
        sharedPref.clear();
        stopService(new Intent(this, ManechService.class));
        clearBackStackFragments();
        fetchUser();
    }

    private void clearBackStackFragments() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        tvFragmentTitle.setText(title);
    }

    public void actionBack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            showDashboardFragment();
        }
    }

    public void actionHighlight(Integer[] indexes) {
        final Intent intent = new Intent("animate");
        intent.putExtra("indexes", indexes);
        intent.setComponent(new ComponentName("com.manech.manager.app", "com.manech.manager.app.service.ManechService"));
        ComponentName c = getApplicationContext().startService(intent);

        if (c == null) {
            Log.e("MANECHAPP", "failed to start ManechService with " + indexes.length);
        } else {
            Log.d("MANECHAPP", "send intent: " + indexes.length);
        }
    }
}

package com.manech.manechapp.view.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.User;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.helper.StringValidation;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.view.address.AddressesFragment;
import com.manech.manechapp.view.address.AddressChooserFragment;
import com.manech.manechapp.view.address.AddressChooserListener;


public class UserFragment extends Fragment {
    private final StringValidation stringValidation = new StringValidation();
    private UserViewModel userViewModel;

    private ImageView imageView;

    private EditText txtUserName;
    private EditText txtPassword;
    private EditText txtCurrentPassword;
    private EditText txtConfirm;
    private CheckBox checkBox;
    private TextView txtEmail;
    private TextView tvDefaultAddress;

    private User currentUser;

    boolean passwordChanged = false;
    private boolean refreshInvoked = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);

        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (refreshInvoked) {
                    ((MainActivity)getActivity()).hideProgressBar();
                    if (user != null) {
                        currentUser = user;
                        userToForm();
                    } else {
                        ((MainActivity) getActivity()).logout();
                    }
                }
            }
        });
        userViewModel.getUserUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (isResumed() && isVisible() && aBoolean) {
                    if (passwordChanged) {
                        ((MainActivity) getActivity()).logout();
                    } else {
                        refreshAction(true);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.manage_account));
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initForm(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(true);
    }

    private void refreshAction(boolean reload) {
        if (userViewModel != null) {
            refreshInvoked = true;
            userViewModel.fetchCurrentUser(reload, false, false, false);
        }
    }

    private void initForm(View view) {

        imageView = view.findViewById(R.id.userImage);
        imageView.setOnClickListener(v -> selectImage());

        txtEmail = view.findViewById(R.id.txtEmail);
        txtUserName = view.findViewById(R.id.txtUserName);

        txtPassword = view.findViewById(R.id.txtPassword);
        txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        txtConfirm = view.findViewById(R.id.txtConfirm);
        tvDefaultAddress = view.findViewById(R.id.tvDefaultAddress);

        final FragmentActivity activity = getActivity();

        tvDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressChooserFragment fragment = AddressChooserFragment
                        .newInstance(currentUser.getAddressId(), "Select default address");
                fragment.setListener(new AddressChooserListener() {
                    @Override
                    public void onSelect(Address selectedAddress) {
                        currentUser.setAddress(selectedAddress);
                        tvDefaultAddress.setText(selectedAddress.toString());
                        fragment.dismiss();
                    }
                });
                fragment.show(activity.getSupportFragmentManager(), getString(R.string.address_select_dialog));

            }
        });

        final FrameLayout frameLayout = view.findViewById(R.id.changePwdArea);
        checkBox = view.findViewById(R.id.chkChangePwd);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int id = buttonView.getId();
            if (id == R.id.chkChangePwd) {
                if (checkBox.isChecked()) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }
            }
        });
    }


    private void userToForm() {
        if (currentUser != null) {
            txtCurrentPassword.setText("");
            txtPassword.setText("");
            txtConfirm.setText("");
            checkBox.setChecked(false);
            txtEmail.setText(currentUser.getEmail());
            txtUserName.setText(currentUser.getUserName());
            if (currentUser.getAddress() != null)
                tvDefaultAddress.setText(currentUser.getAddress().toString());
            if (currentUser.getImage() != null) {
                MyUtils.loadResourceImage(currentUser.getImage(), imageView);
            }
        }
    }

    private void formToUser() {
        final FragmentActivity fragmentActivity = requireActivity();
        String userName = txtUserName.getText().toString();
        String email = txtEmail.getText().toString();
        String oldPassword = txtCurrentPassword.getText().toString();
        String password = txtPassword.getText().toString();
        String confirm = txtConfirm.getText().toString();

        passwordChanged = false;

        if (checkBox.isChecked()) {
            if (!stringValidation.isValidPassword(password)) {
                Toast.makeText(fragmentActivity,
                        getString(R.string.password_warning), Toast.LENGTH_LONG).show();
                return;
            } else if (!confirm.equals(password)) {
                Toast.makeText(fragmentActivity,
                        getString(R.string.password_confirm_warning), Toast.LENGTH_LONG).show();
                return;
            } else if (!currentUser.getPassword().equals(oldPassword)
                    || !currentUser.getEmail().equals(email)) {
                Toast.makeText(fragmentActivity,
                        getString(R.string.wrong_password_email), Toast.LENGTH_LONG).show();
                return;
            } else { // now we can update password
                currentUser.setPassword(password);
                passwordChanged = true;

            }
        }
        currentUser.setUserName(userName);
        if (currentUser.getAddress() != null) {
            currentUser.setAddressId(currentUser.getAddress().getId());
        }
    }

    private void saveAction() {
        ((MainActivity)getActivity()).showProgressBar();

        formToUser();
        userViewModel.updateCurrentUser(null, null, null);
    }


    private void selectImage() {

        //todo user images to select default image
        /*
        final CharSequence[] options = {
                getString(R.string.take_photo_option),
                getString(R.string.choose_from_gallery_option),
                getString(R.string.cancel_option)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.add_photo_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    final String profile_path = android.os.Environment.getExternalStorageDirectory() + File.separator
                            + getString(R.string.app_name) + File.separator + "PROFILE";
                    File dir = new File(profile_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File f = new File(profile_path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    final Uri uri = Uri.fromFile(f);
                    imageUrl = uri.getPath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 1);
                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imageUrl = c.getString(columnIndex);
                c.close();
            }
            new DownloadImageTask(imageView).execute(imageUrl);
        } */
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            ((MainActivity) getActivity()).actionBack();
            return true;
        }
        if (id == R.id.action_save) {
            saveAction();
            return true;
        }
        if (id == R.id.action_refresh) {
            refreshAction(true);
            return true;
        }
        if (id == R.id.action_addresses) {
            ((MainActivity) getActivity()).initFragment(AddressesFragment.class,
                    getString(R.string.manage_addresses_edit_mode));
            return true;
        }
        if (id == R.id.action_delete_account) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete_account))
                    .setMessage(R.string.delete_account_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            userViewModel.disableCurrentUser();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
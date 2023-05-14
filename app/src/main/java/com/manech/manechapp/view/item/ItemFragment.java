package com.manech.manechapp.view.item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.ItemViewModel;

import java.util.UUID;

public class ItemFragment extends Fragment implements Observer<Item>, View.OnClickListener {

    private ImageView imageView;

    private EditText txtTitle;
    private EditText txtCode;
    private EditText txtLocation;
    private EditText txtQuantity;
    private EditText txtMinQty;
    private EditText txtMaxQty;
    private EditText txtPrice;
    private EditText txtBasePrice;
    private EditText txtShippingFee;

    private EditText txtDescription;
    private ProgressDialog mDialog;
    private ItemViewModel itemViewModel;
    private UUID itemId;
    private Item currentItem = null;

    private int editMode = ItemsFragment.BUY_MODE;
    private SecureSharedPref sharedPref;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref=SecureSharedPref.getInstance(getActivity().getApplicationContext(),getString(R.string.preference_file_key));

        editMode = sharedPref.getInt(getString(R.string.items_view_mode), -1);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate((editMode==ItemsFragment.MANAGE_MODE) ? R.layout.fragment_item_edit_mode : R.layout.fragment_item_buy_mode, container, false);

        initForm(view);

        itemViewModel = Injection.provideViewModelFactory(getActivity()).create(ItemViewModel.class);
        itemViewModel.getCurrentItem().observe(this, this);
        itemId = sharedPref.getUUID(getString(R.string.item_id), null);
        itemViewModel.fetchItem(itemId);
        return view;
    }

    private void initForm(View view) {

        imageView =view.findViewById(R.id.imgItem);

        imageView.setOnClickListener(this);

        txtTitle = view.findViewById(R.id.txtItemTitle);
        txtCode = view.findViewById(R.id.txtItemCode);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtQuantity = view.findViewById(R.id.txtQuantity);
        txtMinQty = view.findViewById(R.id.txtMinQty);
        txtMaxQty = view.findViewById(R.id.txtMaxQty);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtBasePrice = view.findViewById(R.id.txtBasePrice);
        txtShippingFee = view.findViewById(R.id.txtShippingFee);
        txtDescription = view.findViewById(R.id.txtItemDescription);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();

        inflater.inflate((editMode==ItemsFragment.MANAGE_MODE) ? R.menu.save_back_menu : R.menu.back_menu, menu);
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
        return super.onOptionsItemSelected(item);
    }

    private void saveAction() {
        showProgressDialog();
        formToItem();
        itemViewModel.update(currentItem);
        itemViewModel.fetchItem(itemId);
    }

    private void formToItem() {
        if (currentItem != null) {
            currentItem.setTitle(txtTitle.getText().toString());
            currentItem.setCode(txtCode.getText().toString());
            currentItem.setLocation(Integer.parseInt(txtLocation.getText().toString()));
            currentItem.setQuantity( MyUtils.parseDouble(txtQuantity.getText().toString()));
            currentItem.setMinQty( MyUtils.parseDouble(txtMinQty.getText().toString()));
            currentItem.setMaxQty( MyUtils.parseDouble(txtMaxQty.getText().toString()));
            currentItem.setPrice( MyUtils.parseDouble(txtPrice.getText().toString()));
            currentItem.setBasePrice( MyUtils.parseDouble(txtBasePrice.getText().toString()));
            currentItem.setShipping( MyUtils.parseDouble(txtShippingFee.getText().toString()));

            currentItem.setDescription(txtDescription.getText().toString());
            //todo store pics and default pic
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }


    private void showProgressDialog() {
        mDialog = new ProgressDialog(getContext());
        mDialog.setTitle(getString(R.string.please_wait));
        mDialog.setMessage(getString(R.string.loading_data));
        mDialog.setCancelable(true);
        mDialog.show();
    }

    private void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onChanged(Item item) {
        hideProgressDialog();
        if (item != null) {
            this.currentItem = item;
            itemToForm();
            getActivity().setTitle(getString(R.string.item_details));
        } else {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    private void itemToForm() {
        if (currentItem != null) {
            txtTitle.setText(currentItem.getTitle());
            txtCode.setText(currentItem.getCode());
            txtLocation.setText("" + currentItem.getLocation());
            txtQuantity.setText("" + currentItem.getQuantity());
            txtMinQty.setText("" + currentItem.getMinQty());
            txtMaxQty.setText("" + currentItem.getMaxQty());
            txtPrice.setText("" + currentItem.getPrice());
            txtBasePrice.setText("" + currentItem.getBasePrice());
            txtShippingFee.setText("" + currentItem.getShipping());
            txtDescription.setText(currentItem.getDescription());

            if(currentItem.getImage()!=null){
                MyUtils.loadResourceImage(currentItem.getImage(),imageView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgItem:
                selectImage();
                break;
        }
    }


    private void selectImage() {

        // todo load images to select default image

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
                            + getString(R.string.app_name) + File.separator + "ITEMS";
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
        builder.show(); */
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
        }*/
    }
}

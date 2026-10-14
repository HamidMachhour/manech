package com.manech.manechapp.view.images;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.Resource;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.model.UserViewModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ItemImagesFragment extends Fragment implements ImagesManager {
    private final int REQUEST_CODE_PICK_IMAGES = 4567;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private ImageListAdapter adapter;
    private Item currentItem;

    private boolean refreshInvoked = false;
    private boolean actionInvoked =false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getItemUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    actionInvoked =false;
                    refreshAction(true, true);
                }

            }
        });
        userViewModel.getItemMutableLiveData().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (item != null) {
                        currentItem =item;
                        fillUI();
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
        if(!actionInvoked )
        refreshAction(false,false);
    }

    private void fillUI() {
        if (currentItem !=null && currentItem.getImages()!=null) {
                adapter.getImages().clear();
                adapter.getImages().addAll(currentItem.getImages());
                adapter.setDefaultImageId(currentItem.getImageId());
                adapter.notifyDataSetChanged();
        }
    }


    private void refreshAction(boolean reload,boolean reloadImages) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentItem(reload,reloadImages);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.item_images));
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        recyclerView = view.findViewById(R.id.image_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new ImageListAdapter(new ArrayList<>(), this, null);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.images_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_back:
                ((MainActivity) getActivity()).actionBack();
                return true;
            case R.id.action_refresh:
                refreshAction(false,false);
                return true;
            case R.id.action_add:
                addAction();
                return true;
            case R.id.action_save:
                saveAction();
                return true;
            case R.id.action_delete_all:
                deleteAllAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAction() {

        ((MainActivity)getActivity()).showProgressBar();

        userViewModel.updateCurrentItem(Resource.castList(adapter.getImages(), ItemImage.class));
    }

    private void deleteAllAction() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_images))
                .setMessage(R.string.delete_images_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.getImages().clear();
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        actionInvoked=true;
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void addAction() {
        showAddResourceDialog();
    }

    public void deleteItem(int layoutPosition) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_image))
                .setMessage(R.string.delete_image_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        adapter.getImages().remove(layoutPosition);

                        adapter.notifyDataSetChanged();

                        actionInvoked=true;

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void setDefaultImage(int layoutPosition) {
        final ItemImage itemImage = (ItemImage) adapter.getItem(layoutPosition);
        currentItem.setImageId(itemImage.getId());
        currentItem.setImage(itemImage);
        adapter.setDefaultImageId(itemImage.getId());
        adapter.notifyDataSetChanged();
    }

    private void showAddResourceDialog() {
        // Create a new dialog to display options for adding a new resource
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_images);
        builder.setItems(new CharSequence[]{getString(R.string.from_gallery), getString(R.string.from_url)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        // Add resources from the phone gallery
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_images)), REQUEST_CODE_PICK_IMAGES);
                        break;
                    case 1:
                        // Add a resource from a URL
                        showAddResourceFromUrlDialog();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void showAddResourceFromUrlDialog() {
        // Create a new dialog to input a URL for the new resource
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_image_from_url);

        // Add an EditText view for inputting the URL
        final EditText urlEditText = new EditText(getActivity());
        urlEditText.setHint(R.string.enter_url);
        builder.setView(urlEditText);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Add the new resource with the provided URL
                String url = urlEditText.getText().toString();
                adapter.getImages().add(new ItemImage(url, currentItem.getId()));
                adapter.notifyDataSetChanged();

                actionInvoked=true;

            }
        });

        builder.setNegativeButton(R.string.cancel_option, null);
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Multiple images selected
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    long imageSize = MyUtils.getImageSize(getContext(), imageUri); // get image size
                    if (imageSize < 150 * 1024) { // check if image size is less than 150KB
                        String imagePath = MyUtils.getRealPathFromUri(getContext(), imageUri);
                        if (imagePath != null) {
                            adapter.getImages().add(new ItemImage(imagePath, currentItem.getId()));
                        }
                        adapter.notifyDataSetChanged();

                        actionInvoked=true;

                    }
                }
            } else {
                // Single image selected
                long imageSize = MyUtils.getImageSize(getContext(), data.getData()); // get image size
                if (imageSize < 150 * 1024) { // check if image size is less than 150KB

                    String imageUri = MyUtils.getRealPathFromUri(getContext(), data.getData());
                    if (imageUri != null) {
                        adapter.getImages().add(new ItemImage(imageUri, currentItem.getId()));
                    }
                    adapter.notifyDataSetChanged();

                    actionInvoked=true;

                } else {
                    Toast.makeText(getContext(), R.string.image_size_limit, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

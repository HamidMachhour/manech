package com.manech.manechapp.view.item.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.view.EditHtmlDialog;
import com.manech.manechapp.view.images.ItemImagesFragment;
import com.manech.manechapp.view.images.ResourceAdapter;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class ItemManagerFragment extends Fragment {
    private UserViewModel userViewModel;

    private WebView webViewDescription;
    private SliderView imagesSliderView;
    ResourceAdapter adapter;

    private LinearLayout titleCardHeader, detailsCardHeader, descriptionCardHeader;
    private LinearLayout titleLayout, detailsLayout, descriptionLayout;

    private ImageView imgArrow1, imgArrow2, imgArrow3, imgEditDescription;

    private EditText txtTitle;
    private EditText txtCode;
    private EditText txtLocation;
    private EditText txtQuantity;
    private EditText txtMinQty;
    private EditText txtMaxQty;
    private EditText txtPrice;
    private EditText txtBasePrice;
    private EditText txtShippingFee;

    private Item currentItem = null;
    private boolean refreshInvoked = false;

    private String htmlDescription = "";

    private View.OnClickListener onClickListener;
    private ResourceAdapter.OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getItemUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true);
                }
            }
        });
        userViewModel.getItemMutableLiveData().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (item != null) {
                        currentItem = item;
                        fillUI();
                    }
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.title_card_header:
                        collapseCardView(imgArrow1, titleLayout);
                        break;
                    case R.id.details_card_header:
                        collapseCardView(imgArrow2, detailsLayout);
                        break;
                    case R.id.description_card_header:
                        collapseCardView(imgArrow3, descriptionLayout);
                        break;
                    case R.id.imgEditDescription:
                        editDescription();
                        break;
                    case R.id.imagesSliderView:
                        ((MainActivity) getActivity()).initFragment(ItemImagesFragment.class, getString(R.string.images_edit_mode));
                        break;
                }
            }
        };

        // click on imagesSliderView
        onItemClickListener=new ResourceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ((MainActivity) getActivity()).initFragment(ItemImagesFragment.class, getString(R.string.images_edit_mode));
            }
        };
    }

    private void editDescription() {
        EditHtmlDialog dialog = EditHtmlDialog.newInstance(htmlDescription, new EditHtmlDialog.OnOkClickListener() {
            @Override
            public void onOkClick(String editedHtml) {
                htmlDescription = editedHtml;
                webViewDescription.loadDataWithBaseURL(null, editedHtml, "text/html", "UTF-8", null);
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "edit_html_dialog");
    }

    private void collapseCardView(ImageView img, ViewGroup viewGroup) {
        viewGroup.setVisibility(viewGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        img.setImageResource(viewGroup.getVisibility() == View.GONE ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
    }


    private void refreshAction(boolean reload) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentItem(reload, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        getActivity().setTitle(getString(R.string.item_details));

        View view = inflater.inflate(R.layout.fragment_item_edit_mode, container, false);

        initForm(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(true);
    }

    private void initForm(View view) {

        imagesSliderView = view.findViewById(R.id.imagesSliderView);
        imagesSliderView.setOnClickListener(onClickListener); // invoked when no images is set

        titleCardHeader = view.findViewById(R.id.title_card_header);
        titleCardHeader.setOnClickListener(onClickListener);

        detailsCardHeader = view.findViewById(R.id.details_card_header);
        detailsCardHeader.setOnClickListener(onClickListener);

        descriptionCardHeader = view.findViewById(R.id.description_card_header);
        descriptionCardHeader.setOnClickListener(onClickListener);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        imgArrow2 = view.findViewById(R.id.imgArrow2);
        imgArrow3 = view.findViewById(R.id.imgArrow3);

        imgEditDescription = view.findViewById(R.id.imgEditDescription);
        imgEditDescription.setOnClickListener(onClickListener);

        titleLayout =view.findViewById(R.id.title_layout);
        detailsLayout=view.findViewById(R.id.details_layout);
        descriptionLayout=view.findViewById(R.id.description_layout);

        adapter = new ResourceAdapter(onItemClickListener, new ArrayList<>());
        imagesSliderView.setSliderAdapter(adapter);

        txtTitle = view.findViewById(R.id.txtItemTitle);
        txtCode = view.findViewById(R.id.txtItemCode);
        txtLocation = view.findViewById(R.id.txtLocation);
        txtQuantity = view.findViewById(R.id.txtQuantity);
        txtMinQty = view.findViewById(R.id.txtMinQty);
        txtMaxQty = view.findViewById(R.id.txtMaxQty);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtBasePrice = view.findViewById(R.id.txtBasePrice);
        txtShippingFee = view.findViewById(R.id.txtShippingFee);
        webViewDescription = view.findViewById(R.id.txtDescription);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.save_back_menu, menu);
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
            refreshAction(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAction() {
        ((MainActivity)getActivity()).showProgressBar();

        formToItem();
        userViewModel.updateCurrentItem(null);
    }

    private void formToItem() {
        if (currentItem != null) {
            currentItem.setTitle(txtTitle.getText().toString());
            currentItem.setCode(txtCode.getText().toString());
            currentItem.setLocation(Integer.parseInt(txtLocation.getText().toString()));
            currentItem.setQuantity(MyUtils.parseDouble(txtQuantity.getText().toString()));
            currentItem.setMinQty(MyUtils.parseDouble(txtMinQty.getText().toString()));
            currentItem.setMaxQty(MyUtils.parseDouble(txtMaxQty.getText().toString()));
            currentItem.setPrice(MyUtils.parseDouble(txtPrice.getText().toString()));
            currentItem.setBasePrice(MyUtils.parseDouble(txtBasePrice.getText().toString()));
            currentItem.setShipping(MyUtils.parseDouble(txtShippingFee.getText().toString()));
            currentItem.setDescription(htmlDescription);
        }

    }


    private void fillUI() {
        if (currentItem != null && currentItem.getImages() != null) {
            adapter.getResourceList().clear();
            adapter.getResourceList().addAll(currentItem.getImages());
            adapter.notifyDataSetChanged();

            txtTitle.setText(currentItem.getTitle());
            txtCode.setText(currentItem.getCode());
            txtLocation.setText("" + currentItem.getLocation());
            txtQuantity.setText("" + currentItem.getQuantity());
            txtMinQty.setText("" + currentItem.getMinQty());
            txtMaxQty.setText("" + currentItem.getMaxQty());
            txtPrice.setText("" + currentItem.getPrice());
            txtBasePrice.setText("" + currentItem.getBasePrice());
            txtShippingFee.setText("" + currentItem.getShipping());
            htmlDescription = currentItem.getDescription();

            webViewDescription.loadDataWithBaseURL(null, htmlDescription, "text/html", "UTF-8", null);
        }
    }
}

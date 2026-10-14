package com.manech.manechapp.view.mstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.HtmlDescription;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.view.EditHtmlDialog;
import com.manech.manechapp.view.address.AddressChooserFragment;
import com.manech.manechapp.view.address.AddressChooserListener;
import com.manech.manechapp.view.address.AddressesFragment;
import com.manech.manechapp.view.images.ResourceAdapter;
import com.manech.manechapp.view.images.StoreImagesFragment;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;


public class StoreFragment extends Fragment {
    private UserViewModel userViewModel;

    ManechStore store;

    // general settings
    private TextView tvState;
    private EditText txtName;
    private WebView webViewDescription;

    private SliderView imagesSliderView;
    ResourceAdapter adapter;

    // address settings
    private TextView tvStreet;
    private TextView tvCity;
    private TextView tvZipCode;
    private TextView tvCountry;
    private TextView tvPhones;
    private TextView tvEmail;
    private TextView tvMapLocation;

    private Button btnManageAddresses;
    private Button btnSelectAddress;
    private ImageView imgEditDescription;

    private ImageView imgArrow1, imgArrow2, imgArrow3;
    private ViewGroup titleLayout, addressLayout;

    private LinearLayout titleCardHeader, addressCardHeader, descriptionCardHeader, descriptionLayout;

    private View.OnClickListener onClickListener;
    private ResourceAdapter.OnItemClickListener onItemClickListener;

    private boolean refreshInvoked = false;
    private String htmlDescription = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getStoreUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true);
                }
            }
        });
        userViewModel.getStoreMutableLiveData().observe(this, new Observer<ManechStore>() {
            @Override
            public void onChanged(ManechStore manechStore) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (manechStore != null) {
                        store = manechStore;
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
                    case R.id.generalSettingLayout:
                        collapseCardView(imgArrow1, titleLayout);
                        break;
                    case R.id.addressSettingLayout:
                        collapseCardView(imgArrow2, addressLayout);
                        break;
                    case R.id.descriptionHeaderLayout:
                        collapseCardView(imgArrow3, descriptionLayout);
                        break;
                    case R.id.imgEditDescription:
                        editDescription();
                        break;
                    case R.id.imagesSliderView:
                        ((MainActivity) getActivity()).initFragment(StoreImagesFragment.class, getString(R.string.images_edit_mode));
                        break;
                    case R.id.btnManageAddresses:
                        ((MainActivity) getActivity()).initFragment(AddressesFragment.class, getString(R.string.manage_addresses_edit_mode));
                        break;
                    case R.id.btnSelectAddress:
                        if (store != null) {
                            AddressChooserFragment fragment = AddressChooserFragment.newInstance((store.getAddress() != null) ? store.getAddress().getId() : null, "Select store address");
                            fragment.setListener(new AddressChooserListener() {
                                @Override
                                public void onSelect(Address selectedAddress) {
                                    store.setAddressId(selectedAddress.getId());
                                    store.setAddress(selectedAddress);
                                    fillAddressViewCard();
                                    fragment.dismiss();
                                }
                            });
                            fragment.show(getActivity().getSupportFragmentManager(), getString(R.string.address_select_dialog));
                        }
                        break;
                }
            }
        };

        // click on imagesSliderView
        onItemClickListener=new ResourceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int  position) {
                ((MainActivity) getActivity()).initFragment(StoreImagesFragment.class, getString(R.string.images_edit_mode));
            }
        };
    }

    private void editDescription() {
        EditHtmlDialog dialog = EditHtmlDialog.newInstance(htmlDescription, new EditHtmlDialog.OnOkClickListener() {
            @Override
            public void onOkClick(String editedHtml) {
                htmlDescription = editedHtml;
                HtmlDescription.render(webViewDescription, editedHtml);
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "edit_html_dialog");
    }


    private void collapseCardView(ImageView img, ViewGroup viewGroup) {
        viewGroup.setVisibility(viewGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        img.setImageResource(viewGroup.getVisibility() == View.GONE ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.store_details));

        View view = inflater.inflate(R.layout.fragment_store, container, false);

        initViewCards(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(true);
    }

    private void fillUI() {
        if (store != null && store.getImages() != null) {
            adapter.getResourceList().clear();
            adapter.getResourceList().addAll(store.getImages());
            adapter.notifyDataSetChanged();
            fillStoreViewCard();
            fillAddressViewCard();
        }
    }

    private void refreshAction(boolean reload) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentStore(reload, true, false);
        }
    }

    private void initViewCards(View view) {
        imgArrow1 = view.findViewById(R.id.imgArrow1);
        imgArrow2 = view.findViewById(R.id.imgArrow2);
        imgArrow3 = view.findViewById(R.id.imgArrow3);

        titleCardHeader = view.findViewById(R.id.generalSettingLayout);
        addressCardHeader = view.findViewById(R.id.addressSettingLayout);
        descriptionCardHeader = view.findViewById(R.id.descriptionHeaderLayout);
        descriptionLayout = view.findViewById(R.id.descriptionLayout);

        titleCardHeader.setOnClickListener(onClickListener);
        addressCardHeader.setOnClickListener(onClickListener);
        descriptionCardHeader.setOnClickListener(onClickListener);

        titleLayout = view.findViewById(R.id.storeGeneralView);
        addressLayout = view.findViewById(R.id.storeAddressView);
        addressLayout.setVisibility(View.GONE);
        titleLayout.setVisibility(View.VISIBLE);

        tvState = view.findViewById(R.id.tvState);
        txtName = view.findViewById(R.id.txtName);
        webViewDescription = view.findViewById(R.id.txtDescription);

        imagesSliderView = view.findViewById(R.id.imagesSliderView);
        imagesSliderView.setOnClickListener(onClickListener); // invoked when no images is set

        adapter = new ResourceAdapter(onItemClickListener, new ArrayList<>());
        imagesSliderView.setSliderAdapter(adapter);

        tvStreet = view.findViewById(R.id.tvStreetAddress);
        tvCity = view.findViewById(R.id.tvCity);
        tvZipCode = view.findViewById(R.id.tvZipCode);
        tvCountry = view.findViewById(R.id.tvCountry);
        tvPhones = view.findViewById(R.id.tvPhones);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMapLocation = view.findViewById(R.id.tvCoordinates);
        btnManageAddresses = view.findViewById(R.id.btnManageAddresses);
        btnSelectAddress = view.findViewById(R.id.btnSelectAddress);

        btnManageAddresses.setOnClickListener(onClickListener);
        btnSelectAddress.setOnClickListener(onClickListener);

        imgEditDescription = view.findViewById(R.id.imgEditDescription);
        imgEditDescription.setOnClickListener(onClickListener);

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

        formToStore();
        userViewModel.updateCurrentStore(null);
    }

    private void formToStore() {
        if (store != null) {
            store.setName(txtName.getText().toString());
            store.setDescription(htmlDescription);
        }
    }

    private void fillStoreViewCard() {

        if (store != null) {
            String state = "";
            if (store.getPublishDate() != null) {
                state = getString(R.string.published_state);
            }
            if (store.getId().equals(userViewModel.getCurrentUser().getStoreId())) {
                state += " (" + getString(R.string.default_store_label) + ")";
            }
            tvState.setText(state);
            txtName.setText(store.getName());
            htmlDescription = store.getDescription();
            HtmlDescription.render(webViewDescription, htmlDescription);
        }
    }


    private void fillAddressViewCard() {
        if (store != null && store.getAddress() != null) {
            tvStreet.setText(store.getAddress().getStrAddress());
            tvCity.setText(store.getAddress().getCity());
            tvZipCode.setText(store.getAddress().getZipCode());
            tvCountry.setText(store.getAddress().getCountry());
            tvPhones.setText(store.getAddress().getPhones());
            tvEmail.setText(store.getAddress().getEmail());
            tvMapLocation.setText(store.getAddress().getLocation().getLatitude() + "\n"
                    + store.getAddress().getLocation().getLongitude());
        }
    }
}

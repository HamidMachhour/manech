package com.manech.manechapp.view.address;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.model.UserViewModel;

public class AddressFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private EditText txtStreet;
    private EditText txtCity;
    private EditText txtCountry;
    private EditText txtZipcode;
    private EditText txtPhones;
    private TextView txtLatitude;
    private TextView txtLongitude;

    private EditText txtEmail;

    private UserViewModel userViewModel;
    private Address currentAddress;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;

    private MapView mapView;
    private Marker marker;
    private boolean refreshInvoked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getAddressUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true);
                }
            }
        });
        userViewModel.getAddressMutableLiveData().observe(this, new Observer<Address>() {
            @Override
            public void onChanged(Address address) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (address != null) {
                        currentAddress = address;
                        addressToForm();
                    }
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        refreshInvoked = false;
        refreshAction(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.address_details));
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        initForm(view, savedInstanceState);
        return view;
    }

    private void initForm(View view, Bundle savedInstanceState) {
        txtStreet = view.findViewById(R.id.txtStreetAddress);
        txtCity = view.findViewById(R.id.txtCity);
        txtCountry = view.findViewById(R.id.txtCountry);
        txtZipcode = view.findViewById(R.id.txtZipCode);
        txtPhones = view.findViewById(R.id.txtPhones);
        txtLatitude = view.findViewById(R.id.txtLatitude);
        txtLongitude = view.findViewById(R.id.txtLongitude);

        txtEmail = view.findViewById(R.id.txtEmail);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Initialize the location manager
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        }
        return super.onOptionsItemSelected(item);
    }


    private void refreshAction(boolean reload) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentAddress(reload);
        }
    }

    private void saveAction() {
        ((MainActivity)getActivity()).showProgressBar();

        formToAddress();
        userViewModel.updateCurrentAddress();
    }

    private void formToAddress() {
        if (currentAddress != null) {
            currentAddress.setStrAddress(txtStreet.getText().toString());
            currentAddress.setCity(txtCity.getText().toString());
            currentAddress.setCountry(txtCountry.getText().toString());
            currentAddress.setZipCode(txtZipcode.getText().toString());
            currentAddress.setPhones(txtPhones.getText().toString());
            currentAddress.setEmail(txtEmail.getText().toString());
            currentAddress.setLocation(mCurrentLocation);
        }

    }

    private void addressToForm() {
        if (currentAddress != null) {
            txtStreet.setText(currentAddress.getStrAddress());
            txtCity.setText(currentAddress.getCity());
            txtCountry.setText(currentAddress.getCountry());
            txtZipcode.setText(currentAddress.getZipCode());
            txtPhones.setText(currentAddress.getPhones());
            txtEmail.setText(currentAddress.getEmail());

            mCurrentLocation = currentAddress.getLocation();
            if (mCurrentLocation != null) {
                txtLatitude.setText(mCurrentLocation.getLatitude() + "");
                txtLongitude.setText(mCurrentLocation.getLongitude() + "");
            }
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mCurrentLocation == null) {
            mCurrentLocation = getCurrentLocation();
        }

        //Log.e("MANECH_TAG",mCurrentLocation.toString());

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

            if (marker == null) {
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.setVisible(true);
            } else {
                marker.setPosition(latLng);
            }
        }

        // Set a click listener to allow the user to choose a location
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        marker.setPosition(latLng);
        // Store the chosen location in a Location object
        if (mCurrentLocation == null)
            mCurrentLocation = new Location("");

        mCurrentLocation.setLatitude(latLng.latitude);
        mCurrentLocation.setLongitude(latLng.longitude);

        txtLatitude.setText(currentAddress.getLocation().getLatitude() + "");
        txtLongitude.setText(currentAddress.getLocation().getLongitude() + "");

    }

    private Location getCurrentLocation() {
        // Check if location services are enabled
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Prompt the user to enable location services
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return null;
        }

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission from the user
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return null;
        }

        // Get the current location of the entity
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

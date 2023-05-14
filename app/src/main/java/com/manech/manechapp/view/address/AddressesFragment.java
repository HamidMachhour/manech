package com.manech.manechapp.view.address;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.User;
import com.manech.manechapp.model.UserViewModel;

import java.util.ArrayList;

public class AddressesFragment extends Fragment{
    private UserViewModel userViewModel;

    private RecyclerView addressListView;
    private AddressListAdapter adapter;

    private boolean refreshInvoked = false;

    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(refreshInvoked){
                    refreshInvoked=false;
                    if(user!=null) {
                        currentUser=user;
                        fillUI();
                    }else{
                        ((MainActivity) getActivity()).logout();
                    }
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
        userViewModel.getUserUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true,true);
                }
            }
        });
    }

    private void fillUI() {
        if (currentUser != null && currentUser.getUserAddresses() != null) {
            adapter.getAddressList().clear();
            adapter.getAddressList().addAll(currentUser.getUserAddresses());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.manage_addresses));
        View view = inflater.inflate(R.layout.fragment_addresses, container, false);
        addressListView = view.findViewById(R.id.address_list_view);
        addressListView.setHasFixedSize(true);
        addressListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new AddressListAdapter(new ArrayList<>(), AddressesFragment.this);
        addressListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(false,true);
    }

    private void refreshAction(boolean reload, boolean reloadAddresses) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentUser(reload,false,reloadAddresses,false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.addresses_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
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
                refreshAction(false,true);
                return true;
            case R.id.action_add:
                addAction();
                return true;
            case R.id.action_save:
                saveAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAction() {

        ((MainActivity)getActivity()).showProgressBar();
        userViewModel.updateCurrentUser(null, null,adapter.getAddressList());
    }

    private void addAction() {
        adapter.getAddressList().add(new Address("No Street", "No City", "00000",
                "No country", null, "0.0", "", currentUser.getId()));
        adapter.notifyDataSetChanged();
    }

    public void addressClicked(int position) {

        userViewModel.setCurrentAddress(adapter.getItem(position));
        ((MainActivity) getActivity()).initFragment(AddressFragment.class, getString(R.string.manage_address_edit_mode));
    }

    public void duplicateItem(int layoutPosition) {
        final Address address = adapter.getItem(layoutPosition);
        adapter.getAddressList().add(address.clone());
        adapter.notifyDataSetChanged();
    }

    public void deleteItem(int layoutPosition) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_address))
                .setMessage(R.string.delete_address_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.getAddressList().remove(layoutPosition);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}

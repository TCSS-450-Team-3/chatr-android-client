package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentLocationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends DialogFragment {


    private MapFragment mMapFragment;
    private List<Address> mAddressList;
    private Geocoder mGeocoder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeocoder = new Geocoder(requireContext(), Locale.US);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentLocationBinding binding = FragmentLocationBinding.bind(requireView());
        mMapFragment = binding.mapContainer.getFragment();
        binding.saveLocationButton.setOnClickListener(v -> {
            Marker marker = mMapFragment.getMarker();
            if (marker == null) {
                Snackbar.make(
                    requireView(),
                "Place a marker to save a location.",
                    BaseTransientBottomBar.LENGTH_SHORT
                ).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                return;
            }
            // TODO: Add a location object, myLocation, with a name alias and latlng object
            String locationAlias = openCustomLocationNameDialog(marker); // update custom location name with dialog

        });
        binding.currentLocationButton.setOnClickListener(v -> {
            ((MapFragment) binding.mapContainer.getFragment()).panCameraToCurrentLocation();
        });
        binding.mapSearchView.getEditText()
                .setOnEditorActionListener((v, actionId, event) -> {
                    // Carry over the search text to the SearchBar
                    binding.mapSearchBar.setText(binding.mapSearchView.getText());
                    String query = Objects.requireNonNull(binding.mapSearchView.getText()).toString();
                    // Hide the SearchView when the user finishes typing and presses enter
                    binding.mapSearchView.hide();
                    if (!query.equals("")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mGeocoder.getFromLocationName(query, 1, addresses -> {
                                mAddressList = addresses;
                                Log.d("GEOCODER", "Updating address lists.");
                            });
                        } else {
                            try {
                                mAddressList = mGeocoder.getFromLocationName(query, 5);
                            } catch (IOException e) {
                                Log.e("GEOCODER", e.getMessage());
                            }
                        }
                        if (mAddressList != null) {
                            if (!mAddressList.isEmpty()) mMapFragment.placeMarker(
                                    new LatLng(mAddressList.get(0).getLatitude(), mAddressList.get(0).getLongitude())
                            );
                        }
                    }
                    return false;
                });
    }

    /**
     * Opens a dialog to prompt the user to set a custom location name for the given marker.
     * @param marker The marker for the location to be given a custom name.
     * @return The custom name the user has chosen for the location.
     */
    private String openCustomLocationNameDialog(Marker marker) {
        EditText locNameField = new EditText(requireContext()); // used for custom name
        locNameField.setText(marker.getTitle());
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save Location")
                .setMessage("Customize the name for the saved location:")
                .setView(locNameField.getRootView(),48, 0, 48, 0)
                .setPositiveButton("Save", (dialog, which) -> {
                    Log.d("POSITIVE", "clicked ");
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d("NEGATIVE", "clicked ");
                    dialog.cancel();
                })
                .show();
        return locNameField.getText().toString();
    }
}
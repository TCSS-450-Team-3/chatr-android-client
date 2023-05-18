package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;
import java.util.Objects;

import edu.uw.tcss450.kylerr10.chatapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    /**
     * Holds the current location selected on the {@link MapFragment#mMap}.
     */
    private Marker mMarker;
    /**
     * Holds the current latitude and longitude of the user's device.
     */
    private LatLng mLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // add this fragment as the OnMapReadyCallback -> See onMapReady()

        if (mapFragment != null) { //TODO: Figure out why this is null
            mapFragment.getMapAsync(this);
        } else Log.e("MAP FRAGMENT", "Map fragment is null.");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LocationViewModel model = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false); // disable zoom buttons
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // disable location button
        model.addLocationObserver(getViewLifecycleOwner(), Objects.requireNonNull(location -> {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) { // Should never happen (checks done in main activity)
                Log.e("LOCATION", "Necessary permissions not granted.");
            }
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            panCameraToCurrentLocation();
        }));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        placeMarker(latLng);
    }

    /**
     * Returns a BitmapDescriptor of the given drawable vector as a rendered bitmap image. Intended
     * for use in setting the marker icon in a map.
     * @param drawable The id of the drawable vector icon.
     * @param scale The scale at which to draw the icon.
     * @return A BitmapDescriptor of the given drawable vector as a rendered bitmap image.
     */
    private BitmapDescriptor asMarkerIcon(int drawable, double scale) {
        Canvas canvas = new Canvas();
        Drawable markerDrawable = ContextCompat.getDrawable(requireContext(), drawable);
        assert markerDrawable != null;
        Bitmap markerBitmap = Bitmap.createBitmap(
                (int) (markerDrawable.getIntrinsicWidth()*scale),
                (int) (markerDrawable.getIntrinsicHeight()*scale),
                Bitmap.Config.ARGB_8888
        );
        canvas.setBitmap(markerBitmap);
        markerDrawable.setBounds(
                0,
                0,
                (int) (markerDrawable.getIntrinsicWidth()*scale),
                (int) (markerDrawable.getIntrinsicHeight()*scale)
        );
        markerDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    /**
     * Removes previous markers that may exist and places a new {@link Marker} at the given
     * {@link LatLng}'s latitude and longitude.
     * @param latLng The latitude and longitude to place the new marker.
     */
    void placeMarker(@NonNull LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
        if (mMarker != null) mMarker.remove(); // Remove previous marker
        mMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(asMarkerIcon(R.drawable.ic_marker_red_24dp, 1.5))
                        .title(String.format(Locale.US, "%.2f, %.2f", latLng.latitude, latLng.longitude))
        );
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom)
        );
    }

    /**
     * Returns the current {@link MapFragment#mMarker} as it exists on the current {@link MapFragment#mMap}.
     * @return The current {@link MapFragment#mMarker} as it exists on the current {@link MapFragment#mMap}.
     */
    Marker getMarker() {
        return mMarker;
    }

    void panCameraToCurrentLocation() {
        if (mMap != null && mLatLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15.0f));
        }
    }
}

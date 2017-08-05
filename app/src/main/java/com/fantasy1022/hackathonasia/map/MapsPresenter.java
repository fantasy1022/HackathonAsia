package com.fantasy1022.hackathonasia.map;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.fantasy1022.hackathonasia.R;
import com.fantasy1022.hackathonasia.base.BasePresenter;
import com.fantasy1022.hackathonasia.entity.PlaceDetailEntity;
import com.fantasy1022.hackathonasia.entity.PlaceEntity;
import com.fantasy1022.hackathonasia.repository.FirebaseRepository;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by fantasy_apple on 2017/8/5.
 */

public class MapsPresenter extends BasePresenter<MapsContract.View> implements MapsContract.Presenter,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = MapsFragment.class.getSimpleName();
    private final int DEFAULT_ZOOM = 15;
    private final LatLng DEFAULT_LOCATION = new LatLng(-33.8523341, 151.2106085); //Use taipei
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;
    private FragmentActivity fragmentActivity;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private String[] areaItem;
    private BitmapDescriptor bitmapDescriptor;

    public MapsPresenter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;

    }

    @Override
    public void initGoogleApiClient(GoogleApiClient.OnConnectionFailedListener listener, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity /* FragmentActivity */,
                        listener /* OnConnectionFailedListener */)
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void getDateFromFirebase(String key) {
        FirebaseRepository.getInstance().getDateFromFirebase(key);
    }

    @Override
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        areaItem = fragmentActivity.getResources().getStringArray(R.array.choice_area_item);
        bitmapDescriptor = getMarkerIcon(ContextCompat.getColor(fragmentActivity, R.color.colorRoad));
    }


    @Override
    public void updateLocationUI() {

        if (ContextCompat.checkSelfPermission(fragmentActivity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(fragmentActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (locationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            lastKnownLocation = null;
        }
    }

    @Override
    public void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(fragmentActivity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(fragmentActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (locationPermissionGranted) {
            lastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (cameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else if (lastKnownLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            Log.d(TAG, "lat:" + lastKnownLocation.getLatitude() + " lon:" + lastKnownLocation.getLongitude());
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void handlePermission(int requestCode, int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void updateMapMaker(int space, int density, int oldNum) {
        //Log.d(TAG, "space:" + space + " density:" + density);
        ArrayList<PlaceDetailEntity> placeDetailEntities = new ArrayList<>();
        PlaceEntity placeEntity = FirebaseRepository.getInstance().getPlaceEntity();

        if (placeEntity != null) {
            placeDetailEntities = placeEntity.getData();
        }

        if (googleMap != null) {
            googleMap.clear();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            for (int i = 0; i < placeDetailEntities.size(); i++) {
                String lmbig = placeDetailEntities.get(i).getLmbig();
                int spaceRank = placeDetailEntities.get(i).getFloorra();
                int densityRank = placeDetailEntities.get(i).getGridra();
                int oldRank = placeDetailEntities.get(i).getOldra();
//                double x = placeDetailEntities.get(i).getX();
//                double y = placeDetailEntities.get(i).getY();
                // Log.d(TAG,"spaceRank:"+spaceRank+ " densityRank:"+densityRank);

                if (spaceRank == space && densityRank == density && oldRank == oldNum) {
                    LatLng latLng = new LatLng(placeDetailEntities.get(i).getY(), placeDetailEntities.get(i).getX());
                    String content = String.format("地標名稱:%s\n地標大分類:%s\n地標小分類:%s\n周邊人口數:%s\n周邊服務老人數:%s\n開放空間指數:%s\n建築密度:%s",
                            placeDetailEntities.get(i).getName(),
                            placeDetailEntities.get(i).getLmbig(),
                            placeDetailEntities.get(i).getLmsmall(),
                            placeDetailEntities.get(i).getPop(),
                            placeDetailEntities.get(i).getOld(),
                            placeDetailEntities.get(i).getGrid(),
                            placeDetailEntities.get(i).getFloor());


                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(bitmapDescriptor)
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(latLng)
                            .title(content);

                    MapInfoWindowAdapter adapter = new MapInfoWindowAdapter(fragmentActivity);
                    googleMap.setInfoWindowAdapter(adapter);
                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.getCameraPosition().zoom));

                }
            }
        }

    }


    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        //  Log.d(TAG, "marker:" + marker.getTitle());
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        MapDetailActivity.newIntent(fragmentActivity, marker.getSnippet(), marker.getTitle());

        // Log.d(TAG, "marker:" + marker.getTitle());
    }

    private boolean isInTargetArea(Integer[] areaChoice, String districtChoice) {
        for (int i = 0; i < areaChoice.length; i++) {
            if (areaItem[areaChoice[i]].equals(districtChoice)) {
                return true;
            }
        }
        return false;
    }

}

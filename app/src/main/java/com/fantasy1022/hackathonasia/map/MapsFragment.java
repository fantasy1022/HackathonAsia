package com.fantasy1022.hackathonasia.map;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantasy1022.hackathonasia.R;
import com.fantasy1022.hackathonasia.event.DataChangeEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fantasy_apple on 2017/8/5.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback, MapsContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();
    private MapsPresenter mapPresenter;
//    @BindView(R.id.map_type_spinner)
//    Spinner mapTypeSpinner;

    @BindView(R.id.seekbarLeft)
    BubbleSeekBar seekbarSpace;
    @BindView(R.id.seekbarRight)
    BubbleSeekBar seekbarDensity;
    @BindView(R.id.seekbarTop)
    BubbleSeekBar seekbarTop;

    private boolean isSync;

    private int space = 5;
    private int density = 5;
    private int oldNum = 5;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mapPresenter == null) {
            Log.d(TAG, "mapPresenter");
            mapPresenter = new MapsPresenter(getActivity());
            mapPresenter.initGoogleApiClient(this, this);
            mapPresenter.getDateFromFirebase("map_type");

//            mapPresenter.updateLocationUI();
//            mapPresenter.getDeviceLocation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
//        MapTypeAdapter adapter = new MapTypeAdapter(getActivity().getResources().getStringArray(R.array.map_type_spinner), getActivity().getResources().getIntArray(R.array.colorTypeMaps));
//        mapTypeSpinner.setAdapter(adapter);
        seekbarSpace.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                space = progress;
                mapPresenter.updateMapMaker(space, density, oldNum);
            }
        });
        seekbarSpace.setProgress(space);

        seekbarDensity.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                density = progress;
                mapPresenter.updateMapMaker(space, density, oldNum);
            }
        });
        seekbarDensity.setProgress(density);
        seekbarTop.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                oldNum = progress;
                mapPresenter.updateMapMaker(space, density, oldNum);
            }
        });

        seekbarTop.setProgress(oldNum);


        return rootView;
    }

//    @OnItemSelected(R.id.map_type_spinner)
//    public void onMapTypeSelected(Spinner spinner, int position) {
//        Log.d(TAG, "position:" + position);
//        typeIndex = position;
//        mapPresenter.updateMapMaker(typeIndex, weekSeekBar);
//    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mapPresenter.setGoogleMap(googleMap);
        mapPresenter.updateLocationUI();
        mapPresenter.getDeviceLocation();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Play services connection onConnected");
        if (!isSync) {
            isSync = true;
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mapPresenter.handlePermission(requestCode, grantResults);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFirebaseDataChange(DataChangeEvent event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapPresenter.updateMapMaker(space, density, oldNum);
            }
        }, 1000);
    }


    @Override
    public void onResume() {
        super.onResume();
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map_fragment);
//        mapFragment.getMapAsync(this);
    }

//    public void updateAreaChoice(Integer[] which) {
//        areaItem = which;
//        mapPresenter.updateMapMaker(space, density, areaItem);
//    }

//    public Integer[] getAreaItem(){
//        return areaItem;
//    }

}

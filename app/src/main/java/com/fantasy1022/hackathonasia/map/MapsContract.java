package com.fantasy1022.hackathonasia.map;

import com.fantasy1022.hackathonasia.base.MvpPresenter;
import com.fantasy1022.hackathonasia.base.MvpView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by fantasy_apple on 2017/8/5.
 */

public interface MapsContract {

    interface View extends MvpView {


    }

    interface Presenter extends MvpPresenter<View> {

        void initGoogleApiClient(GoogleApiClient.OnConnectionFailedListener listener, GoogleApiClient.ConnectionCallbacks connectionCallbacks);

        void getDateFromFirebase(String key);

        void setGoogleMap(GoogleMap googleMap);

        void updateLocationUI();

        void getDeviceLocation();

        void handlePermission(int requestCode, int[] grantResults);

        void updateMapMaker(int space, int density, int oldNum);

    }
}

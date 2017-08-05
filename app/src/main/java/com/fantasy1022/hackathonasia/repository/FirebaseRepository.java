package com.fantasy1022.hackathonasia.repository;

import android.util.Log;

import com.fantasy1022.hackathonasia.entity.PlaceEntity;
import com.fantasy1022.hackathonasia.event.DataChangeEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by fantasy_apple on 2017/8/5.
 */

public class FirebaseRepository {
    private String TAG = getClass().getSimpleName();
    private final FirebaseDatabase database;
    private DatabaseReference myRef;
    private ValueEventListener placeListener;
    private PlaceEntity placeEntity;

    private static class Loader {
        static volatile FirebaseRepository INSTANCE = new FirebaseRepository();
    }

    public static FirebaseRepository getInstance() {
        return Loader.INSTANCE;
    }

    private FirebaseRepository() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        placeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange:");
//                Gson gson = new Gson();
//                placeEntity = gson.fromJson(dataSnapshot.toString(), PlaceEntity.class);

                // dataSnapshot.toString();
                placeEntity = dataSnapshot.getValue(PlaceEntity.class);
                EventBus.getDefault().post(new DataChangeEvent(true));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "dataSnapshot:" + databaseError.toString());
                EventBus.getDefault().post(new DataChangeEvent(false));

            }
        };
    }

    public PlaceEntity getPlaceEntity() {
        return placeEntity;
    }

    public void getDateFromFirebase(String key) {
        myRef = database.getReference(key);
        myRef.addValueEventListener(placeListener);
    }


    public void removeListener() {
        myRef.removeEventListener(placeListener);
    }
}


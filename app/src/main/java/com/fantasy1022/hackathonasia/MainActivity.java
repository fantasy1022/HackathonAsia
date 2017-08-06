package com.fantasy1022.hackathonasia;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fantasy1022.hackathonasia.map.MapsFragment;
import com.fantasy1022.hackathonasia.report.ReportActivity;
import com.fantasy1022.hackathonasia.repository.FirebaseRepository;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final String TAG = getClass().getSimpleName();

    MapsFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        FirebaseRepository.getInstance().getDateFromFirebase("map_type");

        ButterKnife.bind(this);

        initFragment();
    }

    private void initFragment() {
        mapFragment = new MapsFragment();
        setFragment(mapFragment, false);
    }


    private void setFragment(Fragment fragment, boolean isAddBackStack) {
        FragmentManager fragmentMgr = getSupportFragmentManager();
        FragmentTransaction fragmentTrans = fragmentMgr.beginTransaction();
        Log.d(TAG, "fragment.getClass().getName():" + fragment.getClass().getName());
        fragmentTrans.replace(R.id.content_layout, fragment, fragment.getClass().getName());//TODO: Check tag fragmentTrans
        if (isAddBackStack) {
            fragmentTrans.addToBackStack(null);
        }
        fragmentMgr.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d(TAG, "onBackStackChanged");
            }
        });
        fragmentTrans.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_case_query) {
            // Handle the camera action
            String road ="道路維護";
            String roadSub = "路平問題";
            String[] roadSubOpt = getResources().getStringArray(R.array.report_road_sub_item);
            String roadSubTwo = getString(R.string.report_road_sub_two);
            String[] roadSubTwoOpt = getResources().getStringArray(R.array.report_road_sub_two_item);
            ReportActivity.newIntent(this,road,roadSub,roadSubOpt,roadSubTwo,roadSubTwoOpt);
        } else if (id == R.id.nav_telephone_query) {

           // http://www.society.taichung.gov.tw/tour/index.asp?Parser=13,3,17

        } else if (id == R.id.nav_normal_qa) {

        } else if (id == R.id.nav_introduction) {
          //  https://mayormail.taichung.gov.tw/nTaichung/writemail/FAQ.aspx?t=11
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

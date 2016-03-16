package com.example.sarabjeetsingh.beacondetector.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sarabjeetsingh.beacondetector.R;
import com.example.sarabjeetsingh.beacondetector.adapters.LevelAdapter;
import com.example.sarabjeetsingh.beacondetector.adapters.MembersAdapter;
import com.example.sarabjeetsingh.beacondetector.utils.MyApplication;
import com.example.sarabjeetsingh.beacondetector.utils.ZPreferences;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.ParseUser;

import org.altbeacon.beacon.BeaconManager;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String [] level_hints;
    TypedArray level_images;
    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView recyclerView;
    LevelAdapter levelAdapter;
    TextView textViewHint;
    ImageView imageViewHint;
    int currentLevel = 0;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyBluetooth();

        currentLevel = Integer.valueOf(ZPreferences.getHuntLevel(this));

        level_hints = getResources().getStringArray(R.array.level_hints);
        level_images  = getResources().obtainTypedArray(R.array.level_images);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(" ");

        textViewHint = (TextView) findViewById(R.id.text_level_hint);
        imageViewHint = (ImageView) findViewById(R.id.image_level_hint);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUserDetails();

       levelAdapter = new LevelAdapter(this, currentLevel);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewLevels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(levelAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        levelAdapter.SetOnItemClickListener(new LevelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(position <= currentLevel){
                    updateLevel(position);
                }else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Level is Locked", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    public void setUserDetails(){
        View headerLayout = navigationView.getHeaderView(0);
        CircularImageView userImageView = (CircularImageView) headerLayout.findViewById(R.id.user_imageview);
        TextView userName = (TextView) headerLayout.findViewById(R.id.user_name);
        TextView userEmail = (TextView) headerLayout.findViewById(R.id.user_email);

        ParseUser user = ParseUser.getCurrentUser();
       Glide.with(this).load(user.get("profilePic")).into(userImageView);
        if(user.get("lastName") == null){
            userName.setText(user.get("firstName") + " ");
        }else userName.setText(user.get("firstName") + " " + user.get("lastName"));

        userEmail.setText(user.getEmail());

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item != null && item.getItemId() == R.id.action_drawer_toggle) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            }
            else {
              drawer.openDrawer(Gravity.RIGHT);
            }
        }

        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }  else if (id == R.id.nav_share) {

        }else if(id == R.id.nav_create_group){
            Intent intent = new Intent(this, GroupRegistration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void logToDisplay(final String level) {
        runOnUiThread(new Runnable() {
            public void run() {
                currentLevel = Integer.valueOf(level);
                updateLevel(currentLevel);

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MyApplication) this.getApplicationContext()).setMonitoringActivity(this);

        currentLevel = Integer.valueOf(ZPreferences.getHuntLevel(this));
        if(currentLevel >= 0){
            updateLevel(currentLevel);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MyApplication) this.getApplicationContext()).setMonitoringActivity(null);
    }

    public void updateLevel(int level){

        textViewHint.setText(level_hints[level] + " ");
        Log.d("ImageResource", String.valueOf(level_images.getResourceId(level, -1)));
        imageViewHint.setImageResource(level_images.getResourceId(level, -1));

        levelAdapter.notifyItemChanged(level);

    }


    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                            System.exit(0);
                        }
                    });
                }
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }

                });
            }
            builder.show();

        }

    }
}

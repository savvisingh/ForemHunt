package com.example.sarabjeetsingh.beacondetector.utils;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sarabjeetsingh.beacondetector.Activity.HomeActivity;
import com.example.sarabjeetsingh.beacondetector.R;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarabjeet Singh on 3/9/2016.
 */
public class MyApplication extends Application implements BootstrapNotifier
{
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;
    private BeaconManager mBeaconManager;
    private BackgroundPowerSaver backgroundPowerSaver;
    private HomeActivity monitoringActivity = null;

    List<Region> region_list ;

    String[] instanceIds ;
    Identifier myBeaconNamespaceId;
    Identifier myBeaconsInstanceId;

    int current_level = 0;

    String beacon_detected_instance_id;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // FacebookSdk.sdkInitialize(this);

        Parse.initialize(this,"jO1ouDITRQhoGbUrAMH1qtGcGiWChssLmEDV3P7d","hKoMBhozLW3iJyrzkKenPnNXC73oCiEBFeyqAANp");
        ParseUser.enableRevocableSessionInBackground();

        ParseFacebookUtils.initialize(this);
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        // defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        region_list = new ArrayList<>();
        instanceIds = getResources().getStringArray(R.array.instanceId_beacons);
        mBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().clear();
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.setBackgroundScanPeriod(11001);
        mBeaconManager.setBackgroundBetweenScanPeriod(10000);
       // mBeaconManager.bind(this);
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Identifier myBeaconNamespaceId = Identifier.parse("0x5DC33487F02E477D4058");

        for(int i = 0; i < 3 ;i++){
            myBeaconsInstanceId = Identifier.parse(instanceIds[i]);
            Region region = new Region("my-beacon-region level-"+ Integer.valueOf(i), myBeaconNamespaceId, myBeaconsInstanceId, null);
            region_list.add(region);
        }

        Log.d("size", String.valueOf(region_list.size()));
        regionBootstrap = new RegionBootstrap(this, region_list);

        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }


    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.


        current_level = Integer.valueOf(ZPreferences.getHuntLevel(this));
        beacon_detected_instance_id = arg0.getId2().toString();

        Log.d(TAG, "did enter region." + arg0.getId1() + " " + beacon_detected_instance_id);

        if(instanceIds[current_level].equals(beacon_detected_instance_id)){
            current_level++;
            ZPreferences.setHuntLevel(this, String.valueOf(current_level));
            if (monitoringActivity != null) {
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                monitoringActivity.logToDisplay(String.valueOf(current_level));
                sendNotification();
            } else {
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
                Log.d(TAG, "Sending notification.");
                sendNotification();
            }
        }


        }


    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "Called DidExitRegion");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {

    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Hurray you have solved the Clue")
                        .setContentText("Your level has upgraded. Tap to see clue...")
                        .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, HomeActivity.class));
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }


    }

    public void setMonitoringActivity(HomeActivity activity) {
        this.monitoringActivity = activity;
    }

}


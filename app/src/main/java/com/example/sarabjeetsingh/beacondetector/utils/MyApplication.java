package com.example.sarabjeetsingh.beacondetector.utils;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sarabjeetsingh.beacondetector.Activity.MainActivity;
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
    private MainActivity monitoringActivity = null;

    List<Region> region_list = new ArrayList();
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

        mBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().clear();
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.setBackgroundScanPeriod(11001);
        mBeaconManager.setBackgroundBetweenScanPeriod(10000);
       // mBeaconManager.bind(this);
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Identifier myBeaconNamespaceId = Identifier.parse("0x5dc33487f02e477d4058");
        Region region = new Region("my-beacon-region", myBeaconNamespaceId, null, null);

        region_list.add(region);
        regionBootstrap = new RegionBootstrap(this, region_list);

        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }


    @Override
    public void didEnterRegion(Region arg0) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "did enter region." + arg0.getId1() + " " + arg0.getId2() + " " + arg0.getId3());

//        if (!haveDetectedBeaconsSinceBoot) {
//            Log.d(TAG, "auto launching MainActivity");
//
//            // The very first time since boot that we detect an beacon, we launch the
//            // MainActivity
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
//            // to keep multiple copies of this activity from getting created if the user has
//            // already manually launched the app.
//            this.startActivity(intent);
//            haveDetectedBeaconsSinceBoot = true;
//        } else {
            if (monitoringActivity != null) {
                // If the Monitoring Activity is visible, we log info about the beacons we have
                // seen on its display
                monitoringActivity.logToDisplay("I see a beacon again" );
            } else {
                // If we have already seen beacons before, but the monitoring activity is not in
                // the foreground, we send a notification to the user on subsequent detections.
                Log.d(TAG, "Sending notification.");
                sendNotification();
            }
        }


    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "Called DidExitRegion");
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I no longer see a beacon.");
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon Reference Application")
                        .setContentText("An beacon is nearby.")
                        .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
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

    public void setMonitoringActivity(MainActivity activity) {
        this.monitoringActivity = activity;
    }

}


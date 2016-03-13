package com.example.sarabjeetsingh.beacondetector.Activity;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.sarabjeetsingh.beacondetector.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;

    @Override
    public void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);
        mBeaconManager.setBackgroundScanPeriod(5000);
    }



    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
    }
    Identifier namespaceId, instanceId;

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, org.altbeacon.beacon.Region region) {
        for (final Beacon beacon: collection) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                // This is a Eddystone-UID frame
                 namespaceId = beacon.getId1();
                 instanceId = beacon.getId2();
                Log.d("RangingActivity", "I see a beacon transmitting namespace id: " + namespaceId +
                        " and instance id: " + instanceId +
                        " approximately " + beacon.getDistance() + " meters away.");
                runOnUiThread(new Runnable() {
                    public void run() {
                        ((TextView)RangingActivity.this.findViewById(R.id.message)).setText("I see a beacon transmitting namespace id: " + namespaceId +
                                " and instance id: " + instanceId +
                                " approximately " + beacon.getDistance() + " meters away.");
                    }
                });
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }
}

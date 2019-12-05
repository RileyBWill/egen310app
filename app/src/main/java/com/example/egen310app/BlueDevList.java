package com.example.egen310app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;


// This class creates a popup activity to look at tall the lists of Bluetooth devices discovered
public class BlueDevList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "BlueDevList";
    BluetoothAdapter baAdapter;
    public ArrayList<BluetoothDevice> mBTDevices;
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    ConnectionManager mConnectionManager;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothDevice mBTDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_list);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .6), (int) (height * .9));

        baAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        IntentFilter ifListView = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver2, ifListView);
        lvNewDevices.setOnItemClickListener(BlueDevList.this);

        this.generateListView();

    }

    // This method will start the bluetooth connection
    public void startBTConnection(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: initializing Bluetooth Connection");

        mConnectionManager.startClient(device, uuid);
    }

    // This method is called to start the bluetooth connection
    public void startConnection() {
        startBTConnection(mBTDevice, MY_UUID);
    }



    // broadcast receivers required by this activity
    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION_FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    mBTDevices.add(device);
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                }
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState() == BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                    mBTDevice = device;

                    startConnection();
                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };


    // This method creates the list view containing the discovered bluetooth devices
    private void generateListView() {

        if(baAdapter.isDiscovering()) {
            baAdapter.cancelDiscovery();
            Log.d(TAG, "connectBluetooth: stopping discovery");
            //permission check method if required by android version
            checkBTPermissions();

            baAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver1, discoverDevicesIntent);
        }
        if(!baAdapter.isDiscovering()) {

            //permission check method if required by android version
            checkBTPermissions();

            baAdapter.startDiscovery();
            Log.d(TAG, "connectBluetooth: starting discovery");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver1, discoverDevicesIntent);
        }
    }


    // This onclick listener handles listening to the user select a device to connect with, and beings the connection
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        baAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: you clicked on a device");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            Log.d(TAG, "Trying to pair with " + deviceName);

            if ( mBTDevices.get(position).getBondState() != BOND_BONDED) {
                // creates a bond for devices that aren't paired yet
                mBTDevices.get(position).createBond();
                mBTDevice = mBTDevices.get(position);
                mConnectionManager = ConnectionManager.getInstance(BlueDevList.this);
            } else {
                // if the device is already paired, skips creating the bond and starts the connection
                mBTDevice = mBTDevices.get(position);
                mConnectionManager = ConnectionManager.getInstance(BlueDevList.this);
                startConnection();
            }
        }
    }

    // checks permissions to avoid errors
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOACTION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if(permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else {
                Log.d(TAG, "checkBTPermissions: not required. SDK version < LOLLIPOP");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
    }
}

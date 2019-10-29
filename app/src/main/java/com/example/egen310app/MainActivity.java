package com.example.egen310app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity /*implements AdapterView.OnItemClickListener*/ {
    private static final String TAG = "MainActivity";
    BluetoothAdapter baAdapter;
    //public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    //public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    ConnectionManager mConnectionManager;
    private static final UUID myUUid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothDevice mBTDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button enableDisable = (Button) findViewById(R.id.enableDisable);
        ImageButton goForward = (ImageButton) findViewById(R.id.goForward);
        ImageButton goBackwards = (ImageButton) findViewById(R.id.goBackwards);
        ImageButton turnLeft = (ImageButton) findViewById(R.id.turnLeft);
        ImageButton turnRight = (ImageButton) findViewById(R.id.turnRight);
        ImageButton rotateLeft = (ImageButton) findViewById(R.id.rotateLeft);
        ImageButton rotateRight = (ImageButton) findViewById(R.id.turnRight);
        Button connectBluetooth = (Button) findViewById(R.id.connectBluetooth);

        //lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        //mBTDevices = new ArrayList<>();

        /*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver3, filter);*/
        //lvNewDevices.setOnItemClickListener(MainActivity.this);
        baAdapter = BluetoothAdapter.getDefaultAdapter();

        enableDisable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                enableDisableBT();
            }
        });

        goForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    goForward(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });

        goBackwards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    goBackwards(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });


    }

    /*

        Following section contains broadcast receivers

     */
    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };
    /*private final BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION_FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };*/

    /*private final BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };*/
    /*
        Following section contains button methods executed on clicks
     */
    public void goForward(View view){
        byte[] bytes = {'1'};
        mConnectionManager.write(bytes);
    }

    public void goBackwards(View view){
        byte[] bytes = {'2'};
        mConnectionManager.write(bytes);
    }

    public void turnLeft(View view){

    }

    public void turnRight(View view){

    }

    public void rotateLeft(View view){

    }

    public void rotateRight(View view){

    }

    public void stop() {
        byte[] bytes = {'0'};
        mConnectionManager.write(bytes);
    }


    public void enableDisableBT(){
        if(baAdapter == null){
            Log.d(TAG, "enableDisableBT: does not have BT capabilities.");
        }
        if(!baAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            Log.d(TAG, "enableDisable: enabling Bt");
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);
        }
        if(baAdapter.isEnabled()){
            baAdapter.disable();
            Log.d(TAG, "enableDisable: disabling Bt");
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);
        }

    }


    public void connectBluetooth(View view){

        /*if(baAdapter.isDiscovering()) {
            baAdapter.cancelDiscovery();
            Log.d(TAG, "connectBluetooth: stopping discovery");
            //permission check method if required by android version
            checkBTPermissions();

            baAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver2, discoverDevicesIntent);
        }
        if(!baAdapter.isDiscovering()) {

            //permission check method if required by android version
            checkBTPermissions();

            baAdapter.startDiscovery();
            Log.d(TAG, "connectBluetooth: starting discovery");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver2, discoverDevicesIntent);
        }*/

        Intent intent = new Intent(MainActivity.this, BlueDevList.class);
        startActivity(intent);

        mConnectionManager = ConnectionManager.getInstance(MainActivity.this);

    }

    /*// Method to check that app has correct permissions
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
    }*/
/*
    Following section contains listeners
 */
    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        baAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: you clicked on a device");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(position).createBond();
        }
        lvNewDevices.setVisibility(View.GONE);


    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver1);
        /*unregisterReceiver(broadcastReceiver2);
        unregisterReceiver(broadcastReceiver3);*/
    }
}


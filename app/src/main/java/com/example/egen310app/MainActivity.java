package com.example.egen310app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.lang.reflect.Method;

// This class handles the main activity functions of the app
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    BluetoothAdapter baAdapter;
    ConnectionManager mConnectionManager;


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
        ImageButton rotateRight = (ImageButton) findViewById(R.id.rotateRight);
        Button connectBluetooth = (Button) findViewById(R.id.connectBluetooth);

        baAdapter = BluetoothAdapter.getDefaultAdapter();

        enableDisable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                enableDisableBT();
            }
        });


        // Creates a listener for the appropriate button
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

        // Creates a listener for the appropriate button
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

        // Creates a listener for the appropriate button
        turnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    turnLeft(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });

        // Creates a listener for the appropriate button
        turnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    turnRight(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });

        // Creates a listener for the appropriate button
        rotateLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    rotateLeft(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });

        // Creates a listener for the appropriate button
        rotateRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent mEvent) {
                if (mEvent.getAction() == MotionEvent.ACTION_DOWN)
                    rotateRight(view);
                else if (mEvent.getAction() == MotionEvent.ACTION_UP)
                    stop();
                return true;
            }
        });
    }

    // On click method to control the radio button, which controls the speed of the vehicle
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.slow:
                if (checked) {
                    byte[] bytes = {'s'};
                    mConnectionManager.write(bytes);
                }
                break;
            case R.id.medium:
                if (checked) {
                    byte[] bytes = {'m'};
                    mConnectionManager.write(bytes);
                }
                break;
            case R.id.fast:
                if (checked) {
                    byte[] bytes = {'f'};
                    mConnectionManager.write(bytes);
                }
                break;
        }
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
                String deviceName = device.getName(); // Name of Bluetooth device (might be null)
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    /*
        Following section contains button methods executed on clicks
     */

    // This method sends a bit to the arduino which makes the car go forward.
    public void goForward(View view){
        Log.d(TAG, "go forward");
        byte[] bytes = {'1'};
        mConnectionManager.write(bytes);
    }

    // This method sends a bit to the arduino which makes the car go backwards.
    public void goBackwards(View view){
        Log.d(TAG, "go backwards");
        byte[] bytes = {'2'};
        mConnectionManager.write(bytes);
    }

    // This method sends a bit to the arduino which will rotate the front wheels left.
    public void turnLeft(View view){
        Log.d(TAG, "turn left");
        byte[] bytes = {'3'};
        mConnectionManager.write(bytes);
    }

    // This method sends a bit to the arduino which will rotate the front wheels right.
    public void turnRight(View view){
        Log.d(TAG, "turn right");
        byte[] bytes = {'4'};
        mConnectionManager.write(bytes);
    }

    // This method sends a bit to the arduino which will cause the motors to spin in opposite directions
    public void rotateLeft(View view){
        Log.d(TAG, "rotate left");
        byte[] bytes = {'5'};
        mConnectionManager.write(bytes);
    }

    // This method sends a bit to the arduino which will cause the motors to spin in opposite directions
    public void rotateRight(View view){
        Log.d(TAG, "rotate right");
        byte[] bytes = {'6'};
        mConnectionManager.write(bytes);
    }

    // This method will send a bit to the arduino causing the motors to stop
    public void stop() {
        byte[] bytes = {'0'};
        mConnectionManager.write(bytes);
    }


    // This method handles enabling/disableing the bluetooth adapter
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


    // This method begins the connection process for the bluetooth adapter
    public void connectBluetooth(View view){

        Intent intent = new Intent(MainActivity.this, BlueDevList.class);
        startActivity(intent);
        mConnectionManager = ConnectionManager.getInstance(MainActivity.this);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);

    }
}


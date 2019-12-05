// code for this section gotten from CodingWithMitch on youtube or mitchtabian on Github

package com.example.egen310app;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";
    private static final String appName = "EGEN310";
    private static ConnectionManager mConnectionmanager;
    private static final UUID mUUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private final BluetoothAdapter baAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;

    private BluetoothDevice device;
    private UUID deviceUUID;

    private ConnectedThread mConnectedThread;

    ProgressDialog mProgressDialog;

    public ConnectionManager(Context context) {
        mContext = context;
        baAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    // get instance method to make connectionManager singleton
    // singleton connectionManager so all activities can use the connection
    public static ConnectionManager getInstance(Context context) {
        if(mConnectionmanager == null) {
            mConnectionmanager = new ConnectionManager(context);
        }
        return mConnectionmanager;
    }

    public BluetoothDevice getBTDevice() {
        return device;
    }


    /*This thread will listen for any incoming connections, and will run until there is a connection or until cancelled.*/
    private class AcceptThread extends Thread {
        // Server socket
        private final BluetoothServerSocket bssSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // New listening server socket
            try {
                tmp = baAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, mUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            bssSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: thread running");

            BluetoothSocket sock = null;

            /*
            Blocking call that will return when a connection is made,
            or if an exception is thrown
             */
            try {
                Log.d(TAG, "run: server socket start:");

                sock = bssSocket.accept();

                Log.d(TAG, "socket connection accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(sock != null) {
                connected(sock, device);
            }

            Log.i(TAG, "END AcceptThread");

        }

        public void cancel() {
            Log.d(TAG, "cancel: canceling AcceptThread");
            try {
                bssSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cance: close of AcceptThread server socket failed: " + e.getMessage());
            }
        }
    }

    /*This class will initiate the Bluetooth connection with the AcceptThread and will run until successful or it fails*/
    private class ConnectThread extends Thread {
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice inDevice, UUID uuid) {
            Log.d(TAG, "Connectthread: started");
            device = inDevice;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "run ConnectThread");

            // get socket for a connection with the given device
            try {
                Log.d(TAG, "ConnectThread: trying to create RFcommSocket using UUID");
                tmp = device.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            socket = tmp;

            // canceling discover because it uses lots of resources
            baAdapter.cancelDiscovery();

            // Blocking call that will only return on a connection, or when an exception is thrown
            try {
                socket.connect();

                Log.d(TAG, "run: ConnectThread connected");
            } catch (IOException e) {
                // close socket
                try {
                    socket.close();
                    Log.d(TAG, "run: Socket Closed");
                } catch (IOException ex) {
                    Log.e(TAG, "ConnectThread: unable to close connection socket: " + ex.getMessage());
                }
                Log.d(TAG, "run: ConnectThread could not connect to UUID: " + mUUID);
            }
            connected(socket, device);
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: closing client socket");
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of socket in ConnectThread failed: " + e.getMessage());
            }
        }
    }

    /*
    Start the AcceptThread to start communication with the device
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        //cancel any thread attempting to connect
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // if we don't have an accept thread, create one and start it
        if(mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    /*
    ConnectThread starts and attempts to connect with the other devices AcceptThread
     */
    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startClient: Started");

        //initiate progress dialog
        mProgressDialog = ProgressDialog.show(mContext, "connecting Bluetooth", "Please Wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // connection has been established, so progressDialog no longer necessary
            mProgressDialog.dismiss();

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024]; //byte array to store input steam
            int bytes; // bytes returned from read()

            while (true) {

                // read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "read: Error reading from inputstream: " + e.getMessage());
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG, "write: Error writting to outputstream: " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting");

        // Start the thread to manage the connection and transmit data
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    /*
    Writes to the connected thread
     */
    public void write(byte[] out) {
        // synchronize a copy of the connectedThread
        Log.d(TAG, "write: write called");
        // perform write
        mConnectedThread.write(out);
    }

}

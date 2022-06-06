package com.gd.android.demolib;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InstantiateSingleton extends  GodotPlugin {

    // vars
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private final String TAG = InstantiateSingleton.class.getSimpleName();
    private BluetoothAdapter mBTAdapter;
    private Activity m_Activity;
    private Set<BluetoothDevice> mPairedDevices;
    private InputStream mmInStream;
    private BluetoothSocket mSocket;

    public InstantiateSingleton(Godot godot) {
        super(godot);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        m_Activity = godot.getActivity();
        mmInStream = null;
    }

    @NonNull
    @Override
    public String getPluginName() {

        return "DemoPlugin";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {

        return Arrays.asList(
                "SayHello",
                "TriggerSignal",
                "BluetoothOn",
                "ListPairedDevices",
                "Connect",
                "ReadMessage");
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();

        signals.add(new SignalInfo("OnSignalTriggered", String.class));

        return signals;

    }

    public String SayHello(String user) {

        return ("Test, " + user);
    }

    public void BluetoothOn()
    {
        try
        {
            if (!mBTAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                m_Activity.startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }

    }

    public String[] ListPairedDevices()
    {
        List<String> res = new ArrayList<>();
        try
        {
            mPairedDevices = mBTAdapter.getBondedDevices();
            for (BluetoothDevice device : mPairedDevices)
                res.add(device.getName());

        }
        catch(Exception e)
        {
            Log.e(TAG,e.getMessage());
        }
        final String[] strings = res.toArray(new String[res.size()]);
        return strings;
    }
    // handle threading and looping from Godot so just have the read method here
    public String ReadMessage() {
        if (mmInStream == null)
        {
            Log.e(TAG, "Missing instream, is device connected?");
            return "NULL";
        }
        byte[] buffer = new byte[1024];  // buffer store for the strea
        int bytes; // bytes returned from read()
        String readMessage = null;

            try {
                // Read from the InputStream
                bytes = mmInStream.available();
                if (bytes != 0) {
                    SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                    bytes = mmInStream.available(); // how many bytes are ready to be read?
                    bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                    readMessage = new String(Arrays.copyOfRange(buffer,0,bytes), "UTF-8");
                    Log.i(TAG,readMessage);
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return "NULL";
            }
        return readMessage;
    }

    public boolean Connect(String deviceName)
    {
        // the read and connect methods will only work for one device at a time for now
        boolean fail = false;
        for (BluetoothDevice device : mPairedDevices)
        {
            if (device.getName().equals(deviceName))
            {
                try
                {
                    mSocket = createBluetoothSocket(device);
                    mmInStream = mSocket.getInputStream();
                }
                catch (Exception e)
                {
                    Log.e(TAG,e.getMessage());
                    return false;
                }

                try
                {
                    mSocket.connect();
                }
                catch (IOException e)
                {
                    Log.e(TAG, e.getMessage());
                    fail = true;
                }
                if (fail)
                    try {
                        mSocket.close();
                    } catch (IOException ioException) {
                        Log.e(TAG, ioException.getMessage());
                    }
            }
            return !fail;
        }
    return true;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    public  void TriggerSignal()
    {
        emitSignal("OnSignalTriggered","SignalFromPlugin");
    }
}

package com.gd.android.demolib;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstantiateSingleton extends  GodotPlugin {

    // vars
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private BluetoothAdapter mBTAdapter;
    private Activity m_Activity;
    private Set<BluetoothDevice> mPairedDevices;

    public InstantiateSingleton(Godot godot) {
        super(godot);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        m_Activity = godot.getActivity();
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
                "ListPairedDevices");
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
            Log.e("Error:",e.getMessage());
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
            Log.e("Error:",e.getMessage());
        }
        final String[] strings = res.toArray(new String[res.size()]);
        return strings;
    }

    public  void TriggerSignal()
    {
        emitSignal("OnSignalTriggered","SignalFromPlugin");
    }
}

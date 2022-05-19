package com.gd.android.demolib;

import android.app.Activity;
import android.app.Activity.*;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.ArraySet;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstantiateSingleton extends  GodotPlugin {

    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private BluetoothAdapter mBTAdapter;
    private Activity m_Activity;
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

        return Arrays.asList("SayHello",
                "TriggerSignal", "BluetoothOn");
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

    public  void TriggerSignal()
    {
        emitSignal("OnSignalTriggered","SignalFromPlugin");
    }
}

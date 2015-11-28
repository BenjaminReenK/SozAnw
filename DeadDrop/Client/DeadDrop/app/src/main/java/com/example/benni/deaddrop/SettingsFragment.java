package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

/**
 * Created by Benni on 27.11.2015.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_settings, container,false);
        Button btnStop = (Button) view.findViewById(R.id.btn_settings_stop);
        btnStop.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_settings_stop) {
            MainActivity activity = (MainActivity) getActivity();
            activity.getWifiPeer().stopWifi();
        }
    }
}

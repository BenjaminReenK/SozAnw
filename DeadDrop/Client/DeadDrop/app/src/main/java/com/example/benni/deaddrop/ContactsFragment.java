package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.peer.SharkEngine;

import java.io.IOException;

/**
 * Created by Benni on 25.11.2015.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener{

    MainActivity activity;
    SharkEngine se;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_contacts, container,false);
        Button addContact = (Button) view.findViewById(R.id.btn_addcontact);
        addContact.setOnClickListener(this);

        activity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_addcontact) {
            se = activity.getSharkEngine();
            if (se != null) {

                try {
                    se.startWifiDirect();
                } catch (SharkProtocolNotSupportedException e) {
                    Toast.makeText(activity, "Wifi direct start error", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(activity, "Wifi direct start error", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(activity, "Wifi direct started", Toast.LENGTH_LONG).show();
            }

        }
    }


}

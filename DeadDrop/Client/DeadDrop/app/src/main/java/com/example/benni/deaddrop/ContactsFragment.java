package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.peer.SharkEngine;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Benni on 25.11.2015.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener{

    MainActivity activity;
    WifiPeer wPeer;
    ContactListAdapter cAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_contacts, container,false);

        activity = (MainActivity) getActivity();

        ImageButton addContact = (ImageButton) view.findViewById(R.id.btn_addcontact);
        addContact.setOnClickListener(this);

        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("test");
        cAdapter = new ContactListAdapter(getContext(), tmp);
        ListView lvContacts = (ListView) view.findViewById(R.id.lv_contacts);
        lvContacts.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_addcontact) {
            wPeer = activity.getWifiPeer();
            if (wPeer != null) {
                Toast.makeText(activity, "Sending ContactRequest", Toast.LENGTH_SHORT).show();
                wPeer.sendContactRequest();
            }

        }
    }


}

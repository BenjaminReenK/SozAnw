package com.example.benni.deaddrop;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benni on 28.11.2015.
 */
public class NewMessageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_newmessage, container,false);

        Spinner spTo = (Spinner) view.findViewById(R.id.sp_adresslist);
        Spinner spVia = (Spinner) view.findViewById(R.id.sp_via);

        List<String> list = new ArrayList<String>();
        list.add("Recipent 1");
        list.add("Recipent 2");
        list.add("Recipent 3");


        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spTo.setAdapter(dataAdapter);
        spVia.setAdapter(dataAdapter);


        return view;

    }
}

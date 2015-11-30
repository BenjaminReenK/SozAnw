package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benni on 25.11.2015.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener {

    private MessageAdapter adapter;
    private MainActivity main;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_messages, container,false);
        ListView lv = (ListView) view.findViewById(R.id.lv_msglist);

        ImageButton btnNewMsg = (ImageButton) view.findViewById(R.id.btn_newmsg);
        btnNewMsg.setOnClickListener(this);

        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("test");
        adapter = new MessageAdapter(getContext(), tmp);
        lv.setAdapter(adapter);
        main = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "New Message Btn", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = main.getSupportFragmentManager();
        NewMessageFragment newMessageFragment = new NewMessageFragment();
        fragmentManager.beginTransaction().replace(R.id.container, newMessageFragment).commit();
    }
}

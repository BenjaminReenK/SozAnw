package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Benni on 25.11.2015.
 */
public class LogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_log, container,false);

        TextView lvLog = (TextView) view.findViewById(R.id.tv_log);
        lvLog.append("row 1 \n");
        lvLog.append("row 1 \n");
        lvLog.append("row 1 \n");
        lvLog.append("row 1 \n");
        lvLog.append("row 1 \n");
        lvLog.append("row 1 \n");
        return view;


    }


}

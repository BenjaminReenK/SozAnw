package com.example.benni.deaddrop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Benni on 25.11.2015.
 */
public class LogFragment extends Fragment implements View.OnClickListener{

    private TextView lvLog;
    private KbTextViewWriter logger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_log, container,false);

        lvLog = (TextView) view.findViewById(R.id.tv_log);
        lvLog.setMovementMethod(new ScrollingMovementMethod());
        MainActivity main = (MainActivity) getActivity();
        logger = main.getLogger();
        logger.setOutputTextView(lvLog);
        logger.showAllText();

        Button btnAll = (Button) view.findViewById(R.id.btn_log_all);
        Button btnKb = (Button) view.findViewById(R.id.btn_log_kb);
        Button btnCon = (Button) view.findViewById(R.id.btn_log_con);

        btnAll.setOnClickListener(this);
        btnKb.setOnClickListener(this);
        btnCon.setOnClickListener(this);

        return view;


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_log_all) {
            lvLog.setText("");
            logger.showAllText();
        }
        else if (v.getId() == R.id.btn_log_con) {
            lvLog.setText("");
            logger.showLogText();
        }
        else if (v.getId() == R.id.btn_log_kb) {
            lvLog.setText("");
            logger.showKbText();
        }
    }
}

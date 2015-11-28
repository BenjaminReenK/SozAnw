package com.example.benni.deaddrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Benni on 28.11.2015.
 */
public class MessageAdapter extends ArrayAdapter<String> {

    private Context context;

    public MessageAdapter(Context ctx, ArrayList<String> contacts) {
        super(ctx, R.layout.row_contacts, contacts);
        this.context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_messages, parent, false);
        return rowView;
    }


}

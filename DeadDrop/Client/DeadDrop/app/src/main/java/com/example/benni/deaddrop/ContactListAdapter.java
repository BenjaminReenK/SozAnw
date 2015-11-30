package com.example.benni.deaddrop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Benni on 27.11.2015.
 * Fills ListView in ContactsFragment
 */
public class ContactListAdapter extends ArrayAdapter<String> {

    private Context context;

    public ContactListAdapter(Context ctx, ArrayList<String> contacts) {
        super(ctx, R.layout.row_contacts, contacts);
        this.context = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_contacts, parent, false);
        return rowView;
    }
}

package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by dev on 10/7/15.
 */
public class MovieDetailsAdapter extends BaseAdapter {

    Context context;
    ArrayList detailItems;

    public MovieDetailsAdapter (Context c, ArrayList a){
        this.context = c;
        this.detailItems = a;

    }

    @Override
    public int getCount(){
        return detailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return detailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return detailItems.indexOf(getItemId(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

        }


        return null;
    }
}

package com.example.sample.chatting.Helper;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.chatting.R;

/**
 * Created by Hassan Javaid on 11/24/2018.
 */

public class CustomAdapter extends BaseAdapter {

    Context context;
    String names[];
    int images[];
    LayoutInflater layoutInflater;

    public CustomAdapter(Context activity, String[] names, int[] images) {
    this.context = context;
    this.images = images;
    this.names = names ;
    layoutInflater = (LayoutInflater.from(activity));
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       convertView = layoutInflater.inflate(R.layout.custom_profile_seeting,null);
        TextView custom_name = (TextView)convertView.findViewById(R.id.custom_name);
        ImageView custom_image = (ImageView)convertView.findViewById(R.id.custom_image);
        custom_name.setText(names[position]);
        custom_image.setImageResource(images[position]);
        return convertView;
    }
}

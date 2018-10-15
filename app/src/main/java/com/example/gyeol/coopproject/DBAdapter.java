package com.example.gyeol.coopproject;

/**
 * Created by Park Gyeol on 2018-10-12.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DBAdapter extends CursorAdapter {

    public DBAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int temp, temp2;

        final ImageView image = (ImageView)view.findViewById(R.id.image); // 이미지 나중에 중식, 양식 등에 따라 다르게 넣어주도록 설정
        final TextView name = (TextView)view.findViewById(R.id.name); // 가게 이름
        final TextView distance = (TextView)view.findViewById(R.id.distance); // 거리

        image.setImageResource(R.drawable.food);

        name.setText(cursor.getString(cursor.getColumnIndex("name"))); // 가게 이름

        temp = cursor.getInt(cursor.getColumnIndex("distance")); // 거리
        if(temp >= 1000) { // 1km 이상의 거리일 경우
            temp2 = temp / 1000;
            temp = (temp - temp2 * 1000) / 100;
            distance.setText("거리  :  "+ temp2 + "." + temp +" km");
        } else { // 1km 이하의 거리일 경우
            distance.setText("거리  :  "+ temp +" m");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.listlayout, parent, false);
        return v;
    }
}
package com.example.notiflication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TimeViewAdapter extends RecyclerView.Adapter<TimeViewAdapter.MyViewHolder> {

    Context context;
    static ArrayList<PrayerTime> list;


    public TimeViewAdapter(Context c, ArrayList<PrayerTime> l) {
        context = c;
        list = l;
    }

    public static ArrayList<PrayerTime> getlist() {
        return list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.time_view, viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        holder.name.setText(list.get(i).getName());
        holder.time.setText(list.get(i).getTime());
        holder.img.setImageResource(list.get(i).getImage());//
        //holder.name.setTextColor(getResources().getColor(R.color.lightyellow));
        boolean test = list.get(i).isNext();

        if (test) { //for test
            holder.layout.setBackgroundResource(R.drawable.now_prayer);
            //holder.icon.setBackgroundResource(R.drawable.nextpray);
            holder.name.setTextColor(R.color.bluelight);
            holder.time.setTextColor(R.color.bluelight);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, time;
        LinearLayout layout;
        TextView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = (TextView) itemView.findViewById(R.id.prayerName);
            time = (TextView) itemView.findViewById(R.id.prayerTime);
            layout = itemView.findViewById(R.id.timeView);
            // icon = (TextView) itemView.findViewById(R.id.notifyIcon);


        }
    }
}


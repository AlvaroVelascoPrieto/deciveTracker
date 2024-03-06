package com.example.entrega;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceRVAdapter extends RecyclerView.Adapter<DeviceRVAdapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<DeciveModal> courseModalArrayList;
    private Context context;

    // constructor
    public DeviceRVAdapter(ArrayList<DeciveModal> courseModalArrayList, Context context) {
        this.courseModalArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        DeciveModal modal = courseModalArrayList.get(position);
        holder.courseNameTV.setText(modal.getDeviceName());
        holder.deviceYearTV.setText(modal.getdeviceYear());
        holder.deviceModelTV.setText(modal.getdeviceModel());
        holder.deviceTypeTV.setText(modal.getdeviceType());

        // below line is to add on click listener for our recycler view item.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are calling an intent.
                Intent i = new Intent(context, UpdateDeviceActivity.class);

                // below we are passing all our values.
                i.putExtra("name", modal.getDeviceName());
                i.putExtra("year", modal.getdeviceYear());
                i.putExtra("type", modal.getdeviceType());
                i.putExtra("model", modal.getdeviceModel());


                // starting our activity.
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return courseModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView courseNameTV, deviceYearTV, deviceModelTV, deviceTypeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            courseNameTV = itemView.findViewById(R.id.idTVDeviceName);
            deviceYearTV = itemView.findViewById(R.id.idTVDeviceType);
            deviceModelTV = itemView.findViewById(R.id.idTVDeviceModel);
            deviceTypeTV = itemView.findViewById(R.id.idTVDeviceYear);
        }
    }
}

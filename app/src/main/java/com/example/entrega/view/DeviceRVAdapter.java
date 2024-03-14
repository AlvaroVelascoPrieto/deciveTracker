package com.example.entrega.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrega.R;

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
        holder.locationNameTV.setText(modal.getLocationName());
        holder.locationTypeTV.setText(modal.getdeviceType());
        holder.latitudeTV.setText(modal.getLatitude());
        holder.longitudeTV.setText(modal.getLongitude());
        holder.altitudeTV.setText(modal.getAltitude());
        holder.deviceTypeTV.setText(modal.getdeviceType());

        // below line is to add on click listener for our recycler view item.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are calling an intent.
                Intent i = new Intent(context, UpdateDeviceActivity.class);

                // below we are passing all our values.
                i.putExtra("name", modal.getLocationName());
                i.putExtra("type", modal.getdeviceType());
                i.putExtra("latitude", modal.getLatitude());
                i.putExtra("longitude", modal.getLongitude());
                i.putExtra("altitude", modal.getAltitude());
                i.putExtra("date", modal.getLogDate());


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
        private TextView locationNameTV, locationTypeTV, latitudeTV, deviceTypeTV, longitudeTV, altitudeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            locationNameTV = itemView.findViewById(R.id.idTVLocationName);
            locationTypeTV = itemView.findViewById(R.id.idTVLocationType);
            latitudeTV = itemView.findViewById(R.id.idTVLatitude);
            longitudeTV = itemView.findViewById(R.id.idTVLongitude);
            altitudeTV = itemView.findViewById(R.id.idTVAltitude);
            deviceTypeTV = itemView.findViewById(R.id.idTVLogYear);
        }
    }
}

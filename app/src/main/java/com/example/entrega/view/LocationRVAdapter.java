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

public class LocationRVAdapter extends RecyclerView.Adapter<LocationRVAdapter.ViewHolder> {
    private ArrayList<LocationModal> courseModalArrayList;
    private Context context;

    public LocationRVAdapter(ArrayList<LocationModal> courseModalArrayList, Context context) {
        this.courseModalArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModal modal = courseModalArrayList.get(position);
        holder.locationNameTV.setText(modal.getLocationName());
        holder.locationTypeTV.setText(modal.getdeviceType());
        holder.latitudeTV.setText(modal.getLatitude());
        holder.longitudeTV.setText(modal.getLongitude());
        holder.altitudeTV.setText(modal.getAltitude());
        holder.deviceTypeTV.setText(modal.getdeviceType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LocationStats.class);

                i.putExtra("id", String.valueOf(modal.getId()));
                i.putExtra("name", modal.getLocationName());
                i.putExtra("type", modal.getdeviceType());
                i.putExtra("latitude", modal.getLatitude());
                i.putExtra("longitude", modal.getLongitude());
                i.putExtra("altitude", modal.getAltitude());
                i.putExtra("date", modal.getLogDate());

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

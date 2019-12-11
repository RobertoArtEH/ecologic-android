package com.example.eco_logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterWeather extends RecyclerView.Adapter<AdapterWeather.ViewHolder> {
    ArrayList<Clima> weatherList;

    public AdapterWeather(ArrayList<Clima> weatherList) { this.weatherList = weatherList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.weatherDate.setText(weatherList.get(position).getDate());
        holder.weatherTemp.setText(weatherList.get(position).getTemp());
        holder.weatherImage.setImageResource(weatherList.get(position).getImg());
    }

    @Override
    public int getItemCount() { return weatherList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView weatherDate, weatherTemp;
        ImageView weatherImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            weatherDate = itemView.findViewById(R.id.rv_date);
            weatherTemp = itemView.findViewById(R.id.rv_temperature);
            weatherImage = itemView.findViewById(R.id.rv_img);
        }
    }
}

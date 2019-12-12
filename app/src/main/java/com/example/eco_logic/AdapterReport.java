package com.example.eco_logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterReport extends RecyclerView.Adapter<AdapterReport.ViewHolder> {
    ArrayList<Reporte> reportList;

    public AdapterReport(ArrayList<Reporte> reportList) { this.reportList = reportList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.reportName.setText(reportList.get(position).getUser());
        holder.reportDate.setText(reportList.get(position).getDate());
        holder.reportTime.setText(reportList.get(position).getTime());
        holder.reportHumidity.setText(reportList.get(position).getHumidity());
    }

    @Override
    public int getItemCount() { return reportList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportName, reportDate, reportTime, reportHumidity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            reportName = itemView.findViewById(R.id.ir_name);
            reportDate = itemView.findViewById(R.id.ir_date);
            reportTime = itemView.findViewById(R.id.ir_time);
            reportHumidity = itemView.findViewById(R.id.ir_humidity);
        }
    }
}

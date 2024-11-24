package edu.cmpe277.smarthealth.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.cmpe277.smarthealth.R;

public class DailyDataAdapter extends RecyclerView.Adapter<DailyDataAdapter.ViewHolder> {
    private List<DailyData> dailyDataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView stepCountTextView;
        public TextView sleepDurationTextView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            stepCountTextView = view.findViewById(R.id.stepCountTextView);
            sleepDurationTextView = view.findViewById(R.id.sleepDurationTextView);
        }
    }

    public DailyDataAdapter(List<DailyData> dailyDataList) {
        this.dailyDataList = dailyDataList;
    }

    @Override
    public DailyDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailyDataAdapter.ViewHolder holder, int position) {
        DailyData data = dailyDataList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        holder.dateTextView.setText(sdf.format(new Date(data.date)));
        holder.stepCountTextView.setText("Steps: " + data.stepCount);
        holder.sleepDurationTextView.setText("Sleep: " + data.sleepHours + "h " + data.sleepMinutes + "m");
    }

    @Override
    public int getItemCount() {
        return dailyDataList.size();
    }
}

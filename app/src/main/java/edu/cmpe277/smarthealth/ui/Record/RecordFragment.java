package edu.cmpe277.smarthealth.ui.Record;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.SleepEntry;
import edu.cmpe277.smarthealth.database.StepEntry;
import edu.cmpe277.smarthealth.databinding.FragmentRecordBinding;

public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;

    private Spinner dropdownMenu;
    private ArrayAdapter<String> arrayAdapter;

    private BarChart barChart;

    private AppDB appDB;
    private ExecutorService executorService;

    private static final int DAYS_TO_VIEW = 7;

    @ColorInt private int solidColor;
    @ColorInt private int barTextColor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        appDB = AppDB.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();

        dropdownMenu = binding.dropdownLayout;
        String[] recordGraphs = getResources().getStringArray(R.array.record_graphs);
        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recordGraphs);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(arrayAdapter);
        dropdownMenu.setSelection(0);

        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    if (barChart != null) {
                        barChart.clear();
                    }
                    return;
                }

                String selectedItem = arrayAdapter.getItem(i);
                if(selectedItem != null){
                    loadGraph(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (barChart != null) {
                    barChart.clear();
                }
            }
        });

        barChart = binding.barChart;
        barChart.getLegend().setEnabled(false);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = requireContext().getTheme();
        theme.resolveAttribute(R.attr.barAxisColor, typedValue, true);
        solidColor = typedValue.data;
        theme.resolveAttribute(R.attr.barTextColor, typedValue, true);
        barTextColor = typedValue.data;
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(barTextColor);
        xAxis.setGridColor(solidColor);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(barTextColor);
        leftAxis.setAxisMinimum(0);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        return root;
    }

    private void loadGraph(String selectedItem) {
        if (barChart != null) {
            barChart.clear();
            switch (selectedItem) {
                case "Step":
                    loadStepGraph();
                    break;
                case "Sleep":
                    loadSleepGraph();
                    break;
                default:
                    break;
            }
        }
    }

    private void loadStepGraph() {
        executorService.execute(() -> {
            List<BarEntry> barEntryList = new ArrayList<>();
            List<String> dateList = new ArrayList<>();

            long currentTime = System.currentTimeMillis();
            long oneDayInMillis = 24 * 60 * 60 * 1000;

            long startDate = getStartDate(currentTime - (DAYS_TO_VIEW - 1) * oneDayInMillis);
            long endDate = getEndDate(currentTime);

            List<StepEntry> stepEntryList = appDB.stepDao().getStepsBetween(startDate, endDate);

            HashMap<Long, Integer> hashMap = new HashMap<>();
            for(StepEntry stepEntry : stepEntryList){
                hashMap.put(stepEntry.date, stepEntry.steps);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            for(int i = 0; i < DAYS_TO_VIEW; i++){
                long startDay = getStartDate(startDate + i * oneDayInMillis);
                int steps = 0;

                if (isSameDay(startDay, currentTime)) {
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("StepCountPref", Context.MODE_PRIVATE);
                    steps = sharedPreferences.getInt("totalStep", 0);
                } else {
                    steps = hashMap.getOrDefault(startDay, 0);
                }

                barEntryList.add(new BarEntry(i, steps));
                dateList.add(simpleDateFormat.format(startDay));
            }

            GraphMarkView markerView = new GraphMarkView(getContext(), R.layout.graph_mark_view, dateList, "Step");
            markerView.setChartView(barChart);
            barChart.setMarker(markerView);

            requireActivity().runOnUiThread(() -> {
                BarDataSet dataSet = new BarDataSet(barEntryList, "Steps");
                dataSet.setColor(Color.CYAN);
                dataSet.setDrawValues(false);
                BarData data = new BarData(dataSet);
                data.setBarWidth(0.5f);

                barChart.setData(data);
                barChart.setFitBars(true);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dateList));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setDrawGridLines(false);

                barChart.animateY(1000);
                barChart.getDescription().setEnabled(false);

                barChart.invalidate();
            });
        });
    }

    private boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void loadSleepGraph() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            long currentTime = System.currentTimeMillis();
            long oneDayMillis = 24 * 60 * 60 * 1000;

            long startDate = getStartDate(currentTime - (DAYS_TO_VIEW - 1) * oneDayMillis);
            long endDate = getEndDate(currentTime);

            List<SleepEntry> sleepEntries = appDB.sleepDao().getSleepEntriesBetweenDatesNonLive(startDate, endDate);

            HashMap<Long, Float> dateSleepMap = new HashMap<>();
            for (SleepEntry entry : sleepEntries) {
                float sleepHours = entry.hours + entry.minutes / 60.0f;
                dateSleepMap.put(entry.date, sleepHours);
            }

            List<BarEntry> entries = new ArrayList<>();
            List<String> dates = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

            for (int i = 0; i < DAYS_TO_VIEW; i++) {
                long dayStart = getStartDate(startDate + i * oneDayMillis);
                float sleepHours = dateSleepMap.getOrDefault(dayStart, 0f);

                entries.add(new BarEntry(i, sleepHours));
                dates.add(dateFormat.format(dayStart));
            }

            GraphMarkView markerView = new GraphMarkView(getContext(), R.layout.graph_mark_view, dates, "Sleep");
            markerView.setChartView(barChart);
            barChart.setMarker(markerView);

            requireActivity().runOnUiThread(() -> {
                BarDataSet dataSet = new BarDataSet(entries, "Sleep (hours)");
                dataSet.setColor(Color.CYAN);
                dataSet.setDrawValues(false);
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.5f);

                barChart.setData(barData);
                barChart.setFitBars(true);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setDrawGridLines(false);

                barChart.animateY(1000);
                barChart.getDescription().setEnabled(false);

                barChart.invalidate();
            });
        });
    }

    private long getStartDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (barChart != null) {
            barChart.clear();
        }
    }
}
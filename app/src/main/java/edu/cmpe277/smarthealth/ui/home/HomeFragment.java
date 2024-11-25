package edu.cmpe277.smarthealth.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import edu.cmpe277.smarthealth.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BroadcastReceiver broadcastReceiver;

    private TextView stepCountTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        stepCountTextView = binding.stepCountTextView;

        loadCurrentStepCount();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int totalStepsCurrent = intent.getIntExtra("totalStepsCurrent", 0);
                stepCountTextView.setText("Steps: " + totalStepsCurrent);
            }
        };

        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(broadcastReceiver, new IntentFilter("StepCountUpdate"));

        return root;
    }

    private void loadCurrentStepCount() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("StepCountPref", Context.MODE_PRIVATE);
        int totalStepsToday = sharedPreferences.getInt("totalStep", 0);
        stepCountTextView.setText("Steps: " + totalStepsToday);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(broadcastReceiver != null)
            LocalBroadcastManager.getInstance(requireContext())
                    .unregisterReceiver(broadcastReceiver);
        binding = null;
    }
}
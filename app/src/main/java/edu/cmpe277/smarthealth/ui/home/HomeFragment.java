package edu.cmpe277.smarthealth.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import edu.cmpe277.smarthealth.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BroadcastReceiver broadcastReceiver;

    private int totalStepsCurrent;
    private int targetSteps = 8000;

    private TextView stepCountTextView;
    private CircularProgressIndicator stepProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        stepCountTextView = binding.stepCountTextView;
        stepProgressBar = binding.stepProgressBar;

        stepProgressBar.setMax(targetSteps);

        loadCurrentStepCount();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                totalStepsCurrent = intent.getIntExtra("totalStepsCurrent", 0);
                stepCountTextView.setText("" + totalStepsCurrent);

                updateProgreeBar();
            }
        };

        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(broadcastReceiver, new IntentFilter("StepCountUpdate"));

        return root;
    }

    private void loadCurrentStepCount() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("StepCountPref", Context.MODE_PRIVATE);
        totalStepsCurrent = sharedPreferences.getInt("totalStep", 0);
        stepCountTextView.setText("" + totalStepsCurrent);

        updateProgreeBar();
    }

    private void updateProgreeBar() {
        if(totalStepsCurrent >= targetSteps){
            stepProgressBar.setProgress(targetSteps);
            stepProgressBar.setIndicatorColor(Color.rgb(42, 219, 48));
        }
        else{
            stepProgressBar.setProgress(totalStepsCurrent);
        }
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
package edu.cmpe277.smarthealth.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.system.Os;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.SleepEntry;
import edu.cmpe277.smarthealth.database.StepEntry;

import edu.cmpe277.smarthealth.databinding.FragmentHomeBinding;
import io.noties.markwon.Markwon;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BroadcastReceiver broadcastReceiver;

    private long totalStepsCurrent;
    private int targetSteps;

    private SharedPreferences sharedPreferences;

    private TextView stepCountTextView;
    private CircularProgressIndicator stepProgressBar;
    private TextView sleepStatusTextView;
    private TextView sleepTimeTextView;

    private Executor executor;

    private TextView suggestionTextView;

    private GenerativeModel generativeModel;
    private GenerativeModelFutures modelFutures;

    private static final String TAG = "smarthealth.HomeFragment";
    private static final String API_KEY = "AIzaSyApWdxQQBWBh_dX93oCz5mg_KuwrlT8fh8";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        executor = Executors.newSingleThreadExecutor();

        generativeModel = new GenerativeModel("gemini-1.5-flash-8b", API_KEY);
        modelFutures = GenerativeModelFutures.from(generativeModel);

        stepCountTextView = binding.stepCountTextView;
        stepProgressBar = binding.stepProgressBar;

        sharedPreferences = requireActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        targetSteps = sharedPreferences.getInt("step", 5000);
        stepProgressBar.setMax(targetSteps);

        sleepStatusTextView = binding.sleepStatusTextView;
        sleepTimeTextView = binding.sleepTimeTextView;

        suggestionTextView = binding.suggestionTextView;
        suggestionTextView.setOnLongClickListener((view) -> {
            if(suggestionTextView.getText() != null){
                ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("AI suggestion", suggestionTextView.getText());
                clipboardManager.setPrimaryClip(clipData);
            }

            Toast.makeText(requireContext(), "Text copied", Toast.LENGTH_SHORT).show();
            return true;
        });

        loadCurrentStepCount();
        loadPrevSleepTime();

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

//        getAllData();
        getAiSuggestion();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        suggestionTextView.setText("");
        targetSteps = sharedPreferences.getInt("step", 8000);
        stepProgressBar.setMax(targetSteps);
        updateProgreeBar();
        getAiSuggestion();
    }

    private void getAiSuggestion() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserInfo", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "User");
        int weight = sharedPreferences.getInt("weight", -1);
        int height = sharedPreferences.getInt("height", -1);

        aiCall(name, weight, height);
    }

    private void aiCall(String name, int weight, int height) {
        String prompt = "Hello, my name is " + name + ". "
                + (weight!= -1 ? "My weight is " + weight + ". " : "")
                + (height!= -1 ? "My height is " + height + ". " : "")
                + "Can you give me some health suggestion to me?\n"
                + "I want the ouput to be something similar to the following:\n"
                + "Hello <name>. "
                + (height == -1 && weight == -1 ? "According to your weight and/or height, your health condition is <your-consideration>.\n" : "")
                + "The following is my health suggestion: \n"
                + "Based on the information your provided, the suggestion number of steps you should take is <your-steps-suggestion> steps. \n"
                + "The number of hours that I recommend you to sleep is <your-hour-suggestion> hours. \n"
                + "<Further suggestions you might have based on the information I provide. But remember, you must include the steps and sleep suggestion.> \n\n";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> responseFuture = modelFutures.generateContent(content);

        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>(){

            @Override
            public void onSuccess(GenerateContentResponse result) {
                String response = result.getText();
                Markwon markwon = Markwon.create(requireContext());

                requireActivity().runOnUiThread(() -> markwon.setMarkdown(suggestionTextView, response));
            }

            @Override
            public void onFailure(Throwable t) {
                String response = t.getMessage();
                requireActivity().runOnUiThread(() -> suggestionTextView.setText(response));
            }
        }, executor);

    }

//    private void getAllData() {
//        AppDB appDB = AppDB.getInstance((requireContext()));
//
//        String googleAPIStr;
//        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
//        if (status == ConnectionResult.SUCCESS) {
//            googleAPIStr = "Yes Supported.";
//        }
//        else{
//            googleAPIStr = "No, not supported.";
//        }
//
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            List<SleepEntry> sleeps = appDB.sleepDao().getAllSleepData();
//            List<StepEntry> steps = appDB.stepDao().getAllStepData();
//
//            List<String> strList = new ArrayList<>();
//
//            if(sleeps != null){
//                for (SleepEntry sleep : sleeps){
//                    strList.add(sleep.date + ": " + sleep.hours + "hr " + sleep.minutes + "mins\n");
//                }
//            }
//
//            if(steps != null){
//                for (StepEntry step : steps){
//                    strList.add(step.date + ": " + step.steps + "\n");
//                }
//            }
//            strList.add(googleAPIStr);
//
//            suggestionTextView.setText(strList.toString());
//
//        });
//    }

    private void loadPrevSleepTime() {
        AppDB appDB = AppDB.getInstance(requireContext());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                SleepEntry sleepEntry = appDB.sleepDao().getSleepEntryMostRecent();

                if(sleepEntry != null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

                    String dateString = dateFormat.format(new Date(sleepEntry.date));
                    sleepStatusTextView.setText("Sleeping Status: " + dateString);
                    int hours = sleepEntry.hours;
                    int minutes = sleepEntry.minutes;
                    String sleepTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                    sleepTimeTextView.setText(sleepTime);
                }
                else {
                    sleepStatusTextView.setText("Sleeping Status: No Data");
                    sleepTimeTextView.setText("");
                }
            }
            catch (Exception e){
                requireActivity().runOnUiThread(() -> {
                    sleepStatusTextView.setText("Error loading sleep data");
                    sleepTimeTextView.setText("");
                });
                e.printStackTrace();
            }
        });
    }

    private void loadCurrentStepCount() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("StepCountPref", MODE_PRIVATE);
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
            stepProgressBar.setProgress((int)totalStepsCurrent);
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
package edu.cmpe277.smarthealth.ui.diagnose;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import edu.cmpe277.smarthealth.databinding.FragmentDiagnoseBinding;
import edu.cmpe277.smarthealth.ui.chat.ChatActivity;

public class DiagnoseFragment extends Fragment {

    private FragmentDiagnoseBinding binding;
    private EditText symptoms;
    private EditText issue;
    private EditText whenSymptomsStarts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiagnoseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        symptoms = binding.symptoms;
        issue = binding.issue;
        whenSymptomsStarts = binding.whenSymptomsStarts;

        binding.sendButton.setOnClickListener(v -> sendPrompt());

        return root;
    }

    private void sendPrompt() {

        String symptomsText = symptoms.getText().toString();
        String issueText = issue.getText().toString();
        String whenSymptomsStartsText = whenSymptomsStarts.getText().toString();

        String prompt = "I have the following symptoms: " + symptomsText + ". " +
                "The symptoms started " + whenSymptomsStartsText + ". " +
                "I have the following issue: " + issueText + ". " +
                "Can you provide a guess what issue do I have based on this information?";


        Bundle args = new Bundle();
        args.putString("initial_prompt", prompt);

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("initial_prompt", prompt);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

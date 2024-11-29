package edu.cmpe277.smarthealth.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cmpe277.smarthealth.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        EditText nameEditText = binding.editTextName;
        EditText dateOfBirthEditText = binding.editTextName;
        EditText weightEditText = binding.editTextWeight;
        EditText heightEditText = binding.editTextHeight;
        EditText idNumberEditText = binding.editTextIdNumber;
        EditText emailEditText = binding.editTextEmail;
        EditText phoneNumberEditText = binding.editTextPhoneNumber;
        Button submitButton = binding.buttonSubmit;


        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        dateOfBirthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setDateOfBirth(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setWeight(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setHeight(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        idNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setIdNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                homeViewModel.setPhoneNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        submitButton.setOnClickListener((v) -> {
            // Placeholder: Validation logic if needed

            saveInformation();

            Toast.makeText(getContext(), "Information submitted", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private void saveInformation() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package edu.cmpe277.smarthealth.ui.chat;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.MainActivity;
import edu.cmpe277.smarthealth.R;

public class ChatActivity extends AppCompatActivity {

    private TextView chatTextView;
    private EditText messageInput;
    private Button sendButton;
    private ScrollView chatScrollView;
    private Button backButton;
    private GenerativeModel generativeModel;
    private GenerativeModelFutures modelFutures;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        // Initialize views
        chatTextView = findViewById(R.id.chat_text_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        chatScrollView = findViewById(R.id.chat_scroll_view);
        backButton = findViewById(R.id.back_button);

        String apiKey = "AIzaSyApWdxQQBWBh_dX93oCz5mg_KuwrlT8fh8"; // Replace with your actual API key
        generativeModel = new GenerativeModel("gemini-1.5-flash", apiKey);
        modelFutures = GenerativeModelFutures.from(generativeModel);
        executor = Executors.newSingleThreadExecutor();


        String initialPrompt = getIntent().getStringExtra("initial_prompt");
        if (initialPrompt != null && !initialPrompt.isEmpty()) {
            sendPrompt(initialPrompt);
        }

        // Setup Send button listener
        setupSendButtonListener();


    }


    private void setupSendButtonListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prompt = messageInput.getText().toString().trim();
                if (!prompt.isEmpty()) {
                    sendPrompt(prompt);
                    messageInput.setText(""); // Clear the input field
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a prompt.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendPrompt(String prompt) {
        // Append the user's message to the chat
        runOnUiThread(() -> appendMessageToChat("You: " + prompt));

        // Create content object for Gemini API
        Content content = new Content.Builder().addText(prompt).build();

        // Send the prompt to Gemini
        ListenableFuture<GenerateContentResponse> responseFuture = modelFutures.generateContent(content);

        // Handle the response from Gemini
        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();

                runOnUiThread(() -> appendMessageToChat("Gemini: " + resultText));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error generating response.", Toast.LENGTH_SHORT).show());
            }
        }, executor);
    }

    private void appendMessageToChat(String message) {
        // Append a message to the chat and scroll to the bottom
        chatTextView.append(message + "\n\n");
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }
}

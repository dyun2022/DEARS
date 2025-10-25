package com.example.dears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final Button button = findViewById(R.id.Generate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTextView();
            }
        });
    }

    public void updateTextView(){
        TextView textView = findViewById(R.id.LLMResults);
        // create new llminference object, to be able to connect w/ LLM
        LLMInference llminf = new LLMInference(this);

        // send prompt + display results
        String prompt = "In JSON format, give me a random color and a random number";
        textView.setText(llminf.callLLM(prompt));

    }
}

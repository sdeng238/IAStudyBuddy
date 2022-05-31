package com.example.iastudybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class LaunchActivity extends AppCompatActivity {

    private String[] inspirationalQuotes;

    private TextView randomQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        String[] inspirationalQuotes = {"In the middle of difficulties\nhide opportunities", "Opportunities don't happen,\nyou create them"};
        randomQuote = findViewById(R.id.randomQuoteTextView);

        Random rand = new Random();
        randomQuote.setText(inspirationalQuotes[rand.nextInt(inspirationalQuotes.length)]);
    }

    public void startStudying(View v)
    {
        startActivity(new Intent(this, AuthActivity.class));
    }
}
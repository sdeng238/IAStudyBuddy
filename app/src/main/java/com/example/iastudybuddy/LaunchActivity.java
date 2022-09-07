package com.example.iastudybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * This class appears once the user opens the app. It displays an inspirational quote and has a
 * button that users can click to navigate to authentication or the home page (if they are already logged in).
 *
 * @author Shirley Deng
 * @version 0.1
 */
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
package com.example.meatme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TitleActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);

		Button loginButton = findViewById(R.id.login_button);


		loginButton.setOnClickListener(v -> {
			Intent intent = new Intent(this, FaceActivty.class);
			startActivity(intent);
		});

	}
}
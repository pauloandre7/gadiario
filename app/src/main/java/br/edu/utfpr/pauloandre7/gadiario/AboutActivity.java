package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle(getString(R.string.about_class_text_about));

    }


}
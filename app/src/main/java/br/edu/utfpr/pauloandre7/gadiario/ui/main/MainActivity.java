package br.edu.utfpr.pauloandre7.gadiario.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.ui.bovine.BovinesActivity;
import br.edu.utfpr.pauloandre7.gadiario.ui.event.EventsActivity;
import br.edu.utfpr.pauloandre7.gadiario.ui.pasture.PastureListingActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnBovines, btnPastures, btnEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBovines = findViewById(R.id.main_btnBovines);
        btnPastures = findViewById(R.id.main_btnPastures);
        btnEvents = findViewById(R.id.main_btnEvents);

        btnBovines.setOnClickListener(v -> {
            Intent intent = new Intent(this, BovinesActivity.class);
            startActivity(intent);
        });

        btnPastures.setOnClickListener(v -> {
            Intent intent = new Intent(this, PastureListingActivity.class);
            startActivity(intent);
        });

        btnEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        });
    }
}

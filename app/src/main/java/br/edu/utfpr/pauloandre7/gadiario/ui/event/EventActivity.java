package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.models.EventType;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class EventActivity extends AppCompatActivity {

    public static final String KEY_ID = "ID";
    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    private Spinner spinnerBovine, spinnerType, spinnerOriginPasture, spinnerDestinationPasture;
    private EditText editTextDate, editTextQtyCalves, editTextObservations;

    private List<Bovine> bovines;
    private List<Pasture> pastures;

    private Event eventOriginal;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        spinnerBovine               = findViewById(R.id.event_spinnerBovine);
        spinnerType                 = findViewById(R.id.event_spinnerType);
        editTextDate                = findViewById(R.id.event_etDate);
        editTextQtyCalves           = findViewById(R.id.event_etQtyCalves);
        spinnerOriginPasture        = findViewById(R.id.event_spinnerOriginPasture);
        spinnerDestinationPasture   = findViewById(R.id.event_spinnerDestinationPasture);
        editTextObservations        = findViewById(R.id.event_etObservations);

        loadSpinnersData();

        Intent intentOpen = getIntent();
        Bundle bundle = intentOpen.getExtras();

        if (bundle != null) {
            mode = bundle.getInt(KEY_MODE);

            if (mode == MODE_NEW) {
                setTitle(getString(R.string.event_reg_title));
            } else if (mode == MODE_EDIT) {
                setTitle(getString(R.string.event_reg_title)); // Pode ajustar se tiver string de edição

                long id = bundle.getLong(KEY_ID);
                GadiarioDatabase database = GadiarioDatabase.getInstance(this);
                eventOriginal = database.getEventDao().queryById(id);

                if (eventOriginal != null) {
                    fillFields();
                }
            }
        }
    }

    private void loadSpinnersData() {
        GadiarioDatabase database = GadiarioDatabase.getInstance(this);
        bovines = database.getBovinesDao().queryAllAscending();
        pastures = database.getPastureDao().queryAllAscending();

        ArrayAdapter<Bovine> bovineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bovines);
        bovineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBovine.setAdapter(bovineAdapter);

        ArrayAdapter<Pasture> pastureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pastures);
        pastureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOriginPasture.setAdapter(pastureAdapter);
        spinnerDestinationPasture.setAdapter(pastureAdapter);
    }

    private void fillFields() {
        editTextDate.setText(eventOriginal.getDate());
        editTextQtyCalves.setText(String.valueOf(eventOriginal.getQtyCalves()));
        editTextObservations.setText(eventOriginal.getObservation());

        // Set type
        EventType[] types = EventType.values();
        for (int i = 0; i < types.length; i++) {
            if (types[i] == eventOriginal.getType()) {
                spinnerType.setSelection(i);
                break;
            }
        }

        // Set Bovine
        for (int i = 0; i < bovines.size(); i++) {
            if (bovines.get(i).getId() == eventOriginal.getIdBovine()) {
                spinnerBovine.setSelection(i);
                break;
            }
        }

        // Set Pastures
        for (int i = 0; i < pastures.size(); i++) {
            if (pastures.get(i).getId() == eventOriginal.getIdPastureOrigin()) {
                spinnerOriginPasture.setSelection(i);
            }
            if (pastures.get(i).getId() == eventOriginal.getIdPastureDestination()) {
                spinnerDestinationPasture.setSelection(i);
            }
        }
    }

    public void clearFields() {
        final int bovinePos = spinnerBovine.getSelectedItemPosition();
        final int typePos = spinnerType.getSelectedItemPosition();
        final String date = editTextDate.getText().toString();
        final String qty = editTextQtyCalves.getText().toString();
        final int originPos = spinnerOriginPasture.getSelectedItemPosition();
        final int destPos = spinnerDestinationPasture.getSelectedItemPosition();
        final String obs = editTextObservations.getText().toString();

        spinnerBovine.setSelection(0);
        spinnerType.setSelection(0);
        editTextDate.setText(null);
        editTextQtyCalves.setText("1");
        spinnerOriginPasture.setSelection(0);
        spinnerDestinationPasture.setSelection(0);
        editTextObservations.setText(null);

        final ScrollView scrollView = findViewById(R.id.event_scrollView);
        Snackbar snackbar = Snackbar.make(scrollView, R.string.reg_bov_toast_fields_cleaned, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.common_undo, v -> {
            spinnerBovine.setSelection(bovinePos);
            spinnerType.setSelection(typePos);
            editTextDate.setText(date);
            editTextQtyCalves.setText(qty);
            spinnerOriginPasture.setSelection(originPos);
            spinnerDestinationPasture.setSelection(destPos);
            editTextObservations.setText(obs);
        });
        snackbar.show();
    }

    public void saveValues() {
        String date = editTextDate.getText().toString();
        if (date.trim().isEmpty()) {
            AlertUtils.showAlert(this, R.string.event_reg_alertDialog_dateMissing);
            editTextDate.requestFocus();
            return;
        }

        if (spinnerBovine.getSelectedItem() == null) {
            // Se não houver bovinos cadastrados
            return;
        }

        long idBovine = ((Bovine) spinnerBovine.getSelectedItem()).getId();
        EventType type = EventType.valueOf(spinnerType.getSelectedItem().toString().toUpperCase());
        int qtyCalves = Integer.parseInt(editTextQtyCalves.getText().toString());
        
        long idPastOrigin = 0;
        if (spinnerOriginPasture.getSelectedItem() != null) {
            idPastOrigin = ((Pasture) spinnerOriginPasture.getSelectedItem()).getId();
        }

        long idPastDest = 0;
        if (spinnerDestinationPasture.getSelectedItem() != null) {
            idPastDest = ((Pasture) spinnerDestinationPasture.getSelectedItem()).getId();
        }

        String observations = editTextObservations.getText().toString();

        Event event = new Event(idBovine, type, date, observations);
        event.setQtyCalves(qtyCalves);
        event.setIdPastureOrigin(idPastOrigin);
        event.setIdPastureDestination(idPastDest);

        GadiarioDatabase database = GadiarioDatabase.getInstance(this);

        if (mode == MODE_EDIT && eventOriginal != null) {
            event.setId(eventOriginal.getId());
            // Verificação de igualdade se desejar implementar equals em Event
            database.getEventDao().update(event);
        } else {
            long newId = database.getEventDao().insert(event);
            if (newId <= 0) {
                AlertUtils.showAlert(this, R.string.common_alertDialog_dbErrorInsert);
                return;
            }
            event.setId(newId);
        }

        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_ID, event.getId());
        setResult(RESULT_OK, intentResult);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuItem_save) {
            saveValues();
            return true;
        } else if (id == R.id.menuItem_clear) {
            clearFields();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

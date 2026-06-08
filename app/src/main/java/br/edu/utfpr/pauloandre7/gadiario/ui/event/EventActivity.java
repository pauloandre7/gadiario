package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.models.EventType;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;
import br.edu.utfpr.pauloandre7.gadiario.utils.LocalDateUtils;

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
    private LocalDate dateSelected;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        // Desabilita digitação manual e configura o Picker
        editTextDate.setFocusable(false);
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        loadSpinnersData();

        Intent intentOpen = getIntent();
        Bundle bundle = intentOpen.getExtras();

        if (bundle != null) {
            mode = bundle.getInt(KEY_MODE);

            if (mode == MODE_NEW) {
                setTitle(getString(R.string.event_reg_title));
                dateSelected = LocalDate.now();
                updateDateDisplay();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dateSelected = LocalDate.of(year, month + 1, dayOfMonth);
            updateDateDisplay();
        };

        if (dateSelected == null) {
            dateSelected = LocalDate.now();
        }

        DatePickerDialog picker = new DatePickerDialog(this,
                listener,
                dateSelected.getYear(),
                dateSelected.getMonthValue() - 1,
                dateSelected.getDayOfMonth()
        );

        // Impede a seleção de datas futuras, seguindo o padrão de Bovine
        picker.getDatePicker().setMaxDate(LocalDateUtils.toMilliSeconds(LocalDate.now()));
        picker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDateDisplay() {
        if (dateSelected != null) {
            editTextDate.setText(LocalDateUtils.formatLocalDate(dateSelected));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillFields() {
        dateSelected = eventOriginal.getDate();
        updateDateDisplay();
        
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clearFields() {
        final int bovinePos = spinnerBovine.getSelectedItemPosition();
        final int typePos = spinnerType.getSelectedItemPosition();
        final LocalDate oldDate = dateSelected;
        final String qty = editTextQtyCalves.getText().toString();
        final int originPos = spinnerOriginPasture.getSelectedItemPosition();
        final int destPos = spinnerDestinationPasture.getSelectedItemPosition();
        final String obs = editTextObservations.getText().toString();

        spinnerBovine.setSelection(0);
        spinnerType.setSelection(0);
        dateSelected = LocalDate.now();
        updateDateDisplay();
        editTextQtyCalves.setText("1");
        spinnerOriginPasture.setSelection(0);
        spinnerDestinationPasture.setSelection(0);
        editTextObservations.setText(null);

        final ScrollView scrollView = findViewById(R.id.event_scrollView);
        Snackbar snackbar = Snackbar.make(scrollView, R.string.reg_bov_toast_fields_cleaned, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.common_undo, v -> {
            spinnerBovine.setSelection(bovinePos);
            spinnerType.setSelection(typePos);
            dateSelected = oldDate;
            updateDateDisplay();
            editTextQtyCalves.setText(qty);
            spinnerOriginPasture.setSelection(originPos);
            spinnerDestinationPasture.setSelection(destPos);
            editTextObservations.setText(obs);
        });
        snackbar.show();
    }

    public void saveValues() {
        if (dateSelected == null) {
            AlertUtils.showAlert(this, R.string.event_reg_alertDialog_dateMissing);
            return;
        }

        if (spinnerBovine.getSelectedItem() == null) {
            // Se não houver bovinos cadastrados
            return;
        }

        long idBovine = ((Bovine) spinnerBovine.getSelectedItem()).getId();
        EventType type = EventType.valueOf(spinnerType.getSelectedItem().toString().toUpperCase());
        
        int qtyCalves = 0;
        try {
            qtyCalves = Integer.parseInt(editTextQtyCalves.getText().toString());
        } catch (NumberFormatException ignored) {}
        
        long idPastOrigin = 0;
        if (spinnerOriginPasture.getSelectedItem() != null) {
            idPastOrigin = ((Pasture) spinnerOriginPasture.getSelectedItem()).getId();
        }

        long idPastDest = 0;
        if (spinnerDestinationPasture.getSelectedItem() != null) {
            idPastDest = ((Pasture) spinnerDestinationPasture.getSelectedItem()).getId();
        }

        String observations = editTextObservations.getText().toString();

        Event event = new Event(idBovine, type, dateSelected, observations);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

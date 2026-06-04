package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.models.EventType;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class EventActivity extends AppCompatActivity {

    // Constantes para Intent
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_BOVINE_ID = "KEY_BOVINE_ID";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_DATE = "KEY_DATE";
    public static final String KEY_QTY_CALVES = "KEY_QTY_CALVES";
    public static final String KEY_PASTURE_ORIGIN = "KEY_PASTURE_ORIGIN";
    public static final String KEY_PASTURE_DESTINATION = "KEY_PASTURE_DESTINATION";
    public static final String KEY_OBSERVATIONS = "KEY_OBSERVATIONS";

    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    // Atributos da UI
    private Spinner spinnerBovine, spinnerType, spinnerOriginPasture, spinnerDestinationPasture;
    private EditText editTextDate, editTextQtyCalves, editTextObservations;

    // Atributo para verificar mudanças no objeto original (Edição)
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

        // todo: Verificar - Adicionar adaptadores para os spinners de Bovinos e Pastos quando o DAO estiver pronto

        Intent intentOpen = getIntent();
        Bundle bundle = intentOpen.getExtras();

        if (bundle != null) {
            mode = bundle.getInt(KEY_MODE);

            if (mode == MODE_NEW) {
                setTitle(getString(R.string.event_reg_title));
            } else if (mode == MODE_EDIT) {
                setTitle(getString(R.string.event_reg_title)); // todo: Verificar - Criar string específica para edição de evento se necessário

                int id = bundle.getInt(KEY_ID);
                int idBovine = bundle.getInt(KEY_BOVINE_ID);
                String typeStr = bundle.getString(KEY_TYPE);
                String date = bundle.getString(KEY_DATE);
                int qtyCalves = bundle.getInt(KEY_QTY_CALVES);
                int idPastOrigin = bundle.getInt(KEY_PASTURE_ORIGIN);
                int idPastDest = bundle.getInt(KEY_PASTURE_DESTINATION);
                String observations = bundle.getString(KEY_OBSERVATIONS);

                EventType typeEnum = EventType.valueOf(typeStr);

                eventOriginal = new Event(id, idBovine, typeEnum, qtyCalves, idPastOrigin, idPastDest, date, observations);

                // Preencher campos
                editTextDate.setText(date);
                editTextQtyCalves.setText(String.valueOf(qtyCalves));
                editTextObservations.setText(observations);

                // todo: Verificar - Selecionar itens corretos nos Spinners baseados nos IDs recebidos
                // spinnerType.setSelection(...) 
            }
        }
    }

    public void clearFields() {
        // Salva valores para o Undo
        final int bovinePos = spinnerBovine.getSelectedItemPosition();
        final int typePos = spinnerType.getSelectedItemPosition();
        final String date = editTextDate.getText().toString();
        final String qty = editTextQtyCalves.getText().toString();
        final int originPos = spinnerOriginPasture.getSelectedItemPosition();
        final int destPos = spinnerDestinationPasture.getSelectedItemPosition();
        final String obs = editTextObservations.getText().toString();

        // Limpa
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
        // Validações básicas similares a BovineActivity
        String date = editTextDate.getText().toString();
        if (date.trim().isEmpty()) {
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_birthMissing); // Reaproveitando string de data
            editTextDate.requestFocus();
            return;
        }

        // Recuperar valores dos campos
        // todo: Verificar - Implementar a lógica de pegar IDs dos spinners de Bovino e Pastos
        int idBovine = 0; 
        EventType type = EventType.values()[spinnerType.getSelectedItemPosition()];
        int qtyCalves = Integer.parseInt(editTextQtyCalves.getText().toString());
        int idPastOrigin = 0;
        int idPastDest = 0;
        String observations = editTextObservations.getText().toString();

        if (mode == MODE_EDIT) {
            // Verificação de mudanças similar a BovineActivity
            if (idBovine == eventOriginal.getIdBovine() &&
                type == eventOriginal.getType() &&
                date.equals(eventOriginal.getDate()) &&
                qtyCalves == eventOriginal.getQtyCalves() &&
                observations.equals(eventOriginal.getObservation())) {
                
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        Intent intentResult = new Intent();
        intentResult.putExtra(KEY_BOVINE_ID, idBovine);
        intentResult.putExtra(KEY_TYPE, type.toString());
        intentResult.putExtra(KEY_DATE, date);
        intentResult.putExtra(KEY_QTY_CALVES, qtyCalves);
        intentResult.putExtra(KEY_PASTURE_ORIGIN, idPastOrigin);
        intentResult.putExtra(KEY_PASTURE_DESTINATION, idPastDest);
        intentResult.putExtra(KEY_OBSERVATIONS, observations);

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

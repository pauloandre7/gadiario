package br.edu.utfpr.pauloandre7.gadiario.ui.pasture;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class PastureActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";

    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    private int mode;

    private EditText editTextName, editTextDescription;
    private Pasture pastureOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasture);

        // Mapeia os elementos da tela
        editTextName = findViewById(R.id.past_editTextText_Name);
        editTextDescription = findViewById(R.id.past_editText_Description);

        // recebe a intent que originou a activity
        Intent intentOpen = getIntent();

        Bundle bundle = intentOpen.getExtras();
        mode = bundle.getInt(KEY_MODE);

        if(mode == MODE_NEW){
            setTitle(getString(R.string.past_reg_title));
        }else if(mode == MODE_EDIT){
            setTitle(getString(R.string.past_edit_Title));

            // primeiro salva um objeto original para verificar se teve mudanças
            pastureOriginal = new Pasture(bundle.getString(KEY_NAME), bundle.getString(KEY_DESCRIPTION));

            editTextName.setText(bundle.getString(KEY_NAME));
            editTextDescription.setText(bundle.getString(KEY_DESCRIPTION));
        }

    }

    public void clearFields(){
        // Salva os valores atuais para o Undo
        final String name = editTextName.getText().toString();
        final String description = editTextDescription.getText().toString();

        // Limpa os campos
        editTextName.setText(null);
        editTextDescription.setText(null);

        final ScrollView scrollView = findViewById(R.id.main);
        final View focusedView = scrollView.findFocus();

        Snackbar snackbar = Snackbar.make(scrollView,
                R.string.reg_bov_toast_fields_cleaned,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.common_undo, new View.OnClickListener() {

            @Override
            public void onClick(View v){
                // refaz tudo quando clica em Undo.
                editTextName.setText(name);
                editTextDescription.setText(description);
            }
        });

        // Mantém o foco onde o usuário estava ou volta para o primeiro campo
        if(focusedView != null){
            focusedView.requestFocus();
            snackbar.setAnchorView(focusedView);
        } else {
            editTextName.requestFocus();
            snackbar.setAnchorView(editTextName);
        }

        snackbar.show();
    }

    public void saveValues(){

        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();

        if(name == null || name.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_nameMissing);

            editTextName.requestFocus();
            return;
        }
        if(description == null || description.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.past_reg_warning_missingDescription);

            editTextDescription.requestFocus();
            return;
        }

        // cancela a resposta se nada for alterado
        if(mode == MODE_EDIT){
            if(pastureOriginal.getName().equals(name) &&
                pastureOriginal.getDescription().equals(description)){

                setResult(PastureActivity.RESULT_CANCELED);
                finish();
                return;
            }
        }

        // Intent pra passar os resultados
        Intent intentResult = new Intent();
        intentResult.putExtra(PastureActivity.KEY_NAME, name);
        intentResult.putExtra(PastureActivity.KEY_DESCRIPTION, description);

        // seta o resultado com a resposta e o objeto de intenção de resposta;
        setResult(PastureActivity.RESULT_OK, intentResult);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();

        if(idMenuItem == R.id.menuItem_clear){
            clearFields();
        } else if(idMenuItem == R.id.menuItem_save){
            saveValues();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

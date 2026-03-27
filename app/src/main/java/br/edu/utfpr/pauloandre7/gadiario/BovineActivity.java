package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BovineActivity extends AppCompatActivity {

    private EditText editTextTag, editTextName, editTextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);

        editTextTag = findViewById(R.id.editTextTag);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
    }

    public void cleanFields(View view){
        editTextTag.setText(null);
        editTextName.setText(null);
        editTextDate.setText(null);

        Toast.makeText(this,
                R.string.cad_bov_toast_fields_cleaned, Toast.LENGTH_LONG).show();
    }

    public void saveValues(View view){
        String tag = editTextTag.getText().toString();
        String name = editTextName.getText().toString();
        String date = editTextDate.getText().toString();

        if(tag == null || tag.trim().isEmpty()){
            Toast.makeText(this,
                    R.string.cad_bov_toast_text_tagMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        if(name == null || name.trim().isEmpty()){
            Toast.makeText(this,
                            R.string.cad_bov_toast_text_nameMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        if(date == null || date.trim().isEmpty()){
            Toast.makeText(this,
                    R.string.cad_bov_toast_text_birthMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        String resultFields = getString(R.string.cad_bov_toast_text_tagValue)+tag+"\n"
                            +getString(R.string.cad_bov_toast_text_nameValue)+name+"\n"
                            +getString(R.string.cad_bov_toast_text_birthValue)+date;
        Toast.makeText(this,
                        resultFields, Toast.LENGTH_LONG).show();
    }
}
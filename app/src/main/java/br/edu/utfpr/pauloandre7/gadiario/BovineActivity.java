package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BovineActivity extends AppCompatActivity {

    private EditText editTextTag, editTextName, editTextDate;
    private final List<CheckBox> checkBoxVaccines = new ArrayList<>();
    private RadioGroup radioGroupSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);

        editTextTag = findViewById(R.id.editTextTag);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op1));
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op2));
        radioGroupSex = findViewById(R.id.radioGroupSex);
    }

    public void cleanFields(View view){
        editTextTag.setText(null);
        editTextName.setText(null);
        editTextDate.setText(null);
        checkBoxVaccines.get(0).setChecked(false);
        radioGroupSex.clearCheck();

        Toast.makeText(this,
                R.string.reg_bov_toast_fields_cleaned, Toast.LENGTH_LONG).show();
    }

    public void saveValues(View view){
        String tag = editTextTag.getText().toString();
        String name = editTextName.getText().toString();
        String date = editTextDate.getText().toString();

        if(tag == null || tag.trim().isEmpty()){
            Toast.makeText(this,
                    R.string.reg_bov_toast_text_tagMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        if(name == null || name.trim().isEmpty()){
            Toast.makeText(this,
                            R.string.reg_bov_toast_text_nameMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        if(date == null || date.trim().isEmpty()){
            Toast.makeText(this,
                    R.string.reg_bov_toast_text_birthMissing, Toast.LENGTH_LONG).show();

            editTextName.requestFocus();
            return;
        }

        boolean isVaccinated = false;
        // For-each em java pega cada elemento que tiver no array e guarda na variável option
        // Cada elemento é considerado como um ciclo, então facilita minha vida na hora de percorrer array.
        for (CheckBox option : checkBoxVaccines){
            isVaccinated = option.isChecked();
            if (isVaccinated == true){
                break;
            }
        }

        String animalSex;
        int radioButtonId = radioGroupSex.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radioBtnFemale){

            animalSex = getString(R.string.reg_bov_text_female);
        } else if (radioButtonId == R.id.radioBtnMale){

            animalSex = getString(R.string.reg_bov_text_male);
        } else{
            // if the animal sex was not selected
            Toast.makeText(this,
                    R.string.reg_bov_toast_text_animalSexMissing, Toast.LENGTH_LONG).show();

            radioGroupSex.requestFocus();
            return;
        }

        String resultFields = getString(R.string.reg_bov_toast_text_tagValue)+tag+"\n"
                            +getString(R.string.reg_bov_toast_text_nameValue)+name+"\n"
                            +getString(R.string.reg_bov_toast_text_birthValue)+date+"\n"
                            +(isVaccinated ? getString(R.string.reg_bov_toast_text_isVaccinated) : getString(R.string.reg_bov_toast_text_notVaccinated))+"\n"
                            +getString(R.string.reg_bov_toast_text_sexValue)+animalSex;
        Toast.makeText(this,
                        resultFields, Toast.LENGTH_LONG).show();
    }
}
package br.edu.utfpr.pauloandre7.gadiario;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BovineActivity extends AppCompatActivity {

    // Constantes de resultado para IntentResult
    public static final String KEY_TAG = "KEY_TAG";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_BIRTH = "KEY_BIRTH";
    public static final String KEY_SEX = "KEY_SEX";
    public static final String KEY_BREED = "KEY_BREED";
    public static final String KEY_VACCINES = "KEY_VACCINES";

    private EditText editTextTag, editTextName, editTextDate;
    private final List<CheckBox> checkBoxVaccines = new ArrayList<>();
    private RadioGroup radioGroupSex;
    private Spinner spinnerBreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);
        setTitle(getString(R.string.reg_bov_title));

        editTextTag = findViewById(R.id.editTextTag);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op1));
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op2));
        radioGroupSex = findViewById(R.id.radioGroupSex);
        spinnerBreed = findViewById(R.id.spinnerBreed);

        // fillSpinner();
    }

    /* First Method to populate Spinner showed by teacher
        The another one is using String Arrays at resources
    private void fillSpinner(){

        // Spinner's data (First Method)
        List<String> breedList = new ArrayList<>();

        // de Corte (Zebuínas e Taurinas)
        breedList.add("Nelore");
        breedList.add("Angus");
        breedList.add("Brahman");
        breedList.add("Brangus");
        breedList.add("Senepol");

        // Leiteiras
        breedList.add("Holandesa");
        breedList.add("Gir Leiteiro");
        breedList.add("Girolando");
        breedList.add("Jersey");

        // Outras / Mestiços
        breedList.add("Guzerá");
        breedList.add("Tabapuã");
        breedList.add("Cruzamento Industrial");

        // Renderiza cada linha do array no Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                        android.R.layout.simple_list_item_1,
                                                        breedList);

        spinnerBreed.setAdapter(adapter );
    }
    */

    public void clearFields(){
        editTextTag.setText(null);
        editTextName.setText(null);
        editTextDate.setText(null);
        checkBoxVaccines.get(0).setChecked(false);
        checkBoxVaccines.get(1).setChecked(false);
        radioGroupSex.clearCheck();
        spinnerBreed.setSelection(0);

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

        String[] vaccines = new String[checkBoxVaccines.size()];
        int cont= 0;
        // For-each em java pega cada elemento que tiver no array e guarda na variável option
        // Cada elemento é considerado como um ciclo, então facilita minha vida na hora de percorrer array.
        for (CheckBox option : checkBoxVaccines){
            if (option.isChecked()){
                vaccines[cont] = option.getText().toString();
            } else {
                vaccines[cont] = "\n";
            }

            cont++;
        }

        AnimalSex animalSex;
        int radioButtonId = radioGroupSex.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radioBtnFemale){

            animalSex = AnimalSex.FEMALE;
        } else if (radioButtonId == R.id.radioBtnMale){

            animalSex = AnimalSex.MALE;
        } else{
            // if the animal sex was not selected
            Toast.makeText(this,
                    R.string.reg_bov_toast_text_animalSexMissing, Toast.LENGTH_LONG).show();

            radioGroupSex.requestFocus();
            return;
        }

        String animalBreed = spinnerBreed.getSelectedItem().toString();
        if (animalBreed == null){

            Toast.makeText(this,
                            R.string.reg_bov_toast_text_warningSpinnerEmpty,
                            Toast.LENGTH_LONG);
        }

        // Para passar resultados entre activities, é necessário usar um Intent;
        Intent intentResult = new Intent();


        // Para passar um objeto construído, é necessário fazer com que seja serializável (mas não é recomendado)
        intentResult.putExtra(KEY_TAG, tag);
        intentResult.putExtra(KEY_NAME, name);
        intentResult.putExtra(KEY_BIRTH, date);
        intentResult.putExtra(KEY_SEX, animalSex.toString());
        intentResult.putExtra(KEY_BREED, animalBreed);
        intentResult.putExtra(KEY_VACCINES, vaccines);

        // seta o resultado com a resposta e o objeto de intenção de resposta;
        setResult(BovineActivity.RESULT_OK, intentResult);

        // precisa encerrar a activity para que o resultado seja passado;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bovine_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if(idMenuItem == R.id.menuItem_clear){
            clearFields();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
}
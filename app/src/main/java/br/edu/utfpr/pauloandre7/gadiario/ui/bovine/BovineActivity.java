package br.edu.utfpr.pauloandre7.gadiario.ui.bovine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.AnimalSex;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;

public class BovineActivity extends AppCompatActivity {

    // Constantes de resultado para IntentResult
    public static final String KEY_TAG = "KEY_TAG";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_BIRTH = "KEY_BIRTH";
    public static final String KEY_SEX = "KEY_SEX";
    public static final String KEY_BREED = "KEY_BREED";
    public static final String KEY_VACCINES = "KEY_VACCINES";

    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    // CLASS ATTRIBUTES
    private EditText editTextTag, editTextName, editTextDate;
    private final List<CheckBox> checkBoxVaccines = new ArrayList<>();
    private RadioGroup radioGroupSex;
    private RadioButton radioButtonFemale, radioButtonMale;
    private Spinner spinnerBreed;

    // Atributo para verificar mudanças no objeto original
    private Bovine bovineOriginal;

    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);

        editTextTag         = findViewById(R.id.editTextTag);
        editTextName        = findViewById(R.id.editTextName);
        editTextDate        = findViewById(R.id.editTextDate);
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op1));
        checkBoxVaccines.add(findViewById(R.id.checkBox_vaccines_op2));
        radioGroupSex       = findViewById(R.id.radioGroupSex);
        radioButtonFemale   = findViewById(R.id.radioBtnFemale);
        radioButtonMale     = findViewById(R.id.radioBtnMale);
        spinnerBreed        = findViewById(R.id.spinnerBreed);

        // recebe a intent que originou a activity
        Intent intentOpen = getIntent();

        Bundle bundle = intentOpen.getExtras();

        if(bundle != null){
            mode = bundle.getInt(KEY_MODE);

            if(mode == MODE_NEW){
                setTitle(getString(R.string.reg_bov_title));
            } else if (mode == MODE_EDIT){
                setTitle(getString(R.string.bov_edit_title));

                String tag = bundle.getString(BovineActivity.KEY_TAG);
                String name = bundle.getString(BovineActivity.KEY_NAME);
                String date = bundle.getString(BovineActivity.KEY_BIRTH);
                String animalSex = bundle.getString(BovineActivity.KEY_SEX);
                String animalBreed = bundle.getString(BovineActivity.KEY_BREED);
                String[] vaccines = bundle.getStringArray(BovineActivity.KEY_VACCINES);

                AnimalSex animalSex_enum = AnimalSex.valueOf(animalSex);

                bovineOriginal = new Bovine(tag, name, date, animalSex_enum, animalBreed, List.of(vaccines));

                editTextTag.setText(tag);
                editTextName.setText(name);
                editTextDate.setText(date);
                if(animalSex_enum == AnimalSex.FEMALE){
                    radioButtonFemale.setChecked(true);
                } else {
                    radioButtonMale.setChecked(true);
                }

                // Pego o array de raças que tem no string para poder comparar.
                String[] breedArray = getResources().getStringArray(R.array.animalBreed);

                for (int i = 0; i < breedArray.length; i++){
                    if(breedArray[i].equals(animalBreed)){
                        spinnerBreed.setSelection(i);
                    }
                }

                for (int i = 0; i < vaccines.length; i++){
                    if(vaccines[i] != null || !vaccines[i].isEmpty() || !vaccines[i].equals("")){
                        checkBoxVaccines.get(i).setChecked(true);
                    }
                }



            }
        }

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

    public void saveValues(){
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
                vaccines[cont] = "";
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



        // Antes da Intent de resposta, verifica se os valores sofreram mudança
        if(mode == MODE_EDIT) {

            // Verifica se as vacinas são iguais
            boolean isVaccinesEqual = false;
            cont = 0;

            for (String vaccine : vaccines) {
                isVaccinesEqual = false;
                if (vaccine.equals(bovineOriginal.getVaccines().get(cont))) {
                    isVaccinesEqual = true;
                }
                cont++;
            }

            // Compara os outros atributos;
            if (tag.equals(bovineOriginal.getTag())
                    && name.equalsIgnoreCase(bovineOriginal.getName())
                    && date.equals(bovineOriginal.getDate())
                    && animalSex.equals(bovineOriginal.getAnimalSex())
                    && animalBreed.equals(bovineOriginal.getBreed())
                    && isVaccinesEqual) {

                setResult(BovineActivity.RESULT_CANCELED);
                finish();
                return;
            }
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

        getMenuInflater().inflate(R.menu.register_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if(idMenuItem == R.id.menuItem_clear) {
            clearFields();
            return true;
        }else if(idMenuItem == R.id.menuItem_save){
            saveValues();
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }
}
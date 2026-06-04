package br.edu.utfpr.pauloandre7.gadiario.ui.bovine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import br.edu.utfpr.pauloandre7.gadiario.models.ReproductiveStatus;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class BovineActivity extends AppCompatActivity {

    // Constantes de resultado para IntentResult
    public static final String KEY_TAG = "KEY_TAG";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_BIRTH = "KEY_BIRTH";
    public static final String KEY_SEX = "KEY_SEX";
    public static final String KEY_BREED = "KEY_BREED";
    public static final String KEY_VACCINES = "KEY_VACCINES";
    public static final String KEY_REPSTATUS = "KEY_REPSTATUS";

    // Keys para usar no Shared Preferences
    public static final String KEY_SUGGEST_BREED = "SUGGEST_BASIC";
    public static final String KEY_LAST_BREED = "LAST_BREED";
    public static final String KEY_LAST_TAG = "LAST_TAG";

    // Keys para controlar o modo de uso da activity de cadastro
    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    // CLASS ATTRIBUTES
    private EditText editTextTag, editTextName, editTextDate, editTextVaccines;
    private RadioGroup radioGroupSex;
    private RadioButton radioButtonFemale, radioButtonMale;
    private Spinner spinnerBreed, spinnerRepStatus;

    // Atributo para verificar mudanças no objeto original
    private Bovine bovineOriginal;

    private int mode;

    // Atributos para gerenciar o SharedPreferences inicializados com valores padrões
    private boolean suggestInfo = false;
    private int     lastBreed = 0;
    private String  lastTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);

        editTextTag         = findViewById(R.id.editTextTag);
        editTextName        = findViewById(R.id.editTextName);
        editTextDate        = findViewById(R.id.editTextDate);
        radioGroupSex       = findViewById(R.id.radioGroupSex);
        radioButtonFemale   = findViewById(R.id.radioBtnFemale);
        radioButtonMale     = findViewById(R.id.radioBtnMale);
        editTextVaccines    = findViewById(R.id.bov_editTextVaccines);
        spinnerBreed        = findViewById(R.id.bov_spinnerBreed);
        spinnerRepStatus    = findViewById(R.id.bov_spinnerRepStatus);

        // Realiza a leitura das preferências na abertura da activity;
        readPreferences();

        // recebe a intent que originou a activity
        Intent intentOpen = getIntent();

        Bundle bundle = intentOpen.getExtras();

        if(bundle != null){
            mode = bundle.getInt(KEY_MODE);

            if(mode == MODE_NEW){
                setTitle(getString(R.string.reg_bov_title));

                if(suggestInfo){
                    spinnerBreed.setSelection(lastBreed);
                    editTextTag.setText(lastTag);
                }

            } else if (mode == MODE_EDIT){
                setTitle(getString(R.string.bov_edit_title));

                String tag = bundle.getString(BovineActivity.KEY_TAG);
                String name = bundle.getString(BovineActivity.KEY_NAME);
                String date = bundle.getString(BovineActivity.KEY_BIRTH);
                String animalSex = bundle.getString(BovineActivity.KEY_SEX);
                String animalBreed = bundle.getString(BovineActivity.KEY_BREED);
                String[] vaccines = bundle.getStringArray(BovineActivity.KEY_VACCINES);
                String repStatus = bundle.getString(BovineActivity.KEY_REPSTATUS);

                AnimalSex animalSex_enum = AnimalSex.valueOf(animalSex);
                ReproductiveStatus repStatus_enum = ReproductiveStatus.valueOf(repStatus);

                bovineOriginal = new Bovine(tag, name, date, animalSex_enum, animalBreed,
                                            List.of(vaccines), repStatus_enum);

                editTextTag.setText(tag);
                editTextName.setText(name);
                editTextDate.setText(date);
                if(animalSex_enum == AnimalSex.FEMALE){
                    radioButtonFemale.setChecked(true);
                } else {
                    radioButtonMale.setChecked(true);
                }

                // Pego o array de raças que tem no string para poder comparar e selecionar.
                String[] breedArray = getResources().getStringArray(R.array.animalBreed);

                for (int i = 0; i < breedArray.length; i++){
                    if(breedArray[i].equals(animalBreed)){
                        spinnerBreed.setSelection(i);
                    }
                }

                String[] statusArray = getResources().getStringArray(R.array.reproductiveStatus);
                for(int i = 0; i < statusArray.length; i++){
                    if(statusArray[i].equals(repStatus)){
                        spinnerRepStatus.setSelection(i);
                    }
                }

                String vaccines_text = "";
                for (int i = 0; i < vaccines.length; i++){
                    if(vaccines[i] != null || !vaccines[i].isEmpty()){
                        vaccines_text = vaccines_text + vaccines[i] + ", ";
                    }
                }

                editTextVaccines.setText(vaccines_text);
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
        editTextVaccines.setText(null);
        radioGroupSex.clearCheck();
        spinnerBreed.setSelection(0);
        spinnerRepStatus.setSelection(0);

        Toast.makeText(this,
                R.string.reg_bov_toast_fields_cleaned, Toast.LENGTH_LONG).show();
    }

    public void saveValues(){
        String tag          = editTextTag.getText().toString();
        String name         = editTextName.getText().toString();
        String date         = editTextDate.getText().toString();
        String vaccines     = editTextVaccines.getText().toString();

        if(tag == null || tag.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_tagMissing);

            editTextTag.requestFocus();
            return;
        }

        if(name == null || name.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_nameMissing);

            editTextName.requestFocus();
            return;
        }

        if(date == null || date.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_birthMissing);

            editTextDate.requestFocus();
            return;
        }

        // Separa a string a cada vírgula e cria uma lista com os elementos
        String[] vaccinesArray      = vaccines.split(",");
        List<String> vaccinesList   = new ArrayList<>();
        for (String vac : vaccinesArray){
            // Se a vacina em questão não ficar vazia após o trim, significa que foi escrito algo (add isso na lista)
            String item = vac.trim();
            if(!item.isEmpty()){
                vaccinesList.add(item);
            }
        }

        AnimalSex animalSex;
        int radioButtonId = radioGroupSex.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radioBtnFemale){

            animalSex = AnimalSex.FEMALE;
        } else if (radioButtonId == R.id.radioBtnMale){

            animalSex = AnimalSex.MALE;
        } else{
            // if the animal sex was not selected
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_animalSexMissing);

            radioGroupSex.requestFocus();
            return;
        }

        ReproductiveStatus repStatus_enum;
        String repStatus_string = spinnerRepStatus.getSelectedItem().toString().toUpperCase();
        if(repStatus_string.equals(ReproductiveStatus.SECA.toString())){

            repStatus_enum = ReproductiveStatus.SECA;
        } else if (repStatus_string.equals(ReproductiveStatus.PRENHA.toString())){

            repStatus_enum = ReproductiveStatus.PRENHA;
        } else if(repStatus_string.equals(ReproductiveStatus.LACTANTE.toString())){

            repStatus_enum = ReproductiveStatus.LACTANTE;
        } else if(repStatus_string.equals(ReproductiveStatus.PRONTA.toString())){

            repStatus_enum = ReproductiveStatus.PRONTA;
        } else {
            AlertUtils.showAlert(this, R.string.reg_bov_toast_ReproductiveStatusMissing);

            spinnerRepStatus.requestFocus();
            return;
        }

        String animalBreed = spinnerBreed.getSelectedItem().toString();
        if (animalBreed == null){

            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_warningSpinnerEmpty);
        }



        // Antes da Intent de resposta, verifica se os valores sofreram mudança
        if(mode == MODE_EDIT) {

            // Verifica se as vacinas são iguais
            boolean isVaccinesEqual = vaccinesList.equals(bovineOriginal.getVaccines());

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

        saveLastBreed(spinnerBreed.getSelectedItemPosition());
        saveLastTag(editTextTag.getText().toString());

        // Para passar resultados entre activities, é necessário usar um Intent;
        Intent intentResult = new Intent();


        // Para passar um objeto construído, é necessário fazer com que seja serializável (mas não é recomendado)
        intentResult.putExtra(KEY_TAG, tag);
        intentResult.putExtra(KEY_NAME, name);
        intentResult.putExtra(KEY_BIRTH, date);
        intentResult.putExtra(KEY_SEX, animalSex.toString());
        intentResult.putExtra(KEY_REPSTATUS, repStatus_enum.toString());
        intentResult.putExtra(KEY_BREED, animalBreed);
        intentResult.putExtra(KEY_VACCINES, vaccinesList.toArray(new String[0]));

        // seta o resultado com a resposta e o objeto de intenção de resposta;
        setResult(BovineActivity.RESULT_OK, intentResult);

        // precisa encerrar a activity para que o resultado seja passado;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bov_register_options, menu);
        return true;
    }

    // Diferente do OnCreat, o OnPrepare é chamado sempre que o menu for exibido
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Recupera o menu item para mudar sua condição com base na variável local
        MenuItem item = menu.findItem(R.id.menuItem_suggestBasic);
        item.setChecked(suggestInfo);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if(idMenuItem == R.id.bov_menuItem_clear) {
            clearFields();
            return true;
        }else if(idMenuItem == R.id.bov_menuItem_save) {
            saveValues();
            return true;

        } else if(idMenuItem == R.id.menuItem_suggestBasic){
            boolean value = !item.isChecked();

            saveSuggestBreed(value);

            // precisa forçar o menuItem a trocar o valor do check
            item.setChecked(value);

            if(suggestInfo){
                spinnerBreed.setSelection(lastBreed);
                editTextTag.setText(lastTag);
            }

            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void readPreferences(){
        // abrir o sharedPreferences primeiro; NO modo privado, apenas esse app usa o arquivo.
        // Na primeira vez, essa linha cria o arquivo, na segunda, pega um criado.
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        // O primeiro param é a key, mas o segundo é para definir qual é o valor default se não houver valor gravado
        suggestInfo = shared.getBoolean(KEY_SUGGEST_BREED, suggestInfo);
        lastBreed   = shared.getInt(KEY_LAST_BREED, lastBreed);
        lastTag     = shared.getString(KEY_LAST_TAG, lastTag);
    }

    private void saveSuggestBreed(boolean newValue){
        // abre novamente o sharedPreferences
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        // Precisa de um editor para gravar coisas no arquivo;
        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(KEY_SUGGEST_BREED, newValue);

        // realiza a gravação de fato, pois antes ainda estava em memória
        editor.commit();
        suggestInfo = newValue;
    }

    private void saveLastBreed(int newValue){
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(KEY_LAST_BREED, newValue);

        editor.commit();
        lastBreed = newValue;
    }

    private void saveLastTag(String newValue){
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putString(KEY_LAST_TAG, newValue);

        editor.commit();
        lastTag = newValue;
    }
}
package br.edu.utfpr.pauloandre7.gadiario.ui.bovine;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.AnimalSex;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.models.ReproductiveStatus;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;
import br.edu.utfpr.pauloandre7.gadiario.utils.LocalDateUtils;

public class BovineActivity extends AppCompatActivity {

    // Constantes de resultado para IntentResult
    public static final String KEY_ID = "ID";

    // Keys para usar no Shared Preferences
    public static final String KEY_SUGGEST_BREED = "SUGGEST_BASIC";
    public static final String KEY_LAST_BREED = "LAST_BREED";
    public static final String KEY_LAST_TAG = "LAST_TAG";

    // Keys para controlar o modo de uso da activity de cadastro
    public static final String KEY_MODE = "MODE";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    // CLASS ATTRIBUTES
    private EditText editTextTag, editTextName, editTextBovBirth, editTextVaccines;
    private RadioGroup radioGroupSex;
    private RadioButton radioButtonFemale, radioButtonMale;
    private Spinner spinnerBreed, spinnerRepStatus, spinnerPasture;

    private List<Pasture> pastures;

    private LocalDate bovBirth;

    // Atributo para verificar mudanças no objeto original
    private Bovine bovineOriginal;

    private int mode;

    // Atributos para gerenciar o SharedPreferences inicializados com valores padrões
    private boolean suggestInfo = false;
    private int     lastBreed = 0;
    private String  lastTag = "";

    // O compilador me fez usar essa anotação para cessar o aviso no uso de LocalDate.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovine);

        editTextTag         = findViewById(R.id.editTextTag);
        editTextName        = findViewById(R.id.editTextName);
        editTextBovBirth    = findViewById(R.id.editTextBovBirth);
        radioGroupSex       = findViewById(R.id.radioGroupSex);
        radioButtonFemale   = findViewById(R.id.radioBtnFemale);
        radioButtonMale     = findViewById(R.id.radioBtnMale);
        editTextVaccines    = findViewById(R.id.bov_editTextVaccines);
        spinnerBreed        = findViewById(R.id.bov_spinnerBreed);
        spinnerRepStatus    = findViewById(R.id.bov_spinnerRepStatus);
        spinnerPasture      = findViewById(R.id.bov_spinnerPasture);

        //desabilita o editTextDate para usar somente picker
        editTextBovBirth.setFocusable(false);

        editTextBovBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Realiza a leitura das preferências na abertura da activity;
        readPreferences();

        // Carrega os pastos do banco de dados
        loadPasturesData();

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

                bovBirth = LocalDate.now();

            } else if (mode == MODE_EDIT){
                setTitle(getString(R.string.bov_edit_title));

                long id = bundle.getLong(KEY_ID);

                GadiarioDatabase database = GadiarioDatabase.getInstance(this);

                bovineOriginal = database.getBovinesDao().queryById(id);

                AnimalSex animalSex_enum = bovineOriginal.getAnimalSex();
                ReproductiveStatus repStatus_enum = bovineOriginal.getRepStatus();

                editTextTag.setText(bovineOriginal.getTag());
                editTextName.setText(bovineOriginal.getName());

                bovBirth = bovineOriginal.getBirth();
                if(bovBirth != null){
                    editTextBovBirth.setText(LocalDateUtils.formatLocalDate(bovBirth));
                }

                if(animalSex_enum == AnimalSex.FEMALE){
                    radioButtonFemale.setChecked(true);
                } else {
                    radioButtonMale.setChecked(true);
                }

                // Pego o array de raças que tem no string para poder comparar e selecionar.
                String[] breedArray = getResources().getStringArray(R.array.animalBreed);

                for (int i = 0; i < breedArray.length; i++){
                    if(breedArray[i].equals(bovineOriginal.getBreed())){
                        spinnerBreed.setSelection(i);
                    }
                }

                String[] statusArray = getResources().getStringArray(R.array.reproductiveStatus);
                for(int i = 0; i < statusArray.length; i++){
                    if(statusArray[i].equals(bovineOriginal.getRepStatus().toString())){
                        spinnerRepStatus.setSelection(i);
                    }
                }

                // Seleciona o pasto correto no spinner
                if (pastures != null) {
                    for (int i = 0; i < pastures.size(); i++) {
                        if (pastures.get(i).getId() == bovineOriginal.getIdPasture()) {
                            spinnerPasture.setSelection(i);
                            break;
                        }
                    }
                }

                String vaccines_text = "";
                for (int i = 0; i < bovineOriginal.getVaccines().size(); i++){
                    if(bovineOriginal.getVaccines().get(i) != null || !bovineOriginal.getVaccines().get(i).isEmpty()){
                        vaccines_text = vaccines_text + bovineOriginal.getVaccines().get(i) + ", ";
                    }
                }

                editTextVaccines.setText(vaccines_text);

                // joga o cursos no final do edit text tag
                editTextTag.requestFocus();
                editTextTag.setSelection(editTextTag.getText().length());
            }
        }

        // fillSpinner();
    }

    private void loadPasturesData() {
        GadiarioDatabase database = GadiarioDatabase.getInstance(this);
        pastures = database.getPastureDao().queryAllAscending();

        ArrayAdapter<Pasture> pastureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pastures);
        pastureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPasture.setAdapter(pastureAdapter);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog(){

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // month do date picker começa do 0, mas o LocalDate começa em 1. Então soma +1;
                bovBirth = LocalDate.of(year, month + 1, dayOfMonth);
                editTextBovBirth.setText(LocalDateUtils.formatLocalDate(bovBirth));
            }
        };

        if(bovBirth == null){
            bovBirth = LocalDate.now();
        }

        DatePickerDialog picker = new DatePickerDialog(this,
                listener,
                bovBirth.getYear(),
                bovBirth.getMonthValue() - 1,
                bovBirth.getDayOfMonth()
        );

        long dateMaxMillis = LocalDateUtils.toMilliSeconds(LocalDate.now());

        picker.getDatePicker().setMaxDate(dateMaxMillis);
        picker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clearFields(){

        // Define final para manter as variáveis mesmo após o término do méthod
        final String tag = editTextTag.getText().toString();
        final String name = editTextName.getText().toString();
        final LocalDate birth = bovBirth;
        final String vaccines = editTextVaccines.getText().toString();
        final int radioButtonSex = radioGroupSex.getCheckedRadioButtonId();
        final int breed = spinnerBreed.getSelectedItemPosition();
        final int repStatus = spinnerRepStatus.getSelectedItemPosition();
        final int pasture = spinnerPasture.getSelectedItemPosition();

        // Limpa os campos
        editTextTag.setText(null);
        editTextName.setText(null);
        editTextBovBirth.setText(null);
        bovBirth = LocalDate.now();

        editTextVaccines.setText(null);
        radioGroupSex.clearCheck();
        spinnerBreed.setSelection(0);
        spinnerRepStatus.setSelection(0);
        spinnerPasture.setSelection(0);


        final ScrollView scrollView = findViewById(R.id.main);
        final View focusedView = scrollView.findFocus();


        Snackbar snackbar = Snackbar.make(scrollView,
                R.string.reg_bov_toast_fields_cleaned,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.common_undo, new View.OnClickListener() {

            @Override
            public void onClick(View v){
                // refaz tudo quando clica em Undo.

                editTextTag.setText(tag);
                editTextName.setText(name);
                editTextBovBirth.setText(LocalDateUtils.formatLocalDate(birth));
                editTextVaccines.setText(vaccines);

                if (radioButtonSex == R.id.radioBtnFemale){

                    radioButtonFemale.setChecked(true);
                } else if (radioButtonSex == R.id.radioBtnMale) {

                    radioButtonMale.setChecked(true);
                }

                spinnerBreed.setSelection(breed);
                spinnerRepStatus.setSelection(repStatus);
                spinnerPasture.setSelection(pasture);

            }
        });

        if(focusedView != null){
            focusedView.requestFocus();
            snackbar.setAnchorView(focusedView);
        } else {
            editTextTag.requestFocus();
            snackbar.setAnchorView(editTextTag);
        }

        snackbar.show();
    }

    public void saveValues(){
        String tag          = editTextTag.getText().toString();
        String name         = editTextName.getText().toString();
        LocalDate date      = bovBirth;
        String dateString   = editTextBovBirth.getText().toString();
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

        if(date == null || dateString.trim().isEmpty()){
            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_birthMissing);

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
        if(repStatus_string.equals(ReproductiveStatus.DRY.toString())){

            repStatus_enum = ReproductiveStatus.DRY;
        } else if (repStatus_string.equals(ReproductiveStatus.PREGNANT.toString())){

            repStatus_enum = ReproductiveStatus.PREGNANT;
        } else if(repStatus_string.equals(ReproductiveStatus.LACTATING.toString())){

            repStatus_enum = ReproductiveStatus.LACTATING;
        } else if(repStatus_string.equals(ReproductiveStatus.READY.toString())){

            repStatus_enum = ReproductiveStatus.READY;
        } else {
            AlertUtils.showAlert(this, R.string.reg_bov_toast_ReproductiveStatusMissing);

            spinnerRepStatus.requestFocus();
            return;
        }

        String animalBreed = spinnerBreed.getSelectedItem().toString();
        if (animalBreed == null){

            AlertUtils.showAlert(this, R.string.reg_bov_toast_text_warningSpinnerEmpty);
        }

        // Captura o ID do pasto selecionado
        int idPasture = 0;
        if (spinnerPasture.getSelectedItem() != null) {
            idPasture = (int) ((Pasture) spinnerPasture.getSelectedItem()).getId();
        }

        // Preservamos o idMother como 0 por enquanto ou o valor original se estiver editando
        int idMother = 0;
        if (mode == MODE_EDIT && bovineOriginal != null) {
            idMother = bovineOriginal.getIdMother();
        }

        Bovine bovine = new Bovine(tag, name, date, animalSex, animalBreed,
                    vaccinesList, repStatus_enum, idPasture, idMother);

        if(bovine.equals(bovineOriginal)){

            // não mudou nenhum campo, então cancela
            setResult(BovineActivity.RESULT_CANCELED);
            finish();
            return;
        }

        // Para passar resultados entre activities, é necessário usar um Intent;
        Intent intentResult = new Intent();

        GadiarioDatabase database = GadiarioDatabase.getInstance(this);

        if(mode == MODE_NEW){
            long newId = database.getBovinesDao().insert(bovine);

            // Se retornar inválido, alerta o erro
            if (newId <=0 ){
                AlertUtils.showAlert(this, R.string.common_alertDialog_dbErrorInsert);
                return;
            }

            // Se encontrar o id, adiciona o id nesse objeto para reaproveitamento
            bovine.setId(newId);

        } else {

            bovine.setId(bovineOriginal.getId());

            int updateInstances = database.getBovinesDao().update(bovine);

            if(updateInstances != 1 ){
                AlertUtils.showAlert(this,
                        R.string.common_alertDialog_dbErrorUpdate);
                return;
            }

        }

        saveLastBreed(spinnerBreed.getSelectedItemPosition());

        intentResult.putExtra(KEY_ID, bovine.getId());

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
}

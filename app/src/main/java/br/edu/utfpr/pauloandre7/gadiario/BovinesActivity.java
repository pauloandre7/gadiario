package br.edu.utfpr.pauloandre7.gadiario;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BovinesActivity extends AppCompatActivity {

    private ListView listViewBovines;
    private List<Bovine> listBovines;

    BovineAdapter adapterBovine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovines);

        setTitle(getString(R.string.bov_list_activityTitle));

        listViewBovines = findViewById(R.id.listViewBovines);

        listViewBovines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, // ListView
                                    View view, // Item da List
                                    int position, long id) {

                Bovine bovine = (Bovine) parent.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), getString(R.string.bov_toast_bovine_with_tag)+bovine.getTag()+ getString(R.string.bov_toast_bovClicked),
                        Toast.LENGTH_LONG).show();
            }
        });

        fillListBovines();

        registerForContextMenu(listViewBovines);
    }

    public void onClickAbout(){
        // Criação de intent explícita: digo onde estou e onde quero ir
        Intent intentOpen = new Intent(this, AboutActivity.class);

        // start é um método da activity. Passar o intent nesse método irá abrir a activity desejada
        startActivity(intentOpen);

    }

    // A tipagem em Intent é para um contrato genérico;
    // Se for usar activities mais complexas, é recomendado pelo prof. usar uma intent específica.
    ActivityResultLauncher<Intent> launcherRegBovine = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    /* Esse método é específico para esse launcher;
                    * O prof comentou em aula sobre a antiga prática de ter um método desse
                    * na classe da Activity que receberia os resultados de várias activities. */

                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();

                        // Para receber a resposta aqui, precisa construir Intent de resposta na
                        // Activity que vai retornar;

                        // abre os resultados em um bundle
                        if (intent == null){
                            throw new RuntimeException("Intent is null");
                        }

                        Bundle bundle = intent.getExtras();

                        if (bundle != null){
                            String tag = bundle.getString(BovineActivity.KEY_TAG);
                            String name = bundle.getString(BovineActivity.KEY_NAME);
                            String date = bundle.getString(BovineActivity.KEY_BIRTH);
                            String animalSex = bundle.getString(BovineActivity.KEY_SEX);
                            String animalBreed = bundle.getString(BovineActivity.KEY_BREED);
                            String[] vaccines = bundle.getStringArray(BovineActivity.KEY_VACCINES);

                            Bovine bovine;
                            if (vaccines != null){
                                List<String> vaccines_list = List.<String>of(vaccines);

                                bovine = new Bovine(tag, name, date,
                                                    AnimalSex.valueOf(animalSex), animalBreed,
                                                    vaccines_list);
                            } else {
                                bovine = new Bovine(tag, name, date,
                                        AnimalSex.valueOf(animalSex), animalBreed,
                                        null);
                            }

                            listBovines.add(bovine);

                            // Avisa o adapter que a base de dados dele foi modificada;
                            adapterBovine.notifyDataSetChanged();
                        }
                    }
                }
            });

    public void onClickRegisterBov(){

        Intent intentOpen = new Intent(this, BovineActivity.class);

        // Aqui utiliza-se o launcher configurado para abrir a activity BovineActivity;
        // Usando o launcher, ele irá gerir a resposta e chamar o contrato onActivityResult;
        launcherRegBovine.launch(intentOpen);
    }

    private void fillListBovines(){
        /* ANTIGA PRÁTICA DE PEGAR VALORES DE ARRAY NO XML
        String[] bovines_tags       = getResources().getStringArray(R.array.bovines_Tags);
        String[] bovines_names      = getResources().getStringArray(R.array.bovines_names);
        String[] bovines_births     = getResources().getStringArray(R.array.bovines_births);
        int[] bovines_sex           = getResources().getIntArray(R.array.bovines_sex);
        String[] bovines_breeds      = getResources().getStringArray(R.array.bovines_breeds);
        */

        listBovines = new ArrayList<>();

        /* ANTIGA PRÁTICA DE CRIAR OBJETOS A PARTIR DOS VALORES DE ARRAY
        Bovine bovine;
        AnimalSex animalSex;
        AnimalSex[] sex_values = AnimalSex.values();
        List<String> vaccines_list = new ArrayList<>();

        vaccines_list.add("Salmonelose");
        vaccines_list.add("Intranasais");

        for (int i = 0; i < bovines_tags.length; i++){

            animalSex = sex_values[bovines_sex[i]];

            bovine = new Bovine(bovines_tags[i],
                    bovines_names[i],
                    bovines_births[i],
                    animalSex,
                    bovines_breeds[i],
                    vaccines_list
                    );

            listBovines.add(bovine);
        }
        */

        adapterBovine = new BovineAdapter(this, listBovines);

        listViewBovines.setAdapter(adapterBovine);
    }

    // Criar e infla o menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bovines_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemRegister){
            onClickRegisterBov();
            return true;
        } else if (idMenuItem == R.id.menuItemAbout){
            onClickAbout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // infla o context menu
        getMenuInflater().inflate(R.menu.bovines_item_selected, menu);
    }

    public void editBovine(int position){

    }

    public void deleteBovine(int position){
        listBovines.remove(position);

        adapterBovine.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        // Só funciona em listview;
        // Pega a posição do item clicado;
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (idMenuItem == R.id.contextMenuItem_Edit){

            editBovine(info.position);
            return true;
        } else if (idMenuItem == R.id.contextMenuItem_Delete){
            deleteBovine(info.position);
            return true;
        } else{
            return super.onContextItemSelected(item);
        }
    }
}
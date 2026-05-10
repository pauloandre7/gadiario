package br.edu.utfpr.pauloandre7.gadiario.ui.pasture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.ui.bovine.BovineActivity;
import br.edu.utfpr.pauloandre7.gadiario.ui.main.AboutActivity;

public class PastureListingActivity extends AppCompatActivity {

    private ListView listViewPastures;
    private List<Pasture> listPastures;
    private int positionSelected = -1;

    private View viewSelected;
    private Drawable backgroundView;

    PastureAdapter pastureAdapter;

    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasture_listing);

        setTitle(getString(R.string.past_list_Title));

        listViewPastures = findViewById(R.id.listViewPastures);

        // Method para adicionar Listener de click na listview
        listViewPastures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // se o actionmode já estiver aberto, não abre denovo
                if(actionMode != null){
                    return false;
                }

                // esses atributos serão usados em métodos de crud abaixo
                positionSelected = position;
                viewSelected = view;
                backgroundView = viewSelected.getBackground();

                viewSelected.setBackgroundColor(Color.LTGRAY);

                // desativa a view pra evitar novos clicks
                listViewPastures.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        fillListPastures();
        registerForContextMenu(listViewPastures);
    }

    private void fillListPastures(){
        listPastures = new ArrayList<Pasture>();

        pastureAdapter = new PastureAdapter(this, listPastures);
        listViewPastures.setAdapter(pastureAdapter);
    }

    public void onClickAbout(){
        // Criação de intent explícita: digo onde estou e onde quero ir
        Intent intentOpen = new Intent(this, AboutActivity.class);

        // start é um méthod da activity. Passar o intent nesse méthod irá abrir a activity desejada
        startActivity(intentOpen);

    }

    /****************************************** CRUD ABAIXO ******************************************/

    public void deletePasture(){
        listPastures.remove(positionSelected);

        pastureAdapter.notifyDataSetChanged();
    }

    // launcher e method para adicionar novo pasto
    ActivityResultLauncher<Intent> launcherRegPasture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK){

                        Intent intent = result.getData();

                        if(intent == null){
                            throw new RuntimeException("Intent is null");
                        }

                        Bundle bundle = intent.getExtras();

                        if(bundle != null){
                            String name = bundle.getString(PastureActivity.KEY_NAME);
                            String description = bundle.getString(PastureActivity.KEY_DESCRIPTION);

                            Pasture pasture = new Pasture(name, description);

                            listPastures.add(pasture);
                            Collections.sort(listPastures, Pasture.ascendingNameSort);

                            pastureAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );

    public void onClickRegisterPasture(){
        Intent intentOpen = new Intent(this, PastureActivity.class);
        intentOpen.putExtra(PastureActivity.KEY_MODE, PastureActivity.MODE_NEW);

        // O launcher é quem vai gerenciar essa intent
        launcherRegPasture.launch(intentOpen);
    }

    // launcher e method para editar o pasto
    ActivityResultLauncher<Intent> launcherEditPasture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();

                        if(intent == null){
                            throw new RuntimeException("Intent is null");
                        }

                        Bundle bundle = intent.getExtras();

                        if(bundle != null){
                            String name = bundle.getString(PastureActivity.KEY_NAME);
                            String description = bundle.getString(PastureActivity.KEY_DESCRIPTION);

                            Pasture pasture = listPastures.get(positionSelected);

                            pasture.setName(name);
                            pasture.setDescription(description);

                            Collections.sort(listPastures, Pasture.ascendingNameSort);

                            pastureAdapter.notifyDataSetChanged();
                        }
                    }
                    // zera a variável de posição
                    positionSelected = -1;

                    if(actionMode != null){
                        actionMode.finish();
                    }
                }
            }
    );

    public void editPasture(){

        Pasture pasture = listPastures.get(positionSelected);

        Intent intentOpen = new Intent(this, PastureActivity.class);
        intentOpen.putExtra(PastureActivity.KEY_MODE, PastureActivity.MODE_EDIT);

        intentOpen.putExtra(PastureActivity.KEY_NAME, pasture.getName());
        intentOpen.putExtra(PastureActivity.KEY_DESCRIPTION, pasture.getDescription());

        launcherEditPasture.launch(intentOpen);
    }

    // Criar e inflar o menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_options, menu);
        return true;
    }

    // tratamento de click do menu options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idItem = item.getItemId();

        if(idItem == R.id.menuItemRegister){
            onClickRegisterPasture();
            return true;
        } else if (idItem == R.id.menuItemAbout){
            onClickAbout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    // Implementar ContextMenu e Callback para o gerenciar interações na list
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.list_item_selected, menu);
    }

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();

            inflater.inflate(R.menu.list_item_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idItem = item.getItemId();

            if( idItem == R.id.contextMenuItem_Edit ){

                editPasture();
                return true;
            } else if( idItem== R.id.contextMenuItem_Delete ){

                deletePasture();
                return true;
            } else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(viewSelected != null){
                viewSelected.setBackground(backgroundView);
            }

            // destroi objetos armazenados
            actionMode = null;
            viewSelected = null;
            backgroundView = null;

            listViewPastures.setEnabled(true);
        }
    };

}
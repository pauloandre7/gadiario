package br.edu.utfpr.pauloandre7.gadiario.ui.bovine;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.AnimalSex;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.ReproductiveStatus;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.ui.main.AboutActivity;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class BovinesActivity extends AppCompatActivity {

    private ListView listViewBovines;
    private List<Bovine> listBovines;

    private int positionSelected = -1;

    // usado para o menu de ação contextual para mudar cor de fundo do item selecionado
    private View viewSelecionada;
    private Drawable drawableSelecionado;

    BovineAdapter adapterBovine;

    // arquivo de sharedPreferences setado na classe principal
    public static final String PREFERENCES_FILE = "br.edu.utfpr.pauloandre7.gadiario.PREFERENCES";

    private static final boolean DEFAULT_INITIAL_ASCENDING_SORT = true;

    private boolean sortingAscending = DEFAULT_INITIAL_ASCENDING_SORT;
    public static final String KEY_ASCENDING_SORT = "ASCENDING_SORT";

    private MenuItem menuItemSorting;

    // usado para o menu de ação contextual
    private ActionMode actionMode;

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
            }
        });

        // Method para tratar click long na list view
        listViewBovines.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Se o action mode já estiver aberto, não abre mais.
                if (actionMode != null){
                    return false;
                }

                positionSelected = position;

                viewSelecionada = view;
                drawableSelecionado = viewSelecionada.getBackground();

                // muda a cor de fundo para um cinza claro
                viewSelecionada.setBackgroundColor(getColor(R.color.itemSelected));

                // desativa a view para que não seja clicada
                listViewBovines.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        readPreferences();

        fillListBovines();

        registerForContextMenu(listViewBovines);
    }

    public void onClickAbout(){
        // Criação de intent explícita: digo onde estou e onde quero ir
        Intent intentOpen = new Intent(this, AboutActivity.class);

        // start é um méthod da activity. Passar o intent nesse méthod irá abrir a activity desejada
        startActivity(intentOpen);

    }

    // A tipagem em Intent é para um contrato genérico;
    // Se for usar activities mais complexas, é recomendado pelo prof. usar uma intent específica.
    ActivityResultLauncher<Intent> launcherRegBovine = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    /* Esse méthod é específico para esse launcher;
                    * O prof comentou em aula sobre a antiga prática de ter um méthod desse
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

                            // agora só retorna o id long
                            long id = bundle.getLong(BovineActivity.KEY_ID);

                            GadiarioDatabase database = GadiarioDatabase.getInstance(BovinesActivity.this);

                            // já pega no banco pelo ID retornado e já adiciona na lista
                            listBovines.add(database.getBovinesDao().queryById(id));

                            sortList();
                        }
                    }
                }
            });

    public void onClickRegisterBov(){

        Intent intentOpen = new Intent(this, BovineActivity.class);
        intentOpen.putExtra(BovineActivity.KEY_MODE, BovineActivity.MODE_NEW);


        // Aqui utiliza-se o launcher configurado para abrir a activity BovineActivity;
        // Usando o launcher, ele irá gerir a resposta e chamar o contrato onActivityResult;
        launcherRegBovine.launch(intentOpen);
    }

    private void fillListBovines(){

        GadiarioDatabase database = GadiarioDatabase.getInstance(this);

        // agora basta verificar a preferência de ordenação e chamar a query corresponde.
        // O objeto retornado já é uma List
        if(sortingAscending){
            listBovines = database.getBovinesDao().queryAllAscending();
        } else {
            listBovines = database.getBovinesDao().queryAllDownward();
        }

        adapterBovine = new BovineAdapter(this, listBovines);

        listViewBovines.setAdapter(adapterBovine);
    }

    // Criar e infla o menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_options, menu);

        menuItemSorting = menu.findItem(R.id.menuItemSorting);
        return true;
    }

    // Utiliza esse method toda vez que o menu for exibido.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateSortingIcon();

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

        } else if(idMenuItem == R.id.menuItemSorting){
            saveSortingPreference(!sortingAscending);
            updateSortingIcon();
            sortList();
            return true;

        } else if(idMenuItem == R.id.menuItemReset){

            confirmResetPreferences();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // infla o context menu
        getMenuInflater().inflate(R.menu.list_item_selected, menu);
    }

    ActivityResultLauncher<Intent> launcherEditBovine = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();

                        if (intent == null){
                            throw new RuntimeException("Intent is null");
                        }

                        Bundle bundle = intent.getExtras();

                        if (bundle != null){
                            final Bovine originalBovine = listBovines.get(positionSelected);

                            long id = bundle.getLong(BovineActivity.KEY_ID);

                            final GadiarioDatabase database = GadiarioDatabase.getInstance(BovinesActivity.this);

                            // agora a tela de cadastro salva no banco de dados sozinha, então pegar do banco
                            // é trazer o objeto já alterado
                            final Bovine bovineEdited = database.getBovinesDao().queryById(id);

                            listBovines.set(positionSelected, bovineEdited);

                            sortList();

                            final ConstraintLayout constraintLayout = findViewById(R.id.main);

                            Snackbar snackbar = Snackbar.make( constraintLayout,
                                    R.string.common_snackbar_dataUpdateDone,
                                    Snackbar.LENGTH_LONG
                            );

                            snackbar.setAction(R.string.common_undo, new View.OnClickListener(){

                                @Override
                                public void onClick(View view){

                                    // Ao clicar em undo, então é necessário fazer update no banco novamente
                                    int updatedInstances = database.getBovinesDao().update(originalBovine);

                                    if(updatedInstances != 1 ){
                                        AlertUtils.showAlert(BovinesActivity.this,
                                                R.string.common_alertDialog_dbErrorUpdate);
                                        return;
                                    }

                                    // E nesse caso quem será removido é o objeto editado
                                    listBovines.remove(bovineEdited);
                                    listBovines.add(originalBovine);

                                    sortList();
                                }
                            });

                            snackbar.show();
                        }
                    }
                    positionSelected = -1;

                    // fecha o menu se ele voltar e tiver um menu aberto
                    if(actionMode != null){
                        actionMode.finish();
                    }
                }
            });

    private void editBovine(){

        Bovine bovine = listBovines.get(positionSelected);

        // prepara a intent de abertura com o modo de uso da classe.
        Intent intentOpen = new Intent(this, BovineActivity.class);
        intentOpen.putExtra(BovineActivity.KEY_MODE, BovineActivity.MODE_EDIT);

        // passa apenas o ID para as operações no banco
        intentOpen.putExtra(BovineActivity.KEY_ID, bovine.getId());

        launcherEditBovine.launch(intentOpen);
    }

    private void deleteBovine(){
        Bovine bovine = listBovines.get(positionSelected);

        String message = getString(R.string.common_confirmation_wantDelete, bovine.getName());

        DialogInterface.OnClickListener listenerYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // como o context foi passado, podemos tratar a lista dentro desse Listener como se
                // fosse o méthod interno de BovinesACtivity.

                // Apenas adiciona o delete do dabase também, mas sem excluir a ideia de apagar da lista em memória
                GadiarioDatabase database = GadiarioDatabase.getInstance(BovinesActivity.this);

                if (database.getBovinesDao().delete(bovine) != 1 ){
                    AlertUtils.showAlert(BovinesActivity.this,
                            R.string.common_alertDialog_dbErrorDelete);
                    return;
                }


                listBovines.remove(positionSelected);

                adapterBovine.notifyDataSetChanged();

                // CORREÇÃO: Verifica se o actionMode não é nulo antes de finalizar
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        };

        AlertUtils.confirmAction(this, message, listenerYes, null);
    }

    // O callback cuida da gestão do menu de ação contextual
    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Pega o inflador fornecido pela classe;
            MenuInflater inflater = mode.getMenuInflater();

            // infla o menu e, assim, exibe na activity
            inflater.inflate(R.menu.list_item_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int idMenuItem = item.getItemId();

            // Só funciona em listview;
            // Pega a posição do item clicado;
            AdapterView.AdapterContextMenuInfo info;
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            if (idMenuItem == R.id.contextMenuItem_Edit){

                editBovine();
                return true;
            } else if (idMenuItem == R.id.contextMenuItem_Delete){
                deleteBovine();
                // O actionMode será finalizado dentro do deleteBovine se confirmado
                return true;
            } else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if(viewSelecionada != null){
                // volta o background original
                viewSelecionada.setBackground(drawableSelecionado);
            }

            // destroi os objetos armazenados
            actionMode = null;
            viewSelecionada = null;
            drawableSelecionado = null;

            // ativa a listview;
            listViewBovines.setEnabled(true);
        }
    };

    private void readPreferences(){
        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        sortingAscending = shared.getBoolean(KEY_ASCENDING_SORT, sortingAscending);
    }

    public void saveSortingPreference(boolean newValue){

        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_ASCENDING_SORT, newValue);

        editor.commit();

        sortingAscending = newValue;
    }

    private void sortList(){

        if(sortingAscending){
            Collections.sort(listBovines, Bovine.ascendingTagSort);
        } else {
            Collections.sort(listBovines, Bovine.descendingTagSort);
        }

        adapterBovine.notifyDataSetChanged();
    }

    private void updateSortingIcon(){

        if(sortingAscending){
            menuItemSorting.setIcon(R.drawable.ic_action_ascending_order);
        } else {
            menuItemSorting.setIcon(R.drawable.ic_action_descending_order);
        }
    }

    private void confirmResetPreferences(){

        DialogInterface.OnClickListener listenerYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPreferences();
                updateSortingIcon();
                sortList();


                Toast.makeText(BovinesActivity.this,
                                R.string.common_toast_resetMessage,
                                Toast.LENGTH_LONG)
                        .show();
            }
        };

        AlertUtils.confirmAction(this, getString(R.string.common_dialog_deleteConfirmation),
                listenerYes, null);

    }

    private void resetPreferences(){
        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        // limpa o shared inteiro
        editor.clear();
        editor.commit();

        sortingAscending = DEFAULT_INITIAL_ASCENDING_SORT;
    }
}

package br.edu.utfpr.pauloandre7.gadiario.ui.pasture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Collections;
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.ui.bovine.BovinesActivity;
import br.edu.utfpr.pauloandre7.gadiario.ui.main.AboutActivity;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class PastureListingActivity extends AppCompatActivity {

    private ListView listViewPastures;
    private List<Pasture> listPastures;
    private int positionSelected = -1;

    private View viewSelected;
    private Drawable backgroundView;

    PastureAdapter pastureAdapter;

    private ActionMode actionMode;

    private boolean sortingAscending = true;
    public static final String KEY_ASCENDING_SORT_PASTURE = "ASCENDING_SORT_PASTURE";

    private MenuItem menuItemSorting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasture_listing);

        setTitle(getString(R.string.past_list_Title));

        listViewPastures = findViewById(R.id.listViewPastures);

        listViewPastures.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(actionMode != null){
                    return false;
                }

                positionSelected = position;
                viewSelected = view;
                backgroundView = viewSelected.getBackground();

                viewSelected.setBackgroundColor(getColor(R.color.itemSelected));

                listViewPastures.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        readPreferences();
        fillListPastures();
        registerForContextMenu(listViewPastures);
    }

    private void fillListPastures(){
        GadiarioDatabase database = GadiarioDatabase.getInstance(this);

        if(sortingAscending){
            listPastures = database.getPastureDao().queryAllAscending();
        } else {
            listPastures = database.getPastureDao().queryAllDownward();
        }

        pastureAdapter = new PastureAdapter(this, listPastures);
        listViewPastures.setAdapter(pastureAdapter);
    }

    public void onClickAbout(){
        Intent intentOpen = new Intent(this, AboutActivity.class);
        startActivity(intentOpen);
    }

    /****************************************** CRUD ABAIXO ******************************************/

    public void deletePasture(){
        Pasture pasture = listPastures.get(positionSelected);

        String message = getString(R.string.common_confirmation_wantDelete, pasture.getName());

        DialogInterface.OnClickListener listenerYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GadiarioDatabase database = GadiarioDatabase.getInstance(PastureListingActivity.this);

                if (database.getPastureDao().delete(pasture) != 1 ){
                    AlertUtils.showAlert(PastureListingActivity.this,
                            R.string.common_alertDialog_dbErrorDelete);
                    return;
                }

                listPastures.remove(positionSelected);
                pastureAdapter.notifyDataSetChanged();

                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        };

        AlertUtils.confirmAction(this, message, listenerYes, null);
    }

    ActivityResultLauncher<Intent> launcherRegPasture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK){

                        Intent intent = result.getData();
                        if(intent == null) return;

                        Bundle bundle = intent.getExtras();

                        if(bundle != null){
                            long id = bundle.getLong(PastureActivity.KEY_ID);

                            GadiarioDatabase database = GadiarioDatabase.getInstance(PastureListingActivity.this);
                            listPastures.add(database.getPastureDao().queryById(id));
                            
                            sortList();
                        }
                    }
                }
            }
    );

    public void onClickRegisterPasture(){
        Intent intentOpen = new Intent(this, PastureActivity.class);
        intentOpen.putExtra(PastureActivity.KEY_MODE, PastureActivity.MODE_NEW);
        launcherRegPasture.launch(intentOpen);
    }

    ActivityResultLauncher<Intent> launcherEditPasture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if(intent == null) return;

                        Bundle bundle = intent.getExtras();

                        if(bundle != null){
                            final Pasture originalPasture = listPastures.get(positionSelected);
                            long id = bundle.getLong(PastureActivity.KEY_ID);

                            final GadiarioDatabase database = GadiarioDatabase.getInstance(PastureListingActivity.this);
                            final Pasture pastureEdited = database.getPastureDao().queryById(id);

                            listPastures.set(positionSelected, pastureEdited);
                            sortList();

                            final ConstraintLayout constraintLayout = findViewById(R.id.main);

                            Snackbar snackbar = Snackbar.make(constraintLayout,
                                    R.string.common_snackbar_dataUpdateDone,
                                    Snackbar.LENGTH_LONG
                            );

                            snackbar.setAction(R.string.common_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int updatedInstances = database.getPastureDao().update(originalPasture);

                                    if(updatedInstances != 1 ){
                                        AlertUtils.showAlert(PastureListingActivity.this,
                                                R.string.common_alertDialog_dbErrorUpdate);
                                        return;
                                    }

                                    listPastures.remove(pastureEdited);
                                    listPastures.add(originalPasture);
                                    sortList();
                                }
                            });

                            snackbar.show();
                        }
                    }
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
        intentOpen.putExtra(PastureActivity.KEY_ID, pasture.getId());

        launcherEditPasture.launch(intentOpen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_options, menu);
        menuItemSorting = menu.findItem(R.id.menuItemSorting);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateSortingIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idItem = item.getItemId();

        if(idItem == R.id.menuItemRegister){
            onClickRegisterPasture();
            return true;
        } else if (idItem == R.id.menuItemAbout){
            onClickAbout();
            return true;
        } else if(idItem == R.id.menuItemSorting){
            saveSortingPreference(!sortingAscending);
            updateSortingIcon();
            sortList();
            return true;
        } else if(idItem == R.id.menuItemReset){
            confirmResetPreferences();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

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
            actionMode = null;
            viewSelected = null;
            backgroundView = null;
            listViewPastures.setEnabled(true);
        }
    };

    private void readPreferences(){
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        sortingAscending = shared.getBoolean(KEY_ASCENDING_SORT_PASTURE, true);
    }

    public void saveSortingPreference(boolean newValue){
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_ASCENDING_SORT_PASTURE, newValue);
        editor.commit();
        sortingAscending = newValue;
    }

    private void sortList(){
        if(sortingAscending){
            Collections.sort(listPastures, Pasture.ascendingNameSort);
        } else {
            Collections.sort(listPastures, Pasture.descendingNameSort);
        }
        pastureAdapter.notifyDataSetChanged();
    }

    private void updateSortingIcon(){
        if(menuItemSorting == null) return;
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
                Toast.makeText(PastureListingActivity.this, R.string.common_toast_resetMessage, Toast.LENGTH_LONG).show();
            }
        };
        AlertUtils.confirmAction(this, getString(R.string.common_dialog_deleteConfirmation), listenerYes, null);
    }

    private void resetPreferences(){
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(KEY_ASCENDING_SORT_PASTURE);
        editor.commit();
        sortingAscending = true;
    }
}

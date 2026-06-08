package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.ui.bovine.BovinesActivity;
import br.edu.utfpr.pauloandre7.gadiario.ui.main.AboutActivity;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class EventsActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private List<Event> listEvents;
    private EventAdapter adapterEvent;

    private int positionSelected = -1;
    private View viewSelecionada;
    private Drawable drawableSelecionado;
    private ActionMode actionMode;

    private boolean sortingAscending = false; // Por padrão, eventos mais recentes primeiro? (DESC)
    public static final String KEY_ASCENDING_SORT_EVENT = "ASCENDING_SORT_EVENT";

    private MenuItem menuItemSorting;

    // Atributo para salvar o estado do bovino antes da edição para o métod UNDO
    private Bovine bovineBeforeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        setTitle(getString(R.string.event_reg_title));

        listViewEvents = findViewById(R.id.listViewEvents);

        listViewEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null) {
                    return false;
                }

                positionSelected = position;
                viewSelecionada = view;
                drawableSelecionado = viewSelecionada.getBackground();

                viewSelecionada.setBackgroundColor(getColor(R.color.itemSelected));
                listViewEvents.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);
                return true;
            }
        });

        readPreferences();
        fillListEvents();
    }

    private void fillListEvents() {
        GadiarioDatabase database = GadiarioDatabase.getInstance(this);

        if (sortingAscending) {
            listEvents = database.getEventDao().queryAllAscending();
        } else {
            listEvents = database.getEventDao().queryAllDownward();
        }

        adapterEvent = new EventAdapter(this, listEvents);
        listViewEvents.setAdapter(adapterEvent);
    }

    // Launcher para Cadastro de Evento
    ActivityResultLauncher<Intent> launcherRegEvent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Bundle bundle = intent.getExtras();
                            if (bundle != null) {
                                long id = bundle.getLong(EventActivity.KEY_ID);

                                GadiarioDatabase database = GadiarioDatabase.getInstance(EventsActivity.this);
                                listEvents.add(database.getEventDao().queryById(id));
                                sortList();
                            }
                        }
                    }
                }
            });

    // Launcher para Edição de Evento
    ActivityResultLauncher<Intent> launcherEditEvent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Bundle bundle = intent.getExtras();
                            if (bundle != null) {
                                final Event originalEvent = listEvents.get(positionSelected);
                                long id = bundle.getLong(EventActivity.KEY_ID);

                                final GadiarioDatabase database = GadiarioDatabase.getInstance(EventsActivity.this);
                                final Event eventEdited = database.getEventDao().queryById(id);

                                // Guarda o estado original do bovino para o UNDO
                                final Bovine originalBovineState = bovineBeforeEdit;

                                listEvents.set(positionSelected, eventEdited);
                                sortList();

                                final ConstraintLayout constraintLayout = findViewById(R.id.events_main);
                                Snackbar snackbar = Snackbar.make(constraintLayout, R.string.common_snackbar_dataUpdateDone, Snackbar.LENGTH_LONG);
                                snackbar.setAction(R.string.common_undo, v -> {
                                    int updated = database.getEventDao().update(originalEvent);
                                    if (updated == 1) {
                                        // Reverte o estado do bovino para o que era antes da edição
                                        if (originalBovineState != null) {
                                            database.getBovinesDao().update(originalBovineState);
                                        }

                                        listEvents.remove(eventEdited);
                                        listEvents.add(originalEvent);
                                        sortList();
                                    }
                                });
                                snackbar.show();
                            }
                        }
                    }
                    positionSelected = -1;
                    bovineBeforeEdit = null;
                    if (actionMode != null) actionMode.finish();
                }
            });

    private void editEvent() {
        Event event = listEvents.get(positionSelected);

        // Antes de abrir a tela de edição, salva o estado atual do bovino para possibilitar o UNDO
        GadiarioDatabase database = GadiarioDatabase.getInstance(this);
        bovineBeforeEdit = database.getBovinesDao().queryById(event.getIdBovine());

        Intent intentOpen = new Intent(this, EventActivity.class);
        intentOpen.putExtra(EventActivity.KEY_MODE, EventActivity.MODE_EDIT);
        intentOpen.putExtra(EventActivity.KEY_ID, event.getId());
        launcherEditEvent.launch(intentOpen);
    }

    private void deleteEvent() {
        Event event = listEvents.get(positionSelected);
        String message = getString(R.string.common_dialog_deleteConfirmation);
        DialogInterface.OnClickListener listenerYes = (dialog, which) -> {
            GadiarioDatabase database = GadiarioDatabase.getInstance(EventsActivity.this);
            if (database.getEventDao().delete(event) == 1) {
                listEvents.remove(positionSelected);
                adapterEvent.notifyDataSetChanged();
            } else {
                AlertUtils.showAlert(EventsActivity.this, R.string.common_alertDialog_dbErrorDelete);
            }
            if (actionMode != null) actionMode.finish();
        };
        AlertUtils.confirmAction(this, message, listenerYes, null);
    }

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.list_item_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.contextMenuItem_Edit) {
                editEvent();
                return true;
            } else if (id == R.id.contextMenuItem_Delete) {
                deleteEvent();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null) viewSelecionada.setBackground(drawableSelecionado);
            actionMode = null;
            viewSelecionada = null;
            drawableSelecionado = null;
            listViewEvents.setEnabled(true);
        }
    };

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
        int id = item.getItemId();
        if (id == R.id.menuItemRegister) {
            Intent intentOpen = new Intent(this, EventActivity.class);
            intentOpen.putExtra(EventActivity.KEY_MODE, EventActivity.MODE_NEW);
            launcherRegEvent.launch(intentOpen);
            return true;
        } else if (id == R.id.menuItemAbout) {
            Intent intentOpen = new Intent(this, AboutActivity.class);
            startActivity(intentOpen);
            return true;
        } else if (id == R.id.menuItemSorting) {
            saveSortingPreference(!sortingAscending);
            updateSortingIcon();
            sortList();
            return true;
        } else if (id == R.id.menuItemReset) {
            confirmResetPreferences();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readPreferences() {
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        sortingAscending = shared.getBoolean(KEY_ASCENDING_SORT_EVENT, false);
    }

    public void saveSortingPreference(boolean newValue) {
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_ASCENDING_SORT_EVENT, newValue);
        editor.apply();
        sortingAscending = newValue;
    }

    private void sortList() {
        if (sortingAscending) {
            Collections.sort(listEvents, Event.ascendingDateSort);
        } else {
            Collections.sort(listEvents, Event.descendingDateSort);
        }
        adapterEvent.notifyDataSetChanged();
    }

    private void updateSortingIcon() {
        if (menuItemSorting == null) return;
        if (sortingAscending) {
            menuItemSorting.setIcon(R.drawable.ic_action_ascending_order);
        } else {
            menuItemSorting.setIcon(R.drawable.ic_action_descending_order);
        }
    }

    private void confirmResetPreferences() {
        DialogInterface.OnClickListener listenerYes = (dialog, which) -> {
            resetPreferences();
            updateSortingIcon();
            sortList();
            Toast.makeText(this, R.string.common_toast_resetMessage, Toast.LENGTH_LONG).show();
        };
        AlertUtils.confirmAction(this, getString(R.string.common_dialog_deleteConfirmation), listenerYes, null);
    }

    private void resetPreferences() {
        SharedPreferences shared = getSharedPreferences(BovinesActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(KEY_ASCENDING_SORT_EVENT);
        editor.apply();
        sortingAscending = false;
    }
}

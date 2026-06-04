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
import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.models.EventType;
import br.edu.utfpr.pauloandre7.gadiario.ui.bovine.BovinesActivity;
import br.edu.utfpr.pauloandre7.gadiario.utils.AlertUtils;

public class EventsActivity extends AppCompatActivity {

    private ListView listViewEvents;
    private List<Event> listEvents;
    private EventAdapter adapterEvent;

    private int positionSelected = -1;
    private View viewSelecionada;
    private Drawable drawableSelecionado;
    private ActionMode actionMode;

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

        fillListEvents();
    }

    private void fillListEvents() {
        listEvents = new ArrayList<>();
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
                                int idBovine = bundle.getInt(EventActivity.KEY_BOVINE_ID);
                                String typeStr = bundle.getString(EventActivity.KEY_TYPE);
                                String date = bundle.getString(EventActivity.KEY_DATE);
                                int qtyCalves = bundle.getInt(EventActivity.KEY_QTY_CALVES);
                                int idPastOrigin = bundle.getInt(EventActivity.KEY_PASTURE_ORIGIN);
                                int idPastDest = bundle.getInt(EventActivity.KEY_PASTURE_DESTINATION);
                                String observations = bundle.getString(EventActivity.KEY_OBSERVATIONS);

                                Event event = new Event(0, idBovine, EventType.valueOf(typeStr), qtyCalves, idPastOrigin, idPastDest, date, observations);
                                listEvents.add(event);
                                adapterEvent.notifyDataSetChanged();
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
                                final Event event = listEvents.get(positionSelected);
                                final Event cloneEvent;
                                try {
                                    cloneEvent = (Event) event.clone();
                                } catch (CloneNotSupportedException e) {
                                    return;
                                }

                                event.setIdBovine(bundle.getInt(EventActivity.KEY_BOVINE_ID));
                                event.setType(EventType.valueOf(bundle.getString(EventActivity.KEY_TYPE)));
                                event.setDate(bundle.getString(EventActivity.KEY_DATE));
                                event.setQtyCalves(bundle.getInt(EventActivity.KEY_QTY_CALVES));
                                event.setIdPastureOrigin(bundle.getInt(EventActivity.KEY_PASTURE_ORIGIN));
                                event.setIdPastureDestination(bundle.getInt(EventActivity.KEY_PASTURE_DESTINATION));
                                event.setObservation(bundle.getString(EventActivity.KEY_OBSERVATIONS));

                                adapterEvent.notifyDataSetChanged();

                                final ConstraintLayout constraintLayout = findViewById(R.id.events_main);
                                Snackbar snackbar = Snackbar.make(constraintLayout, R.string.common_snackbar_dataUpdateDone, Snackbar.LENGTH_LONG);
                                snackbar.setAction(R.string.common_undo, v -> {
                                    listEvents.set(listEvents.indexOf(event), cloneEvent);
                                    adapterEvent.notifyDataSetChanged();
                                });
                                snackbar.setAnchorView(listViewEvents);
                                snackbar.show();
                            }
                        }
                    }
                    positionSelected = -1;
                    if (actionMode != null) actionMode.finish();
                }
            });

    private void editEvent() {
        Event event = listEvents.get(positionSelected);
        Intent intentOpen = new Intent(this, EventActivity.class);
        intentOpen.putExtra(EventActivity.KEY_MODE, EventActivity.MODE_EDIT);
        intentOpen.putExtra(EventActivity.KEY_ID, event.getId());
        intentOpen.putExtra(EventActivity.KEY_BOVINE_ID, event.getIdBovine());
        intentOpen.putExtra(EventActivity.KEY_TYPE, event.getType().toString());
        intentOpen.putExtra(EventActivity.KEY_DATE, event.getDate());
        intentOpen.putExtra(EventActivity.KEY_QTY_CALVES, event.getQtyCalves());
        intentOpen.putExtra(EventActivity.KEY_PASTURE_ORIGIN, event.getIdPastureOrigin());
        intentOpen.putExtra(EventActivity.KEY_PASTURE_DESTINATION, event.getIdPastureDestination());
        intentOpen.putExtra(EventActivity.KEY_OBSERVATIONS, event.getObservation());
        launcherEditEvent.launch(intentOpen);
    }

    private void deleteEvent() {
        String message = getString(R.string.common_dialog_deleteConfirmation);
        DialogInterface.OnClickListener listenerYes = (dialog, which) -> {
            listEvents.remove(positionSelected);
            adapterEvent.notifyDataSetChanged();
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
        }
        return super.onOptionsItemSelected(item);
    }
}

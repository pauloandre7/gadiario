package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.utils.LocalDateUtils;

public class EventAdapter extends BaseAdapter {

    private Context context;
    private List<Event> listEvents;

    public EventAdapter(Context context, List<Event> listEvents) {
        this.context = context;
        this.listEvents = listEvents;
    }

    public static class EventHolder {
        public TextView textViewType;
        public TextView textViewDate;
        public TextView textViewBovineValue;
        public TextView textViewDetailValue;
    }

    @Override
    public int getCount() {
        return listEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return listEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_list_events, parent, false);

            holder = new EventHolder();
            holder.textViewType = convertView.findViewById(R.id.line_event_tvType);
            holder.textViewDate = convertView.findViewById(R.id.line_event_tvDate);
            holder.textViewBovineValue = convertView.findViewById(R.id.line_event_tvBovineValue);
            holder.textViewDetailValue = convertView.findViewById(R.id.line_event_tvDetailValue);

            convertView.setTag(holder);
        } else {
            holder = (EventHolder) convertView.getTag();
        }

        Event event = listEvents.get(position);

        holder.textViewType.setText(event.getType().toString());
        
        holder.textViewDate.setText(LocalDateUtils.formatLocalDate(event.getDate()));
        
        // Busca o bovino no banco para exibir TAG e Nome
        GadiarioDatabase database = GadiarioDatabase.getInstance(context);
        Bovine bovine = database.getBovinesDao().queryById(event.getIdBovine());
        
        if (bovine != null) {
            String bovineDisplay = bovine.getTag() + " - " + bovine.getName();
            holder.textViewBovineValue.setText(bovineDisplay);
        } else {
            holder.textViewBovineValue.setText("ID Bovine: " + event.getIdBovine());
        }

        // Lógica para preencher o detalhe baseado no tipo de evento
        String details = "";
        if (event.getType() != null) {
            switch (event.getType()) {
                case CALVING:
                    details = context.getString(R.string.event_label_qty_calves) + ": " + event.getQtyCalves();
                    break;
                case MOVEMENT:
                    details = "Pasto " + event.getIdPastureOrigin() + " -> Pasto " + event.getIdPastureDestination();
                    break;
                default:
                    details = event.getObservation();
                    break;
            }
        }
        holder.textViewDetailValue.setText(details);

        return convertView;
    }
}

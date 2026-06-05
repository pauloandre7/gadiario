package br.edu.utfpr.pauloandre7.gadiario.ui.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Event;

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
        holder.textViewDate.setText(event.getDate());
        
        // todo: Verificar - Buscar o nome/tag do bovino real através do ID futuramente
        holder.textViewBovineValue.setText("ID Bovine: " + event.getIdBovine());

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

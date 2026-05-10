package br.edu.utfpr.pauloandre7.gadiario.ui.pasture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.pauloandre7.gadiario.R;
import br.edu.utfpr.pauloandre7.gadiario.models.Bovine;
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;

public class PastureAdapter extends BaseAdapter {

    private Context context;
    private List<Pasture> listPastures;

    public PastureAdapter(Context context, List<Pasture> pastures){
        this.context = context;
        this.listPastures = pastures;
    }

    public static class PastureHolder {
        public TextView textViewName;
        public TextView textViewDescription;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return listPastures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PastureHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_list_pastures, parent, false);

            holder = new PastureHolder();
            holder.textViewName = convertView.findViewById(R.id.line_past_textViewName);
            holder.textViewDescription = convertView.findViewById(R.id.line_past_textViewDescription);

            convertView.setTag(holder);
        } else {
            holder = (PastureHolder) convertView.getTag();
        }

        Pasture pasture = listPastures.get(position);

        holder.textViewName.setText(pasture.getName());
        holder.textViewDescription.setText(pasture.getDescription());

        return convertView;
    }
}

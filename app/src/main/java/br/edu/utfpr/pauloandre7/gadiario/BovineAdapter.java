package br.edu.utfpr.pauloandre7.gadiario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class BovineAdapter extends BaseAdapter {

    private Context context;
    private List<Bovine> listBovines;

    private String[] types;
    public BovineAdapter(Context context, List<Bovine> listBovines) {
        this.context = context;
        this.listBovines = listBovines;
    }
    public static class BovineHolder {
        public TextView textViewTagValue;
        public TextView textViewNameValue;
        public TextView textViewSexValue;
        public TextView textViewBirthValue;
        public TextView textViewBreedValue;
        public TextView textViewVaccineValue;
    }

    @Override
    public int getCount() {
        return listBovines.size();
    }

    @Override
    public Object getItem(int position) {
        return listBovines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BovineHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_list_bovines, parent, false);

            // Cria o holder e seta os endereços
            holder = new BovineHolder();
            holder.textViewTagValue = convertView.findViewById(R.id.textViewTagValue);
            holder.textViewNameValue = convertView.findViewById(R.id.textViewNameValue);
            holder.textViewSexValue = convertView.findViewById(R.id.textViewSexValue);
            holder.textViewBirthValue = convertView.findViewById(R.id.textViewBirthValue);
            holder.textViewBreedValue = convertView.findViewById(R.id.textViewBreedValue);
            holder.textViewVaccineValue = convertView.findViewById(R.id.textViewVaccineValue);

            convertView.setTag(holder);
        } else {
            holder = (BovineHolder) convertView.getTag();
        }

        Bovine bovine = listBovines.get(position);

        holder.textViewTagValue.setText(bovine.getTag());
        holder.textViewNameValue.setText(bovine.getName());

        holder.textViewBirthValue.setText(bovine.getDate());
        holder.textViewBreedValue.setText(bovine.getBreed());

        switch (bovine.getAnimalSex()){
            case MALE:
                holder.textViewSexValue.setText(context.getString(R.string.reg_bov_text_male));
                break;

            case FEMALE:
                holder.textViewSexValue.setText(context.getString(R.string.reg_bov_text_female));
                break;
        }


        if(bovine.getVaccines().isEmpty()){
            holder.textViewVaccineValue.setText(context.getString(R.string.bov_list_text_notVaccinated));
        } else {
            holder.textViewVaccineValue.setText(bovine.getVaccines().get(0));
        }

        return convertView;
    }
}

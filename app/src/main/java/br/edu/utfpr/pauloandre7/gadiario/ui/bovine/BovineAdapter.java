package br.edu.utfpr.pauloandre7.gadiario.ui.bovine;

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
import br.edu.utfpr.pauloandre7.gadiario.models.Pasture;
import br.edu.utfpr.pauloandre7.gadiario.persistence.GadiarioDatabase;
import br.edu.utfpr.pauloandre7.gadiario.utils.LocalDateUtils;

public class BovineAdapter extends BaseAdapter {

    private Context context;
    private List<Bovine> listBovines;

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
        public TextView textViewRepStatusValue;
        public TextView textViewPastureValue;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BovineHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_list_bovines, parent, false);

            // Cria o holder e seta os endereços
            holder = new BovineHolder();
            holder.textViewTagValue = convertView.findViewById(R.id.line_bov_textView_TagValue);
            holder.textViewNameValue = convertView.findViewById(R.id.line_bov_textView_NameValue);
            holder.textViewSexValue = convertView.findViewById(R.id.line_bov_textView_genreValue);
            holder.textViewBirthValue = convertView.findViewById(R.id.line_bov_textView_birthValue);
            holder.textViewBreedValue = convertView.findViewById(R.id.line_bov_textView_breedValue);
            holder.textViewVaccineValue = convertView.findViewById(R.id.line_bov_textView_vaccineValue);
            holder.textViewRepStatusValue = convertView.findViewById(R.id.line_bov_textView_repStatusValue);
            holder.textViewPastureValue = convertView.findViewById(R.id.line_bov_textView_pastureValue);

            convertView.setTag(holder);
        } else {
            holder = (BovineHolder) convertView.getTag();
        }

        Bovine bovine = listBovines.get(position);

        holder.textViewTagValue.setText(bovine.getTag());
        holder.textViewNameValue.setText(bovine.getName());

        holder.textViewBirthValue.setText(LocalDateUtils.formatLocalDate(bovine.getBirth()));
        holder.textViewBreedValue.setText(bovine.getBreed());

        if (bovine.getAnimalSex() != null) {
            switch (bovine.getAnimalSex()){
                case MALE:
                    holder.textViewSexValue.setText(context.getString(R.string.reg_bov_text_male));
                    break;

                case FEMALE:
                    holder.textViewSexValue.setText(context.getString(R.string.reg_bov_text_female));
                    break;
            }
        }

        if (bovine.getRepStatus() != null) {
            switch (bovine.getRepStatus()){
                case LACTATING:
                    holder.textViewRepStatusValue.setText(R.string.bov_list_lactating);
                    break;
                case PREGNANT:
                    holder.textViewRepStatusValue.setText(R.string.bov_list_repStatusValue_pregnant);
                    break;
                case DRY:
                    holder.textViewRepStatusValue.setText(R.string.bov_list_repStatusValue_dry);
                    break;
                case READY:
                    holder.textViewRepStatusValue.setText(R.string.bov_list_repStatusValue_ready);
                    break;
            }
        }

        if(bovine.getVaccines() == null){
            holder.textViewVaccineValue.setText(context.getString(R.string.bov_list_text_notVaccinated));
        } else{
            if(bovine.getVaccines().isEmpty()){
                holder.textViewVaccineValue.setText(context.getString(R.string.bov_list_text_notVaccinated));
            } else {
                StringBuilder vaccine_text = new StringBuilder();
                for(int i = 0; i < bovine.getVaccines().size(); i++){
                    vaccine_text.append(bovine.getVaccines().get(i)).append(" | ");
                }
                holder.textViewVaccineValue.setText(vaccine_text.toString());
            }
        }

        // Busca o nome do pasto no banco de dados para exibição
        GadiarioDatabase database = GadiarioDatabase.getInstance(context);
        Pasture pasture = database.getPastureDao().queryById(bovine.getIdPasture());
        if (pasture != null) {
            holder.textViewPastureValue.setText(pasture.getName());
        } else {
            holder.textViewPastureValue.setText("N/A");
        }

        return convertView;
    }
}

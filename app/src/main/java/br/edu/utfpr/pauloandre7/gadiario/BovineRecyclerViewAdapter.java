package br.edu.utfpr.pauloandre7.gadiario;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BovineRecyclerViewAdapter extends RecyclerView.Adapter<BovineRecyclerViewAdapter.BovineHolder> {
    // ATRIBUTOS DA RECYCLER VIEW
    private Context context;
    private List<Bovine> listBovines;
    private BovineHolder holder;
    private OnItemClickListener onItemClickListener;

    public BovineRecyclerViewAdapter(Context context, List<Bovine> listBovines,
                                    OnItemClickListener listener) {
        this.context = context;
        this.listBovines = listBovines;

        // Não é obrigado a passar Listener, apenas passe null se não quiser tratar evento.
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        // Contrato de criação de Listener para clicks curtos e longos
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public class BovineHolder extends RecyclerView.ViewHolder
                            implements View.OnClickListener, View.OnLongClickListener{
        public TextView textViewTagValue;
        public TextView textViewNameValue;
        public TextView textViewSexValue;
        public TextView textViewBirthValue;
        public TextView textViewBreedValue;
        public TextView textViewVaccineValue;
        public BovineHolder(@NonNull View itemView) {
            super(itemView);

            // Já seta os endereços dos campos na criação do holder, não posteriormente.
            textViewTagValue = itemView.findViewById(R.id.textViewTagValue);
            textViewNameValue = itemView.findViewById(R.id.textViewNameValue);
            textViewSexValue = itemView.findViewById(R.id.textViewSexValue);
            textViewBirthValue = itemView.findViewById(R.id.textViewBirthValue);
            textViewBreedValue = itemView.findViewById(R.id.textViewBreedValue);
            textViewVaccineValue = itemView.findViewById(R.id.textViewVaccineValue);


            // O holder cuidará desses eventos de click para a linha.
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null){
                // Pega a posição
                int position = getAdapterPosition();

                // Se a posição for válida, chama o listener.
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(v, position);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(onItemClickListener != null){

                int position = getAdapterPosition();


                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            }

            return false;
        }
    }



    @NonNull
    @Override
    public BovineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Mesma lógica de inflar a linha do ListView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.line_list_bovines, parent, false);

        // Cria o holder e seta os endereços dentro do construtor usando essa view inflada;
        return new BovineHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BovineHolder holder, int position) {

        // Mesma lógica da ListView. Esse método será usado para atualizar a linha

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
    }

    @Override
    public int getItemCount()  {
        return listBovines.size();
    }

    // O RecyclerView obriga o uso de um Holder para tipar a RecyclerView.Adapter<>;


}

package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BovinesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBovines;

    // Precisa definir um layout manager para o RecyclerView
    private RecyclerView.LayoutManager layoutManager;

    // Objeto baseado naquele contrado de clickListener da Recycler
    private BovineRecyclerViewAdapter.OnItemClickListener onItemClickListener;
    private List<Bovine> listBovines;

    BovineRecyclerViewAdapter bovineRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovines);

        recyclerViewBovines = findViewById(R.id.recyclerViewBovines);

        // Tipo de de Gerenciador de Layout que expõe linha a linha (padrão vertical)
        layoutManager = new LinearLayoutManager(this);
        recyclerViewBovines.setLayoutManager(layoutManager);

        // Otimiza a renderização das linhas com tamanho fixo
        recyclerViewBovines.setHasFixedSize(true);
        // Adiciona um divisor de linhas para separar os itens.
        recyclerViewBovines.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        /*
            ESSE MÉTODO FUNCIONA APENAS PARA O LISTVIEW

        listViewBovines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, // ListView
                                    View view, // Item da List
                                    int position, long id) {

                Bovine bovine = (Bovine) parent.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), getString(R.string.bov_toast_bovine_with_tag)+bovine.getTag()+ getString(R.string.bov_toast_bovClicked),
                        Toast.LENGTH_LONG).show();
            }
        });
        */

        onItemClickListener = new BovineRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Quando o click acontecer, então o comportamento do método definido no contrato
                // será o mesmo do Listener do ListView.

                Bovine bovine = listBovines.get(position);

                Toast.makeText(getApplicationContext(), getString(R.string.bov_toast_bovine_with_tag)+bovine.getTag()+ getString(R.string.bov_toast_bovClicked),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Bovine bovine = listBovines.get(position);

                Toast.makeText(getApplicationContext(), getString(R.string.bov_toast_bovine_with_tag)+bovine.getTag()+
                                                                getString(R.string.bov_toast_longClick),
                        Toast.LENGTH_LONG).show();
            }
        };
        fillListBovines();
    }

    private void fillListBovines(){
        String[] bovines_tags       = getResources().getStringArray(R.array.bovines_Tags);
        String[] bovines_names      = getResources().getStringArray(R.array.bovines_names);
        String[] bovines_births     = getResources().getStringArray(R.array.bovines_births);
        int[] bovines_sex           = getResources().getIntArray(R.array.bovines_sex);
        String[] bovines_breeds      = getResources().getStringArray(R.array.bovines_breeds);

        listBovines = new ArrayList<>();

        Bovine bovine;
        AnimalSex animalSex;
        AnimalSex[] sex_values = AnimalSex.values();
        List<String> vaccines_list = new ArrayList<>();

        vaccines_list.add("Salmonelose");
        vaccines_list.add("Intranasais");

        for (int i = 0; i < bovines_tags.length; i++){

            animalSex = sex_values[bovines_sex[i]];

            bovine = new Bovine(bovines_tags[i],
                    bovines_names[i],
                    bovines_births[i],
                    animalSex,
                    bovines_breeds[i],
                    vaccines_list
                    );

            listBovines.add(bovine);
        }

        bovineRecyclerViewAdapter = new BovineRecyclerViewAdapter(this, listBovines, onItemClickListener);

        recyclerViewBovines.setAdapter(bovineRecyclerViewAdapter);
    }
}
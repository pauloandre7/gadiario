package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BovinesActivity extends AppCompatActivity {

    private ListView listViewBovines;
    private List<Bovine> listBovines;

    BovineAdapter adapterBovine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovines);

        listViewBovines = findViewById(R.id.listViewBovines);

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

        adapterBovine = new BovineAdapter(this, listBovines);

        listViewBovines.setAdapter(adapterBovine);
    }
}
package br.edu.utfpr.pauloandre7.gadiario;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BovinesActivity extends AppCompatActivity {

    private ListView listViewBovines;
    private List<Bovine> listBovines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bovines);

        listViewBovines = findViewById(R.id.listViewBovines);
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

        ArrayAdapter<Bovine> adapter = new ArrayAdapter<>(this,
                                                            android.R.layout.simple_list_item_1,
                                                            listBovines);

        listViewBovines.setAdapter(adapter);
    }
}
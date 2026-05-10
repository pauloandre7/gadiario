package br.edu.utfpr.pauloandre7.gadiario.models;

import java.util.Comparator;

public class Pasture {
    private int id;
    private String name;
    private String description;

    public static Comparator<Pasture> ascendingNameSort = new Comparator<Pasture>(){
        @Override
        public int compare(Pasture past1, Pasture past2) {

            return past1.getName().compareToIgnoreCase(past2.getName());
        }
    };

    public Pasture(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
    }

    public Pasture(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

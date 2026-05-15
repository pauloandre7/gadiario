package br.edu.utfpr.pauloandre7.gadiario.models;

import java.util.Comparator;
import java.util.List;

public class Bovine {

    // O objeto ascendingSort vai representar um dos comparadores possíveis
    public static Comparator<Bovine> ascendingTagSort = new Comparator<Bovine>() {
        @Override
        public int compare(Bovine bov1, Bovine bov2) {

            // Compara dois objetos e retorna um inteiro
            // Method usado pelo Collections.sort
            return bov1.getTag().compareToIgnoreCase(bov2.getTag());
        }
    };

    private int id;
    private String tag;
    private String name;
    private String date;
    private AnimalSex animalSex;
    private String breed;
    private List<String> vaccines;
    private ReproductiveStatus reproductiveStatus;
    private int idPasture;
    private int idMother;

    public Bovine(String tag, String name, String date, AnimalSex animalSex,
                  String breed, List<String> vaccines, ReproductiveStatus repStatus) {
        this.tag = tag;
        this.name = name;
        this.date = date;
        this.animalSex = animalSex;
        this.breed = breed;
        this.vaccines = vaccines;
        this.reproductiveStatus = repStatus;
    }

    public Bovine(String tag, String name, String date, AnimalSex animalSex,
                    String breed, List<String> vaccines, ReproductiveStatus repStatus,
                    int idPasture, int idMother) {
        this.tag = tag;
        this.name = name;
        this.date = date;
        this.animalSex = animalSex;
        this.breed = breed;
        this.vaccines = vaccines;
        this.reproductiveStatus = repStatus;
        this.idPasture = idPasture;
        this.idMother = idMother;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AnimalSex getAnimalSex() {
        return animalSex;
    }

    public void setAnimalSex(AnimalSex animalSex) {
        this.animalSex = animalSex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public ReproductiveStatus getRepStatus(){
        return reproductiveStatus;
    }

    public void setRepStatus(ReproductiveStatus repStatus){
        this.reproductiveStatus = repStatus;
    }

    public List<String> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<String> vaccines) {
        this.vaccines = vaccines;
    }

    @Override
    public String toString() {
        return  tag + "\n" +
                name + "\n" +
                date + "\n" +
                animalSex + "\n" +
                breed + "\n" +
                vaccines.get(0) + "\n" +
                vaccines.get(1);
    }
}

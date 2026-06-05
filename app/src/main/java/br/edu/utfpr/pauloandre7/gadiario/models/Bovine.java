package br.edu.utfpr.pauloandre7.gadiario.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

// a Anotação entity diz que esta model deve ser uma tabela. Existem opções para colocar entre parênteses @Entity()
@Entity
public class Bovine implements Cloneable{

    // O objeto ascendingSort vai representar um dos comparadores possíveis
    public static Comparator<Bovine> ascendingTagSort = new Comparator<Bovine>() {
        @Override
        public int compare(Bovine bov1, Bovine bov2) {

            // Compara dois objetos e retorna um inteiro
            // Method usado pelo Collections.sort
            return bov1.getTag().compareToIgnoreCase(bov2.getTag());
        }
    };

    public static Comparator<Bovine> descendingTagSort = new Comparator<Bovine>() {
        @Override
        public int compare(Bovine bov1, Bovine bov2) {

            // Compara dois objetos e retorna um inteiro
            // Method usado pelo Collections.sort
            return -1 * bov1.getTag().compareToIgnoreCase(bov2.getTag());
        }
    };


    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String tag;
    private String name;
    private String date;
    private AnimalSex animalSex;
    private String breed;

    private List<String> vaccines;
    private ReproductiveStatus repStatus;

    private int idPasture;
    private int idMother;

    // O room utiliza os métodos gets, sets e o construtor para manipular os objetos e persistir

    // faz com que o room ignore este construtor e apenas utilize o outro
    @Ignore
    public Bovine(String tag, String name, String date, AnimalSex animalSex,
                  String breed, List<String> vaccines, ReproductiveStatus repStatus) {
        this.tag = tag;
        this.name = name;
        this.date = date;
        this.animalSex = animalSex;
        this.breed = breed;
        this.vaccines = vaccines;
        this.repStatus = repStatus;
        this.idPasture = 0;
        this.idMother = 0;
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
        this.repStatus = repStatus;
        this.idPasture = idPasture;
        this.idMother = idMother;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return repStatus;
    }

    public void setRepStatus(ReproductiveStatus repStatus){
        this.repStatus = repStatus;
    }

    public List<String> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<String> vaccines) {
        this.vaccines = vaccines;
    }

    public int getIdMother() {
        return idMother;
    }

    public void setIdMother(int idMother) {
        this.idMother = idMother;
    }

    public int getIdPasture() {
        return idPasture;
    }

    public void setIdPasture(int idPasture) {
        this.idPasture = idPasture;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        // Cópia rasa funcinoa porque essa classe tem atributos primitivos ou mutáveis (gloria)

        return super.clone();
    }

    // métodos equals e hash implementados pelo java
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bovine bovine = (Bovine) o;

        return tag.equals(bovine.tag) &&
                name.equals(bovine.name) &&
                date.equals(bovine.date) &&
                animalSex == bovine.animalSex &&
                breed.equals(bovine.breed) &&
                vaccines.equals(bovine.vaccines) &&
                repStatus == bovine.repStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, name, date, animalSex, breed, vaccines, repStatus);
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

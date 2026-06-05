package br.edu.utfpr.pauloandre7.gadiario.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

@Entity
public class Pasture implements Cloneable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String name;

    private String description;

    public static Comparator<Pasture> ascendingNameSort = new Comparator<Pasture>(){
        @Override
        public int compare(Pasture past1, Pasture past2) {
            return past1.getName().compareToIgnoreCase(past2.getName());
        }
    };

    public static Comparator<Pasture> descendingNameSort = new Comparator<Pasture>(){
        @Override
        public int compare(Pasture past1, Pasture past2) {
            return -1 * past1.getName().compareToIgnoreCase(past2.getName());
        }
    };

    @Ignore
    public Pasture(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
    }

    public Pasture(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pasture pasture = (Pasture) o;
        return Objects.equals(name, pasture.name) &&
                Objects.equals(description, pasture.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return name + "\n" + description;
    }
}

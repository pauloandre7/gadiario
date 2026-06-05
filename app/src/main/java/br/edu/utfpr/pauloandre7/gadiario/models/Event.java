package br.edu.utfpr.pauloandre7.gadiario.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

@Entity
public class Event implements Cloneable {

    public static Comparator<Event> ascendingDateSort = new Comparator<Event>() {
        @Override
        public int compare(Event e1, Event e2) {
            return e1.getDate().compareTo(e2.getDate());
        }
    };

    public static Comparator<Event> descendingDateSort = new Comparator<Event>() {
        @Override
        public int compare(Event e1, Event e2) {
            return -1 * e1.getDate().compareTo(e2.getDate());
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(index = true)
    private long idBovine;

    private EventType type;
    private int qtyCalves;
    private long idPastureOrigin;
    private long idPastureDestination;

    @NonNull
    @ColumnInfo(index = true)
    private String date;

    private String observation;

    @Ignore
    public Event(long idBovine, EventType type, String date, String observation) {
        this.id = 0;
        this.idBovine = idBovine;
        this.type = type;
        this.date = date;
        this.observation = observation;
        this.qtyCalves = 0;
        this.idPastureOrigin = 0;
        this.idPastureDestination = 0;
    }

    public Event(long id, long idBovine, EventType type, int qtyCalves, long idPastureOrigin, long idPastureDestination, @NonNull String date, String observation) {
        this.id = id;
        this.idBovine = idBovine;
        this.type = type;
        this.qtyCalves = qtyCalves;
        this.idPastureOrigin = idPastureOrigin;
        this.idPastureDestination = idPastureDestination;
        this.date = date;
        this.observation = observation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdBovine() {
        return idBovine;
    }

    public void setIdBovine(long idBovine) {
        this.idBovine = idBovine;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getQtyCalves() {
        return qtyCalves;
    }

    public void setQtyCalves(int qtyCalves) {
        this.qtyCalves = qtyCalves;
    }

    public long getIdPastureOrigin() {
        return idPastureOrigin;
    }

    public void setIdPastureOrigin(long idPastureOrigin) {
        this.idPastureOrigin = idPastureOrigin;
    }

    public long getIdPastureDestination() {
        return idPastureDestination;
    }

    public void setIdPastureDestination(long idPastureDestination) {
        this.idPastureDestination = idPastureDestination;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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
        Event event = (Event) o;
        return idBovine == event.idBovine &&
                qtyCalves == event.qtyCalves &&
                idPastureOrigin == event.idPastureOrigin &&
                idPastureDestination == event.idPastureDestination &&
                type == event.type &&
                date.equals(event.date) &&
                Objects.equals(observation, event.observation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBovine, type, qtyCalves, idPastureOrigin, idPastureDestination, date, observation);
    }

    @Override
    public String toString() {
        return type + "\n" + date + "\n" + observation;
    }
}

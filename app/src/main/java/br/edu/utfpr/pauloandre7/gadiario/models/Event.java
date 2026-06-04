package br.edu.utfpr.pauloandre7.gadiario.models;

import androidx.annotation.NonNull;
import java.util.Comparator;

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

    private int id;
    private int idBovine;
    private EventType type;
    private int qtyCalves;
    private int idPastureOrigin;
    private int idPastureDestination;
    private String date;
    private String observation;

    public Event(int idBovine, EventType type, String date, String observation) {
        this.id = 0;
        this.idBovine = idBovine;
        this.type = type;
        this.date = date;
        this.observation = observation;
        this.qtyCalves = 0;
    }

    public Event(int id, int idBovine, EventType type, int qtyCalves, int idPastureOrigin, int idPastureDestination, String date, String observation) {
        this.id = id;
        this.idBovine = idBovine;
        this.type = type;
        this.qtyCalves = qtyCalves;
        this.idPastureOrigin = idPastureOrigin;
        this.idPastureDestination = idPastureDestination;
        this.date = date;
        this.observation = observation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdBovine() {
        return idBovine;
    }

    public void setIdBovine(int idBovine) {
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

    public int getIdPastureOrigin() {
        return idPastureOrigin;
    }

    public void setIdPastureOrigin(int idPastureOrigin) {
        this.idPastureOrigin = idPastureOrigin;
    }

    public int getIdPastureDestination() {
        return idPastureDestination;
    }

    public void setIdPastureDestination(int idPastureDestination) {
        this.idPastureDestination = idPastureDestination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
}

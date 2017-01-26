package br.com.leinadlarama.diadobatecabeca.helper;

import android.widget.ImageView;

import java.util.List;

import br.com.leinadlarama.diadobatecabeca.model.Event;

/**
 * Created by eumagnun on 21/01/2017.
 */

public class DataHolder {
    private Event eventSelected;

    private ImageView imageView;
    private List<Event> eventsCollection;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public List<Event> getEventsCollection() {
        return eventsCollection;
    }

    public void setEventsCollection(List<Event> eventsCollection) {
        this.eventsCollection = eventsCollection;
    }

    public Event getEventSelected() {
        return eventSelected;
    }

    public void setEventSelected(Event eventSelected) {
        this.eventSelected = eventSelected;
    }

    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }
}

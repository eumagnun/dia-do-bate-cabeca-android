package br.com.leinadlarama.diadobatecabeca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.leinadlarama.diadobatecabeca.adapter.EventAdapter;
import br.com.leinadlarama.diadobatecabeca.dao.BandaDao;
import br.com.leinadlarama.diadobatecabeca.model.Event;

public class MainEventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
//teste
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrieveDataFromFirebase();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
   }

    private void retrieveDataFromFirebase(){

        DatabaseReference eventsRef = BandaDao.getDataBaseRef("events");
        eventsRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String,Event>> listGenericTypeIndicator = new GenericTypeIndicator<HashMap<String,Event>>() {
                        };

                        HashMap<String,Event> hash =(HashMap<String,Event>)dataSnapshot.getValue(listGenericTypeIndicator);
                        loadListView(convertDataFromFirebase(hash));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

    }

    private void loadListView(List<Event>lista){

        recyclerView.setAdapter(new EventAdapter(lista, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
    }

    private List<Event> convertDataFromFirebase(HashMap mp) {
        List<Event> lista = new ArrayList<Event>();
        Event event;
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            event = (Event)pair.getValue();
            event.setIdEvento(pair.getKey().toString());
            lista.add(event);
            it.remove();
        }
        return lista;
    }
}

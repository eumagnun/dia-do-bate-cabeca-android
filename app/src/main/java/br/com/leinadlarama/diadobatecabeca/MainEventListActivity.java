package br.com.leinadlarama.diadobatecabeca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.leinadlarama.diadobatecabeca.adapter.EventAdapter;
import br.com.leinadlarama.diadobatecabeca.dao.BandaDao;
import br.com.leinadlarama.diadobatecabeca.model.Event;

public class MainEventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText tvSearchBand;
    private List<Event> listaBandas = new ArrayList<Event>();
    private List<Event> listaBandasFiltrada = new ArrayList<Event>();

    //teste
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrieveDataFromFirebase();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        tvSearchBand = (EditText) findViewById(R.id.tvSearchBand);
        tvSearchBand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tvSearchBand.getRight() - tvSearchBand.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        filterData(tvSearchBand.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void retrieveDataFromFirebase() {

        Query eventsRef = BandaDao.getDataBaseRef("events").orderByChild("existeEvento").startAt("1").endAt("1");
        eventsRef.keepSynced(true);
        eventsRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Event>> listGenericTypeIndicator = new GenericTypeIndicator<HashMap<String, Event>>() {
                        };

                        HashMap<String, Event> hash = (HashMap<String, Event>) dataSnapshot.getValue(listGenericTypeIndicator);

                        listaBandas = convertDataFromFirebase(hash);
                        loadListView(listaBandas);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

    }

    private void loadListView(List<Event> lista) {

        recyclerView.setAdapter(new EventAdapter(lista, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
    }

    private List<Event> convertDataFromFirebase(HashMap mp) {
        List<Event> lista = new ArrayList<Event>();
        Event event;
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            event = (Event) pair.getValue();
            event.setIdEvento(pair.getKey().toString());
            lista.add(event);
            it.remove();
        }
        Collections.sort(lista);
        return lista;
    }

    public void filterData(String bandName) {
        listaBandasFiltrada = new ArrayList<Event>();
        for (Event e : listaBandas) {
            if (e.getNomeBanda().toUpperCase().contains(bandName.toUpperCase())) {
                listaBandasFiltrada.add(e);
            }
        }

        if (listaBandasFiltrada.size() == 0) {
            loadListView(listaBandas);
        } else {
            loadListView(listaBandasFiltrada);
        }
    }
}

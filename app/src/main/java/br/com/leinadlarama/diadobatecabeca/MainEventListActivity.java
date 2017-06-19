package br.com.leinadlarama.diadobatecabeca;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.leinadlarama.diadobatecabeca.adapter.EventAdapter;
import br.com.leinadlarama.diadobatecabeca.dao.BandaDao;
import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;
import br.com.leinadlarama.diadobatecabeca.model.Event;

public class MainEventListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private EditText tvSearchBand;
    private List<Event> listaBandas = new ArrayList<Event>();
    private List<Event> listaBandasFiltrada = new ArrayList<Event>();
    private FirebaseAuth mAuth;
    private TextInputLayout tilSearchForm;

    //teste
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        retrieveDataFromFirebase();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        tvSearchBand = (EditText) findViewById(R.id.tvSearchBand);
        tilSearchForm = (TextInputLayout) findViewById(R.id.tilSearchForm);


        tvSearchBand.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_NULL
                                && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            filterData(tvSearchBand.getText().toString());
                            return true;

                        } else {
                            return false;
                        }
                    }
                }
        );

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

        initProgressDialog(this);

        Query eventsRef = BandaDao.getDataBaseRef("events")
                .orderByChild("existeEvento")
                .startAt("1")
                .endAt("1");

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

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }


                });

    }

    private void retrieveFavouritesFromFirebase() {

        if (mAuth.getCurrentUser() != null) {


            initProgressDialog(this);

            Query eventsRef = BandaDao.getDataBaseRef(Constants.COLLECTION_FAVOURITES).child(mAuth.getCurrentUser().getUid());
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

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }


                    });
        } else {
            Snackbar.make(findViewById(R.id.mainActivityRoot), "Para exibir os favoritados, logado você deve estar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void loadListView(List<Event> lista) {

        recyclerView.setAdapter(new EventAdapter(lista, this));

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
    }

    private List<Event> convertDataFromFirebase(HashMap mp) {
        List<Event> lista = new ArrayList<Event>();
        try {

            Event event;
            if(mp!=null) {
                Iterator it = mp.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    event = (Event) pair.getValue();
                    event.setIdEvento(pair.getKey().toString());

                    //retornar apenas eventos do ano corrente

                    if (event.getInfoComplementar().contains(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))) {
                        lista.add(event);
                    }
                    it.remove();
                }
                Collections.sort(lista);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_favourites:
                retrieveFavouritesFromFirebase();
                this.tilSearchForm.setVisibility(View.GONE);
                return true;
            case R.id.menu_search:
                retrieveDataFromFirebase();
                this.tvSearchBand.setText("");
                this.tilSearchForm.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_login:
                this.tilSearchForm.setVisibility(View.GONE);
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;
            default:
                break;
        }
        return false;
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

    public void removeFromFavourites(final Event event) {
        if (mAuth.getCurrentUser() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
            builder.setTitle(R.string.confirmar_exclusao);


            String positiveText = this.getString(android.R.string.ok);
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            BandaDao.getDataBaseRef(Constants.COLLECTION_FAVOURITES)
                                    .child(mAuth.getCurrentUser()
                                            .getUid())
                                    .child(event.getNomeBanda())
                                    .removeValue();
                        }


                    });

            String negativeText = this.getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}

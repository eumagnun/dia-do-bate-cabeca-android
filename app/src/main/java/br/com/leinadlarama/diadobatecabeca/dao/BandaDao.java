package br.com.leinadlarama.diadobatecabeca.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;
import br.com.leinadlarama.diadobatecabeca.model.Event;

/**
 * Created by eumagnun on 21/01/2017.
 */

public class BandaDao {
    static DatabaseReference eventsRef;
    public BandaDao(){
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("users/ugPOMt1YxfVzgrhv8S2C73NNG7A3/events");
        eventsRef.keepSynced(true);
    }



}

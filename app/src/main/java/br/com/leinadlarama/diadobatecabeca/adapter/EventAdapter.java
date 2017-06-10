package br.com.leinadlarama.diadobatecabeca.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.leinadlarama.diadobatecabeca.R;
import br.com.leinadlarama.diadobatecabeca.ViewEventActivity;
import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;
import br.com.leinadlarama.diadobatecabeca.model.Event;

/**
 * Created by eumagnun on 21/01/2017.
 */

public class EventAdapter extends BasicAdapter {

    private List<Event> listaEvents;
    private Context context;
    private Event currentEvent;


    public EventAdapter(List<Event> listaEvents, Context context) {
        this.listaEvents = listaEvents;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return listaEvents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lista_view, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final EventViewHolder eventViewHolder = (EventViewHolder) holder;
        currentEvent = listaEvents.get(position);
        loadImage(currentEvent,eventViewHolder.icone);
        eventViewHolder.titulo.setText(currentEvent.getNomeBanda());


        eventViewHolder.itemId.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataHolder.getInstance().setEventSelected(getItem(eventViewHolder.getAdapterPosition()));
                        DataHolder.getInstance().setImageView(eventViewHolder.icone);
                        context.startActivity(new Intent(context, ViewEventActivity.class));
                    }
                }
        );

    }

    private Event getItem(int position) {
        return listaEvents.get(position);
    }


    private void loadImage(final Event currentEvent,final ImageView img) {


        Picasso.with(context)
                .load(currentEvent.getFotoBanda())
                .placeholder(R.drawable.banner)
                .resize(300, 300)
                .onlyScaleDown()
                .into(img);

       /*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Constants.URL_STORAGE_REFERENCE);

        StorageReference bannerRef = storageRef.child("/images/" + currentEvent.getId());

        bannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {

                Picasso.with(context)
                        .load(DataHolder.getInstance().getEventSelected().getFotoBanda())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.banner)
                        .resize(200, 200)
                        .onlyScaleDown()
                        .into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                // Handle any errors
            }
        });

        */
    }

    private class EventViewHolder extends RecyclerView.ViewHolder
    {

        final ImageView icone;
        final TextView titulo;
        final LinearLayout itemId;

        public EventViewHolder(View view) {
            super(view);
            icone = (ImageView) view.findViewById(R.id.icone);
            titulo = (TextView) view.findViewById(R.id.titulo);
            itemId  = (LinearLayout) view.findViewById(R.id.itemId);
        }

    }

}

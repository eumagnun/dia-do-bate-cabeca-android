package br.com.leinadlarama.diadobatecabeca;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.URL;

import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;

public class ViewEventActivity extends AppCompatActivity {

    private CollapsingToolbarLayout header;
    private TextView text_body;
    private Context context;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        header = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        img = (ImageView) findViewById(R.id.img);

        text_body = (TextView) findViewById(R.id.text_body);
        text_body.setText(mountEventBodymessage());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, TopTracksActivity.class));
//                Snackbar.make(view, DataHolder.getInstance().getEventSelected().getNomeBanda()+" adicionado aos favoritos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        setTitle(DataHolder.getInstance().getEventSelected().getNomeBanda());

        context = this;
        loadBanner();
    }


    private String mountEventBodymessage() {
        StringBuffer bodyMessage = new StringBuffer();
        bodyMessage.append("Informações:\n ")
                .append(DataHolder.getInstance().getEventSelected()
                .getInfoComplementar()
                .replaceAll("(?i)Data:","\n\nData: ")
                .replaceAll("(?i)Endereço:","\n\nEndereço:\n\n")
                .replaceAll("(?i)Local:","\n\nLocal: ")
                .replaceAll("(?i)Formas","\n\nFormas")
                .replaceAll("(?i)PONTOS DE","\n\nPontos de")
                .replaceAll("(?i)Ingressos: ","\n\nIngressos:\n")
                .replaceAll("(?i)SETOR","\n\nSetor:")
                .replaceAll("(?i)Informações: ","\n\nInformações:\n")
        ).append("\n\n");

        return bodyMessage.toString();
    }


    private void loadBanner() {

        try {
            URL thumb_u = new URL(DataHolder.getInstance().getEventSelected().getFotoBanda());
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            header.setBackground(thumb_d);
        }
        catch (Exception e) {
            // handle it
        }


        header.setBackground(DataHolder.getInstance().getImageView().getDrawable());

    }

}

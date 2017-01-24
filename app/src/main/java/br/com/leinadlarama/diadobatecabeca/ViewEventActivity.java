package br.com.leinadlarama.diadobatecabeca;

import android.content.Context;
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
                Snackbar.make(view, DataHolder.getInstance().getEventSelected().getNomeBanda()+" adicionado aos favoritos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle(DataHolder.getInstance().getEventSelected().getNomeBanda());

        context = this;
        loadBanner();
    }


    private String mountEventBodymessage() {
        StringBuffer bodyMessage = new StringBuffer();
        bodyMessage.append("Local: ").append(DataHolder.getInstance().getEventSelected().getLocalEvento()).append("\n\n");
        bodyMessage.append("Data: ").append(DataHolder.getInstance().getEventSelected().getDataEvento()).append("\n\n");
        bodyMessage.append("Horário: ").append(DataHolder.getInstance().getEventSelected().getHoraEvento()).append("\n\n");
        bodyMessage.append("Preço Inteira: ").append(DataHolder.getInstance().getEventSelected().getPrecoIngresso()).append("\n\n");
        bodyMessage.append("Observações:\n ").append(DataHolder.getInstance().getEventSelected().getInfoComplementar()).append("\n\n");

        return bodyMessage.toString();
    }


    private void loadBanner() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Constants.URL_STORAGE_REFERENCE);

        StorageReference bannerRef = storageRef.child("/images/" + DataHolder.getInstance().getEventSelected().getId());

        bannerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {


                Picasso.with(context).
                        load(uri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resize(400,200)
                        .centerInside()
                        .priority(Picasso.Priority.HIGH)
                        .into(img);
                header.setBackground(img.getDrawable());
                img.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                // Handle any errors
            }
        });
    }

}

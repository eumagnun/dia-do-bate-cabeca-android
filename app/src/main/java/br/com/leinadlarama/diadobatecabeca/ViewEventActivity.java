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
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.HashMap;

import br.com.leinadlarama.diadobatecabeca.dao.BandaDao;
import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;
import br.com.leinadlarama.diadobatecabeca.model.Event;

public class ViewEventActivity extends BaseActivity {

    private CollapsingToolbarLayout header;
    private TextView text_body;
    private Context context;
    private ImageView img;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        header = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        img = (ImageView) findViewById(R.id.img);

        text_body = (TextView) findViewById(R.id.text_body);
        text_body.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        shareInfo();
                        return true;
                    }
                }
        );
        text_body.setText(mountEventBodymessage());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shareInfo();
                //Snackbar.make(view, "Issae! Quanto mais cabeças para bater melhor!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                addToFavourite();
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
                        .replaceAll("(?i)Data:", "\n\nData: ")
                        .replaceAll("(?i)Endereço:", "\n\nEndereço:\n\n")
                        .replaceAll("(?i)Local:", "\n\nLocal: ")
                        .replaceAll("(?i)Formas", "\n\nFormas")
                        .replaceAll("(?i)PONTOS DE", "\n\nPontos de")
                        .replaceAll("(?i)Ingressos: ", "\n\nIngressos:\n")
                        .replaceAll("(?i)SETOR", "\n\nSetor:")
                        .replaceAll("(?i)Informações: ", "\n\nInformações:\n")
                ).append("\n\n");

        return bodyMessage.toString();
    }


    private void loadBanner() {

        try {
            URL thumb_u = new URL(DataHolder.getInstance().getEventSelected().getFotoBanda());
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            header.setBackground(thumb_d);
        } catch (Exception e) {
            e.printStackTrace();
        }


        header.setBackground(DataHolder.getInstance().getImageView().getDrawable());

    }

    private void addToFavourite() {
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child(Constants.COLLECTION_FAVOURITES)
                    .child(mAuth.getCurrentUser()
                            .getUid())
                    .child(DataHolder.getInstance().getEventSelected().getNomeBanda())
                    .setValue(DataHolder.getInstance().getEventSelected());

            String nomeBandaTratado = StringUtils.capitalize(StringUtils.lowerCase(DataHolder.getInstance().getEventSelected().getNomeBanda()));
            Snackbar.make(findViewById(R.id.viewRoot), "Favoritado com sucesso '" + nomeBandaTratado + "' foi!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(findViewById(R.id.viewRoot), "Opa! Antes de favoritar é necessário logar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                shareInfo();
                return true;
            default:
                break;
        }
        return false;
    }


    private void shareInfo() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.msg_share_part1) + DataHolder.getInstance().getEventSelected().getNomeBanda());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.msg_share_part1) + DataHolder.getInstance().getEventSelected().getNomeBanda() + "\n\n" + mountEventBodymessage());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.msg_share)));
    }

}

package br.com.leinadlarama.diadobatecabeca;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;

public class TopTracksActivity extends AppCompatActivity {

    WebView wb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...",true);

        wb = ((WebView) findViewById(R.id.top_tracks_web_view));
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setSupportZoom(true);
        wb.loadUrl("http://admin4data-pegasuswe.rhcloud.com/topTracks.html?"+ DataHolder.getInstance().getEventSelected().getIdBanda());

        wb.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                wb.loadUrl("file:///android_asset/error_page.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd.isShowing() && pd != null) {
                    pd.dismiss();
                }
            }

        });
    }

}

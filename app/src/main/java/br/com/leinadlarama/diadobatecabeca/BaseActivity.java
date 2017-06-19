package br.com.leinadlarama.diadobatecabeca;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by eumagnun on 10/06/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;

    public boolean isConnectionAvailable(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;

        } else {
            Toast.makeText(context, "Você está offline", Toast.LENGTH_SHORT).show();
            conectado = false;
        }

        return conectado;
    }

    public void initProgressDialog(Activity context) {
        if (!context.isFinishing()) {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.waiting), true);
            timerDelayRemoveDialog(100000, progressDialog);
        }
    }

    private void timerDelayRemoveDialog(long time, final Dialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }



}

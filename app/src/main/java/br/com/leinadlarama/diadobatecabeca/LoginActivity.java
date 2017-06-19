package br.com.leinadlarama.diadobatecabeca;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.leinadlarama.diadobatecabeca.dao.BandaDao;
import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.model.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final String TAG = "LoginActivity";

    private EditText mEmailView;
    private EditText mPasswordView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private LinearLayout llUserAuthenticatedView;
    private LinearLayout llLoginView;
    private Button mEmailSignInButton;
    private Button mEmailSignOutButton;
    private Button mEmailSignUpButton;
    private TextView tvUserSigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignOutButton = (Button) findViewById(R.id.email_sign_out_button);
        mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);

        mEmailSignInButton.setOnClickListener(this);
        mEmailSignOutButton.setOnClickListener(this);
        mEmailSignUpButton.setOnClickListener(this);

        llUserAuthenticatedView = (LinearLayout) findViewById(R.id.user_authenticated_view);
        llLoginView = (LinearLayout) findViewById(R.id.login_view);

        tvUserSigned = (TextView) findViewById(R.id.tvUserSigned);
        enableFields();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        initProgressDialog(this);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro ao efetuar login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        initProgressDialog(this);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(LoginActivity.this, R.string.msg_ja_existe_usuario_com_email_informado, Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(LoginActivity.this, R.string.msg_a_senha_informada_e_muito_curta, Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(LoginActivity.this, R.string.msg_credenciais_invalidas, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, R.string.msg_erro_tentar_efetuar_login, Toast.LENGTH_SHORT).show();

                            }

                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());
        tvUserSigned.setText(username);
        enableFields();

    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailView.getText().toString())) {
            mEmailView.setError("Required");
            result = false;
        } else {
            mEmailView.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
            mPasswordView.setError("Required");
            result = false;
        } else {
            mPasswordView.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            signIn();
        } else if (i == R.id.email_sign_up_button) {
            signUp();
        } else if (i == R.id.email_sign_out_button) {
            if (mAuth.getCurrentUser() != null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builder.setTitle(R.string.confimar_logout);


                String positiveText = this.getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                enableFields();
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

    public void enableFields() {
        if (mAuth.getCurrentUser() != null) {
            llLoginView.setVisibility(View.GONE);
            llUserAuthenticatedView.setVisibility(View.VISIBLE);
        } else {
            llLoginView.setVisibility(View.VISIBLE);
            llUserAuthenticatedView.setVisibility(View.GONE);
        }
    }
}


package com.uapps.uticket.untels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, contraseña;
    private Button ingresar;
    private String emailUsuario, contraseñaUsuario;
    private TextView nuevoUsuario;
    private FirebaseAuth inicioSesion;
    private ProgressDialog progressBar;
    private static final long CONECCTION_DATABASE_FIREBASE = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        ingresar = findViewById(R.id.ingresar);
        nuevoUsuario = findViewById(R.id.nuevousuario);
        nuevoUsuario.setOnClickListener(this);
        ingresar.setOnClickListener(this);
        inicioSesion = FirebaseAuth.getInstance();

        progressBar = new ProgressDialog(LoginActivity.this);
        progressBar.setMessage("Iniciando sesión. Espere unos segundos...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);


    }

    @Override
    public void onClick(final View v){
        switch (v.getId()){
            case R.id.ingresar:
                if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(contraseña.getText())){
                    Snackbar.make(v, "No puedes dejar algún campo vacio.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }else{
                    progressBar.show();
                    emailUsuario = String.valueOf(email.getText());
                    contraseñaUsuario = String.valueOf(contraseña.getText());
                    inicioSesion.signInWithEmailAndPassword(emailUsuario, contraseñaUsuario)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {
                                    TimerTask timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            if(task.isSuccessful()) {
                                                progressBar.dismiss();
                                                Intent sesion = new Intent(LoginActivity.this, MenuActivity.class);
                                                startActivity(sesion);
                                                finish();
                                            }else{
                                                progressBar.dismiss();
                                                Toast.makeText(LoginActivity.this, "Hubo un error. Inténtelo en otro momento.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    };
                                    Timer timer = new Timer();
                                    timer.schedule(timerTask, CONECCTION_DATABASE_FIREBASE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthException){
                                progressBar.dismiss();
                                String excepcion = ((FirebaseAuthException) e).getErrorCode().toString();
                                if(excepcion=="ERROR_USER_NOT_FOUND"){
                                    Snackbar.make(v,"No se encuentra el usuario con ese correo electrónico.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                }else if(excepcion=="ERROR_WRONG_PASSWORD"){
                                    Snackbar.make(v,"Contraseña incorrecta. Verifique la información.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                }else if(excepcion=="ERROR_INVALID_EMAIL"){
                                    Snackbar.make(v,"Verifique si su correo no contiene ningun espacio.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                                }
                            }
                        }
                    });

                }
                break;

            case R.id.nuevousuario:
                Intent nuevo = new Intent(LoginActivity.this, NuevosUsuarios.class);
                startActivity(nuevo);
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed(){
        new MaterialStyledDialog.Builder(this)
                .setTitle("¿Deseas salir de uTicket?")
                .setDescription("¡Puedes regresar cuando gustes!")
                .setIcon(R.drawable.exit)
                .setStyle(Style.HEADER_WITH_ICON)
                .setHeaderColor(R.color.colorPrimaryDark)
                .withDarkerOverlay(true)
                .setCancelable(true)
                .setPositiveText("Salir")
                .setNegativeText("Deseo seguir aquí")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();

    }
}

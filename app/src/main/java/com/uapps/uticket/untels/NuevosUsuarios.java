package com.uapps.uticket.untels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NuevosUsuarios extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText codigo, datos, email, contraseña, confirmacioncontraseña;
    private Button finalizar;
    private RadioButton rd1, rd2, rd3, rd4, rd5;
    private String codigousuario, datosusuario, emailusuario, contraseñausuario, confirmacionusuario, carrerausuario;
    private ProgressDialog progressBar;
    private FirebaseAuth Auth;
    private DatabaseReference databaseReference;
    private String regex = "(?:[^<>()\\[\\].,;:\\s@\"]+(?:\\.[^<>()\\[\\].,;:\\s@\"]+)*|\"[^\\n\"]+\")@(?:[^<>()\\[\\].,;:\\s@\"]+\\.)+[^<>()\\[\\]\\.,;:\\s@\"]{2,63}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevos_usuarios);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        codigo = findViewById(R.id.codigo);
        datos = findViewById(R.id.datos);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        confirmacioncontraseña = findViewById(R.id.confirmar);
        rd1 = findViewById(R.id.sistemas);
        rd2 = findViewById(R.id.ambiental);
        rd3 = findViewById(R.id.electrónica);
        rd4 = findViewById(R.id.mecanica);
        rd5 = findViewById(R.id.administracion);
        finalizar = findViewById(R.id.finalizar);

        progressBar = new ProgressDialog(NuevosUsuarios.this);
        progressBar.setMessage("Se esta creando su cuenta. Espere unos segundos...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);

        Auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                codigousuario = codigo.getText().toString();
                String confirmarEmail = email.getEditableText().toString().trim();
                datosusuario = datos.getText().toString();
                emailusuario = email.getText().toString();
                contraseñausuario = contraseña.getText().toString();
                confirmacionusuario = confirmacioncontraseña.getText().toString();

                if(rd1.isChecked()==true){
                    carrerausuario = "Ingeniería de Sistemas";
                    nuevoUsuario(view, codigousuario, emailusuario, datosusuario, carrerausuario,contraseñausuario, confirmacionusuario, confirmarEmail);
                }else if(rd2.isChecked()==true){
                    carrerausuario = "Ingeniería Ambiental";
                    nuevoUsuario(view, codigousuario, emailusuario, datosusuario, carrerausuario,contraseñausuario, confirmacionusuario, confirmarEmail);
                }else if(rd3.isChecked()==true){
                    carrerausuario = "Ingeniería Electrónica";
                    nuevoUsuario(view, codigousuario, emailusuario, datosusuario, carrerausuario,contraseñausuario, confirmacionusuario, confirmarEmail);
                }else if(rd4.isChecked()==true){
                    carrerausuario = "Ingeniería Mecánica";
                    nuevoUsuario(view, codigousuario, emailusuario, datosusuario, carrerausuario,contraseñausuario, confirmacionusuario, confirmarEmail);
                }else{
                    carrerausuario = "Administración";
                    nuevoUsuario(view, codigousuario, emailusuario, datosusuario, carrerausuario,contraseñausuario, confirmacionusuario, confirmarEmail);
                }

            }
        });

    }

    public void nuevoUsuario(final View view, final String codigoEstudiante, final String email, final String datos, final String carrera, String contraseña, String confirmacion, String confirmacionEmail){
        if(TextUtils.isEmpty(codigoEstudiante) || TextUtils.isEmpty(email)|| TextUtils.isEmpty(datos) || TextUtils.isEmpty(carrera) || TextUtils.isEmpty(contraseña) || TextUtils.isEmpty(confirmacion)){
            Snackbar.make(view,"No puede dejar ningun campo vacio.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }else if(!confirmacionEmail.matches(regex)){
            Snackbar.make(view,"Ingrese un email correcto.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }else if(!contraseña.equals(confirmacion)){
            Snackbar.make(view,"No coindice las contraseñas, verique su información.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
        }else{
            progressBar.show();
            Auth.createUserWithEmailAndPassword(email, contraseña)
                    .addOnCompleteListener(NuevosUsuarios.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.dismiss();
                                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                String useruid =user.getUid();
                                Users nuevousuario = new Users(Integer.parseInt(codigoEstudiante), datos, email, carrera, 5.00);
                                databaseReference.child("alumnos").child(useruid).setValue(nuevousuario);

                                Intent intent = new Intent(NuevosUsuarios.this,MenuActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e instanceof FirebaseAuthException){
                        progressBar.dismiss();
                        String excepcion = ((FirebaseAuthException) e).getErrorCode().toString();
                        if(excepcion=="ERROR_EMAIL_ALREADY_IN_USE"){
                            Snackbar.make(view,"Ya se encuentra el usuario con el respectivo correo.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(NuevosUsuarios.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}

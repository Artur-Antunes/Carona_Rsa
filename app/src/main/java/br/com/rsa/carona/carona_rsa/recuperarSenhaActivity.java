package br.com.rsa.carona.carona_rsa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class recuperarSenhaActivity extends AppCompatActivity {

    private EditText emailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        emailUsuario =(EditText) findViewById(R.id.email_recuperar);
    }


}

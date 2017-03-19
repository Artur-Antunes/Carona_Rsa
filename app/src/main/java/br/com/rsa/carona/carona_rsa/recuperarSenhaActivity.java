package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;

public class recuperarSenhaActivity extends AppCompatActivity {

    private EditText emailUsuario;
    private Button btnRecuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_recuperar_senha);

        emailUsuario = (EditText) findViewById(R.id.email_recuperar);
        btnRecuperar = (Button) findViewById(R.id.btn_recuperar_senha);

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequisicoesServidor reqServ= new RequisicoesServidor(recuperarSenhaActivity.this);
                reqServ.recuperarSenha(emailUsuario.getText().toString(), new GetRetorno() {
                    @Override
                    public void concluido(Object object) {
                        if(object.toString().equals("1")){
                            Toast.makeText(recuperarSenhaActivity.this,"Uma nova senha foi enviada  para seu E-Mail",Toast.LENGTH_LONG).show();
                            finish();
                        }else if(object.toString().equals("-1")){
                            Toast.makeText(recuperarSenhaActivity.this,"E-Mail não encontrado",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(recuperarSenhaActivity.this,"Erro"+object.toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void concluido(Object object, Object object2) {

                    }
                });


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            //Intent intent = new Intent(this, LoginActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

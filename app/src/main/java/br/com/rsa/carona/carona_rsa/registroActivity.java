package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.Mask;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class registroActivity extends AppCompatActivity {

    private Spinner sexoRegistro;
    private Switch cnhRegistro;
    private EditText emailRegistro;
    private EditText matriculaRegistro;
    private EditText senhaRegistro;
    private EditText senha2Registro;
    private EditText telefoneRegistro;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario, android.R.layout.simple_spinner_dropdown_item);
        sexoRegistro = (Spinner) findViewById(R.id.sexo_registro);
        sexoRegistro.setAdapter(adapter);
        matriculaRegistro = (EditText) findViewById(R.id.matricula_registro);
        matriculaRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (matriculaRegistro.length() <= 7) {
                        matriculaRegistro.setError(" Digite todos os números !");
                    }
                }
            }
        });
        telefoneRegistro = (EditText) findViewById(R.id.telefone_registro);

        //NumFormatoBr addLineNumberFormatter = new NumFormatoBr(new WeakReference(telefoneRegistro));

        telefoneRegistro.addTextChangedListener(Mask.insert("(##)####-####", telefoneRegistro));

        //telefoneRegistro.addTextChangedListener(addLineNumberFormatter);
        telefoneRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (telefoneRegistro.length() <= 13) {
                        telefoneRegistro.setError(" Digite todos os números !");
                    }
                }
            }
        });
        emailRegistro = (EditText) findViewById(R.id.email_registro);
        emailRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (!new Funcoes().isEmailValid(emailRegistro.getText().toString())) {
                        emailRegistro.setError(" Formato inválido !");
                    }
                }
            }
        });
        senhaRegistro = (EditText) findViewById(R.id.senha_registro);
        senha2Registro = (EditText) findViewById(R.id.senha2_registro);
        btnCadastrar = (Button) findViewById(R.id.b_cadastrar);
        cnhRegistro = (Switch) findViewById(R.id.cnh_registro);
        cnhRegistro.setChecked(true);
        //PEGAR VALORES APÓS CLICAR NO BOTÃO CADASTRAR E SALVAR NO BANCO.
            btnCadastrar.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                    Log.e("telefoneee:",telefoneRegistro.length()+"");
                //VERIFICANDO DE OS CAMPOS ESTÃO PREENCHIDOS
                if (!matriculaRegistro.getText().toString().trim().equals("") &&
                        !telefoneRegistro.getText().toString().trim().equals("") &&
                        !emailRegistro.getText().toString().trim().equals("") &&
                        !senhaRegistro.getText().toString().trim().equals("") &&
                        !senha2Registro.getText().toString().trim().equals("")
                        ) {
                    if (senha2Registro.getText().toString().trim().equals(senhaRegistro.getText().toString().trim())) {
                        String matricula = matriculaRegistro.getText().toString();
                        String telefone = telefoneRegistro.getText().toString();
                        String email = emailRegistro.getText().toString();
                        String senha = senhaRegistro.getText().toString();
                        boolean cnh = cnhRegistro.isChecked();
                        Log.e("testador", "cnh " + cnh);
                        String sexo = sexoRegistro.getSelectedItem().toString();
                        Usuario usuario = new Usuario(null, null, matricula, email, telefone, sexo, cnh);
                        usuario.setSenha(senha);
                        usuario.setAtivo(1);

                        Intent i = new Intent(registroActivity.this, Registro2.class);
                        Registro2.usuario = usuario;
                        startActivity(i);

                    } else {
                        Toast.makeText(registroActivity.this, "as senhas não conferem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(registroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

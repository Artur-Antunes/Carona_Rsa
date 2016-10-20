package br.com.rsa.carona.carona_rsa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class registroActivity extends AppCompatActivity {

    private Spinner sexo;
    private Spinner cnh;
    private EditText nomeRegistro;
    private EditText emailRegistro;
    private EditText sobrenomeRegistro;
    private EditText matriculaRegistro;
    private EditText senhaRegistro;
    private EditText senha2Registro;
    private EditText telefoneRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario,android.R.layout.simple_spinner_item);
        sexo = (Spinner) findViewById(R.id.sexo);
        sexo.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.cnh_usuario,android.R.layout.simple_spinner_item);
        cnh = (Spinner) findViewById(R.id.cnh);
        cnh.setAdapter(adapter2);

        nomeRegistro =(EditText)findViewById(R.id.nome_registro);
        sobrenomeRegistro =(EditText)findViewById(R.id.sobrenome_registro);
        matriculaRegistro =(EditText)findViewById(R.id.matricula_registro);
        telefoneRegistro =(EditText)findViewById(R.id.telefone_registro);
        emailRegistro =(EditText)findViewById(R.id.email_registro);
        senhaRegistro =(EditText)findViewById(R.id.senha_registro);
        senha2Registro =(EditText)findViewById(R.id.senha2_registro);

    }
}

package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;


public class EditarDadosActivity extends AppCompatActivity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private Spinner sexoEditar;
    private Switch cnhEditar;
    private EditText nomeEditar;
    private EditText emailEditar;
    private EditText sobrenomeEditar;
    private EditText matriculaEditar;
    private EditText telefoneEditar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e("verifica erro", "aqui 111");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dados);


        mDados = new ManipulaDados(EditarDadosActivity.this);

        usuarioEditar = mDados.getUsuario();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario, android.R.layout.simple_spinner_item);
        sexoEditar = (Spinner) findViewById(R.id.sexo_editar);
        sexoEditar.setAdapter(adapter);

        nomeEditar = (EditText) findViewById(R.id.nome_editar);
        sobrenomeEditar = (EditText) findViewById(R.id.sobrenome_editar);
        matriculaEditar = (EditText) findViewById(R.id.matricula_editar);
        telefoneEditar = (EditText) findViewById(R.id.telefone_editar);
        emailEditar = (EditText) findViewById(R.id.email_editar);
        cnhEditar = (Switch) findViewById(R.id.cnh_editar);
        //Log.e("dados->",usuarioEditar.getNome()+"-"+usuarioEditar.getSobrenome()+"-"+usuarioEditar.getMatricula()+"-"+usuarioEditar.getSexo()+"-"+usuarioEditar.getSenha());

    }

    @Override
    protected void onStart() {

        super.onStart();// A activity está prestes a se tornar visível

        String nome = usuarioEditar.getNome();
        String sobrenome = usuarioEditar.getSobrenome();
        String matricula = usuarioEditar.getMatricula();
        String email = usuarioEditar.getEmail();
        String sexo = usuarioEditar.getSexo();
        String senha = usuarioEditar.getSenha();
        String telefone = usuarioEditar.getTelefone();

        //Log.e("dados-> nome:",nome+" sobrenome:"+sobrenome+" matricula:"+matricula+" sexo:"+sexo+" senha:"+senha+" email:"+email+" telefone:"+telefone);

        nomeEditar.setText(nome);
        sobrenomeEditar.setText(sobrenome);
        matriculaEditar.setText(matricula);
        emailEditar.setText(email);
        telefoneEditar.setText(telefone);
    }

}

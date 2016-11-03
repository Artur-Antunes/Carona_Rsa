package br.com.rsa.carona.carona_rsa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class ExibirDadosUsuarioActivity extends Activity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private TextView nomeExibir;
    private TextView emailExibir;
    private TextView sobrenomeExibir;
    private TextView matriculaExibir;
    private TextView telefoneExibir;
    private TextView sexoExibir;
    private TextView cnhExibir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_dados_usuario);

        mDados=new ManipulaDados(ExibirDadosUsuarioActivity.this);

        usuarioEditar=mDados.getUsuario();

        nomeExibir =(TextView)findViewById(R.id.exibirNomeValor);
        sobrenomeExibir =(TextView)findViewById(R.id.exibirSobrenomeValor);
        matriculaExibir =(TextView)findViewById(R.id.exibirMatriculaValor);
        telefoneExibir =(TextView)findViewById(R.id.exibirTelefoneValor);
        emailExibir =(TextView)findViewById(R.id.exibirEmailValor);
        sexoExibir =(TextView)findViewById(R.id.exibirSexoValor);
        cnhExibir =(TextView)findViewById(R.id.exibirCnhValor);
    }

    @Override
    protected void onStart() {
        super.onStart();// A activity está prestes a se tornar visível

        nomeExibir.setText(usuarioEditar.getNome());
        sobrenomeExibir.setText(usuarioEditar.getSobrenome());
        emailExibir.setText(usuarioEditar.getEmail());
        matriculaExibir.setText(usuarioEditar.getMatricula());
        telefoneExibir.setText(usuarioEditar.getTelefone());

        String converterCnh;

        if(usuarioEditar.isCnh()){
            converterCnh="sim";
        }else{
            converterCnh="Não";
        }

        cnhExibir.setText(converterCnh);
        sexoExibir.setText(usuarioEditar.getSexo());

    }

    public void editarDados(View view) {
        startActivity(new Intent(this, EditarDadosActivity.class));
    }

    public void sairDados(View view) {
        mDados.limparDados();
        startActivity(new Intent(this, LoginActivity.class));
    }


}

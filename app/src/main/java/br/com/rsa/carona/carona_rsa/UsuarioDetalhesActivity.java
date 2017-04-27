package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class UsuarioDetalhesActivity extends AppCompatActivity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private TextView nomeExibir,emailExibir,matriculaExibir,telefoneExibir,sexoExibir,cnhExibir;
    private ImageView imFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_exibir_dados_usuario);

        mDados = new ManipulaDados(UsuarioDetalhesActivity.this);
        usuarioEditar = mDados.getUsuario();

        nomeExibir = (TextView) findViewById(R.id.exibirNomeValor);
        imFoto = (ImageView) findViewById(R.id.foto);
        matriculaExibir = (TextView) findViewById(R.id.exibirMatriculaValor);
        telefoneExibir = (TextView) findViewById(R.id.exibirTelefoneValor);
        emailExibir = (TextView) findViewById(R.id.exibirEmailValor);
        sexoExibir = (TextView) findViewById(R.id.exibirSexoValor);
        cnhExibir = (TextView) findViewById(R.id.exibirCnhValor);
        nomeExibir.setText(usuarioEditar.getNome() + " " + usuarioEditar.getSobrenome());
        emailExibir.setText(usuarioEditar.getEmail());
        matriculaExibir.setText(usuarioEditar.getMatricula());
        telefoneExibir.setText(usuarioEditar.getTelefone());

        imFoto.setImageDrawable(new Funcoes().imagemArredondada(usuarioEditar.getFoto(), getResources()));

        String converterCnh;

        if (usuarioEditar.isCnh()) {
            converterCnh = "SIM";
        } else {
            converterCnh = "NÃO";
        }

        cnhExibir.setText(converterCnh);
        if (usuarioEditar.getSexo().equals("M")) {
            sexoExibir.setText("MASCULINO");
        } else {
            sexoExibir.setText("FEMININO");
        }

    }


    public void editarDados(View view) {
        finish();
        Intent it=new Intent(this, EditarDadosActivity.class);
        startActivity(it);
    }

    public void sairDados(View view) {
        mDados.limparDados();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exibir_dados_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            finish();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            //Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
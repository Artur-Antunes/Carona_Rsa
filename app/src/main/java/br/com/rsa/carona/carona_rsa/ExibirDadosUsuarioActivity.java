package br.com.rsa.carona.carona_rsa;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class ExibirDadosUsuarioActivity extends AppCompatActivity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private TextView nomeExibir;
    private TextView emailExibir;
    private TextView matriculaExibir;
    private TextView telefoneExibir;
    private TextView sexoExibir;
    private TextView cnhExibir;
    private ImageView imFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_exibir_dados_usuario);

        mDados = new ManipulaDados(ExibirDadosUsuarioActivity.this);
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
        byte[] decodedString = Base64.decode(usuarioEditar.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imFoto.setImageBitmap(bitmap);
        imFoto.setScaleType(ImageView.ScaleType.FIT_XY);
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
        Intent it=new Intent(this, EditarDadosActivity.class);
        startActivity(it);
        finish();
    }

    public void sairDados(View view) {
        mDados.limparDados();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exibir_dados_usuario, menu);

        Log.e("será aqui?","1");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.e("será aqui?","2");
            return true;
        }
        if (id == R.id.action_home) {
            Log.e("será aqui?", "3");
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if (id == android.R.id.home) {
            Log.e("será aqui?", "4");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

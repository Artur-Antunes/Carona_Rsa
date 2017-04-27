package br.com.rsa.carona.carona_rsa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class DetalheUsuario extends AppCompatActivity {
    public static Usuario usuarioEditar;
    private TextView nomeExibir;
    private TextView emailExibir;
    private TextView matriculaExibir;
    private TextView telefoneExibir;
    private TextView sexoExibir;
    private TextView cnhExibir;
    private ImageView imFoto;
    private Button editarDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_dados_usuario);
        nomeExibir = (TextView) findViewById(R.id.exibirNomeValor);
        imFoto = (ImageView) findViewById(R.id.foto);
        matriculaExibir = (TextView) findViewById(R.id.exibirMatriculaValor);
        telefoneExibir = (TextView) findViewById(R.id.exibirTelefoneValor);
        emailExibir = (TextView) findViewById(R.id.exibirEmailValor);
        sexoExibir = (TextView) findViewById(R.id.exibirSexoValor);
        cnhExibir = (TextView) findViewById(R.id.exibirCnhValor);
        editarDados=(Button) findViewById(R.id.b_editarDados);
        editarDados.setVisibility(View.INVISIBLE);
        editarDados.setClickable(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhe_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

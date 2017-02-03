package br.com.rsa.carona.carona_rsa;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    View view;
    public static int idCarona;
    LayoutInflater linf;
    LinearLayout llComentario;
    ManipulaDados md;
    ImageButton btComentar;
    EditText comentario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        md = new ManipulaDados(ComentariosActivity.this);
        linf = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linf = LayoutInflater.from(ComentariosActivity.this);
        llComentario = (LinearLayout) findViewById(R.id.caixa_comentario);
        btComentar = (ImageButton) findViewById(R.id.b_commentar);
        comentario = (EditText) findViewById(R.id.et_comment);

        btComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequisicoesServidor rs3 = new RequisicoesServidor(ComentariosActivity.this);
                rs3.gravarComentarioCarona(idCarona, md.getUsuario().getId(), comentario.getText().toString(), new GetRetorno() {
                    @Override
                    public void concluido(Object object) {
                        Toast.makeText(ComentariosActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        if (object.toString().equals("1")) {
                            buscarComentarios();
                        } else {

                        }
                    }

                    @Override
                    public void concluido(Object object, Object object2) {


                    }
                });

            }
        });
        buscarComentarios();
    }


    public void buscarComentarios() {
        llComentario.removeAllViews();
        RequisicoesServidor rs = new RequisicoesServidor(ComentariosActivity.this);
        rs.buscarComentariosCarona(idCarona, new GetRetorno() {
            @Override
            public void concluido(Object object) {

            }

            @Override
            public void concluido(Object object, Object object2) {
                final List<String> textos = (List<String>) object;
                final List<Usuario> usuarios = (List<Usuario>) object2;

                for (int i = 0; i < usuarios.size(); i++) {
                    final View modelo = linf.inflate(R.layout.modelo_comentario, null);
                    TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome3);
                    ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto3);
                    TextView tv_msg = (TextView) modelo.findViewById(R.id.tv_mensagem3);
                    //Button btcomentar = (Button) modelo.findViewById(R.id.b_commentar);


                    tv_nome.setText(usuarios.get(i).getNome());
                    tv_msg.setText(textos.get(i));

                    byte[] decodedString = Base64.decode(usuarios.get(i).getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Resources res = getResources();
                    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                    dr.setCircular(true);
                    c_foto.setImageDrawable(dr);

                    modelo.setId(i);
                    llComentario.addView(modelo, 0);
                }
            }
        });
    }

}

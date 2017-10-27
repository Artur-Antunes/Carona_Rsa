package br.com.rsa.carona.carona_rsa;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class ComentariosActivity extends AppCompatActivity implements Serializable{

    public static int idCarona;
    private LayoutInflater linf;
    private MyReceiver receiver;
    private LinearLayout llComentario;
    private ManipulaDados md;
    private ImageButton btComentar;
    private SwipeRefreshLayout swipeLayout;
    private IntentFilter filter = new IntentFilter();
    private EditText comentario;
    public static int active = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        md = new ManipulaDados(ComentariosActivity.this);
        linf = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linf = LayoutInflater.from(ComentariosActivity.this);
        llComentario = (LinearLayout) findViewById(R.id.caixa_comentario);
        btComentar = (ImageButton) findViewById(R.id.b_commentar);
        comentario = (EditText) findViewById(R.id.et_comment);
        receiver = new MyReceiver(new Handler());

        btComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!comentario.getText().toString().toString().equals("")) {
                    RequisicoesServidor rs3 = new RequisicoesServidor(ComentariosActivity.this);
                    rs3.gravarComentarioCarona(idCarona, md.getUsuario().getId(), comentario.getText().toString(), new GetRetorno() {
                        @Override
                        public void concluido(Object object) {
                            if (object.toString().equals("1")) {
                                comentario.setText("");
                                Toast.makeText(ComentariosActivity.this, "Aguarde...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ComentariosActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void concluido(Object object, Object object2) {


                        }
                    });
                } else {
                    Toast.makeText(ComentariosActivity.this, R.string.campos_branco, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void buscarComentarios(List<String> textos, List<Usuario> usuarios) {
        for (int i = 0; i < usuarios.size(); i++) {
            final View modelo = linf.inflate(R.layout.modelo_comentario, null);
            TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome3);
            ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto3);
            TextView tv_msg = (TextView) modelo.findViewById(R.id.tv_mensagem3);
            TextView tv_hora = (TextView) modelo.findViewById(R.id.tv_horario3);
            tv_nome.setText(usuarios.get(i).getNome());
            tv_msg.setText(textos.get(i));
            tv_hora.setText(new Funcoes().horaSimples(usuarios.get(i).getDataRegistro()));
            byte[] decodedString = Base64.decode(usuarios.get(i).getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Resources res = getResources();
            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
            dr.setCircular(true);
            c_foto.setImageDrawable(dr);
            modelo.setId(usuarios.get(i).getAtivo());
            if(verificaModeloAdd(modelo)==-1) {
                llComentario.addView(modelo);
                active = llComentario.getChildCount();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = -1;
    }

    private int verificaModeloAdd(View modelo) {
        for (int i = 0; i < llComentario.getChildCount(); i++) {
            if (llComentario.getChildAt(i) != null) {
                if (llComentario.getChildAt(i).getId() == modelo.getId()) {
                    return modelo.getId();
                }
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ComentariosActivity.this.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
            } else {
                throw e;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filter.addAction("abcComents");
        ComentariosActivity.this.registerReceiver(receiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String mensagem = intent.getStringExtra("mensagem");
            final List<Usuario> users = (List<Usuario>) intent.getSerializableExtra("users");
            final List<String> textos = (List<String>) intent.getSerializableExtra("textos");
            switch (mensagem) {
                case "nvComents":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            buscarComentarios(textos, users);
                        }
                    });
                    break;
            }
        }
    }
}


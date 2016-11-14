package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Coronas_oferecidas extends Fragment {

    LinearLayout lloferecidas;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("sadasd", "oferecidas ");
        Toast.makeText(getActivity(), "oferecidas", Toast.LENGTH_SHORT).show();
        view = inflater.inflate(R.layout.fragment_coronas_oferecidas, container, false);
        lloferecidas = (LinearLayout) view.findViewById(R.id.caixa_oferecidas);
        atualizarSolicitantes();
        return view;

    }

    public void update() {


    }


    public void atualizarSolicitantes() {
        ManipulaDados M = new ManipulaDados(getActivity());
        Usuario usuario = new Usuario(M.getUsuario().getId());
        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        rs.exibirSolicitacoesCaronas(usuario, new GetRetorno() {
            @Override
            public void concluido(Object object) {

                final List<Usuario> usuarios = (List<Usuario>) object;
                Log.e("pedidos", usuarios.size() + "");
                for (int i = 0; i < usuarios.size(); i++) {

                    final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_solicitadas, null);
                    TextView nomeSolicitante = (TextView) modelo.findViewById(R.id.nomeUserSolicitaCarona);//pega os elemetos do modelo para setar dados
                    TextView telefoneSolicitante = (TextView) modelo.findViewById(R.id.c_telefone);
                    ImageView fotoSolicitante = (ImageView) modelo.findViewById(R.id.c_foto);
                    ImageButton btnAceitar = (ImageButton) modelo.findViewById(R.id.b_aceitar_usuario_carona);
                    ImageButton btnRecusar = (ImageButton) modelo.findViewById(R.id.b_recusar_usuario_carona);
                    btnAceitar.setBackgroundResource(R.drawable.animacao);
                    btnRecusar.setBackgroundResource(R.drawable.animacao);
                    nomeSolicitante.setText(usuarios.get(i).getNome());
                    telefoneSolicitante.setText(usuarios.get(i).getTelefone());
                    Log.e("foto", "concluido " + usuarios.get(i).getFoto());
                    byte[] decodedString = Base64.decode(usuarios.get(i).getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    fotoSolicitante.setImageBitmap(bitmap);
                    fotoSolicitante.setScaleType(ImageView.ScaleType.FIT_XY);
                    int idSolicitante = usuarios.get(i).getId();

                    final Usuario userAtual = new Usuario(idSolicitante);
                    final int j = i;
                    modelo.setId(i);
                    lloferecidas.addView(modelo, 0);

                    btnAceitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            RequisicoesServidor rs2 = new RequisicoesServidor(getActivity());
                            rs2.aceitarRecusarCaronas(userAtual, "ACEITO", new GetRetorno() {
                                @Override
                                public void concluido(Object object) {
                                    Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                    lloferecidas.removeView(modelo);
                                }

                                @Override
                                public void concluido(Object object, Object object2) {

                                }
                            });


                        }
                    });

                    btnRecusar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            RequisicoesServidor rs2 = new RequisicoesServidor(getActivity());
                            rs2.aceitarRecusarCaronas(userAtual, "RECUSADO", new GetRetorno() {
                                @Override
                                public void concluido(Object object) {
                                    Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                    lloferecidas.removeView(modelo);
                                }

                                @Override
                                public void concluido(Object object, Object object2) {

                                }
                            });
                        }
                    });
                    fotoSolicitante.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(getActivity(), DetalheUsuario.class);
                            DetalheUsuario.usuarioEditar = usuarios.get(j);
                        }
                    });
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });


    }
}


package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.SimpleTimeZone;

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
        lloferecidas.removeAllViews();
        ManipulaDados M = new ManipulaDados(getActivity());
        Usuario usuario = new Usuario(M.getUsuario().getId());
        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        rs.buscasSolicitacoesCaronas(usuario, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                final List<Carona> caronas = (List<Carona>) object;
                for (int i = 0; i < caronas.size(); i++) {
                    final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas, null);
                    TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
                    TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);//pega os elemetos do modelo para setar dados
                    TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
                    TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas2);

                    tv_destino.setText(caronas.get(i).getDestino());
                    tv_origem.setText(caronas.get(i).getOrigem());
                    tv_horario.setText(caronas.get(i).getHorario());
                    tv_vagas.setText((caronas.get(i).getVagas()-caronas.get(i).getVagasOcupadas())+"/"+caronas.get(i).getVagas());

                    final LinearLayout ll=(LinearLayout)modelo.findViewById(R.id.caixa_partic);

                    final List<Usuario> participantes=caronas.get(i).getParticipantes();
                    final List statusSolicitacao=caronas.get(i).getParticipantesStatus();
                    for (int j = 0; j < participantes.size(); j++) {
                        if (statusSolicitacao.get(j).equals("AGUARDANDO")) {
                            final RelativeLayout modelo2 = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_solicitadas, null);
                            TextView nomeSolicitante = (TextView) modelo2.findViewById(R.id.nomeUserSolicitaCarona);//pega os elemetos do modelo para setar dados
                            TextView telefoneSolicitante = (TextView) modelo2.findViewById(R.id.c_telefone);
                            ImageView fotoSolicitante = (ImageView) modelo2.findViewById(R.id.c_foto);
                            ImageButton btnAceitar = (ImageButton) modelo2.findViewById(R.id.b_aceitar_usuario_carona);
                            ImageButton btnRecusar = (ImageButton) modelo2.findViewById(R.id.b_recusar_usuario_carona);
                             btnAceitar.setBackgroundResource(R.drawable.animacao);
                             btnRecusar.setBackgroundResource(R.drawable.animacao);
                            nomeSolicitante.setText(participantes.get(j).getNome());
                            telefoneSolicitante.setText(participantes.get(j).getTelefone());
                            Log.e("foto", "concluido " + participantes.get(j).getFoto());
                            byte[] decodedString = Base64.decode(participantes.get(j).getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Resources res = getResources();
                            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                            dr.setCircular(true);
                            fotoSolicitante.setImageDrawable(dr);
                            fotoSolicitante.setScaleType(ImageView.ScaleType.FIT_XY);
                            int idSolicitante = participantes.get(j).getId();

                            final Usuario userAtual = new Usuario(idSolicitante);
                            final int k = j;
                            modelo2.setId(j);

                            ll.addView(modelo2, 0);

                            btnAceitar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    RequisicoesServidor rs2 = new RequisicoesServidor(getActivity());
                                    rs2.aceitarRecusarCaronas(userAtual, "ACEITO", new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                            if(object.equals("Usuario Aceito!")) {
                                                ll.removeView(modelo2);
                                                RelativeLayout m = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_aceitas, null);
                                                TextView nomeSolicitante = (TextView) m.findViewById(R.id.nomeUserSolicitaCarona);//pega os elemetos do modelo para setar dados
                                                TextView telefoneSolicitante = (TextView) m.findViewById(R.id.c_telefone);
                                                ImageView fotoSolicitante = (ImageView) m.findViewById(R.id.c_foto);
                                                nomeSolicitante.setText(participantes.get(k).getNome());
                                                telefoneSolicitante.setText(participantes.get(k).getTelefone());
                                                byte[] decodedString = Base64.decode(participantes.get(k).getFoto(), Base64.DEFAULT);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                Resources res = getResources();
                                                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                                                dr.setCircular(true);
                                                fotoSolicitante.setImageDrawable(dr);
                                                m.setId(k);
                                                ll.addView(m, 0);
                                            }
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
                                            if(object.equals("Usuario Recusado!"))
                                            ll.removeView(modelo2);
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
                                    DetalheUsuario.usuarioEditar = participantes.get(k);
                                }
                            });

                        }else{
                            RelativeLayout modelo2 = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_aceitas, null);
                            TextView nomeSolicitante = (TextView) modelo2.findViewById(R.id.nomeUserSolicitaCarona);//pega os elemetos do modelo para setar dados
                            TextView telefoneSolicitante = (TextView) modelo2.findViewById(R.id.c_telefone);
                            ImageView fotoSolicitante = (ImageView) modelo2.findViewById(R.id.c_foto);
                            nomeSolicitante.setText(participantes.get(j).getNome());
                            telefoneSolicitante.setText(participantes.get(j).getTelefone());
                            byte[] decodedString = Base64.decode(participantes.get(j).getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Resources res = getResources();
                            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                            dr.setCircular(true);
                            fotoSolicitante.setImageDrawable(dr);
                            modelo2.setId(j);
                            ll.addView(modelo2, 0);
                        }

                    }
                    lloferecidas.addView(modelo, 0);
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });


    }
}


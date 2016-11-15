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
import android.widget.Button;
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
import br.com.rsa.carona.carona_rsa.entidades.Servico;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Home extends Fragment {
    LinearLayout ll;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ll = (LinearLayout) view.findViewById(R.id.caixa_home);
        atualizaCaronas();
        atualizarEspera();
        Intent it = new Intent(getActivity(),Servico.class);
        getContext().startService(it);
        return view;
    }

    public void atualizarEspera() {

        final ManipulaDados M = new ManipulaDados(getActivity());
        if(M.getUsuario() !=null) {
            final Usuario usuario = new Usuario(M.getUsuario().getId());
            Log.e("helder", "que doideira  " + M.getCaronaSolicitada());
            if (M.getCaronaSolicitada() != -1) {
                Carona carona = new Carona(M.getCaronaSolicitada());
                RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                rs.aguardaRespostaCarona(usuario, carona, new GetRetorno() {

                    @Override
                    public void concluido(Object object) {
                        final Carona carona = (Carona) object;
                        if (carona != null) {
                            final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_recebidas, null);
                            TextView ta_destino = (TextView) modelo.findViewById(R.id.tv_destinoR);
                            TextView ta_status = (TextView) modelo.findViewById(R.id.tv_status_aguarda);
                            TextView ta_horario = (TextView) modelo.findViewById(R.id.tv_horario_r);
                            Button btnCancelar = (Button) modelo.findViewById(R.id.b_desistencia);
                            ta_destino.setText(carona.getDestino());
                            ta_status.setText(carona.getStatusUsuario());
                            ta_horario.setText(carona.getHorario());
                            modelo.setId(0);
                            modelo.setGravity(0);
                            ll.addView(modelo, 0);

                            btnCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Carona caronaLocal = new Carona(M.getCaronaSolicitada());
                                    RequisicoesServidor rserv = new RequisicoesServidor(getActivity());
                                    rserv.desistirCarona(usuario, caronaLocal, new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            Toast.makeText(getActivity(), object.toString(), Toast.LENGTH_LONG).show();
                                            M.setCaronaSolicitada(-1);
                                            atualizaCaronas();
                                            ll.removeView(modelo);
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {

                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void concluido(Object object, Object object2) {

                    }
                });

            } else {


            }
        }

    }


    public void atualizaCaronas() {
        final ManipulaDados M = new ManipulaDados(getActivity());

        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        ll.removeAllViews();
        rs.buscaCaronas(null, new GetRetorno() {
            @Override
            public void concluido(Object object) {

            }

            @Override
            public void concluido(Object object, Object object2) {
                final List<Carona> caronas = (List<Carona>) object;
                final List<Usuario> usuarios = (List<Usuario>) object2;

                for (int i = 0; i < caronas.size(); i++) {
                    if (caronas.get(i).getId() == M.getCaronaSolicitada()) {
                        continue;
                    }
                    //pega layout modelo de coronas

                    final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_disponiveis, null);
                    TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem);//pega os elemetos do modelo para setar dados
                    TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino);
                    TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas);
                    TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario);
                    TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome);
                    ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto);
                    TextView tv_telefone = (TextView) modelo.findViewById(R.id.tv_telefone);
                    Button btnSolicitar = (Button) modelo.findViewById(R.id.b_solicitar);
                    if(M.getUsuario().getId()!= usuarios.get(i).getId()) {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao);
                    }else {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao_remover);
                        btnSolicitar.setText("CANCELAR ESSA CARONA");
                    }
                    tv_nome.setText(usuarios.get(i).getNome());
                    tv_telefone.setText(usuarios.get(i).getTelefone());
                    byte[] decodedString = Base64.decode(usuarios.get(i).getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Resources res = getResources();
                    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                    dr.setCircular(true);
                    c_foto.setImageDrawable(dr);
                    tv_destino.setText(caronas.get(i).getDestino());
                    tv_origem.setText(caronas.get(i).getOrigem());
                    tv_horario.setText(caronas.get(i).getHorario());
                    tv_vagas.setText(caronas.get(i).getVagas() + "");
                    final int id_carona = caronas.get(i).getId();

                    modelo.setId(i + 1);
                    ll.addView(modelo, 0);
                    final int j = i;
                    btnSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ManipulaDados md = new ManipulaDados(getActivity());
                            //teste aqui -
                            if(M.getUsuario().getId()!= usuarios.get(j).getId()) {
                                if (md.getCaronaSolicitada() == -1) {
                                    Usuario eu = md.getUsuario();
                                    Carona carona = caronas.get(j);
                                    RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                    rs.solicitaCarona(carona, eu, new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                            if (object.toString().equals("Carona Solicitada Com Sucesso!")) {
                                                md.setCaronaSolicitada(id_carona);
                                                atualizarEspera();
                                                ll.removeView(modelo);
                                            }
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), " Você já tem uma carona solicitada ! ", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                rs.alteraStatusCarona(caronas.get(j).getId(), 0, new GetRetorno() {
                                    @Override
                                    public void concluido(Object object) {
                                        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                        atualizaCaronas();
                                    }

                                    @Override
                                    public void concluido(Object object, Object object2) {

                                    }
                                });
                            }
                        }
                    });

                    modelo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(getActivity(), Detalhes_Carona.class);
                            Detalhes_Carona.usuario = usuarios.get(j);
                            Detalhes_Carona.carona = caronas.get(j);
                            startActivity(it);

                        }
                    });


                }
            }
        });

    }
}

package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
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

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Coronas_oferecidas extends Fragment {
    FragmentActivity activity;
    LinearLayout lloferecidas;
    Resources resource;
    View view;
    ImageButton recarrega;
    SwipeRefreshLayout swipeLayout;
    MyReceiver receiver;
    TextView labelOferecidas;
    IntentFilter filter = new IntentFilter();
    AlertDialog.Builder dialog;
    ManipulaDados M;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        resource = getResources();
        M = new ManipulaDados(getActivity());
        view = inflater.inflate(R.layout.fragment_coronas_oferecidas, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        labelOferecidas = (TextView) view.findViewById(R.id.label3Vazio);
        recarrega = (ImageButton) view.findViewById(R.id.b_recarrega3);
        lloferecidas = (LinearLayout) view.findViewById(R.id.caixa_oferecidas);
        swipeLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                atualizarSolicitantes(0, 6, true);
                swipeLayout.setRefreshing(false);
            }
        });

        recarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarSolicitantes(lloferecidas.getChildCount(), 1, false);
            }
        });
        if (M.getUsuario() != null) {
            atualizarSolicitantes(0, 6, true);
        }
        dialog = new AlertDialog.Builder(getActivity());
        receiver = new MyReceiver(new Handler());
        return view;
    }


    public void atualizarSolicitantes(int totalviewsAtual, int totalBuscar, final boolean remover) {
        if (remover) {
            lloferecidas.removeAllViews();
        }

        Usuario usuario = new Usuario(M.getUsuario().getId());
        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        rs.buscasSolicitacoesCaronas(totalviewsAtual, totalBuscar, usuario, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                final List<Carona> caronas = (List<Carona>) object;
                if (caronas.size() > 0) {
                    for (int i = 0; i < caronas.size(); i++) {
                        final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas, null);
                        TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
                        TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);//pega os elemetos do modelo para setar dados
                        TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
                        final TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas3);
                        ImageButton btnClose = (ImageButton) modelo.findViewById(R.id.b_close_oferecida);

                        tv_destino.setText(caronas.get(i).getDestino());
                        tv_origem.setText(caronas.get(i).getOrigem());
                        tv_horario.setText(new Funcoes().horaSimples(caronas.get(i).getHorario()));
                        tv_vagas.setText((caronas.get(i).getVagas() - caronas.get(i).getVagasOcupadas()) + "/" + caronas.get(i).getVagas());

                        final LinearLayout ll = (LinearLayout) modelo.findViewById(R.id.caixa_partic);
                        final int m = i;
                        final List<Usuario> participantes = caronas.get(i).getParticipantes();
                        final List statusSolicitacao = caronas.get(i).getParticipantesStatus();

                        final int idCarona = caronas.get(i).getId();
                        final int idUsuario = M.getUsuario().getId();
                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequisicoesServidor rs3 = new RequisicoesServidor(activity);
                                rs3.fecharCaronaOferecida(idCarona, idUsuario, 1, new GetRetorno() {
                                    @Override
                                    public void concluido(Object object) {
                                        if (object.toString().equals("1")) {
                                            Toast.makeText(activity, R.string.alert_removido, Toast.LENGTH_SHORT).show();
                                            lloferecidas.removeView(modelo);
                                        } else if (object.toString().equals("0")) {
                                            Toast.makeText(activity, R.string.alert_car_ativa, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activity, object.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void concluido(Object object, Object object2) {

                                    }
                                });

                            }
                        });


                        for (int j = 0; j < participantes.size(); j++) {
                            final int k = j;
                            if (statusSolicitacao.get(j).equals("AGUARDANDO")) {
                                final RelativeLayout modelo2 = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_solicitadas, null);
                                TextView nomeSolicitante = (TextView) modelo2.findViewById(R.id.tv_destino_ACEITO2);//pega os elemetos do modelo para setar dados
                                TextView telefoneSolicitante = (TextView) modelo2.findViewById(R.id.c_telefone);
                                ImageView fotoSolicitante = (ImageView) modelo2.findViewById(R.id.c_foto);
                                ImageButton btnAceitar = (ImageButton) modelo2.findViewById(R.id.b_aceitar_usuario_carona);
                                ImageButton btnRecusar = (ImageButton) modelo2.findViewById(R.id.b_recusar_usuario_carona);
                                btnAceitar.setBackgroundResource(R.drawable.animacao);
                                btnRecusar.setBackgroundResource(R.drawable.animacao);
                                nomeSolicitante.setText(participantes.get(j).getNome());
                                telefoneSolicitante.setText(participantes.get(j).getTelefone());
                                byte[] decodedString = Base64.decode(participantes.get(j).getFoto(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Resources res = resource;
                                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                                dr.setCircular(true);
                                fotoSolicitante.setImageDrawable(dr);
                                fotoSolicitante.setScaleType(ImageView.ScaleType.FIT_XY);

                                int idSolicitante = participantes.get(j).getId();
                                final Usuario userAtual = new Usuario(idSolicitante);


                                modelo2.setId(j);
                                ll.addView(modelo2, 0);
                                btnAceitar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.setTitle(R.string.title_confirmacao)
                                                .setMessage(R.string.alert_aceitar_user)
                                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialoginterface, int i) {

                                                    }
                                                })
                                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialoginterface, int i) {
                                                        RequisicoesServidor rs2 = new RequisicoesServidor(activity);
                                                        rs2.aceitarRecusarCaronas(userAtual, "ACEITO", new GetRetorno() {
                                                            @Override
                                                            public void concluido(Object object) {
                                                                Toast.makeText(activity, (String) object, Toast.LENGTH_SHORT).show();
                                                                if (object.equals("Usuario Aceito!")) {
                                                                    ll.removeView(modelo2);
                                                                    RelativeLayout m = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_aceitas, null);
                                                                    TextView nomeSolicitante = (TextView) m.findViewById(R.id.tv_destino_ACEITO2);//pega os elemetos do modelo para setar dados
                                                                    TextView telefoneSolicitante = (TextView) m.findViewById(R.id.c_telefone);
                                                                    ImageView fotoSolicitante = (ImageView) m.findViewById(R.id.c_foto);
                                                                    nomeSolicitante.setText(participantes.get(k).getNome());
                                                                    telefoneSolicitante.setText(participantes.get(k).getTelefone());
                                                                    byte[] decodedString = Base64.decode(participantes.get(k).getFoto(), Base64.DEFAULT);
                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                                    Resources res = resource;
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
                                                }).show();
                                    }
                                });
                                btnRecusar.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                        dialog.setTitle(R.string.title_confirmacao)
                                                .setMessage(R.string.alert_recusar_user)
                                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialoginterface, int i) {


                                                    }
                                                })
                                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialoginterface, int i) {

                                                        RequisicoesServidor rs2 = new RequisicoesServidor(activity);
                                                        rs2.aceitarRecusarCaronas(userAtual, "RECUSADO", new GetRetorno() {
                                                            @Override
                                                            public void concluido(Object object) {
                                                                Toast.makeText(activity, (String) object, Toast.LENGTH_SHORT).show();
                                                                if (object.equals("Usuario Recusado!")) {
                                                                    tv_vagas.setText((caronas.get(m).getVagas() - (caronas.get(m).getVagasOcupadas() - 1)) + "/" + caronas.get(m).getVagas());
                                                                    ll.removeView(modelo2);
                                                                }
                                                            }

                                                            @Override
                                                            public void concluido(Object object, Object object2) {

                                                            }
                                                        });
                                                    }
                                                }).show();
                                    }
                                });

                                fotoSolicitante.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent it = new Intent(activity, DetalheUsuario.class);
                                        DetalheUsuario.usuarioEditar = participantes.get(k);
                                        startActivity(it);
                                    }
                                });


                            } else {
                                RelativeLayout modelo2 = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_aceitas, null);
                                TextView nomeSolicitante = (TextView) modelo2.findViewById(R.id.tv_destino_ACEITO2);//pega os elemetos do modelo para setar dados
                                TextView telefoneSolicitante = (TextView) modelo2.findViewById(R.id.c_telefone);
                                ImageView fotoSolicitante = (ImageView) modelo2.findViewById(R.id.c_foto);
                                nomeSolicitante.setText(participantes.get(j).getNome());
                                telefoneSolicitante.setText(participantes.get(j).getTelefone());
                                byte[] decodedString = Base64.decode(participantes.get(j).getFoto(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Resources res = resource;
                                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                                dr.setCircular(true);
                                fotoSolicitante.setImageDrawable(dr);

                                fotoSolicitante.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent it = new Intent(activity, DetalheUsuario.class);
                                        DetalheUsuario.usuarioEditar = participantes.get(k);
                                        startActivity(it);
                                    }
                                });
                                modelo2.setId(j);
                                ll.addView(modelo2, 0);
                            }

                        }

                        lloferecidas.addView(modelo);
                    }
                } else {
                    if (remover == false)
                        Toast.makeText(getActivity(), R.string.alert_0_caronas, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
        getContInflater();
    }


    private void getContInflater() {
        if (lloferecidas.getChildCount() >= 6) {
            recarrega.setVisibility(View.VISIBLE);
        } else {
            recarrega.setVisibility(View.INVISIBLE);
        }
        if (lloferecidas.getChildCount() == 0) {
            labelOferecidas.setVisibility(View.VISIBLE);
        } else {
            labelOferecidas.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (((MainActivity) activity).numNovasSolicitacoes > 0) {
            ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge3, 2);
        }

    }

    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            getContInflater();
            if (((MainActivity) activity).numNovasSolicitacoes > 0) {
                ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge3, 2);
                new Funcoes().apagarNotificacaoEspecifica(getActivity(), 3);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filter.addAction("abc");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
                Log.w("OK!", "Tried to unregister the reciver when it's not registered");
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String mensagem = intent.getStringExtra("mensagem");
            final String valor = intent.getStringExtra("valor");
            switch (mensagem) {
                case "solicitacao":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizarSolicitantes(0, 6, true);
                        }
                    });
                    break;
                case "myCarona":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizarSolicitantes(0, 6, true);
                        }
                    });
                    break;
            }
        }
    }

}


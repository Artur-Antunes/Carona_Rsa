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

import java.util.LinkedList;
import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class CoronasOferecidas extends Fragment {

    private FragmentActivity activity;
    private LinearLayout lloferecidas;
    private Resources resource;
    private View view;
    private ImageButton recarrega;
    private MyReceiver receiver;
    private TextView labelOferecidas;
    private final int ZERO=0;
    private final int SEIS=6;
    private IntentFilter filter = new IntentFilter();
    private AlertDialog.Builder dialog;
    private ManipulaDados M;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = getActivity();
        this.resource = getResources();
        this.M = new ManipulaDados(getActivity());
        this.view = inflater.inflate(R.layout.fragment_coronas_oferecidas, container, false);
        this.labelOferecidas = (TextView) view.findViewById(R.id.label3Vazio);
        this.recarrega = (ImageButton) view.findViewById(R.id.b_recarrega3);
        this.lloferecidas = (LinearLayout) view.findViewById(R.id.caixa_oferecidas);
        this.recarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarSolicitantes(lloferecidas.getChildCount(), 1, false);
            }
        });
        if (M.getUsuario() != null) {
            atualizarSolicitantes(ZERO, SEIS, true);
        }
        dialog = new AlertDialog.Builder(getActivity());
        receiver = new MyReceiver(new Handler());
        return view;
    }

    private RelativeLayout montarLayoutCarOf_parte1(final Carona carona) {
        final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas, null);
        TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
        TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);
        TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
        final TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas3);
        ImageButton btnClose = (ImageButton) modelo.findViewById(R.id.b_close_oferecida);
        tv_destino.setText(carona.getDestino());
        tv_origem.setText(carona.getOrigem());
        if (carona.getHorario().length() == 4 || carona.getHorario().length() == 5) {
            tv_horario.setText(carona.getHorario());
        } else {
            tv_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
        }
        tv_vagas.setText((carona.getVagas() - carona.getVagasOcupadas()) + "/" + carona.getVagas());
        modelo.setId(carona.getId());
        final List<Usuario> participantes = carona.getParticipantes();
        final List statusSolicitacao = carona.getParticipantesStatus();
        final LinearLayout llsecundario = (LinearLayout) modelo.findViewById(R.id.caixa_partic);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequisicoesServidor rs3 = new RequisicoesServidor(activity);
                rs3.fecharCaronaOferecida(carona.getId(), M.getUsuario().getId(), 1, new GetRetorno() {
                    @Override
                    public void concluido(Object object) {
                        if (object.toString().equals("1")) {
                            Toast.makeText(activity, R.string.alert_rmv, Toast.LENGTH_SHORT).show();
                            lloferecidas.removeView(modelo);
                        } else if (object.toString().equals("0")) {
                            Toast.makeText(activity, R.string.alert_car_at, Toast.LENGTH_SHORT).show();
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
            if (statusSolicitacao.get(j).equals("AGUARDANDO")) {
                final RelativeLayout modelo2 = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_solicitadas, null);
                TextView nomeSolicitante = (TextView) modelo2.findViewById(R.id.tv_destino_ACEITO2);
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
                final Usuario userAtual = new Usuario(participantes.get(j).getId());
                modelo2.setId(participantes.get(j).getId());
                llsecundario.addView(modelo2, 0);
                final int k=j;
                btnAceitar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (modelo.getId() == M.getCaronaOferecida().getId()) {
                            dialog.setTitle(R.string.title_conf)
                                    .setMessage(R.string.alert_act_user)
                                    .setNegativeButton(R.string.n, null)
                                    .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            participantes.get(k).setStatus("0_ACEITO");
                                            M.setParticipantesCarOferecida(participantes.get(k), M.getTtParCarOf());
                                            RelativeLayout modelo3 = montarLayoutCarOf_parte3(participantes.get(k));
                                            llsecundario.removeView(modelo2);
                                            llsecundario.addView(modelo3, 0);
                                            new Funcoes().apagarNotificacaoEspecifica(getActivity(), 3);
                                        }
                                    }).show();
                        } else {
                            llsecundario.removeView(modelo2);
                            Toast.makeText(activity, R.string.alert_car_dst, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                btnRecusar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.title_conf)
                                .setMessage(R.string.alert_rcs_user)
                                .setNegativeButton(R.string.n, null)
                                .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        participantes.get(k).setStatus("0_RECUSADO");
                                        M.setParticipantesCarOferecida(participantes.get(k), M.getTtParCarOf());
                                        RelativeLayout modelo3 = montarLayoutCarOf_parte3(participantes.get(k));
                                        llsecundario.removeView(modelo2);
                                        llsecundario.addView(modelo3, 0);
                                        new Funcoes().apagarNotificacaoEspecifica(getActivity(), 3);
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
                RelativeLayout modelo3 = montarLayoutCarOf_parte3(participantes.get(j));
                llsecundario.addView(modelo3, 0);
            }
        }
        return modelo;
    }



    private RelativeLayout montarLayoutCarOf_parte3(final Usuario user) {
        RelativeLayout modelo3 = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_aceitas, null);
        TextView nomeSolicitante = (TextView) modelo3.findViewById(R.id.tv_destino_ACEITO2);
        TextView telefoneSolicitante = (TextView) modelo3.findViewById(R.id.c_telefone);
        TextView status = (TextView) modelo3.findViewById(R.id.textViewSt2);
        ImageView fotoSolicitante = (ImageView) modelo3.findViewById(R.id.c_foto);
        ImageView imgAndamento = (ImageView) modelo3.findViewById(R.id.imageViewAnd3);
        nomeSolicitante.setText(user.getNome());
        telefoneSolicitante.setText(user.getTelefone());
        byte[] decodedString = Base64.decode(user.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Resources res = resource;
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCircular(true);
        fotoSolicitante.setImageDrawable(dr);
        modelo3.setId(user.getId());
        String stt = user.getStatus();
        String stt2 = user.getStatus();
        stt = stt.substring(0, 2);
        if (!stt.equals("0_")) {
            imgAndamento.setImageResource(R.drawable.icon_atok);
        }else{
            stt2 = stt2.substring(2,stt2.length());
        }
        status.setText(stt2);
        fotoSolicitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, DetalheUsuario.class);
                DetalheUsuario.usuarioEditar = user;
                startActivity(it);
            }
        });
        return modelo3;
    }


    public void atualizaAtualCaronaOferecida() {

        if (lloferecidas.getChildCount() > 0) {
            int idPrimeiroFrame = lloferecidas.getChildAt(0).getId();
            if (M.getCaronaOferecida().getId() == idPrimeiroFrame) {
                lloferecidas.removeViewAt(0);
            }
        }

        Carona car = M.getCaronaOferecida();
        final List<Usuario> participantes = new LinkedList<Usuario>();
        final List<String> statusParticipantes = new LinkedList<String>();

        int vagasOc=0;
        for (int i = 0; i < M.getTtParCarOf(); i++) {
            if (M.getParticipantesCarOferecida(i) != null) {
                participantes.add(M.getParticipantesCarOferecida(i));
                statusParticipantes.add(M.getParticipantesCarOferecida(i).getStatus());
                if(M.getParticipantesCarOferecida(i).getStatus().equals("ACEITO")){
                    vagasOc++;
                }
            }
        }

        car.setParticipantes(participantes);
        car.setParticipantesStatus(statusParticipantes);
        car.setVagasOcupadas(vagasOc);
        final RelativeLayout modelo = montarLayoutCarOf_parte1(car);
        lloferecidas.addView(modelo, 0);
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
                        final RelativeLayout modelo = montarLayoutCarOf_parte1(caronas.get(i));
                        lloferecidas.addView(modelo);
                    }
                } else {
                    if (remover == false)
                        Toast.makeText(getActivity(), R.string.alert_0_car, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
        getRecarrega();
    }


    private void getRecarrega() {
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


    public void criaBroadcastHome(String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abcHome");
        dialogIntent.putExtra("mensagem", tipo);
        activity.sendBroadcast(dialogIntent);
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
                            atualizaAtualCaronaOferecida();
                            //atualizarSolicitantes(0, 6, true);
                        }
                    });
                    break;
                case "myCarona":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizaAtualCaronaOferecida();
                        }
                    });
                    break;
            }
        }
    }

}


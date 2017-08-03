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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
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
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Home extends Fragment {
    LinearLayout ll;
    View view;
    FragmentActivity activity;
    Resources resource;
    ImageButton recarrega;
    TextView labelHome;
    SwipeRefreshLayout swipeLayout;
    public static FloatingActionButton load;
    public static FloatingActionButton newCarona;
    MyReceiver receiver;
    AlertDialog.Builder dialog;
    ManipulaDados m;
    ViewGroup container;
    IntentFilter filter = new IntentFilter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        this.container = container;
        activity = getActivity();
        resource = getResources();
        labelHome = (TextView) view.findViewById(R.id.label1Vazio);
        receiver = new MyReceiver(new Handler());
        dialog = new AlertDialog.Builder(getActivity());
        ll = (LinearLayout) view.findViewById(R.id.caixa_home);
        recarrega = (ImageButton) view.findViewById(R.id.b_recarrega);
        load = (FloatingActionButton) view.findViewById(R.id.b_atualiza);
        newCarona = (FloatingActionButton) view.findViewById(R.id.b_FloatNewCar);
        load.setVisibility(View.INVISIBLE);
        m = new ManipulaDados(activity);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                atualizaCaronaSolicitada();
                atualizaCaronas(0, 6, true);
                swipeLayout.setRefreshing(false);
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Verifique sua conexão...", Toast.LENGTH_LONG).show();
                load.setVisibility(View.INVISIBLE);
            }
        });

        newCarona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novaCarona();
                new Funcoes().apagarNotificacaoEspecifica(getActivity(), 5);
            }
        });
        recarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaCaronas(ll.getChildCount(), 3, false);
            }
        });

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        TabLayout.Tab tab1 = tabLayout.newTab();
        TabLayout.Tab tab2 = tabLayout.newTab();
        TabLayout.Tab tab3 = tabLayout.newTab();
        tab1.setCustomView(R.layout.tab);
        tab2.setCustomView(R.layout.tab);
        tab3.setCustomView(R.layout.tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (m.getUsuario() != null) {
            atualizaCaronas(0, 6, true);
            if (m.getCaronaSolicitada().getId() != -1) {
                atualizaCaronaSolicitada();
            }
        }
        return view;
    }

    private RelativeLayout montarLayoutCaronaDisponivel(final Carona car, final Usuario proprietario) {//DESENVOLVENDO..
        final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_disponiveis, null);
        TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);
        TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
        final TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas2);
        TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
        TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome);
        ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto);
        TextView tv_telefone = (TextView) modelo.findViewById(R.id.tv_telefone);
        Button btnSolicitar = (Button) modelo.findViewById(R.id.b_solicitar);
        Button btnComentar = (Button) modelo.findViewById(R.id.b_comentar_car);
        ImageView imgAndamento = (ImageView) modelo.findViewById(R.id.imageViewAndamento);
        tv_nome.setText(proprietario.getNome());
        tv_telefone.setText(proprietario.getTelefone());
        byte[] decodedString = Base64.decode(proprietario.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Resources res = resource;
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCircular(true);
        c_foto.setImageDrawable(dr);
        tv_destino.setText(car.getDestino());
        tv_origem.setText(car.getOrigem());
        if (car.getHorario().length() == 4) {
            tv_horario.setText("0" + car.getHorario());
        } else if (car.getHorario().length() == 5) {
            tv_horario.setText(car.getHorario());
        } else {
            tv_horario.setText(new Funcoes().horaSimples(car.getHorario()));
        }
        tv_vagas.setText(organizaVagas(car.getVagasOcupadas(), car.getVagas()));
        modelo.setId(car.getId());
        if (isMotorista(proprietario)) {
            btnSolicitar.setText("Cancelar");
            Drawable img = getContext().getResources().getDrawable(R.drawable.icon_cancel_car);
            img.setBounds(0, 0, 35, 35);
            btnSolicitar.setCompoundDrawables(img, null, null, null);
            modelo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_light));
            if (car.getId() == -2) {
                imgAndamento.setImageResource(R.drawable.icon_clock);
            }
        }
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentarios(car.getId());
            }
        });
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m.getUsuario().getId() != proprietario.getId()) {
                    if (m.getCaronaOferecida() == null) {
                        if (m.getCaronaSolicitada().getId() == -1) {
                            dialog.setTitle(R.string.title_confirmacao)
                                    .setMessage(R.string.alert_solicitar_carona)
                                    .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {


                                        }
                                    })
                                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            car.setProprietario(proprietario);
                                            car.setStatusUsuario("AGUARDANDO");
                                            car.setStatus(0);
                                            m.setCaronaSolicitada(car);
                                            atualizaCaronaSolicitada();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.alert_car_solicitada, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.alert_car_oferecida, Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(R.string.title_confirmacao)
                            .setMessage(R.string.alert_cancelar_carona)
                            .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {

                                }
                            })
                            .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                    rs.alteraStatusCarona(car.getId(), 0, new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            exibirMsg((String) object);
                                            ll.removeView(modelo);
                                            m.clearAtualCarOf();
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {
                                        }
                                    });
                                }
                            }).show();

                }
            }
        });
        modelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vagas = organizaVagas(car.getVagasOcupadas(), car.getVagas());
                detalhesCarona(proprietario, car, vagas);
            }
        });

        return modelo;
    }


    public void atualizaCaronaSolicitada() {
        if (m.getCaronaSolicitada().getId() != -1) {
            final Carona carona = m.getCaronaSolicitada();
            final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_recebidas, null);
            TextView ta_destino = (TextView) modelo.findViewById(R.id.tv_destinoR);
            TextView ta_status = (TextView) modelo.findViewById(R.id.tv_status_aguarda);
            TextView ta_horario = (TextView) modelo.findViewById(R.id.tv_horario_r);
            Button btnCancelar = (Button) modelo.findViewById(R.id.b_desistencia);
            Button btnComentar = (Button) modelo.findViewById(R.id.b_comentar_car2);
            ImageView imgLoad = (ImageView) modelo.findViewById(R.id.imageViewAndamento);
            if (carona.getStatus() == 1) {
                imgLoad.setImageResource(R.drawable.icon_atok);
            }
            ta_destino.setText(carona.getDestino());
            ta_status.setText(carona.getStatusUsuario());
            ta_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
            modelo.setId(carona.getId());
            final String vagasCarona = organizaVagas(carona.getVagasOcupadas(), carona.getVagas());
            if (verificaModeloAdd(modelo) != -1) {
                ll.removeViewAt(verificaModeloAdd(modelo));
                ll.addView(modelo, 0);
            } else {
                ll.addView(modelo, 0);
            }
            getExtra();
            getActivity();
            btnComentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comentarios(carona.getId());
                }
            });

            modelo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detalhesCarona(carona.getProprietario(), carona, vagasCarona);
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequisicoesServidor rserv = new RequisicoesServidor(getActivity());
                    rserv.desistirCarona(m.getUsuario().getId(), m.getCaronaSolicitada().getId(), new GetRetorno() {
                        @Override
                        public void concluido(Object object) {
                            Toast.makeText(getActivity(), object.toString(), Toast.LENGTH_LONG).show();
                            m.setCaronaSolicitada(new Carona(-1));
                            atualizaCaronas(0, 6, true);
                            ll.removeView(modelo);
                        }

                        @Override
                        public void concluido(Object object, Object object2) {

                        }
                    });
                }
            });
        }
        getExtra();
    }

    private boolean closePosicao_1(int id) {
        if (ll.getChildCount() > 0) {
            if (ll.getChildAt(0).getId() == id) {
                ll.removeViewAt(0);
                return true;
            }
        }
        return false;
    }

    private void removerSolicitacao() {
        closePosicao_1(m.getCaronaSolicitada().getId());
        m.setCaronaSolicitada(new Carona(-1));
        limparBadge();
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            if (((MainActivity) activity).numNovasCaronas > 0) {
                ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge1, 1);
            }
        }
    }


    private int verificaModeloAdd(RelativeLayout modelo) {
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (ll.getChildAt(i) != null) {
                if (ll.getChildAt(i).getId() == modelo.getId()) {
                    return i;
                }
            }
        }
        return -1;
    }


    private void getExtra() {
        if (ll.getChildCount() == 0) {
            labelHome.setVisibility(View.VISIBLE);
        } else {
            labelHome.setVisibility(View.INVISIBLE);
        }

        if (ll.getChildCount() >= 6) {
            recarrega.setVisibility(View.VISIBLE);
        } else {
            recarrega.setVisibility(View.INVISIBLE);
        }

        if ((m.getCaronaOferecida() != null) || (m.getCaronaSolicitada().getId() != -1) || (load.getVisibility() == View.VISIBLE)) {
            newCarona.setVisibility(View.INVISIBLE);
        } else {
            newCarona.setVisibility(View.VISIBLE);
        }

    }

    private void removeCaronasAntigas(List<Carona> caronas) {
        if (caronas.size() > 0) {
            int[] selecionaIds = new int[caronas.size()];
            for (int i = 0; i < caronas.size(); i++) {
                selecionaIds[i] = caronas.get(i).getId();
            }
            atualizaCaronasTela(selecionaIds);
        }
    }

    private void comentarios(int idCar) {
        Intent it = new Intent(getActivity(), ComentariosActivity.class);
        ComentariosActivity.idCarona = idCar;
        startActivity(it);
    }

    private void detalhesCarona(Usuario user, Carona car, String vagas) {
        Intent it = new Intent(getActivity(), Detalhes_Carona.class);
        Detalhes_Carona.usuario = user;
        Detalhes_Carona.carona = car;
        it.putExtra("vagas", vagas);
        startActivity(it);
    }

    private void atualizaCaronasTela(int[] ids) {
        boolean comparador;
        for (int j = 0; j < ll.getChildCount(); j++) {
            comparador = false;
            for (int i = 0; i < ids.length; i++) {
                if ((ll.getChildAt(j).getId() == ids[i]) && (m.getCaronaOferecida() != null)) {
                    if (ll.getChildAt(j).getId() != m.getCaronaOferecida().getId()) {
                        i = ids.length;
                        comparador = true;
                    }
                }
            }
            if (comparador == false) {
                ll.removeViewAt(j);
            }
        }
        getExtra();
        getActivity();
    }

    private boolean isMotorista(Usuario user) {
        if (m.getUsuario().getId() == user.getId()) {
            return true;
        } else {
            return false;
        }

    }

    public void atualizaCaronas(int ultNum, int ttViews, final boolean removerAntigas) {
        Servico.cntVerificaNovasCaronas = false;
        limparBadge();
        MainActivity.badge1.hide();
        final ManipulaDados M = new ManipulaDados(getActivity());
        if (M.getUsuario() != null) {
            RequisicoesServidor rs = new RequisicoesServidor(getActivity());
            rs.buscaCaronas(M.getUsuario(), ultNum, ttViews, new GetRetorno() {
                @Override
                public void concluido(Object object) {

                }

                @Override
                public void concluido(Object object, Object object2) {
                    final List<Carona> caronas = (List<Carona>) object;
                    if (caronas.size() > 0) {
                        final List<Usuario> usuarios = (List<Usuario>) object2;
                        if (removerAntigas) {
                            removeCaronasAntigas(caronas);
                        }
                        for (int i = 0; i < caronas.size(); i++) {
                            if (caronas.get(i).getId() == m.getCaronaSolicitada().getId()) {
                                continue;
                            }
                            RelativeLayout modelo = montarLayoutCaronaDisponivel(caronas.get(i), usuarios.get(i));
                            if (isMotorista(usuarios.get(i))) {
                                if (verificaModeloAdd(modelo) != -1) {
                                    ll.removeViewAt(verificaModeloAdd(modelo));
                                    ll.addView(modelo, 0);
                                } else {
                                    ll.addView(modelo, 0);
                                }
                            } else {
                                if (verificaModeloAdd(modelo) != -1) {
                                    ll.removeViewAt(verificaModeloAdd(modelo));
                                    ll.addView(modelo);
                                } else {
                                    ll.addView(modelo);
                                }
                            }
                        }
                    } else {
                        if (removerAntigas == false)
                            Toast.makeText(getActivity(), R.string.alert_0_caronas, Toast.LENGTH_SHORT).show();
                    }
                    setBusca();
                    getExtra();
                    Servico.cntVerificaNovasCaronas = true;
                }
            });
        }
    }

    private void setBusca() {
        int maiorId = m.getUltimoIdCarona();
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (maiorId < ll.getChildAt(i).getId()) {
                maiorId = ll.getChildAt(i).getId();
            }
        }
        m.gravarUltimaCarona(maiorId);
    }

    private void exibirMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void atualizaAtualCaronaOferecida() {
        final RelativeLayout modelo = montarLayoutCaronaDisponivel(m.getCaronaOferecida(), m.getUsuario());
        int t1 = verificaModeloAdd(modelo);
        if (t1 != -1) {
            ll.removeViewAt(t1);
            ll.addView(modelo, 0);
        } else if ((ll.getChildCount() > 0) && (ll.getChildAt(0) != null)) {
            int id = ll.getChildAt(0).getId();
            if (id == -2) {
                ll.removeViewAt(0);
                ll.addView(modelo, 0);
            }
        } else {
            ll.addView(modelo, 0);
        }
    }

    private void novaCarona() {
        if (load.getVisibility() == View.INVISIBLE) {
            if (m.getUsuario().getIdCaronaSolicitada() == -1 && m.getCaronaOferecida() == null) {
                startActivity(new Intent(getContext(), Criar_Carona.class));
            } else if (m.getUsuario().getIdCaronaSolicitada() != -1) {
                Toast.makeText(getActivity(), R.string.alert_car_solicitada, Toast.LENGTH_LONG).show();
            } else if (m.getCaronaOferecida() != null) {
                Toast.makeText(getActivity(), R.string.alert_car_oferecida, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.alert_sem_conexao, Toast.LENGTH_LONG).show();
        }
    }

    private String organizaVagas(int vagasOculpadas, int vagasTotal) {
        int vagasDisponiveis = vagasTotal - vagasOculpadas;
        String res = vagasDisponiveis + "/" + vagasTotal;
        return res;
    }


    @Override
    public void onStart() {
        super.onStart();
        ManipulaDados md = new ManipulaDados(getActivity());
        if (md.getUsuario() == null) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        } else {
            limparBadge();
        }
    }

    private void limparBadge() {
        ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge1, 1);
    }


    @Override
    public void onResume() {
        super.onResume();
        filter.addAction("abcHome");
        getExtra();
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                Log.w("OK!", "Tried to unregister the reciver when it's not registered");
            } else {
                throw e;
            }
        }
    }

    public void ultimasCaronas(final List<Carona> caronas, final List<Usuario> usuarios) {
        for (int i = 0; i < caronas.size(); i++) {
            if (caronas.get(i).getId() == m.getCaronaSolicitada().getId()) {
                continue;
            }
            RelativeLayout modelo = montarLayoutCaronaDisponivel(caronas.get(i), usuarios.get(i));
            if (m.getUsuario().getId() == usuarios.get(i).getId()) {
                m.setCaronaOferecida(caronas.get(i));
                int t1 = verificaModeloAdd(modelo);
                if (t1 != -1) {
                    ll.removeViewAt(t1);
                    ll.addView(modelo, 0);
                } else {
                    ll.addView(modelo, 0);
                }
            } else {
                int t2 = verificaModeloAdd(modelo);
                if (t2 != -1) {
                    ll.removeViewAt(t2);
                    if (m.getCaronaOferecida() == null && m.getCaronaSolicitada().getId() == -1) {
                        ll.addView(modelo, 0);
                    } else {
                        ll.addView(modelo);
                    }
                } else {
                    if (m.getCaronaOferecida() == null && m.getCaronaSolicitada().getId() == -1) {
                        ll.addView(modelo, 0);
                    } else {
                        ll.addView(modelo);
                    }
                }
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler;

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String mensagem = intent.getStringExtra("mensagem");
            final List<Carona> caronas = (List<Carona>) intent.getSerializableExtra("caronas");
            final List<Usuario> usuarios = (List<Usuario>) intent.getSerializableExtra("usuarios");
            switch (mensagem) {
                case "novaCarona":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            load.setVisibility(View.VISIBLE);
                            getExtra();
                        }
                    });

                    break;

                case "atSolicitacao":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizaCaronaSolicitada();
                        }
                    });
                    break;
                case "okSlt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new Funcoes().apagarNotificacaoEspecifica(getActivity(), 1);
                            if (closePosicao_1(m.getCaronaSolicitada().getId())) {
                                atualizaCaronaSolicitada();
                                exibirMsg("Carona solicitada!");
                            }
                        }
                    });
                    break;
                case "erro1Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (closePosicao_1(m.getCaronaSolicitada().getId())) {
                                m.setCaronaSolicitada(new Carona(-1));
                                exibirMsg("Essa carona não está mais disponível!");
                            }
                            new Funcoes().apagarNotificacaoEspecifica(activity, 1);
                        }
                    });
                    break;
                case "erro2Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (closePosicao_1(m.getCaronaSolicitada().getId())) {
                                exibirMsg("Sem vagas!");
                            }
                        }
                    });
                    break;
                case "erro3Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (closePosicao_1(m.getCaronaSolicitada().getId())) {
                                exibirMsg("Solicitação Recusada!");
                            }
                        }
                    });
                    break;
                case "nova(s)Carona(s)":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizaCaronas(0, 6, true);
                            getExtra();
                        }
                    });
                    break;
                case "ultima(s)Carona(s)":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ultimasCaronas(caronas, usuarios);
                            getExtra();
                        }
                    });
                    break;
                case "atCarOfertada":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            atualizaAtualCaronaOferecida();
                            getExtra();
                        }
                    });
                    break;
                case "closeSlt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            removerSolicitacao();
                        }
                    });
                    break;
                case "removeCaronaOferecida":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (closePosicao_1(m.getCaronaOferecida().getId())) {
                                ll.removeViewAt(0);
                                m.clearAtualCarOf();
                                getExtra();
                            }
                        }
                    });
                    break;

            }
        }
    }
}

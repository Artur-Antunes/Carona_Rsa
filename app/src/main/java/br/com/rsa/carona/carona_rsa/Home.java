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
import java.util.LinkedList;
import java.util.List;
import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Home extends Fragment {
    private LinearLayout ll;
    private View view;
    private FragmentActivity activity;
    private Resources resource;
    private ImageButton recarrega;
    private TextView labelHome;
    private SwipeRefreshLayout swipeLayout;
    public static FloatingActionButton load;
    public static FloatingActionButton newCarona;
    private MyReceiver receiver;
    private AlertDialog.Builder dialog;
    private ManipulaDados m;
    private ViewGroup container;
    private final int ZERO = 0;
    private final int SEIS = 6;
    private IntentFilter filter = new IntentFilter();

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
                if (m.getCaronaSolicitada() != null) {
                    antigaSolicitacao(m.getUsuario().getId(), m.getCaronaSolicitada().getId());
                }
                atualizaCaronas(ZERO, SEIS, true);
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
            if (m.getCaronaSolicitada() != null) {
                if (m.getCaronaSolicitada().getDestino() == null) {
                    antigaSolicitacao(m.getUsuario().getId(), m.getCaronaSolicitada().getId());
                }
            }
            atualizaCaronas(ZERO, SEIS, true);
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
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, ZERO, decodedString.length);
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

        c_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), DetalheUsuario.class);
                DetalheUsuario.usuarioEditar = proprietario;
                startActivity(it);
            }
        });

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
                        if (m.getCaronaSolicitada() == null) {
                            dialog.setTitle(R.string.title_conf)
                                    .setMessage(R.string.alert_slt_car)
                                    .setNegativeButton(R.string.n, null)
                                    .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            car.setProprietario(proprietario);
                                            car.setStatusUsuario("AGUARDANDO");
                                            car.setStatus(ZERO);
                                            m.setCaronaSolicitada(car);
                                            atualizaCaronaSolicitada();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.alert_car_slt, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.alert_car_ofd, Toast.LENGTH_LONG).show();
                    }
                } else {
                    dialog.setTitle(R.string.title_conf)
                            .setMessage(R.string.alert_cnl_car)
                            .setNegativeButton(R.string.n, null)
                            .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                    rs.alteraStatusCarona(car.getId(), ZERO, new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            exibirMsg((String) object);
                                            ll.removeView(modelo);
                                            m.clearAtualCarOf();
                                            getExtra();
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
        modelo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getActivity()).setNeutralButton("Detalhes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        final String vagas = organizaVagas(car.getVagasOcupadas(), car.getVagas());
                        detalhesCarona(proprietario, car, vagas);
                    }
                })
                        .setPositiveButton("Ocultar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                if (!isMotorista(proprietario)) {
                                    ll.removeView(modelo);
                                } else {
                                    Toast.makeText(getActivity(), "A carona não pode ser removida.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
                return true;
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

    public void antigaSolicitacao(int idUser, int idCar) {
        Servico.ativo = false;
        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        rs.aguardaRespostaCarona(idUser, idCar, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                Carona c = (Carona) object;
                m.setCaronaSolicitada(c);
                atualizaCaronaSolicitada();
                Servico.ativo = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }


    //Atualiza a solicitação
    public void atualizaCaronaSolicitada() {
        if (m.getCaronaSolicitada() != null) {
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

                    dialog.setTitle(R.string.title_conf)
                            .setMessage(R.string.alert_slt_car)
                            .setNegativeButton(R.string.n, null)
                            .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    RequisicoesServidor rserv = new RequisicoesServidor(getActivity());
                                    rserv.desistirCarona(m.getUsuario().getId(), m.getCaronaSolicitada().getId(), new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            Toast.makeText(getActivity(), object.toString(), Toast.LENGTH_LONG).show();
                                            m.setCaronaSolicitada(new Carona(-1));
                                            //atualizaCaronas(0, 6, true);
                                            ll.removeView(modelo);
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {

                                        }
                                    });
                                }
                            }).show();

                }
            });
        }
        getExtra();
    }

    //Método que faz o verifica a primeira posição de todas as caronas ofertadas e solicitadas
    private boolean isPosicao_1(int id) {
        if (ll.getChildCount() > 0) {
            if (ll.getChildAt(0).getId() == id) {
                return true;
            }
        }
        return false;
    }

    //Remover a solicitação que foi realizada pelo usuário
    private void removerSolicitacao() {
        if (isPosicao_1(m.getCaronaSolicitada().getId())) {
            ll.removeViewAt(0);
        }
        m.closeCaronaSolicitada();
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


    //Testa se um novo modelo pode ser adicionada
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


    //Atualiza os botões da tela Principal...
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

        if ((m.getCaronaOferecida() != null) || (m.getCaronaSolicitada() != null) || (load.getVisibility() == View.VISIBLE)) {
            if (m.getCaronaOferecida() != null) {
                Log.e("Tst1:", m.getCaronaOferecida().getId() + "<-");
            }

            if (m.getCaronaSolicitada() != null) {
                Log.e("Tst2:", m.getCaronaSolicitada().getId() + "<-");
            }

            if (load.getVisibility() == View.VISIBLE) {
                Log.e("Tst3:", "visible<-");
            }


            newCarona.setVisibility(View.INVISIBLE);
        } else {
            newCarona.setVisibility(View.VISIBLE);
        }

    }

    //Remove as caronas que foram desativadas...
    private void removeCaronasAntigas(List<Carona> caronas) {
        if (caronas.size() > 0) {
            int[] selecionaIds = new int[caronas.size()];
            for (int i = 0; i < caronas.size(); i++) {
                selecionaIds[i] = caronas.get(i).getId();
            }
            atualizaCaronasTela(selecionaIds);
        } else {
            int n = -9;
            if (m.getCaronaSolicitada() != null) {
                n = m.getCaronaSolicitada().getId();
            } else if (m.getCaronaOferecida() != null) {
                n = m.getCaronaOferecida().getId();
            }
            atualizaCaronasTela(n);
        }
    }

    //Inicia uma nova Activity com os comentários...
    private void comentarios(int idCar) {
        Intent it = new Intent(getActivity(), ComentariosActivity.class);
        ComentariosActivity.idCarona = idCar;
        startActivity(it);
    }

    //Inicia uma nova Activity com os detalhes da carona...
    private void detalhesCarona(Usuario user, Carona car, String vagas) {
        Intent it = new Intent(getActivity(), DetalhesCarona.class);
        DetalhesCarona.usuario = user;
        DetalhesCarona.carona = car;
        it.putExtra("vagas", vagas);
        startActivity(it);
    }

    //Atualiza os frames na página Home
    private void atualizaCaronasTela(int[] ids) {
        boolean comparador;
        boolean tCarOf = m.getCaronaOferecida() != null ? true : false;
        boolean tCarSol = m.getCaronaSolicitada() != null ? true : false;
        for (int j = 0; j < ll.getChildCount(); j++) {
            comparador = false;
            for (int i = 0; i < ids.length; i++) {
                if ((ll.getChildAt(j).getId() == ids[i])) {
                    if (tCarOf || tCarSol) {
                        i = ids.length;
                        comparador = true;
                    }
                }
            }
            if (!comparador) {
                ll.removeViewAt(j);
            }
        }
    }

    private void atualizaCaronasTela(int n) {
        for (int j = 0; j < ll.getChildCount(); j++) {
            int id = ll.getChildAt(j).getId();
            if (id != n) {
                ll.removeViewAt(j);
            }
        }
    }

    //Verifica se um usuário é o proprietário...
    private boolean isMotorista(Usuario user) {
        if (m.getUsuario().getId() == user.getId()) {
            return true;
        } else {
            return false;
        }

    }

    public void atualizaCaronas(int ultNum, int ttViews, final boolean removerAntigas) {
        Servico.ativo = false;
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
                            if (m.getCaronaSolicitada() != null) {
                                if (caronas.get(i).getId() == m.getCaronaSolicitada().getId()) {
                                    continue;
                                }
                            }
                            RelativeLayout modelo = montarLayoutCaronaDisponivel(caronas.get(i), usuarios.get(i));
                            if (isMotorista(usuarios.get(i))) {
                                if (verificaModeloAdd(modelo) != -1) {
                                    ll.removeViewAt(verificaModeloAdd(modelo));
                                    ll.addView(modelo, 0);
                                } else {
                                    ll.addView(modelo, 0);
                                }
                                caronas.get(i).setProprietario(usuarios.get(i));
                                m.setCaronaOferecida(caronas.get(i));
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
                        removeCaronasAntigas(caronas);
                    }
                    setBusca();
                    getExtra();
                    Servico.ativo = true;
                }
            });
        }
    }

    //Armazenar a ultima carona inserida(visualizada)
    private void setBusca() {
        int maiorId = m.getUltimoIdCarona();
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (maiorId < ll.getChildAt(i).getId()) {
                maiorId = ll.getChildAt(i).getId();
            }
        }
        m.gravarUltimaCarona(maiorId);
    }

    //Exibir uma messagem qualquer...
    private void exibirMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    //Atualiza os dados da carona oferecida pelo usuário...
    private void atualizaAtualCaronaOferecida() {
        Carona car = m.getCaronaOferecida();
        List<Usuario> participantes = new LinkedList<Usuario>();
        for (int i = 0; i < m.getTtParCarOf(); i++) {
            if (m.getParticipantesCarOferecida(i) != null) {
                if (m.getParticipantesCarOferecida(i).getStatus().equals("ACEITO")) {
                    participantes.add(m.getParticipantesCarOferecida(i));
                }
            }
        }

        if (participantes.size() > 0) {
            car.setParticipantes(participantes);
            car.setVagasOcupadas(participantes.size());
        }

        final RelativeLayout modelo = montarLayoutCaronaDisponivel(car, m.getUsuario());
        int t1 = verificaModeloAdd(modelo);
        if (t1 != -1) {
            ll.removeViewAt(t1);
            ll.addView(modelo, 0);
        } else if ((ll.getChildCount() > 0) && (ll.getChildAt(0) != null)) {
            int id = ll.getChildAt(0).getId();
            if (id == -2) {
                ll.removeViewAt(0);
                ll.addView(modelo, 0);
            } else {
                ll.addView(modelo, 0);
            }
        } else {
            ll.addView(modelo, 0);
        }
    }

    private void novaCarona() {
        if (load.getVisibility() == View.INVISIBLE) {
            if (m.getCaronaSolicitada() == null && m.getCaronaOferecida() == null) {
                startActivity(new Intent(getContext(), CriarCarona.class));
            } else if (m.getCaronaSolicitada() != null) {
                Toast.makeText(getActivity(), R.string.alert_car_slt, Toast.LENGTH_LONG).show();
            } else if (m.getCaronaOferecida() != null) {
                Toast.makeText(getActivity(), R.string.alert_car_ofd, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.alert_sem_con, Toast.LENGTH_LONG).show();
        }
    }

    //Organizar a visualização das vagas da carona
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
            if (caronas.get(i).getId() == m.getUsuario().getIdCaronaSolicitada()) {
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
                    if (m.getCaronaOferecida() == null && m.getCaronaSolicitada() != null) {
                        ll.addView(modelo, 0);
                    } else {
                        ll.addView(modelo);
                    }
                } else {
                    if (m.getCaronaOferecida() == null && m.getUsuario().getIdCaronaSolicitada() == -1) {
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
                            if (isPosicao_1(m.getCaronaSolicitada().getId())) {
                                ll.removeViewAt(0);
                                atualizaCaronaSolicitada();
                                exibirMsg("Carona solicitada!");
                                ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge1, 1);
                            }
                        }
                    });
                    break;
                case "erro1Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (m.getCaronaSolicitada() != null && isPosicao_1(m.getCaronaSolicitada().getId())) {
                                ll.removeViewAt(0);
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.title_atn)
                                        .setMessage(R.string.alert_slt_can)
                                        .setPositiveButton(R.string.k, null).show();
                                m.closeCaronaSolicitada();
                            }
                        }
                    });
                    break;
                case "erro2Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isPosicao_1(m.getCaronaSolicitada().getId())) {
                                ll.removeViewAt(0);
                                m.closeCaronaSolicitada();
                                exibirMsg("Sem vagas!");
                            }
                        }
                    });
                    break;
                case "erro3Slt":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isPosicao_1(m.getCaronaSolicitada().getId())) {
                                ll.removeViewAt(0);
                                m.closeCaronaSolicitada();
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
                case "remCarOf":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isPosicao_1(m.getCaronaOferecida().getId())) {
                                ll.removeViewAt(0);
                            }
                            m.clearAtualCarOf();
                            getExtra();
                        }
                    });
                    break;
            }
        }
    }
}

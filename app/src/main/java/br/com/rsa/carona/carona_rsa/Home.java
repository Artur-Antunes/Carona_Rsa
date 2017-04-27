package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.BadgeView;
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
    SwipeRefreshLayout swipeLayout;
    public static FloatingActionButton load;
    public static FloatingActionButton newCarona;
    int totalViews = 6;
    int ultimoNum = 0;
    MyReceiver receiver;
    AlertDialog.Builder dialog;
    ManipulaDados m;
    ViewGroup container;
    IntentFilter filter = new IntentFilter();
    public static int userCarOferecida;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        this.container = container;
        userCarOferecida = -1;//NENHUMA CARONA OFERECIDA
        Log.e("Home_adquiriu_1:", userCarOferecida + "");
        activity = getActivity();
        resource = getResources();
        receiver = new MyReceiver(new Handler());
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
        atualizaCaronas(0, totalViews, true);
        atualizarEspera();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userCarOferecida = -1;
                Log.e("Home_adquiriu_2:", userCarOferecida + "");
                atualizarEspera();
                atualizaCaronas(0, totalViews, true);
                swipeLayout.setRefreshing(false);
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCarOferecida = -1;
                Log.e("Home_adquiriu_3:", userCarOferecida + "");
                atualizarEspera();
                atualizaCaronas(0, totalViews, true);
                new Funcoes().apagarNotificacaoEspecifica(getActivity(), 5);
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
                atualizaCaronas(ultimoNum, 3, false);
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

        return view;
    }


    public void atualizarEspera() {
        if (m.getCaronaSolicitada() != -1) {
            final ManipulaDados M = new ManipulaDados(getActivity());
            if (M.getUsuario() != null) {
                final Usuario usuario = new Usuario(M.getUsuario().getId());
                Log.e("helder", "que doideira  " + M.getCaronaSolicitada());
                if (M.getCaronaSolicitada() != -1) {
                    Carona carona = new Carona(M.getCaronaSolicitada());
                    RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                    rs.aguardaRespostaCarona(usuario, carona, new GetRetorno() {

                        @Override
                        public void concluido(Object object) {
                            if (object != null) {
                                final List dados = (List) object;
                                final Carona carona = (Carona) dados.get(0);
                                final Usuario user = (Usuario) dados.get(1);
                                final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_recebidas, null);
                                TextView ta_destino = (TextView) modelo.findViewById(R.id.tv_destinoR);
                                TextView ta_status = (TextView) modelo.findViewById(R.id.tv_status_aguarda);
                                TextView ta_horario = (TextView) modelo.findViewById(R.id.tv_horario_r);
                                Button btnCancelar = (Button) modelo.findViewById(R.id.b_desistencia);
                                Button btnComentar = (Button) modelo.findViewById(R.id.b_comentar_car2);
                                ta_destino.setText(carona.getDestino());
                                ta_status.setText(carona.getStatusUsuario());
                                ta_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
                                modelo.setId(carona.getId());

                                if (verificaModeloAdd(modelo) != -1) {
                                    ll.removeViewAt(verificaModeloAdd(modelo));
                                    ll.addView(modelo, 0);
                                } else {
                                    ll.addView(modelo, 0);
                                }

                                btnComentar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        comentarios(carona.getId());
                                    }
                                });

                                modelo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        detalhesCarona(user, carona);
                                    }
                                });

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
                                                atualizaCaronas(0, totalViews, true);
                                                ll.removeView(modelo);
                                                getContInflater();
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

                }
            }
        } else {
            if (ll.getChildAt(0) != null) {
                if (userCarOferecida == -1) {
                    ll.removeViewAt(0);//REMOVENDO MODELO POSIÇÃO 0
                    Log.e("Home_adquiriu_5:", userCarOferecida + "");
                }
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


    private void getContInflater() {
        if (ll.getChildCount() >= 6) {
            recarrega.setVisibility(View.VISIBLE);
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

    private void detalhesCarona(Usuario user, Carona car) {
        Intent it = new Intent(getActivity(), Detalhes_Carona.class);
        Detalhes_Carona.usuario = user;
        Detalhes_Carona.carona = car;
        startActivity(it);
    }

    private void atualizaCaronasTela(int[] ids) {
        boolean comparador;
        for (int j = 0; j < ll.getChildCount(); j++) {
            comparador = false;
            for (int i = 0; i < ids.length; i++) {
                if ((ll.getChildAt(j).getId() == ids[i]) && (ll.getChildAt(j).getId() != userCarOferecida)) {
                    i = ids.length;
                    comparador = true;
                }
            }
            if (comparador == false) {
                ll.removeViewAt(j);
            }
        }
    }


    public void atualizaCaronas(int ultNum, int ttViews, final boolean removerAntigas) {
        load.setVisibility(View.INVISIBLE);
        MainActivity.badge1.hide();
        final ManipulaDados M = new ManipulaDados(getActivity());
        if (M.getUsuario() != null) {
            RequisicoesServidor rs = new RequisicoesServidor(getActivity());
            getContInflater();
            rs.buscaCaronas(M.getUsuario(), ultNum, ttViews, new GetRetorno() {
                @Override
                public void concluido(Object object) {

                }

                @Override
                public void concluido(Object object, Object object2) {
                    final List<Carona> caronas = (List<Carona>) object;
                    final List<Usuario> usuarios = (List<Usuario>) object2;
                    if (removerAntigas) {
                        removeCaronasAntigas(caronas);
                    }
                    for (int i = 0; i < caronas.size(); i++) {
                        if (caronas.get(i).getId() == M.getCaronaSolicitada()) {
                            continue;
                        }
                        final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_disponiveis, null);
                        TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);//pega os elemetos do modelo para setar dados
                        TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
                        TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas2);
                        TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
                        TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome);
                        ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto);
                        TextView tv_telefone = (TextView) modelo.findViewById(R.id.tv_telefone);
                        Button btnSolicitar = (Button) modelo.findViewById(R.id.b_solicitar);
                        Button btnComentar = (Button) modelo.findViewById(R.id.b_comentar_car);

                        tv_nome.setText(usuarios.get(i).getNome());
                        tv_telefone.setText(usuarios.get(i).getTelefone());
                        byte[] decodedString = Base64.decode(usuarios.get(i).getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        Resources res = resource;
                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
                        dr.setCircular(true);
                        c_foto.setImageDrawable(dr);
                        tv_destino.setText(caronas.get(i).getDestino());
                        tv_origem.setText(caronas.get(i).getOrigem());
                        tv_horario.setText(new Funcoes().horaSimples(caronas.get(i).getHorario()));
                        tv_vagas.setText((caronas.get(i).getVagas() - caronas.get(i).getVagasOcupadas()) + "/" + caronas.get(i).getVagas() + "");
                        modelo.setId(caronas.get(i).getId());

                        if (M.getUsuario().getId() == usuarios.get(i).getId()) {
                            btnSolicitar.setText("Cancelar");
                            Drawable img = getContext().getResources().getDrawable(R.drawable.icon_cancel_car);
                            img.setBounds(0, 0, 35, 35);
                            btnSolicitar.setCompoundDrawables(img, null, null, null);
                            userCarOferecida = modelo.getId();
                            Log.e("Home_adquiriu_8.1:", userCarOferecida + "");
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


                        final int id_carona = caronas.get(i).getId();
                        getContInflater();
                        final int j = i;

                        //final int finalI = i;
                        btnComentar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                comentarios(id_carona);
                            }
                        });


                        btnSolicitar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final ManipulaDados md = new ManipulaDados(getActivity());
                                if (M.getUsuario().getId() != usuarios.get(j).getId()) {
                                    if (userCarOferecida == -1) {
                                        Log.e("Home_adquiriu_9.1:", userCarOferecida + "");
                                        if (md.getCaronaSolicitada() == -1) {
                                            dialog.setTitle(R.string.title_confirmacao)
                                                    .setMessage(R.string.alert_solicitar_carona)
                                                    .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialoginterface, int i) {


                                                        }
                                                    })
                                                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialoginterface, int i) {
                                                            Usuario eu = md.getUsuario();
                                                            Carona carona = caronas.get(j);
                                                            RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                                            rs.solicitaCarona(carona, eu, new GetRetorno() {

                                                                @Override
                                                                public void concluido(Object object) {
                                                                    if (object.toString().trim().equals("1")) {
                                                                        md.setCaronaSolicitada(id_carona);
                                                                        atualizarEspera();
                                                                        ll.removeView(modelo);
                                                                        exibirMsg("Carona solicitada!");
                                                                        new Funcoes().apagarNotificacaoEspecifica(activity, 1);
                                                                    } else if (object.toString().trim().equals("2")) {
                                                                        ll.removeView(modelo);
                                                                        exibirMsg("Essa carona não está mais disponível!");
                                                                        new Funcoes().apagarNotificacaoEspecifica(activity, 1);
                                                                    } else {
                                                                        exibirMsg(object.toString());
                                                                    }
                                                                }

                                                                @Override
                                                                public void concluido(Object object, Object object2) {

                                                                }
                                                            });
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
                                                    rs.alteraStatusCarona(caronas.get(j).getId(), 0, new GetRetorno() {
                                                        @Override
                                                        public void concluido(Object object) {
                                                            userCarOferecida = -1;
                                                            Log.e("Home_adquiriu_9.2:", userCarOferecida + "");
                                                            exibirMsg((String) object);
                                                            ll.removeView(modelo);
                                                            exibirBtnAdd();
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
                                detalhesCarona(usuarios.get(j), caronas.get(j));
                            }
                        });
                    }
                    ultimoNum = ll.getChildCount();
                    exibirBtnAdd();
                }
            });

        }
    }

    private void exibirMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void novaCarona() {
        if (load.getVisibility() == View.INVISIBLE) {
            if (m.getUsuario().getIdCaronaSolicitada() == -1 && Home.userCarOferecida == -1) {
                startActivity(new Intent(getContext(), Criar_Carona.class));
            } else if (m.getUsuario().getIdCaronaSolicitada() != -1) {
                Toast.makeText(getActivity(), R.string.alert_car_solicitada, Toast.LENGTH_LONG).show();
            } else if (Home.userCarOferecida != -1) {
                Toast.makeText(getActivity(), R.string.alert_car_oferecida, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.alert_sem_conexao, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        ManipulaDados md = new ManipulaDados(getActivity());
        if (md.getUsuario() == null) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        } else {
            Log.e("JJJJJJJJJJ", "FFFFOOOOOOIIIII ATIVADO");
            if (((MainActivity) getActivity()).numNovasCaronas > 0) {
                ((MainActivity) getActivity()).LimparBadge(((MainActivity) getActivity()).badge1, 1);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        filter.addAction("abcHome");
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

    private void exibirBtnAdd() {
        if (userCarOferecida != -1 || m.getCaronaSolicitada() != -1 || load.getVisibility() == View.VISIBLE) {
            newCarona.setVisibility(View.INVISIBLE);
        } else {
            newCarona.setVisibility(View.VISIBLE);
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
            switch (mensagem) {
                case "novaCarona":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            load.setVisibility(View.VISIBLE);
                            exibirBtnAdd();
                        }
                    });

                    break;

                case "atSolicitacao":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("atSolicitacao", "ATIVADO");
                            atualizarEspera();
                            exibirBtnAdd();
                        }
                    });
                    break;

                case "nova(s)Carona(s)":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("atNovo", "ATIVADO");
                            atualizaCaronas(0, totalViews, true);
                            exibirBtnAdd();
                        }
                    });
                    break;
                case "removeCaronaOferecida":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (ll.getChildAt(0) != null) {
                                if (ll.getChildAt(0).getId() == userCarOferecida) {
                                    ll.removeViewAt(0);
                                    userCarOferecida = -1;
                                    exibirBtnAdd();
                                    Log.e("Home_adquiriu_10:", userCarOferecida + "");
                                }
                            }
                        }
                    });
                    break;
            }
        }
    }
}

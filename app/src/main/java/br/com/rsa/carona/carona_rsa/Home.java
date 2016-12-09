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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.BadgeView;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
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
    int totalViews = 3;
    int ultimoNum = 0;
    MyReceiver receiver;
    AlertDialog.Builder dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        resource = getResources();
        receiver = new MyReceiver(new Handler());
        dialog = new AlertDialog.Builder(getActivity());
        ll = (LinearLayout) view.findViewById(R.id.caixa_home);
        recarrega = (ImageButton) view.findViewById(R.id.b_recarrega);
        load = (FloatingActionButton) view.findViewById(R.id.b_atualiza);
        ManipulaDados m = new ManipulaDados(getActivity());
        if (m.getUsuario() != null) {
            atualizaCaronas();
            atualizarEspera();
        }
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                atualizarEspera();
                atualizaCaronas();
                swipeLayout.setRefreshing(false);
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaCaronas();
            }
        });
        recarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaCaronas2();
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
                        final Carona carona = (Carona) object;
                        if (carona != null) {
                            final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas_recebidas, null);
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
                                            ultimoNum = 0;
                                            atualizaCaronas();

                                            ll.removeView(modelo);
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {

                                        }
                                    });
                                }
                            });
                        }else{
                            M.setCaronaSolicitada(-1);
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


    public void atualizaCaronas2() {
        load.setVisibility(View.INVISIBLE);
        final ManipulaDados M = new ManipulaDados(getActivity());

        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        rs.buscaCaronas(M.getUsuario(), ultimoNum, totalViews, new GetRetorno() {
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
                    TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);//pega os elemetos do modelo para setar dados
                    TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
                    TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas2);
                    TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
                    TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome);
                    ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto);
                    TextView tv_telefone = (TextView) modelo.findViewById(R.id.tv_telefone);
                    Button btnSolicitar = (Button) modelo.findViewById(R.id.b_solicitar);
                    if (M.getUsuario().getId() != usuarios.get(i).getId()) {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao);
                    } else {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao_remover);
                        btnSolicitar.setCompoundDrawables(getResources().getDrawable(R.drawable.icon_not), null, null, null);
                        btnSolicitar.setText("CANCELAR");
                    }
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
                    tv_horario.setText(caronas.get(i).getHorario());
                    tv_vagas.setText((caronas.get(i).getVagas() - caronas.get(i).getVagasOcupadas()) + "/" + caronas.get(i).getVagas() + "");
                    final int id_carona = caronas.get(i).getId();

                    modelo.setId(i + 1);
                    ll.addView(modelo);
                    final int j = i;

                    btnSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ManipulaDados md = new ManipulaDados(getActivity());
                            //teste aqui -
                            if (M.getUsuario().getId() != usuarios.get(j).getId()) {
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

                                                }
                                            }).show();

                                } else {
                                    Toast.makeText(getActivity(), " Você já tem uma carona solicitada ! ", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setTitle(R.string.title_confirmacao)
                                        .setMessage("teste")
                                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                startActivity(new Intent(getActivity(), ExibirDadosUsuarioActivity.class));
                                            }
                                        })
                                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
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
                                        }).show();
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
                ultimoNum += caronas.size();
            }
        });
    }

    public void atualizaCaronas() {
        load.setVisibility(View.INVISIBLE);
        final ManipulaDados M = new ManipulaDados(getActivity());

        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        ll.removeAllViews();
        rs.buscaCaronas(M.getUsuario(), 0, totalViews, new GetRetorno() {
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
                    TextView tv_origem = (TextView) modelo.findViewById(R.id.tv_origem2);//pega os elemetos do modelo para setar dados
                    TextView tv_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
                    TextView tv_vagas = (TextView) modelo.findViewById(R.id.tv_vagas2);
                    TextView tv_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
                    TextView tv_nome = (TextView) modelo.findViewById(R.id.tv_nome);
                    ImageView c_foto = (ImageView) modelo.findViewById(R.id.c_foto);
                    TextView tv_telefone = (TextView) modelo.findViewById(R.id.tv_telefone);
                    Button btnSolicitar = (Button) modelo.findViewById(R.id.b_solicitar);
                    if (M.getUsuario().getId() != usuarios.get(i).getId()) {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao);
                    } else {
                        btnSolicitar.setBackgroundResource(R.drawable.cor_botao_remover);
                        Drawable img = getContext().getResources().getDrawable(R.drawable.icon_not);
                        img.setBounds(0, 0, 60, 60);
                        btnSolicitar.setCompoundDrawables(img, null, null, null);
                        btnSolicitar.setText("CANCELAR");
                    }
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
                    tv_horario.setText(caronas.get(i).getHorario());
                    tv_vagas.setText((caronas.get(i).getVagas() - caronas.get(i).getVagasOcupadas()) + "/" + caronas.get(i).getVagas() + "");
                    final int id_carona = caronas.get(i).getId();
                    modelo.setId(i + 1);
                    ll.addView(modelo);
                    final int j = i;
                    btnSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ManipulaDados md = new ManipulaDados(getActivity());
                            //teste aqui -
                            if (M.getUsuario().getId() != usuarios.get(j).getId()) {
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
                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(getActivity(), " Você já tem uma carona solicitada ! ", Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                                        atualizaCaronas();
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
                            Intent it = new Intent(getActivity(), Detalhes_Carona.class);
                            Detalhes_Carona.usuario = usuarios.get(j);
                            Detalhes_Carona.carona = caronas.get(j);
                            startActivity(it);

                        }
                    });
                }
                ultimoNum += caronas.size();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("JJJJJJJJJJ", "FFFFOOOOOOIIIII ATIVADO");
        if (((MainActivity) getActivity()).numNovasCaronas > 0) {
            ((MainActivity) getActivity()).LimparBadge(((MainActivity) getActivity()).badge1, 1);
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
                case "novaCarona":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            load.setVisibility(View.VISIBLE);
                        }
                    });

                    break;

            }
        }
    }
}

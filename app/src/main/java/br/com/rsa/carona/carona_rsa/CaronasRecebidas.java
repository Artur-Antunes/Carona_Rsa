package br.com.rsa.carona.carona_rsa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class CaronasRecebidas extends Fragment {

    private View view;
    private LinearLayout ll;
    private FragmentActivity activity;
    private ManipulaDados M;
    private MyReceiver receiver;
    private ImageButton recarrega;
    private TextView labelRecebidas;
    private int utmIdCarIncluida = -2;//Ultima carona exibida
    private IntentFilter filter = new IntentFilter();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_caronas__recebidas, container, false);
        ll = (LinearLayout) view.findViewById(R.id.caixa_aceito);
        recarrega = (ImageButton) view.findViewById(R.id.b_recarrega2);
        labelRecebidas = (TextView) view.findViewById(R.id.label2Vazio);
        recarrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarCaronasAceitas(ll.getChildCount(), 1, false);
            }
        });
        M = new ManipulaDados(getActivity());
        receiver = new MyReceiver(new Handler());
        atualizarCaronasAceitas(0, 6, true);
        return view;
    }

    private RelativeLayout  montarLayoutCar(final Carona carona){
        final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_caronas, null);
        TextView ta_origem = (TextView) modelo.findViewById(R.id.tv_origem2);
        TextView ta_destino = (TextView) modelo.findViewById(R.id.tv_destino2);
        TextView ta_horario = (TextView) modelo.findViewById(R.id.tv_horario2);
        TextView ta_aceito = (TextView) modelo.findViewById(R.id.tv_vagas3);
        TextView img5 = (TextView) modelo.findViewById(R.id.textV5);
        ta_origem.setText(carona.getOrigem());
        ta_destino.setText(carona.getDestino());
        img5.setVisibility(View.INVISIBLE);
        ta_aceito.setText("ACEITO");
        int color = ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark);
        ta_aceito.setTextColor(color);
        ta_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
        ImageButton btnClose = (ImageButton) modelo.findViewById(R.id.b_close_oferecida);
        modelo.setId(carona.getId());
        final int idCarona = carona.getId();
        final int idUsuario = M.getUsuario().getId();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (M.getCaronaSolicitada().getId() != idCarona) {//ERRO AQUI<-----
                    RequisicoesServidor rs3 = new RequisicoesServidor(activity);
                    rs3.fecharCaronaOferecida(idCarona, idUsuario, 2, new GetRetorno() {
                        @Override
                        public void concluido(Object object) {
                            if (object.toString().equals("1")) {
                                Toast.makeText(activity, R.string.alert_rmv, Toast.LENGTH_SHORT).show();
                                ll.removeView(modelo);
                                getRecarrega();
                                getRecebidas();
                            } else if (object.toString().equals("0")) {
                                Toast.makeText(activity, "Erro ao tentar executar está ação!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void concluido(Object object, Object object2) {

                        }
                    });
                } else {
                    Toast.makeText(activity, R.string.alert_car_at, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return modelo;
    }

    public void atualizarCaronasAceitas(int totalviewsAtual, int totalBuscar, final boolean remover) {
        if (M.getUsuario() != null) {
            if (remover) {
                ll.removeAllViews();
            }
            final Usuario usuario = new Usuario(M.getUsuario().getId());
            RequisicoesServidor rs = new RequisicoesServidor(getActivity());
            rs.exibirMinhasSolicitações(totalviewsAtual, totalBuscar, usuario, new GetRetorno() {
                @Override
                public void concluido(Object object) {
                    final List<Carona> caronas = (List<Carona>) object;
                    if (caronas != null) {
                        for (int i = 0; i < caronas.size(); i++) {
                            final RelativeLayout modelo = montarLayoutCar(caronas.get(i));
                            ll.addView(modelo);
                            utmIdCarIncluida = (ll.getChildCount() > 0) ? ll.getChildAt(0).getId() : -2;
                        }
                    } else if (caronas == null && remover == false)
                        Toast.makeText(getActivity(), R.string.alert_0_car, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void concluido(Object object, Object object2) {

                }
            });
        }
        getRecarrega();
        getRecebidas();
    }

    private void getRecarrega() {
        if (ll.getChildCount() >= 6) {
            recarrega.setVisibility(View.VISIBLE);
        } else {
            recarrega.setVisibility(View.INVISIBLE);
        }
    }

    private void getRecebidas() {
        if (ll.getChildCount() == 0) {
            labelRecebidas.setVisibility(View.VISIBLE);
        } else {
            labelRecebidas.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            limparBadge();
            getRecarrega();
            getRecebidas();
        }
    }

    private void limparBadge() {
        if (((MainActivity) activity).numCarAceita > 0) {
            ((MainActivity) activity).LimparBadge(((MainActivity) activity).badge2, 3);
            new Funcoes().apagarNotificacaoEspecifica(getActivity(), 2);
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
                case "solicitacao_aceita":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (M.getUltimoIdCaronaAceita() != utmIdCarIncluida) {
                                atualizarCaronasAceitas(0, 6, true);
                            }
                        }
                    });
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
                Log.w("oiooi", "Tried to unregister the reciver when it's not registered");
            } else {
                throw e;
            }
        }
    }


}

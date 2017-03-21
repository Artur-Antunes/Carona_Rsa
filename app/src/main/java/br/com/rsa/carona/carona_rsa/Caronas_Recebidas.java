package br.com.rsa.carona.carona_rsa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Caronas_Recebidas extends Fragment {
    View view;
    LinearLayout ll;
    FragmentActivity activity;
    ManipulaDados M;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_caronas__recebidas, container, false);
        ll = (LinearLayout) view.findViewById(R.id.caixa_aceito);
        M = new ManipulaDados(getActivity());
        atualizarCaronasAceitas();
        return view;
    }

    public void atualizarCaronasAceitas() {
        if (M.getUsuario() != null) {
            final Usuario usuario = new Usuario(M.getUsuario().getId());
            RequisicoesServidor rs = new RequisicoesServidor(getActivity());
            rs.exibirMinhasSolicitações(usuario, new GetRetorno() {
                @Override
                public void concluido(Object object) {
                    final List<Carona> caronas = (List<Carona>) object;
                    if (caronas != null) {
                        for (int i = 0; i < caronas.size(); i++) {
                            final RelativeLayout modelo = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.modelo_minhas_caronas, null);
                            TextView ta_destino = (TextView) modelo.findViewById(R.id.minha_carona_DESTINO);
                            TextView ta_horario = (TextView) modelo.findViewById(R.id.minha_carona_SAIDA);
                            ta_destino.setText(caronas.get(i).getDestino());
                            ta_horario.setText(caronas.get(i).getHorario());
                            modelo.setId(i);
                            ll.addView(modelo, 0);
                        }
                    }
                }

                @Override
                public void concluido(Object object, Object object2) {

                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            if (M.getCaronaSolicitada()!=-1){
                atualizarCaronasAceitas();
            }
        }
    }


}

package br.com.rsa.carona.carona_rsa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class Home extends Fragment {
     LinearLayout ll;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_home, container, false);
        ll=(LinearLayout)view.findViewById(R.id.caixa_home);

         atualizaCaronas();

        return view;
    }


    public void atualizaCaronas(){

        RequisicoesServidor rs = new RequisicoesServidor(getActivity());
        ll.removeAllViews();
            rs.buscaCaronas(null, new GetRetorno() {
                @Override
                public void concluido(Object object) {
                    final List<Carona> caronas = (List<Carona>) object;

                    for (int i=0; i< caronas.size();i++){
                        final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_disponiveis, null);
                        TextView tv_origem=(TextView) modelo.findViewById(R.id.tv_origem);
                        TextView tv_destino=(TextView) modelo.findViewById(R.id.tv_destino);
                        TextView tv_vagas=(TextView) modelo.findViewById(R.id.tv_vagas);
                        TextView tv_horario=(TextView) modelo.findViewById(R.id.tv_horario);
                        tv_destino.setText(caronas.get(i).getDestino());
                        tv_origem.setText(caronas.get(i).getOrigem());
                        tv_horario.setText(caronas.get(i).getHorario());
                        tv_vagas.setText(caronas.get(i).getVagas()+"");
                        modelo.setId(i);
                        ll.addView(modelo,0);

                    }
                }
            });


    }
}

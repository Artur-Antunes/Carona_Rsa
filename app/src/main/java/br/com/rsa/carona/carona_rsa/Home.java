package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

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
        ManipulaDados M= new ManipulaDados(getActivity());
        Log.e("ppppppppppp",M.getUsuario().getId()+"");
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

                    for (int i=0; i< caronas.size();i++){
                        //pega layout modelo de coronas
                        final RelativeLayout modelo = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.modelo_caronas_disponiveis, null);
                        TextView tv_origem=(TextView) modelo.findViewById(R.id.tv_origem);//pega os elemetos do modelo para setar dados
                        TextView tv_destino=(TextView) modelo.findViewById(R.id.tv_destino);
                        TextView tv_vagas=(TextView) modelo.findViewById(R.id.tv_vagas);
                        TextView tv_horario=(TextView) modelo.findViewById(R.id.tv_horario);
                        Button btnSolicitar=(Button) modelo.findViewById(R.id.b_solicitar);
                        tv_destino.setText(caronas.get(i).getDestino());
                        tv_origem.setText(caronas.get(i).getOrigem());
                        tv_horario.setText(caronas.get(i).getHorario());
                        tv_vagas.setText(caronas.get(i).getVagas() + "");
                        final int id_carona=caronas.get(i).getId();
                        Log.e("id_carona:", id_carona + "");


                        modelo.setId(i);
                        ll.addView(modelo, 0);
                        final  int j= i;
                        btnSolicitar.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                final ManipulaDados md = new ManipulaDados(getActivity());
                                //teste aqui ->
                                if(md.getCaronaSolicitada()==-1){
                                Usuario eu = md.getUsuario();
                                Carona carona = caronas.get(j);
                                RequisicoesServidor rs = new RequisicoesServidor(getActivity());
                                rs.solicitaCarona(carona, eu, new GetRetorno() {
                                    @Override
                                    public void concluido(Object object) {
                                        Toast.makeText(getActivity(), (String) object, Toast.LENGTH_SHORT).show();
                                        if(object.toString().equals("Carona Solicitada Com Sucesso!")) {
                                            md.setCaronaSolicitada(id_carona);
                                        }
                                    }

                                    @Override
                                    public void concluido(Object object, Object object2) {

                                    }
                                });
                            }else{
                                    Toast.makeText(getActivity()," Você já tem uma carona solicitada ! ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        modelo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(getActivity(), Detalhes_Carona.class);
                                Detalhes_Carona.usuario= usuarios.get(j);
                                Detalhes_Carona.carona = caronas.get(j);
                                startActivity(it);

                            }
                        });


                    }
                }
            });


    }
}

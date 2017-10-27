package br.com.rsa.carona.carona_rsa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;

public class Testador extends AppCompatActivity {
    AlertDialog actions;
    private int valor = -1;
    ManipulaDados mDados;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequisicoesServidor rs= new RequisicoesServidor(Testador.this);
        rs.aguardaRespostaCarona(12, 87, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                if(object==null){
                    Log.e("nulo","sim");
                }else{
                    Log.e("nulo","não");
                    Carona c= (Carona) object;
                    Log.e("salvar",""+c.getId());
                    Log.e("salvar", ""+ c.getDestino());
                }

            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }

}

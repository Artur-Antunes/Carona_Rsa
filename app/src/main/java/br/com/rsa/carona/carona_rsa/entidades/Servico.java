package br.com.rsa.carona.carona_rsa.entidades;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Messenger;
import android.util.Base64;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import br.com.rsa.carona.carona_rsa.R;
import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;

/**
 * Created by josehelder on 14/11/2016.
 */
public class Servico extends IntentService {
    private int cont;
    private boolean ativo;
    public Servico() {
        super("Servico");
        ativo=true;
        cont=0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

         while (ativo && cont <1){
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             verificaNovasCaronas();
             cont++;
             Log.e("testando", "cont"+cont);
         }
        ativo=true;
        cont=0;
    }
    public void verificaNovasCaronas(){
        RequisicoesServidor rs = new RequisicoesServidor(this);
        final ManipulaDados md = new ManipulaDados(this);
        final Funcoes f = new Funcoes();
        int ultimoIdCarona=md.getUltimoIdCarona();
        final Usuario usuario = md.getUsuario();
        Log.e("ééééeé", "verificaNovasCaronas "+ultimoIdCarona);
        rs.buscaUltimasCaronas(usuario, ultimoIdCarona, new GetRetorno() {
            @Override
            public void concluido(Object object) {

            }

            @Override
            public void concluido(Object object, Object object2) {
                  List<Usuario> usuarios =(List<Usuario>)object2;
                  List<Carona> caronas =(List<Carona>)object;

                if(usuarios.size()>1){
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                   String titulo= usuarios.size()+" novas caronas foram oferecidas, aproveite!";
                    String texto="";
                    usuarios=f.removeUsuarioRepitidos(usuarios);
                    Log.e("quantos", "nomes "+usuarios.size());
                    for(int i=0;i<usuarios.size();i++){
                        if(i==0){
                            texto += usuarios.get(i).getNome();

                        }else{
                           if(i<2) {
                               if (i == (usuarios.size() - 1)) {
                                   texto += " e " + usuarios.get(i).getNome() + " estão ofertando carona";
                               } else {
                                   texto += ", " + usuarios.get(i).getNome();
                               }
                           }else{
                               texto += "," + usuarios.get(i).getNome() + " e +"+(usuarios.size()-(i+1))+" estão ofertando carona";
                               break;
                           }

                        }

                    }


                    f.notificacao(bm,titulo,texto,getApplicationContext());
                    md.gravarUltimaCarona(caronas.get(caronas.size()-1).getId());
                }else if (usuarios.size()==1){
                    if(usuarios.get(0).getId()!=md.getUsuario().getId()) {
                        byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        String titulo = usuarios.get(0).getNome() + " acabou de oferecer uma carona:";
                        String texto = "DE " + caronas.get(0).getOrigem() + " PARA " + caronas.get(0).getDestino() + " às " + caronas.get(0).getHorario();
                        Funcoes f = new Funcoes();
                        f.notificacao(bitmap, titulo, texto, getApplicationContext());
                        md.gravarUltimaCarona(caronas.get(0).getId());
                    }
                }
            }
        });
    }


}

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

import br.com.rsa.carona.carona_rsa.Home;
import br.com.rsa.carona.carona_rsa.MainActivity;
import br.com.rsa.carona.carona_rsa.R;
import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;

/**
 * Created by josehelder on 14/11/2016.
 */
public class Servico extends IntentService {
    public static final String ACTION_MyIntentService = "br.com.rsa.carona.carona_rsa.entidades.RESPONSE";

    final Funcoes f = new Funcoes();
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

        final ManipulaDados md = new ManipulaDados(this);
        if(md.getUsuario()!=null) {
            while (ativo && cont<100) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                verificaNovasCaronas();
                verificaSolicitacao("AGUARDANDO");
                verificaSolicitacao("DESISTENCIA");
                int idCaronaSolicitada = md.getCaronaSolicitada();
                Log.e("testando", "minha " + idCaronaSolicitada + " ultimaAceita " + md.getUltimoIdCaronaAceita());
                if (idCaronaSolicitada != -1 && idCaronaSolicitada != md.getUltimoIdCaronaAceita()) {
                    verificaSolicitacaoAceita();
                }
                cont++;
                Log.e("testando", "cont" + cont);


            }
            ativo = true;
            cont = 0;
        }else{
            stopSelf();
        }
    }

   public void criaBroadcast(int valor, String tipo){

       Intent dialogIntent = new Intent();
       dialogIntent.setAction("abc");
       dialogIntent.putExtra("mensagem", tipo);
       dialogIntent.putExtra("valor", valor+"");
       sendBroadcast(dialogIntent);
   }
    public void verificaSolicitacao(final String status){
        final ManipulaDados md = new ManipulaDados(this);
        Usuario us= md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.verificaSolicitacoes(status, us, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                List<Usuario> usuarios=(List<Usuario>)object;
                String tipoP="";
                if(status.equals("AGUARDANDO")){
                    tipoP="solicitando";
                }else{
                    tipoP="desistido da";
                }
                if(usuarios.size()>0){
                    criaBroadcast(usuarios.size(),"solicitacao");
                }
                    if(usuarios.size()>1){
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        String titulo= usuarios.size()+" pessoas estão "+tipoP+" carona";
                        String texto="";
                        usuarios=f.removeUsuarioRepitidos(usuarios);
                        Log.e("quantos", "nomes "+usuarios.size());
                        for(int i=0;i<usuarios.size();i++){
                            if(i==0){
                                texto += usuarios.get(i).getNome();

                            }else{
                                if(i<2) {
                                    if (i == (usuarios.size() - 1)) {
                                        texto += " e " + usuarios.get(i).getNome() + " estão "+tipoP+" carona";
                                    } else {
                                        texto += ", " + usuarios.get(i).getNome();
                                    }
                                }else{
                                    texto += "," + usuarios.get(i).getNome() + " e +"+(usuarios.size()-(i+1))+" estão "+tipoP+" carona";
                                    break;
                                }

                            }

                        }
                        f.notificacao(bm,titulo,texto,getApplicationContext(),1);
                    }else if (usuarios.size()==1){
                            byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            String titulo="ME LEVA!";
                            String texto = usuarios.get(0).getNome() + " está "+tipoP+" carona:";
                           // String texto = "DE " + caronas.get(0).getOrigem() + " PARA " + caronas.get(0).getDestino() + " às " + caronas.get(0).getHorario();
                            Funcoes f = new Funcoes();
                            f.notificacao(bitmap, titulo, texto, getApplicationContext(),1);
                        }




            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }
    public void verificaSolicitacaoAceita(){
        final ManipulaDados md = new ManipulaDados(this);
      final int idCaronaSolicitada=  md.getCaronaSolicitada();

      Usuario us= md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.verificaCaronaSolitada(idCaronaSolicitada, us, new GetRetorno() {
            @Override
            public void concluido(Object object) {
               Usuario us=(Usuario)object;
                Log.e("testando", " ENTROU AQUI OOOOOOOO333333333");
                if(us !=null){
                    Log.e("testando", " ENTROU AQUI OOOOOOOO");
                md.gravarUltimaCaronaAceita(idCaronaSolicitada);
                    String titulo="Carona Aceita";
                    String texto=us.getNome()+" aceitou sua Solicitação de Carona";
                    byte[] decodedString = Base64.decode(us.getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    f.notificacao(bitmap,titulo,texto,getApplicationContext(),2);
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }
    public void verificaNovasCaronas(){

        final ManipulaDados md = new ManipulaDados(this);
        int ultimoIdCarona=md.getUltimoIdCarona();
        final Usuario usuario = md.getUsuario();
        Log.e("ééééeé", "verificaNovasCaronas "+ultimoIdCarona);
        RequisicoesServidor rs = new RequisicoesServidor(this);

            rs.buscaUltimasCaronas(usuario, ultimoIdCarona, new GetRetorno() {
                @Override
                public void concluido(Object object) {

                }

                @Override
                public void concluido(Object object, Object object2) {
                    List<Usuario> usuarios = (List<Usuario>) object2;
                    List<Carona> caronas = (List<Carona>) object;
                    if(usuarios.size()>0){
                        criaBroadcast(usuarios.size(),"novaCarona");
                    }
                    if (usuarios.size() > 1) {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        String titulo = usuarios.size() + " novas caronas foram oferecidas, aproveite!";
                        String texto = "";
                        usuarios = f.removeUsuarioRepitidos(usuarios);
                        Log.e("quantos", "nomes " + usuarios.size());
                        for (int i = 0; i < usuarios.size(); i++) {
                            if (i == 0) {
                                texto += usuarios.get(i).getNome();

                            } else {
                                if (i < 2) {
                                    if (i == (usuarios.size() - 1)) {
                                        texto += " e " + usuarios.get(i).getNome() + " estão ofertando carona";
                                    } else {
                                        texto += ", " + usuarios.get(i).getNome();
                                    }
                                } else {
                                    texto += "," + usuarios.get(i).getNome() + " e +" + (usuarios.size() - (i + 1)) + " estão ofertando carona";
                                    break;
                                }

                            }

                        }


                        f.notificacao(bm, titulo, texto, getApplicationContext(),3);
                        md.gravarUltimaCarona(caronas.get(caronas.size() - 1).getId());
                    } else if (usuarios.size() == 1) {
                            byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            String titulo = usuarios.get(0).getNome() + " está oferecendo uma carona:";
                            String texto = "DE " + caronas.get(0).getOrigem() + " PARA " + caronas.get(0).getDestino() + " às " + caronas.get(0).getHorario();
                            Funcoes f = new Funcoes();
                            f.notificacao(bitmap, titulo, texto, getApplicationContext(),3);
                            md.gravarUltimaCarona(caronas.get(0).getId());
                        }



                }
            });

    }


}

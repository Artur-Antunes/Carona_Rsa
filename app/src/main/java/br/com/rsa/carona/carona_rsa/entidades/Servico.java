package br.com.rsa.carona.carona_rsa.entidades;


import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import br.com.rsa.carona.carona_rsa.Home;
import br.com.rsa.carona.carona_rsa.R;
import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;

public class Servico extends IntentService {
    public static final String ACTION_MyIntentService = "br.com.rsa.carona.carona_rsa.entidades.RESPONSE";

    final Funcoes f = new Funcoes();
    private int cont;
    public static volatile boolean ativo;
    private boolean verificaConnexao = true;

    public Servico() {
        super("Servico");//Nome do serviço
        ativo = true;//Serviço ativo
        cont = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        final ManipulaDados md = new ManipulaDados(Servico.this);
        RequisicoesServidor rs = new RequisicoesServidor(Servico.this);
        if (md.getUsuario() != null) {
            while (ativo) {
                if (rs.isConnectedToServer("http://10.0.2.2/Caronas", 10000)) {

                    verificaSolicitacao("AGUARDANDO");//Buscando as solicitações de uma carona que eu ofereci...
                    verificaSolicitacao("DESISTENCIA");//Buscar os usuários que estão desistindo da carona...

                    verificaNovasCaronas();//Buscando as novas caronas e exibindo as notificações...

                    if(Home.userCarOferecida!=-1){
                        verificaCaronaOferecida();
                    }


                    int idCaronaSolicitada = md.getCaronaSolicitada();
                    if ((idCaronaSolicitada != -1) && (idCaronaSolicitada != md.getUltimoIdCaronaAceita())) {
                        verificaSolicitacaoAceita();
                    } else if ((idCaronaSolicitada == md.getUltimoIdCaronaAceita()) && idCaronaSolicitada != -1) {
                        verificaSolicitacaoAceita();
                    }
                    cont++;
                    Log.e("testando", "cont" + cont);
                } else {
                    Log.e("CONEXÃO:", "DESATIVADA");
                    criaBroadcast(0, "sem_conexao");
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //ativo = true;
            cont = 0;
        } else {
            stopSelf();
        }
    }

    public void criaBroadcast(int valor, String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abc");
        dialogIntent.putExtra("mensagem", tipo);
        dialogIntent.putExtra("valor", valor + "");
        sendBroadcast(dialogIntent);
    }


    public void criaBroadcastHome(String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abcHome");
        dialogIntent.putExtra("mensagem", tipo);
        sendBroadcast(dialogIntent);
    }


    public void verificaSolicitacao(final String status) {//ESTUDAR ESSE ????
        final ManipulaDados md = new ManipulaDados(this);
        Usuario us = md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.verificaSolicitacoes(status, us, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                if (object != null && md.getUsuario() != null) {
                    List<Usuario> usuarios = (List<Usuario>) object;
                    String tipoP = "";
                    int idNotificacao = 3;
                    if (status.equals("AGUARDANDO")) {
                        tipoP = "solicitando";
                        if (usuarios.size() > 0) {
                            criaBroadcast(usuarios.size(), "solicitacao");
                        }
                    } else {
                        tipoP = "desistindo da";
                        idNotificacao = 4;
                        if (usuarios.size() > 0) {
                            criaBroadcast(usuarios.size(), "solicitacao");
                        }

                    }

                    if (usuarios.size() > 1) {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        String titulo = usuarios.size() + " pessoas estão " + tipoP + " carona";
                        String texto = "";
                        usuarios = f.removeUsuarioRepitidos(usuarios);
                        for (int i = 0; i < usuarios.size(); i++) {
                            if (i == 0) {
                                texto += usuarios.get(i).getNome();

                            } else {
                                if (i < 2) {
                                    if (i == (usuarios.size() - 1)) {
                                        texto += " e " + usuarios.get(i).getNome() + " estão " + tipoP + " carona";
                                    } else {
                                        texto += ", " + usuarios.get(i).getNome();
                                    }
                                } else {
                                    texto += "," + usuarios.get(i).getNome() + " e +" + (usuarios.size() - (i + 1)) + " estão " + tipoP + " carona";
                                    break;
                                }
                            }
                        }
                        f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), idNotificacao);
                    } else if (usuarios.size() == 1) {
                        byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        String titulo = "ME LEVA!";
                        String texto = usuarios.get(0).getNome() + " está " + tipoP + " carona:";
                        f.notificacaoAbertoFechado(Bitmap.createScaledBitmap(bitmap, 120, 120, false), titulo, texto, getApplicationContext(), idNotificacao);
                    }
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }


    public void verificaCaronaOferecida(){
        final int idCarona = Home.userCarOferecida;
        Log.e("Home_adquiriu_12:",Home.userCarOferecida+"");
        RequisicoesServidor rs = new RequisicoesServidor(this);
        final ManipulaDados md = new ManipulaDados(this);
        Usuario usLoc = md.getUsuario();
        rs.verificaCarona_SolitadaOuOfertada(idCarona, usLoc, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                Usuario us = (Usuario) object;
                String titulo, texto;
                if ((us != null) && (us.getId() == -4)) {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                    titulo = "Obrigado!";
                    texto = "Esperamos que volte a ofertar caronas!";
                    f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                    criaBroadcastHome("removeCaronaOferecida");
                } else {
                    Log.e("Corrija", "ERRO!");
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }



    public void verificaSolicitacaoAceita() {
        final ManipulaDados md = new ManipulaDados(this);
        final int idCaronaSolicitada = md.getCaronaSolicitada();
        Usuario usLoc = md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
            rs.verificaCarona_SolitadaOuOfertada(idCaronaSolicitada, usLoc, new GetRetorno() {
                @Override
                public void concluido(Object object) {
                    if (md.getUsuario() != null) {
                        Usuario us = (Usuario) object;
                        String titulo, texto;
                        if ((us != null) && (us.getId() >= 1) && (md.getCaronaSolicitada() != md.getUltimoIdCaronaAceita())) {
                            titulo = "Carona ";
                            texto = us.getNome();
                            if (us.getEmail().toString().equals("ACEITO")) {
                                titulo += "aceita";
                                texto += " aceitou sua solicitação de Carona";
                                criaBroadcast(1, "solicitacao_aceita");
                                criaBroadcastHome("atSolicitacao");
                                md.gravarUltimaCaronaAceita(idCaronaSolicitada);
                                byte[] decodedString = Base64.decode(us.getFoto(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                f.notificacaoFechado(bitmap, titulo, texto, getApplicationContext(), 2);
                            } else if (us.getEmail().toString().equals("RECUSADO")) {
                                titulo += "recusada";
                                texto += " recusou sua solicitação de carona";
                                md.setCaronaSolicitada(-1);
                                criaBroadcastHome("atSolicitacao");
                                byte[] decodedString = Base64.decode(us.getFoto(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                f.notificacaoAbertoFechado(bitmap, titulo, texto, getApplicationContext(), 2);
                            }
                        } else if ((us != null) && (us.getId() == -1)) {
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                            titulo = "Solicitação expirou";
                            texto = "Sua solicitação de carona expirou !";
                            f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                            md.setCaronaSolicitada(-1);
                            //criaBroadcast(-1, "solicitacao_expirou");
                            criaBroadcastHome("atSolicitacao");

                        } else if ((us != null) && (us.getId() == -2)) {
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                            titulo = "Boa Viagem!";
                            texto = "Aproveite a carona !";
                            f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                            //criaBroadcast(-1, "solicitacao_expirou");
                            md.setCaronaSolicitada(-1);
                            criaBroadcastHome("atSolicitacao");
                        } else if ((us != null) && (us.getId() == -3)) {
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                            titulo = "Cancelamento";
                            texto = "A carona solicitada foi cancelada";
                            f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                            //criaBroadcast(-1, "solicitacao_expirou");
                            criaBroadcastHome("atSolicitacao");
                            md.setCaronaSolicitada(-1);
                        }
                    }

                }

                @Override
                public void concluido(Object object, Object object2) {

                }
            });
    }


    public void verificaNovasCaronas() {
        final ManipulaDados md = new ManipulaDados(this);
        int ultimoIdCarona = md.getUltimoIdCarona();
        final Usuario usuario = md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
            rs.buscaUltimasCaronas(usuario, ultimoIdCarona, new GetRetorno() {
                @Override
                public void concluido(Object object) {

                }

                @Override
                public void concluido(Object object, Object object2) {
                    if (md.getUsuario() != null) {
                        List<Usuario> usuarios = (List<Usuario>) object2;
                        List<Carona> caronas = (List<Carona>) object;

                        Log.e("QUANTIDADE",caronas.size()+"");

                        if (caronas.size() > 0) {
                            criaBroadcast(caronas.size(), "novaCarona");
                            criaBroadcastHome("nova(s)Carona(s)");
                        }

                        if (usuarios.size() > 1) {
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                            String titulo = usuarios.size() + " novas caronas foram oferecidas, aproveite!";
                            String texto = "";
                            usuarios = f.removeUsuarioRepitidos(usuarios);
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
                            f.notificacaoFechado(bm, titulo, texto, getApplicationContext(), 1);
                            md.gravarUltimaCarona(caronas.get(caronas.size() - 1).getId());
                            Log.e("CONFERE ID:", caronas.get(caronas.size() - 1).getId() + "");
                            Log.e("CONFERE ID2:",caronas.get(0).getId()+"");
                        } else if (caronas.size() == 1) {
                            Log.e("aqui", "33");
                            byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            String titulo = usuarios.get(0).getNome() + " está oferecendo uma carona:";
                            String texto = "DE " + caronas.get(0).getOrigem() + " PARA " + caronas.get(0).getDestino() + " às " + caronas.get(0).getHorario();
                            Funcoes f = new Funcoes();
                            f.notificacaoFechado(bitmap, titulo, texto, getApplicationContext(), 5);
                            Log.e("CONFERE ID3:", caronas.get(0).getId() + "");
                            md.gravarUltimaCarona(caronas.get(0).getId());
                        }
                    }
                }
            });
    }
}

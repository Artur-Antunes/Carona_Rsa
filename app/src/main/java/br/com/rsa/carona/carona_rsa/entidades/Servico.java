package br.com.rsa.carona.carona_rsa.entidades;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import br.com.rsa.carona.carona_rsa.ComentariosActivity;
import br.com.rsa.carona.carona_rsa.Home;
import br.com.rsa.carona.carona_rsa.R;
import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;

public class Servico extends IntentService {
    public static final String ACTION_MyIntentService = "br.com.rsa.carona.carona_rsa.entidades.RESPONSE";
    final Funcoes f = new Funcoes();
    private int cont;
    public static volatile boolean ativo;
    private volatile boolean cntVerificaSolicitacao, cntVerificaCaronaOferecida, cntVerificaSolicitacaoAceita,
            cntBuscarComentarios, cntsalvarAtualCaronaOferecida, cntsalvarAtualCaronaSolicitada;
    public static volatile boolean cntVerificaNovasCaronas;

    public Servico() {
        super("Servico");
        cntVerificaSolicitacao = true;
        cntVerificaCaronaOferecida = true;
        cntVerificaSolicitacaoAceita = true;
        cntBuscarComentarios = true;
        cntsalvarAtualCaronaOferecida = true;
        cntsalvarAtualCaronaSolicitada = true;
        ativo = true;
        cont = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ManipulaDados md = new ManipulaDados(Servico.this);
        RequisicoesServidor rs = new RequisicoesServidor(Servico.this);
        if (md.getUsuario() != null) {
            while (ativo) {
                if (rs.isConnectedToServer("http://10.0.2.2/Caronas/")) {
                    if (md.getCaronaOferecida() != null) {
                        if (md.getCaronaOferecida().getId() == -2) {
                            if (cntsalvarAtualCaronaOferecida)
                                salvarAtualCaronaOferecida();//SALVAR A CARONA NO BANCO EXTERNO
                        } else {
                            if (cntVerificaCaronaOferecida) verificaCaronaOferecida();
                        }
                    } else if (md.getCaronaSolicitada().getId() != -1 && md.getCaronaSolicitada().getStatus() == 0) {
                            if (cntsalvarAtualCaronaSolicitada) salvarAtualCaronaSolicitada();
                    }

                    if (cntVerificaSolicitacao) {
                        verificaSolicitacao("AGUARDANDO");
                        verificaSolicitacao("DESISTENCIA");
                    }
                    if ((cntVerificaNovasCaronas) && (ComentariosActivity.active == -1) && (md.getUltimoIdCarona() >= 0)) {
                        verificaNovasCaronas();
                    }

                    if (ComentariosActivity.active > -1) {
                        if (cntBuscarComentarios)
                            buscarComentarios(ComentariosActivity.active, ComentariosActivity.idCarona);
                    }

                    if ((md.getCaronaSolicitada().getId() != -1) && (md.getCaronaSolicitada().getId() != md.getUltimoIdCaronaAceita())) {
                        if (cntVerificaSolicitacaoAceita && cntsalvarAtualCaronaSolicitada)
                            verificaSolicitacaoAceita();
                    } else if ((md.getCaronaSolicitada().getId() == md.getUltimoIdCaronaAceita()) && md.getCaronaSolicitada().getId() != -1) {
                        if (cntVerificaSolicitacaoAceita && cntsalvarAtualCaronaSolicitada)
                            verificaSolicitacaoAceita();
                    }
                    cont++;
                    Log.e("testando", "cont" + cont);
                } else {
                    Log.e("CONEXÃO:", "DESATIVADA");
                    criaBroadcast(0, "sem_conexao");
                    setDelay(8000);
                }
                setDelay(1100);
            }
            cont = 0;
        } else {
            stopSelf();
        }
    }

    private void setDelay(int tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void criaBroadcast(int valor, String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abc");
        dialogIntent.putExtra("mensagem", tipo);
        dialogIntent.putExtra("valor", valor + "");
        sendBroadcast(dialogIntent);
    }


    public void criaBroadcastComents(String tipo, List<Usuario> usuarios, List<String> textos) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abcComents");
        dialogIntent.putExtra("mensagem", tipo);
        dialogIntent.putExtra("users", (Serializable) usuarios);
        dialogIntent.putExtra("textos", (Serializable) textos);
        sendBroadcast(dialogIntent);
    }


    public void criaBroadcastHome(String tipo, List<Carona> caronas, List<Usuario> usuarios) {
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abcHome");
        dialogIntent.putExtra("mensagem", tipo);
        dialogIntent.putExtra("caronas", (Serializable) caronas);
        dialogIntent.putExtra("usuarios", (Serializable) usuarios);
        sendBroadcast(dialogIntent);
    }


    public void verificaSolicitacao(final String status) {
        final ManipulaDados md = new ManipulaDados(Servico.this);
        cntVerificaSolicitacao = false;
        Usuario us = md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.verificaSolicitacoes(status, us, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                if (object != null && md.getUsuario() != null) {
                    List<Usuario> usuarios = (List<Usuario>) object;
                    String tipoP;
                    int idNotificacao = 3;
                    if (status.equals("AGUARDANDO")) {
                        tipoP = "solicitando";
                        if (usuarios.size() > 0) {
                            Log.e("SERV:", usuarios.size() + "");
                            for (int z = 0; z < usuarios.size(); z++) {
                                usuarios.get(z).setStatus(status);
                                md.setParticipantesCarOferecida(usuarios.get(z), md.getTtParCarOf());
                            }
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
                setDelay(10);
                cntVerificaSolicitacao = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }

    public void salvarAtualCaronaOferecida() {
        cntsalvarAtualCaronaOferecida = false;
        RequisicoesServidor rs = new RequisicoesServidor(this);
        final ManipulaDados md = new ManipulaDados(Servico.this);
        rs.gravaCarona(md.getCaronaOferecida(), md.getUsuario().getId(), new GetRetorno() {
            @Override
            public void concluido(Object object) {
                int resposta = Integer.parseInt(object.toString());
                if (resposta > 0) {
                    Carona c = md.getCaronaOferecida();
                    if (c != null) {
                        c.setId(resposta);
                        md.setCaronaOferecida(c);
                        criaBroadcastHome("atCarOfertada", null, null);
                        criaBroadcast(0, "myCarona");
                    }
                }
                cntsalvarAtualCaronaOferecida = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }

    public void salvarAtualCaronaSolicitada() {
        cntsalvarAtualCaronaSolicitada = false;
        final ManipulaDados md = new ManipulaDados(Servico.this);
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.solicitaCarona(md.getCaronaSolicitada(), md.getUsuario(), new GetRetorno() {

            @Override
            public void concluido(Object object) {
                String[] res = (String[]) object;
                if (res[0].trim().equals("1")) {
                    //md.setCaronaSolicitada(-1);
                    Carona car=md.getCaronaSolicitada();
                    car.setVagasOcupadas(Integer.parseInt(res[1]));
                    car.setStatus(1);
                    md.setCaronaSolicitada(car);
                    //tv_vagas.setText(organizaVagas(Integer.parseInt(res[1]), car.getVagas()));
                    //atualizarEspera();
                    //ll.removeView(modelo);
                    //exibirMsg("Carona solicitada!");
                    //new Funcoes().apagarNotificacaoEspecifica(this, 1);
                    criaBroadcastHome("okSlt", null, null);
                } else if (res[0].trim().equals("2")) {
                    //ll.removeView(modelo);
                    //exibirMsg("Essa carona não está mais disponível!");
                    //new Funcoes().apagarNotificacaoEspecifica(activity, 1);
                    criaBroadcastHome("erro1Slt", null, null);
                } else if (res[0].trim().equals("-3")) {
                    //exibirMsg(" Sem vagas!");
                    //tv_vagas.setText(organizaVagas(Integer.parseInt(res[1]), car.getVagas()));
                    criaBroadcastHome("erro2Slt", null, null);
                } else if (res[0].trim().equals("-2")) {
                    //exibirMsg("Solicitação Recusada!");
                    //tv_vagas.setText(organizaVagas(Integer.parseInt(res[1]), car.getVagas()));
                    criaBroadcastHome("erro3Slt", null, null);
                } else {
                    Log.e("SLT:", "NOT");
                    //exibirMsg("Não foi possível realizar a solicitação! Cód:" + res[0]);
                }
                //limparBadge();
                cntsalvarAtualCaronaSolicitada = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }

    public void verificaCaronaOferecida() {
        cntVerificaCaronaOferecida = false;
        ManipulaDados md = new ManipulaDados(Servico.this);
        final int idCarona = md.getCaronaOferecida().getId();
        RequisicoesServidor rs = new RequisicoesServidor(this);
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
                    criaBroadcastHome("removeCaronaOferecida", null, null);
                }
                setDelay(10);
                cntVerificaCaronaOferecida = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }


    public void verificaSolicitacaoAceita() {
        cntVerificaSolicitacaoAceita = false;
        final ManipulaDados md = new ManipulaDados(this);
        Usuario usLoc = md.getUsuario();
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.verificaCarona_SolitadaOuOfertada(md.getCaronaSolicitada().getId(), usLoc, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                if (md.getUsuario() != null) {
                    Usuario us = (Usuario) object;
                    String titulo, texto;
                    if ((us != null) && (us.getId() >= 1) && (md.getCaronaSolicitada().getId() != md.getUltimoIdCaronaAceita())) {
                        titulo = "Carona ";
                        texto = us.getNome();
                        if (us.getEmail().toString().equals("ACEITO")) {
                            titulo += "aceita";
                            texto += " aceitou sua solicitação de Carona";
                            criaBroadcast(1, "solicitacao_aceita");
                            Carona c=md.getCaronaSolicitada();
                            c.setStatusUsuario("ACEITO");
                            md.setCaronaSolicitada(c);
                            criaBroadcastHome("atSolicitacao", null, null);
                            md.gravarUltimaCaronaAceita(md.getCaronaSolicitada().getId());
                            byte[] decodedString = Base64.decode(us.getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            f.notificacaoFechado(bitmap, titulo, texto, getApplicationContext(), 2);
                        } else if (us.getEmail().toString().equals("RECUSADO")) {
                            titulo += "recusada";
                            texto += " recusou sua solicitação de carona";
                            //md.setCaronaSolicitada(-1);
                            criaBroadcastHome("closeSlt", null, null);
                            byte[] decodedString = Base64.decode(us.getFoto(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            f.notificacaoAbertoFechado(bitmap, titulo, texto, getApplicationContext(), 2);
                        }
                    } else if ((us != null) && (us.getId() == -1)) {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        titulo = "Solicitação expirou";
                        texto = "Sua solicitação de carona expirou !";
                        f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                        //criaBroadcast(-1, "solicitacao_expirou");
                        criaBroadcastHome("closeSlt", null, null);
                    } else if ((us != null) && (us.getId() == -2)) {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        titulo = "Boa Viagem!";
                        texto = "Aproveite a carona !";
                        f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                        //criaBroadcast(-1, "solicitacao_expirou");
                        criaBroadcastHome("closeSlt", null, null);
                    } else if ((us != null) && (us.getId() == -3)) {
                        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
                        titulo = "Cancelamento";
                        texto = "A carona solicitada foi cancelada";
                        f.notificacaoAbertoFechado(bm, titulo, texto, getApplicationContext(), 5);
                        //criaBroadcast(-1, "solicitacao_expirou");
                        criaBroadcastHome("closeSlt", null, null);
                    }
                }
                setDelay(10);
                cntVerificaSolicitacaoAceita = true;
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });

    }

    public void buscarComentarios(int total, int idCarona) {
        cntBuscarComentarios = false;
        RequisicoesServidor rs = new RequisicoesServidor(this);
        rs.buscarComentariosCarona(total, 10, idCarona, new GetRetorno() {
            @Override
            public void concluido(Object object) {

            }

            @Override
            public void concluido(Object object, Object object2) {
                List<String> textos = (List<String>) object;
                List<Usuario> usuarios = (List<Usuario>) object2;
                if (usuarios.size() > 0) {
                    criaBroadcastComents("nvComents", usuarios, textos);
                }
                setDelay(10);
                cntBuscarComentarios = true;
            }
        });
    }


    public void verificaNovasCaronas() {
        cntVerificaNovasCaronas = false;
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
                    if (caronas.size() > 0) {
                        Log.e("tamanhoCar:", caronas.size() + "");
                        criaBroadcast(caronas.size(), "novaCarona");
                        criaBroadcastHome("ultima(s)Carona(s)", caronas, usuarios);
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
                    } else if (caronas.size() == 1) {
                        byte[] decodedString = Base64.decode(usuarios.get(0).getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        String titulo = usuarios.get(0).getNome() + " está oferecendo uma carona:";
                        String texto = "DE " + caronas.get(0).getOrigem() + " PARA " + caronas.get(0).getDestino() + " às " + caronas.get(0).getHorario();
                        Funcoes f = new Funcoes();
                        f.notificacaoFechado(bitmap, titulo, texto, getApplicationContext(), 5);
                        md.gravarUltimaCarona(caronas.get(0).getId());
                    }
                }
                setDelay(10);
                cntVerificaNovasCaronas = true;
            }
        });
    }
}
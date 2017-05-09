package br.com.rsa.carona.carona_rsa.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class RequisicoesServidor {
    String TAG = "ERROS";
    ProgressDialog progressDialog;
    public static final int TEMPO_CONEXAO = 1000 * 10;
    public static final String ENDERECO_SERVIDOR = "http://10.0.2.2/Caronas/";
    Context cnt;
    private volatile boolean  running;

    public RequisicoesServidor(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Aguarde...");
        running = true;
        cnt = context;
    }

    public boolean isConnectedToServer(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void gravaDadosDoUsuario(Usuario usuario, GetRetorno retorno) {
        progressDialog.show();
        new armazenaDadosUsuarioAsyncTask(usuario, retorno).execute();
    }

    public void buscaDadosDoUsuario(Usuario usuario, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaDadosUsuarioAsyncTask(usuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscasSolicitacoesCaronas(int ttViewsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo
        new BuscaSolicitacaoAsyncTask(ttViewsAtuais, ttBuscar, usuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscarComentariosCarona(int ttComents, int ttBuscar, int idCarona, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        //progressDialog.show();// Mostra a barra de dialogo.
        new buscarComentariosAsyncTask(ttComents, ttBuscar, idCarona, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void gravaCarona(Carona carona, int  idUsuario, GetRetorno retorno) {
        progressDialog.show();
        new armazenaCaronaAsyncTask(carona, idUsuario, retorno).execute();

    }

    public void gravarComentarioCarona(int IdCarona, int idUsuario, String texto, GetRetorno retorno) {
        //progressDialog.show();
        new gravarComentarioCaronaAsyncTask(idUsuario, IdCarona, texto, retorno).execute();
    }

    public void recuperarSenha(String email, GetRetorno retorno) {
        progressDialog.show();
        new recuperarSenhaAsyncTask(email, retorno).execute();
    }

    public void verificaCarona_SolitadaOuOfertada(int idCarona, Usuario usuario, GetRetorno retorno) {
        new verificaSolicitacaAsyncTask(idCarona, usuario, retorno).execute();
    }


    public void verificaSolicitacoes(String status, Usuario usuario, GetRetorno retorno) {
        new verificaSolicitacoesAsyncTask(usuario, status, retorno).execute();
    }

    public void solicitaCarona(Carona carona, Usuario usuario, GetRetorno retorno) {
        progressDialog.show();
        new solicitaCaronaAsyncTask(carona, usuario, retorno).execute();
    }

    public void fecharCaronaOferecida(int idCarona, int idUsuario, int tipo, GetRetorno retorno) {
        progressDialog.show();
        new fecharCaronaOferecidaAsyncTask(idCarona, idUsuario, tipo, retorno).execute();
    }

    public void exibirMinhasSolicitações(int ttVsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {
        progressDialog.show();// Mostra a barra de dialogo.
        new exibirMinhasSolicitaçõesAsyncTask(ttVsAtuais, ttBuscar, usuario, retorno).execute();
    }

    public void exibirSolicitacoesCaronas(Usuario usuario, GetRetorno retorno) {
        progressDialog.show();// Mostra a barra de dialogo.
        new exibirUsuariosSolicitantesAsyncTask(usuario, retorno).execute();
    }

    public void aceitarRecusarCaronas(Usuario usuario, String resposta, GetRetorno retorno) {
        progressDialog.show();// Mostra a barra de dialogo.
        new aceitaOuRecusaCaronaAsyncTask(usuario, resposta, retorno).execute();
    }

    public void alteraStatusCarona(int idCarona, int valor, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new alteraStatusCaronaAsyncTask(idCarona, valor, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void aguardaRespostaCarona(Usuario usuario, Carona carona, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaCaronaAsyncTask(usuario, carona, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }



    public void desistirCarona(int idUsuario, int idCarona, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new cancelarCaronaAsyncTask(idCarona, idUsuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscaCaronas(Usuario usuario, int ultimoValor, int totalViews, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaCaronasAsyncTask(usuario, ultimoValor, totalViews, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscaUltimasCaronas(Usuario usuario, int id, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        //progressDialog.show();// Mostra a barra de dialogo.
        new BuscaUltimaCaronasAsyncTask(usuario, id, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public class armazenaDadosUsuarioAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        public armazenaDadosUsuarioAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;

        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();
            dadosParaEnvio.add(new BasicNameValuePair("nome", usuario.getNome()));
            dadosParaEnvio.add(new BasicNameValuePair("sobrenome", usuario.getSobrenome()));
            dadosParaEnvio.add(new BasicNameValuePair("matricula", usuario.getMatricula()));
            dadosParaEnvio.add(new BasicNameValuePair("telefone", usuario.getTelefone()));
            dadosParaEnvio.add(new BasicNameValuePair("email", usuario.getEmail()));
            dadosParaEnvio.add(new BasicNameValuePair("sexo", usuario.getSexo()));
            dadosParaEnvio.add(new BasicNameValuePair("ativo", usuario.getAtivo() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("senha", usuario.getSenha()));
            dadosParaEnvio.add(new BasicNameValuePair("foto", usuario.getFoto()));
            dadosParaEnvio.add(new BasicNameValuePair("extencao", usuario.getExtFoto()));
            String aquivoPhp = "Registros.php";
            if (usuario.getEdicao()) {
                dadosParaEnvio.add(new BasicNameValuePair("id_edicao", usuario.getId() + ""));
                aquivoPhp = "Edicao.php";
            }

            if (usuario.isCnh()) {
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "1"));
            } else {
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "0"));
            }
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + aquivoPhp);
            String teste = "não";
            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse
                    JSONObject jObjeto = new JSONObject(resultado);
                    teste = jObjeto.getString("teste");
                    Log.e("BUSCANDO DAOS:", "SIM");
                    return teste;
                }
            } catch (Exception e) {
                Log.e(TAG, "erro registro " + e);
                e.printStackTrace();
            }
            return teste;
        }

        @Override
        protected void onPostExecute(Object resultado) {
            progressDialog.dismiss();
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    private String getEncodedData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (sb.length() > 0)
                sb.append("&");
            sb.append(key + "=" + value);
        }
        return sb.toString();
    }


    public class gravarComentarioCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        int idUsuario;
        int idCarona;
        String texto;
        GetRetorno retornoUsuario;

        public gravarComentarioCaronaAsyncTask(int idUsuario, int idcarona, String texto, GetRetorno retorno) {
            this.idUsuario = idUsuario;
            this.texto = texto;
            this.retornoUsuario = retorno;
            this.idCarona = idcarona;

        }

        @Override
        protected Object doInBackground(Void... params) {

            /**
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();
            dadosParaEnvio.add(new BasicNameValuePair("id_carona", idCarona + ""));
            dadosParaEnvio.add(new BasicNameValuePair("id_user", idUsuario + ""));
            dadosParaEnvio.add(new BasicNameValuePair("texto_comentario", this.texto));*/


            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("id_carona", idCarona + "");
            dataToSend.put("id_user", idUsuario + "");
            dataToSend.put("texto_comentario", this.texto);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;


            /**HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Registros.php");*/
            String teste = "Não foi possível se conectar";
            try {
                /**
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);
                JSONObject jObjeto = new JSONObject(resultado);
                teste = jObjeto.getString("teste");
                 */
                URL url = new URL(ENDERECO_SERVIDOR + "Registros.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setConnectTimeout(TEMPO_CONEXAO);
                con.setReadTimeout(TEMPO_CONEXAO);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                teste = sb.toString();
                JSONObject jObjeto = new JSONObject(teste);
                teste=jObjeto.getString("teste");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return teste;
        }

        @Override
        protected void onPostExecute(Object resultado) {
            //progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    public class BuscaCaronaAsyncTask extends AsyncTask<Void, Void, List> {
        Usuario usuario;
        Carona carona;
        GetRetorno retornoUsuario;

        public BuscaCaronaAsyncTask(Usuario usuario, Carona carona, GetRetorno retorno) {
            this.usuario = usuario;
            this.carona = carona; //O campo usuário recebe o parâmetro de usuário.
            this.retornoUsuario = retorno;    //O campo retornoUsuario recebe o parâmetro de retorno.
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected List doInBackground(Void... params) { //Implementação obrigatória.

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id_usuario", this.usuario.getId() + ""));
            dados.add(new BasicNameValuePair("id_carona", this.carona.getId() + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.
            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "aguardaConfirmacaoCarona.php");    //Fazer uma requisição tipo Post no WebService.
            List dadosX = null;    //Variável que irá receber os dados do usuário.

            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                    HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    JSONObject jObj = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.
                    if (jObj.length() == 0) {    //Se o tamanho de jObj for igual a zero.
                        dadosX = null;
                    } else {
                        dadosX = new LinkedList();
                        String origem = jObj.getString("origem");
                        String destino = jObj.getString("destino");
                        String horario = jObj.getString("horario");
                        String veiculo = jObj.getString("tipoVeiculo");
                        String restricao = jObj.getString("restricao");
                        int vagas = jObj.getInt("vagas");
                        int vagasOculpadas = jObj.getInt("vagasOculpadas");
                        String ponto = jObj.getString("ponto");
                        String statusUsuario = jObj.getString("status_usuario");
                        Carona carona = new Carona(origem, destino, horario, veiculo, restricao, ponto);
                        carona.setVagas(vagas);
                        carona.setVagasOcupadas(vagasOculpadas);
                        carona.setId(this.carona.getId());
                        carona.setStatusUsuario(statusUsuario);
                        dadosX.add(carona);
                        List<Usuario> participantes = new LinkedList<Usuario>();
                        List participantesStatus = new LinkedList();
                        for (int j = 0; j < jObj.getInt("participantes_tamanho"); j++) {
                            int idPart = jObj.getInt("participantes_" + j + "_id");
                            String nomePart = jObj.getString("participantes_" + j + "_nome");
                            String sobrenomePart = jObj.getString("participantes_" + j + "_sobrenome");
                            String statusSoliciacao = jObj.getString("participantes_" + j + "_status_solicitacao");
                            Usuario participante = new Usuario(idPart, nomePart);
                            participante.setSobrenome(sobrenomePart);
                            participantes.add(participante);
                            participantesStatus.add(statusSoliciacao);
                        }
                        carona.setParticipantes(participantes);
                        carona.setParticipantesStatus(participantesStatus);
                        String telefone = jObj.getString("telefone");
                        String nome = jObj.getString("nome");
                        String sobrenome = jObj.getString("sobrenome");
                        String email = jObj.getString("email");
                        String foto = jObj.getString("foto");
                        int cnh1 = jObj.getInt("cnh");
                        int idU = jObj.getInt("idUser");
                        boolean cnh = true;
                        if (cnh1 == 0) {
                            cnh = false;
                        }
                        String matricula = jObj.getString("matricula");
                        String sexo = jObj.getString("sexo");
                        Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);
                        usuario.setFoto(foto);
                        usuario.setId(idU);
                        dadosX.add(usuario);
                    }
                    return dadosX;
                }
            } catch (Exception e) {
                Log.e(TAG, "doInBackground " + e.getMessage());
            }

            return dadosX;
        }

        @Override
        protected void onPostExecute(List x) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(x);
            super.onPostExecute(x);
        }//Fim método.
    }//Fim classe.

    public class alteraStatusCaronaAsyncTask extends AsyncTask<Void, Void, String> {
        int idCarona;
        int status;
        GetRetorno retornoUsuario;

        public alteraStatusCaronaAsyncTask(int idCarona, int status, GetRetorno retorno) {
            this.idCarona = idCarona;
            this.status = status; //O campo usuário recebe o parâmetro de usuário.
            this.retornoUsuario = retorno;    //O campo retornoUsuario recebe o parâmetro de retorno.
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... params) { //Implementação obrigatória.

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("status3", this.status + ""));
            dados.add(new BasicNameValuePair("id_carona3", this.idCarona + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.
            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");    //Fazer uma requisição tipo Post no WebService.
            String mensagem = "Houve um erro ao se conectar ao banco";    //Variável que irá receber os dados do usuário.

            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                    HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    JSONObject jObj = new JSONObject(resultado);
                    if (jObj.length() == 0) {
                    } else {
                        mensagem = jObj.getString("mensagem");
                    }
                    return mensagem;
                }
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            }

            return mensagem;
        }

        @Override
        protected void onPostExecute(String caronaRetornada) {
            progressDialog.dismiss();
            retornoUsuario.concluido(caronaRetornada);
            super.onPostExecute(caronaRetornada);
        }
    }


    public class buscarComentariosAsyncTask extends AsyncTask<Void, Void, JSONObject> {
        int idCarona, ttBuscar, ttComents;
        GetRetorno retornoUsuario;

        public buscarComentariosAsyncTask(int totalComent, int totalBuscar, int idCarona, GetRetorno retorno) {
            this.ttBuscar = totalBuscar;
            this.ttComents = totalComent;
            this.idCarona = idCarona;
            this.retornoUsuario = retorno;
        }

        @Override
        protected JSONObject doInBackground(Void... params) { //Implementação obrigatória.

            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("id_comentario_carona", this.idCarona + "");
            dataToSend.put("tt_buscar", this.ttBuscar + "");
            dataToSend.put("tt_coments", this.ttComents + "");
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;

            /**
            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id_comentario_carona", this.idCarona + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            dados.add(new BasicNameValuePair("tt_buscar", this.ttBuscar + ""));
            dados.add(new BasicNameValuePair("tt_coments", this.ttComents + ""));

            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.


            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");    //Fazer uma requisição tipo Post no WebService
            JSONObject jObjeto = null;*/
            JSONObject jObjeto = null;
            try {
                URL url = new URL(ENDERECO_SERVIDOR + "RetornaDados.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                //con.setConnectTimeout(TEMPO_CONEXAO);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                line = sb.toString();
                jObjeto = new JSONObject(line);
                /**post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);
                jObjeto = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.
            */
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            } finally {
                if(reader != null) {
                    try {
                        reader.close();     //Closing the
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return jObjeto;
        }

        @Override
        protected void onPostExecute(JSONObject jObjeto) {
            List<String> textos = new LinkedList<String>();
            List<Usuario> usuarios = new LinkedList<Usuario>();
            try {

                for (int i = 0; i < jObjeto.getInt("tamanho"); i++) {
                    int id = jObjeto.getInt("id_" + i);
                    int idCom = jObjeto.getInt("idComentarios_" + i);
                    String nome = jObjeto.getString("nome_" + i);
                    String foto = jObjeto.getString("foto_" + i);
                    String texto = jObjeto.getString("texto_" + i);
                    String hora = jObjeto.getString("hora_" + i);
                    Usuario user = new Usuario(id);
                    user.setNome(nome);
                    user.setFoto(foto);
                    user.setDataRegistro(hora);
                    user.setAtivo(idCom);
                    usuarios.add(user);
                    textos.add(texto);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(textos, usuarios);
            super.onPostExecute(jObjeto);
        }//Fim método.
    }//Fim classe.


    public class fecharCaronaOferecidaAsyncTask extends AsyncTask<Void, Void, String> {
        int idCarona;
        int idUsuario;
        int tipo;
        GetRetorno retornoUsuario;

        public fecharCaronaOferecidaAsyncTask(int idCarona, int idUser, int tipo, GetRetorno retorno) {
            this.idCarona = idCarona;
            this.idUsuario = idUser;
            this.tipo = tipo;
            this.retornoUsuario = retorno;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id_user_close", this.idUsuario + ""));
            dados.add(new BasicNameValuePair("id_carona_close", this.idCarona + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            dados.add(new BasicNameValuePair("tipo", this.tipo + ""));

            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.


            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");    //Fazer uma requisição tipo Post no WebService.
            //Página de registro
            String mensagem = "Verifique sua conexão!";    //Variável que irá receber os dados do usuário.

            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                    HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    JSONObject jObj = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.
                    mensagem = jObj.getString("mensagem");
                    return mensagem;
                }
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            }

            return mensagem;
        }//Fim método.

        @Override
        protected void onPostExecute(String mensagem) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(mensagem);
            super.onPostExecute(mensagem);
        }//Fim método.
    }//Fim classe.


    public class aceitaOuRecusaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;
        String resposta;
        int idCarona;

        public aceitaOuRecusaCaronaAsyncTask(Usuario usuario, String resposta, GetRetorno retorno) {
            this.usuario = usuario;
            this.resposta = resposta;
            this.retornoUsuario = retorno;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("id", usuario.getId() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("resposta", this.resposta));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "aceitaRecusaCarona.php");
            String teste = "não";
            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse
                    JSONObject jObjeto = new JSONObject(resultado);
                    teste = jObjeto.getString("teste");
                    return teste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return teste;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object resultado) {
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }


    public class exibirUsuariosSolicitantesAsyncTask extends AsyncTask<Void, Void, List<Usuario>> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        public exibirUsuariosSolicitantesAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario; //O campo usuário recebe o parâmetro de usuário.
            this.retornoUsuario = retorno;    //O campo retornoUsuario recebe o parâmetro de retorno.
        }

        @Override
        protected List<Usuario> doInBackground(Void... params) { //Implementação obrigatória.

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id", this.usuario.getId() + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.
            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "buscarSolicitantesCaronas.php");    //Fazer uma requisição tipo Post no WebService.
            List<Usuario> usuarios = new LinkedList<Usuario>();    //Variável que irá receber os dados do usuário.
            try {
                post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);

                JSONObject jObj = new JSONObject(resultado);
                if (jObj.length() == 0) {
                    usuarios = null;
                } else {
                    JSONObject jObjeto = jObj;
                    for (int i = 0; i < jObjeto.getInt("tamanho"); i++) {
                        String telefone = jObjeto.getString("telefone_" + i);
                        String nome = jObjeto.getString("nome_" + i);
                        String sobrenome = jObjeto.getString("sobrenome_" + i);
                        String email = jObjeto.getString("email_" + i);
                        int cnh1 = jObjeto.getInt("cnh_" + i);
                        boolean cnh = true;
                        if (cnh1 == 0) {
                            cnh = false;
                        }
                        String matricula = jObjeto.getString("matricula_" + i);
                        String sexo = jObjeto.getString("sexo_" + i);
                        String foto = jObjeto.getString("foto_" + i);
                        int idUser = Integer.parseInt(jObjeto.getString("id_" + i));
                        Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);
                        usuario.setId(idUser);
                        usuario.setFoto(foto);
                        usuarios.add(usuario);
                    }
                }

            } catch (Exception e) {
                e.getMessage();
            }

            return usuarios;
        }

        @Override
        protected void onPostExecute(List<Usuario> usuarios) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(usuarios);
            super.onPostExecute(usuarios);
        }
    }

    public class BuscaDadosUsuarioAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        public BuscaDadosUsuarioAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("matricula", this.usuario.getMatricula()));
            dados.add(new BasicNameValuePair("senha", this.usuario.getSenha()));
            HttpParams httpParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "buscaDadosUsuario.php");
            Usuario usuarioRetornado = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);
                JSONObject jObj = new JSONObject(resultado);
                if (jObj.getInt("retorno") == 0) {
                    Usuario user = new Usuario(jObj.getInt("retorno"));
                    usuarioRetornado = user;
                } else {
                    String nome = jObj.getString("nome");
                    String sobrenome = jObj.getString("sobrenome");
                    String matricula = jObj.getString("matricula");
                    String email = jObj.getString("email");
                    String telefone = jObj.getString("telefone");
                    String sexo = jObj.getString("sexo");
                    String foto = jObj.getString("foto");
                    Integer cnh = jObj.getInt("cnh");
                    Integer id = jObj.getInt("id");
                    Integer id_carona = jObj.getInt("id_carona");
                    boolean cnh1 = false;
                    if (cnh == 1) {
                        cnh1 = true;
                    }
                    Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh1);
                    usuario.setSenha(this.usuario.getSenha());
                    usuario.setIdCaronaSolicitada(id_carona);
                    usuario.setId(id);
                    usuario.setFoto(foto);
                    usuarioRetornado = usuario;
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return usuarioRetornado;
        }

        @Override
        protected void onPostExecute(Object usuarioRetornado) {
            progressDialog.dismiss();
            retornoUsuario.concluido(usuarioRetornado);
            super.onPostExecute(usuarioRetornado);
        }
    }

    public class verificaSolicitacoesAsyncTask extends AsyncTask<Void, Void, List<Usuario>> {
        Usuario usuario;
        String status;
        GetRetorno retornoUsuario;

        public verificaSolicitacoesAsyncTask(Usuario usuario, String status, GetRetorno retorno) {
            this.usuario = usuario;
            this.status = status;
            this.retornoUsuario = retorno;
        }

        @Override
        protected List<Usuario> doInBackground(Void... params) { //Implementação obrigatória.
            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id_usuario2", this.usuario.getId() + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            dados.add(new BasicNameValuePair("status", this.status));        //Adicionando a senha do usuário a o array dados com a chave 'senha'.
            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.
            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");    //Fazer uma requisição tipo Post no WebService.
            //Página de registro
            List<Usuario> usuarios = null;    //Variável que irá receber os dados do usuário.

            try {
                post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável
                // Recebendo a resposta do servidor após a execução do HTTP POST.
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);
                //
                JSONObject jObj = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.
                usuarios = new LinkedList<Usuario>();
                if (jObj.getInt("retorno") > 0) {    //Se o tamanho de jObj for igual a zero.
                    //Senão,se o tamanho de jObj for diferente de zero.
                    for (int i = 0; i < jObj.getInt("tamanho"); i++) {
                        String nome = jObj.getString("nome_" + i);
                        String foto = jObj.getString("foto_" + i);
                        Integer id = jObj.getInt("id_" + i);
                        Usuario usuario = new Usuario(id, nome);    //Novo obj de usuário.
                        usuario.setFoto(foto);
                        usuarios.add(usuario);
                    }
                }
            } catch (Exception e) {
                e.getMessage();// se não der certo:mensagem de erro
            }
            return usuarios;    //Retorno para o método 'onPostExecute'.
        }//Fim método.

        @Override
        protected void onPostExecute(List<Usuario> usuariosRetornado) {
            retornoUsuario.concluido(usuariosRetornado);
            super.onPostExecute(usuariosRetornado);
        }//Fim método.
    }//Fim classe.

    public class cancelarCaronaAsyncTask extends AsyncTask<Void, Void, Object> {

        int carona;
        int usuario;
        GetRetorno retorno;

        public cancelarCaronaAsyncTask(int idCarona, int idUsuario, GetRetorno retorno) {
            this.carona = idCarona;
            this.usuario = idUsuario;
            this.retorno = retorno;

        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();
            dadosParaEnvio.add(new BasicNameValuePair("id_carona", carona + ""));
            dadosParaEnvio.add(new BasicNameValuePair("id_usuario", usuario + ""));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "cancelarCarona.php");
            String teste = "Sem conexão!";

            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse
                    JSONObject jObjeto = new JSONObject(resultado);
                    teste = jObjeto.getString("desistencia");
                    return teste;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return teste;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object resultado) {
            progressDialog.dismiss();//encerra o circulo de progresso
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }


    public class armazenaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Carona carona;
        int usuario;
        GetRetorno retorno;

        public armazenaCaronaAsyncTask(Carona carona, int  idUsuario, GetRetorno retorno) {
            this.carona = carona;
            this.retorno = retorno;
            this.usuario = idUsuario;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }


        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("origem", carona.getOrigem()));
            dadosParaEnvio.add(new BasicNameValuePair("destino", carona.getDestino()));
            dadosParaEnvio.add(new BasicNameValuePair("horario", carona.getHorario()));
            dadosParaEnvio.add(new BasicNameValuePair("tipoVeiculo", carona.getTipoVeiculo()));
            dadosParaEnvio.add(new BasicNameValuePair("ponto", carona.getPonto()));
            dadosParaEnvio.add(new BasicNameValuePair("restricao", carona.getRestricao()));
            dadosParaEnvio.add(new BasicNameValuePair("vagas", carona.getVagas() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("id_usuario", usuario+""));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Registros.php");
            String teste = "Erro: Verifique sua conexão!";
            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse
                    JSONObject jObjeto = new JSONObject(resultado);
                    teste = jObjeto.getString("teste");
                    return teste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return teste;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object resultado) {
            progressDialog.dismiss();//encerra o circulo de progresso
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    public class verificaSolicitacaAsyncTask extends AsyncTask<Void, Void, Object> {
        int idCarona;
        Usuario usuario;
        GetRetorno retorno;

        public verificaSolicitacaAsyncTask(int idCarona, Usuario usuario, GetRetorno retorno) {
            this.idCarona = idCarona;
            this.retorno = retorno;
            this.usuario = usuario;

        }

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();
            dadosParaEnvio.add(new BasicNameValuePair("id_carona", idCarona + ""));
            dadosParaEnvio.add(new BasicNameValuePair("id_usuario", usuario.getId() + ""));

            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");
            int teste = -100;
            Usuario usuario = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse
                JSONObject jObjeto = new JSONObject(resultado);
                teste = jObjeto.getInt("retorno");
                if (teste == 1) {
                    usuario = new Usuario(jObjeto.getInt("id"), jObjeto.getString("nome"));
                    usuario.setFoto(jObjeto.getString("foto"));
                    usuario.setEmail(jObjeto.getString("res_solicitacao"));
                } else {
                    usuario = new Usuario(teste);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return usuario;
        }

        @Override
        protected void onPostExecute(Object resultado) {
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    public class exibirMinhasSolicitaçõesAsyncTask extends AsyncTask<Void, Void, List<Carona>> {

        Usuario usuario;
        GetRetorno retornoUsuario;
        int ttVsAtuais, ttBuscar;

        public exibirMinhasSolicitaçõesAsyncTask(int ttVsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {
            this.ttVsAtuais = ttVsAtuais;
            this.ttBuscar = ttBuscar;
            this.usuario = usuario;
            this.retornoUsuario = retorno;
        }

        @Override
        protected List<Carona> doInBackground(Void... params) { //Implementação obrigatória.

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id", this.usuario.getId() + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            dados.add(new BasicNameValuePair("ttVsAtuais", this.ttVsAtuais + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
            dados.add(new BasicNameValuePair("ttBuscar", this.ttBuscar + ""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.


            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.
            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.


            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "caronasAceitas.php");    //Fazer uma requisição tipo Post no WebService.
            //Página de registro
            List<Carona> caronas = new LinkedList<Carona>();    //Variável que irá receber os dados do usuário.

            try {
                post.setEntity(new UrlEncodedFormEntity(dados, "UTF-8"));    //Configurando a entidade na requisição post.
                HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.
                // Recebendo a resposta do servidor após a execução do HTTP POST.
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);
                //
                JSONObject jObj = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.

                if (jObj.length() == 0 || jObj.getInt("tamanho") == 0) {    //Se o tamanho de jObj for igual a zero.
                    caronas = null;
                } else {
                    for (int i = 0; i < jObj.getInt("tamanho"); i++) {

                        String origem = jObj.getString("origem_" + i);
                        String destino = jObj.getString("destino_" + i);
                        String tipoVeiculo = jObj.getString("tipoVeiculo_" + i);
                        String restricao = jObj.getString("restricao_" + i);
                        String horario = jObj.getString("horario_" + i);
                        int status = jObj.getInt("status_" + i);
                        int ativo = jObj.getInt("ativo_" + i);
                        int id = jObj.getInt("id_" + i);
                        String ponto = jObj.getString("ponto_" + i);

                        Carona car = new Carona(origem, destino, horario, tipoVeiculo, restricao, ponto);
                        car.setStatus(status);
                        car.setAtivo(ativo);
                        car.setId(id);

                        caronas.add(car);
                    }
                }

            } catch (Exception e) {
                e.getMessage();// se não der certo:mensagem de erro
            }
            return caronas;    //Retorno para o método 'onPostExecute'.
        }//Fim método.

        @Override
        protected void onPostExecute(List<Carona> Caronas) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(Caronas);
            super.onPostExecute(Caronas);
        }//Fim método.
    }

    public class BuscaCaronasAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        int ultimoValor;
        int totalViews;
        GetRetorno retornoUsuario;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public BuscaCaronasAsyncTask(Usuario usuario, int ultimoValor, int totalViews, GetRetorno retorno) {
            this.usuario = usuario;
            this.ultimoValor = ultimoValor;
            this.totalViews = totalViews;
            this.retornoUsuario = retorno;

        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("sexoUsuario", usuario.getSexo()));
            dadosParaEnvio.add(new BasicNameValuePair("idUser", usuario.getId() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("ultimoValor", ultimoValor + ""));
            dadosParaEnvio.add(new BasicNameValuePair("totalViews", totalViews + ""));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Listas.php");
            String teste = "Não foi possível se conectar";
            JSONObject jObjeto = null;
            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    jObjeto = new JSONObject(resultado);
                    if (jObjeto == null) {
                        Log.e("objeto:", "nulo");
                    } else {
                        Log.e("objeto:", "não nulo");
                    }
                    return jObjeto;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObjeto;
        }

        @Override
        protected void onPostExecute(Object objeto) {
            JSONObject jObjeto = (JSONObject) objeto;
            List<Carona> caronas = new LinkedList<Carona>();
            List<Usuario> usuarios = new LinkedList<Usuario>();
            try {
                for (int i = 0; i <= jObjeto.getInt("tamanho"); i++) {
                    String origem = jObjeto.getString("origem_" + i);
                    String destino = jObjeto.getString("destino_" + i);
                    String ponto = jObjeto.getString("ponto_" + i);
                    String horario = jObjeto.getString("horario_" + i);
                    String tipoVeiculo = jObjeto.getString("tipoVeiculo_" + i);
                    String restricao = jObjeto.getString("restricao_" + i);
                    int vagas = jObjeto.getInt("vagas_" + i);
                    int vagasOcupadas = jObjeto.getInt("vagas_ocupadas_" + i);
                    int status = jObjeto.getInt("status_" + i);
                    int ativo = jObjeto.getInt("ativo_" + i);
                    int id = jObjeto.getInt("id_" + i);

                    String dataCriacao = jObjeto.getString("datacriacao_" + i);
                    Carona car = new Carona(origem, destino, horario, tipoVeiculo, restricao, vagas, ponto);
                    car.setId(id);
                    car.setStatus(status);
                    car.setVagasOcupadas(vagasOcupadas);
                    car.setAtivo(ativo);
                    car.setDataCriacao(dataCriacao);

                    List<Usuario> participantes = new LinkedList<Usuario>();
                    List participantesStatus = new LinkedList();
                    for (int j = 0; j < jObjeto.getInt("participantes_" + i + "_tamanho"); j++) {
                        int idPart = jObjeto.getInt("participantes_" + i + "_" + j + "_id");
                        String nomePart = jObjeto.getString("participantes_" + i + "_" + j + "_nome");
                        String sobrenomenomePart = jObjeto.getString("participantes_" + i + "_" + j + "_sobrenome");
                        String statusSoliciacao = jObjeto.getString("participantes_" + i + "_" + j + "_status_solicitacao");
                        Usuario participante = new Usuario(idPart, nomePart);
                        participante.setSobrenome(sobrenomenomePart);
                        participantes.add(participante);
                        participantesStatus.add(statusSoliciacao);
                    }
                    car.setParticipantes(participantes);
                    car.setParticipantesStatus(participantesStatus);
                    caronas.add(car);

                    String telefone = jObjeto.getString("telefone_" + i);
                    String nome = jObjeto.getString("nome_" + i);
                    String sobrenome = jObjeto.getString("sobrenome_" + i);
                    String email = jObjeto.getString("email_" + i);
                    String foto = jObjeto.getString("foto_" + i);
                    int cnh1 = jObjeto.getInt("cnh_" + i);
                    int idU = jObjeto.getInt("idUser_" + i);
                    boolean cnh = true;
                    if (cnh1 == 0) {
                        cnh = false;
                    }

                    String matricula = jObjeto.getString("matricula_" + i);
                    String sexo = jObjeto.getString("sexo_" + i);
                    Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);
                    usuario.setFoto(foto);
                    usuario.setId(idU);
                    usuarios.add(usuario);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(caronas, usuarios);
            super.onPostExecute(objeto);
        }
    }

    public class recuperarSenhaAsyncTask extends AsyncTask<Void, Void, Object> {
        String emailUser;
        GetRetorno retornoUsuario;

        public recuperarSenhaAsyncTask(String email, GetRetorno retorno) {
            this.emailUser = email;
            this.retornoUsuario = retorno;
        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {

            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("email_user", emailUser + ""));

            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "RetornaDados.php");
            String teste = "Não foi possível se conectar";
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);

                JSONObject jObjeto = new JSONObject(resultado);
                teste = jObjeto.getString("teste");

            } catch (Exception e) {
                Log.e(TAG, "erro registro " + e);
                e.printStackTrace();
            }
            return teste;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object resultado) {
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    public class BuscaSolicitacaoAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        int ttViewsAtuais, ttBuscar;
        GetRetorno retornoUsuario;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public BuscaSolicitacaoAsyncTask(int ttVsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;
            this.ttViewsAtuais = ttVsAtuais;
            this.ttBuscar = ttBuscar;
        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("id_user", usuario.getId() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("ttViewsAtuais", ttViewsAtuais + ""));
            dadosParaEnvio.add(new BasicNameValuePair("ttBuscar", ttBuscar + ""));

            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "buscaSolicitacoes.php");
            String teste = "Não foi possível se conectar";
            JSONObject jObjeto = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse;
                jObjeto = new JSONObject(resultado);

                if (jObjeto == null || jObjeto.getInt("tamanho") == -1) {
                    jObjeto = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObjeto;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object objeto) {
            JSONObject jObjeto = (JSONObject) objeto;
            List<Carona> caronas = new LinkedList<Carona>();
            try {
                for (int i = 0; i <= jObjeto.getInt("tamanho"); i++) {
                    String origem = jObjeto.getString("origem_" + i);
                    String destino = jObjeto.getString("destino_" + i);
                    String ponto = jObjeto.getString("ponto_" + i);
                    String horario = jObjeto.getString("horario_" + i);
                    String tipoVeiculo = jObjeto.getString("tipoVeiculo_" + i);
                    String restricao = jObjeto.getString("restricao_" + i);
                    int vagas = jObjeto.getInt("vagas_" + i);
                    int vagasOcupadas = jObjeto.getInt("vagas_ocupadas_" + i);
                    int status = jObjeto.getInt("status_" + i);
                    int ativo = jObjeto.getInt("ativo_" + i);
                    int id = jObjeto.getInt("id_" + i);

                    String dataCriacao = jObjeto.getString("datacriacao_" + i);
                    Carona car = new Carona(origem, destino, horario, tipoVeiculo, restricao, vagas, ponto);
                    car.setId(id);
                    car.setStatus(status);
                    car.setVagasOcupadas(vagasOcupadas);
                    car.setAtivo(ativo);
                    car.setDataCriacao(dataCriacao);

                    List<Usuario> participantes = new LinkedList<Usuario>();
                    List participantesStatus = new LinkedList();
                    for (int j = 0; j < jObjeto.getInt("participantes_" + i + "_tamanho"); j++) {
                        int idPart = jObjeto.getInt("participantes_" + i + "_" + j + "_id");
                        String nomePart = jObjeto.getString("participantes_" + i + "_" + j + "_nome");
                        String foto = jObjeto.getString("participantes_" + i + "_" + j + "_foto");
                        String telefone = jObjeto.getString("participantes_" + i + "_" + j + "_telefone");
                        String statusSoliciacao = jObjeto.getString("participantes_" + i + "_" + j + "_status_solicitacao");
                        String sexo = jObjeto.getString("participantes_" + i + "_" + j + "_sexo");
                        String cnh = jObjeto.getString("participantes_" + i + "_" + j + "_cnh");
                        String sobrenome = jObjeto.getString("participantes_" + i + "_" + j + "_sobrenome");
                        String matricula = jObjeto.getString("participantes_" + i + "_" + j + "_matricula");
                        String email = jObjeto.getString("participantes_" + i + "_" + j + "_email");
                        Usuario participante = new Usuario(idPart, nomePart);
                        participante.setFoto(foto);
                        participante.setSobrenome(sobrenome);
                        participante.setMatricula(matricula);
                        participante.setEmail(email);
                        participante.setTelefone(telefone);
                        if (cnh.equals("1")) {
                            participante.setChn(true);
                        } else {
                            participante.setChn(false);
                        }
                        participante.setSexo(sexo);
                        participantes.add(participante);
                        participantesStatus.add(statusSoliciacao);
                    }
                    car.setParticipantes(participantes);
                    car.setParticipantesStatus(participantesStatus);
                    caronas.add(car);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(caronas);
            super.onPostExecute(objeto);
        }
    }

    public class BuscaUltimaCaronasAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;
        int idUltimaCarona;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public BuscaUltimaCaronasAsyncTask(Usuario usuario, int idUltimaCarona, GetRetorno retorno) {
            this.usuario = usuario;
            this.idUltimaCarona = idUltimaCarona;
            this.retornoUsuario = retorno;
        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("sexoUsuario", usuario.getSexo()));
            dadosParaEnvio.add(new BasicNameValuePair("id_user", usuario.getId() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("id", this.idUltimaCarona + ""));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            String arquivoServ = "UltimasCaronas.php";

            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + arquivoServ);
            String teste = "Erro de conexão";
            JSONObject jObjeto = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse;

                jObjeto = new JSONObject(resultado);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return jObjeto;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object objeto) {
            JSONObject jObjeto = (JSONObject) objeto;
            List<Carona> caronas = new LinkedList<Carona>();
            List<Usuario> usuarios = new LinkedList<Usuario>();
            try {

                for (int i = 0; i <= jObjeto.getInt("tamanho"); i++) {
                    if (new ManipulaDados(cnt).getCaronaSolicitada() == jObjeto.getInt("id_" + i)) {
                        continue;
                    }
                    String origem = jObjeto.getString("origem_" + i);
                    String destino = jObjeto.getString("destino_" + i);
                    String ponto = jObjeto.getString("ponto_" + i);
                    String horario = jObjeto.getString("horario_" + i);
                    String tipoVeiculo = jObjeto.getString("tipoVeiculo_" + i);
                    String restricao = jObjeto.getString("restricao_" + i);
                    int vagas = jObjeto.getInt("vagas_" + i);
                    int status = jObjeto.getInt("status_" + i);
                    int ativo = jObjeto.getInt("ativo_" + i);
                    int id = jObjeto.getInt("id_" + i);

                    String dataCriacao = jObjeto.getString("datacriacao_" + i);
                    Carona car = new Carona(origem, destino, horario, tipoVeiculo, restricao, vagas, ponto);
                    car.setId(id);
                    car.setStatus(status);
                    car.setAtivo(ativo);
                    car.setDataCriacao(dataCriacao);

                    List<Usuario> participantes = new LinkedList<Usuario>();
                    List participantesStatus = new LinkedList();
                    for (int j = 0; j < jObjeto.getInt("participantes_" + i + "_tamanho"); j++) {
                        int idPart = jObjeto.getInt("participantes_" + i + "_" + j + "_id");
                        String nomePart = jObjeto.getString("participantes_" + i + "_" + j + "_nome");
                        String statusSoliciacao = jObjeto.getString("participantes_" + i + "_" + j + "_status_solicitacao");
                        Usuario participante = new Usuario(idPart, nomePart);
                        participantes.add(participante);
                        participantesStatus.add(statusSoliciacao);
                    }
                    car.setParticipantes(participantes);
                    car.setParticipantesStatus(participantesStatus);
                    caronas.add(car);

                    String telefone = jObjeto.getString("telefone_" + i);
                    String nome = jObjeto.getString("nome_" + i);
                    String sobrenome = jObjeto.getString("sobrenome_" + i);
                    String email = jObjeto.getString("email_" + i);
                    String foto = jObjeto.getString("foto_" + i);
                    int cnh1 = jObjeto.getInt("cnh_" + i);
                    int idU = jObjeto.getInt("idU_" + i);
                    boolean cnh = true;
                    if (cnh1 == 0) {
                        cnh = false;
                    }

                    String matricula = jObjeto.getString("matricula_" + i);
                    String sexo = jObjeto.getString("sexo_" + i);
                    Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);
                    usuario.setFoto(foto);
                    usuario.setId(idU);
                    usuarios.add(usuario);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(caronas, usuarios);
            super.onPostExecute(objeto);
        }
    }

    public class solicitaCaronaAsyncTask extends AsyncTask<Void, Void, String[]> {
        Carona carona;
        Usuario usuario;
        GetRetorno retorno;

        public solicitaCaronaAsyncTask(Carona carona, Usuario usuario, GetRetorno retorno) {
            this.carona = carona;
            this.retorno = retorno;
            this.usuario = usuario;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                    if (isCancelled()) {
                        Toast.makeText(cnt, "Operação cancelada! Verifique sua conexão", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            running = false;
        }


        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected String[] doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            dadosParaEnvio.add(new BasicNameValuePair("idCaronaSolicita", carona.getId() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("idUsuarioSolicita", usuario.getId() + ""));
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Registros.php");
            String res[] = new String[2];
            res[0] = "Erro 404";
            try {
                while (running) {
                    post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio, "UTF-8"));
                    HttpResponse httpResposta = cliente.execute(post);
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    JSONObject jObjeto = new JSONObject(resultado);
                    res[0] = jObjeto.getString("teste");
                    res[1] = jObjeto.getString("v_oculpadas");
                    return res;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String[] resultado) {
            progressDialog.dismiss();
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }
}

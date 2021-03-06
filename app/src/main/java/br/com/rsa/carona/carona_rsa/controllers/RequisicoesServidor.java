package br.com.rsa.carona.carona_rsa.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class RequisicoesServidor {
    String TAG = "ERROS";
    ProgressDialog progressDialog;
    public static final int TEMPO_CONEXAO = 1000 * 10;
    public static final String ENDERECO_SERVIDOR = "http://carona.devdes.com.br/Caronas/";
    Context cnt;
    private volatile boolean running;
    ManipulaDados mDados;

    public RequisicoesServidor(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Aguarde...");
        running = true;
        cnt = context;
        mDados = new ManipulaDados(cnt);
    }

    public boolean isConnectedToServer(String url) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(TEMPO_CONEXAO);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, String> mapaValores(String[] label, String[] valores) {
        Map<String, String> dataToSend = new HashMap<>();
        for (int i = 0; i < label.length; i++) {
            dataToSend.put(label[i], valores[i]);
        }
        return dataToSend;
    }

    private HttpURLConnection modoCon(String nome) throws IOException {
        URL url = new URL(ENDERECO_SERVIDOR + nome);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setConnectTimeout(TEMPO_CONEXAO);
        con.setReadTimeout(TEMPO_CONEXAO);
        return con;
    }

    public void gravaDadosDoUsuario(Usuario usuario, GetRetorno retorno) {
        Log.e("esse:","1.3");
        progressDialog.show();
        new armazenaDadosUsuarioAsyncTask(usuario, retorno).execute();
    }

    public void buscaDadosDoUsuario(Usuario usuario, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","1.4");
        progressDialog.show();
        new BuscaDadosUsuarioAsyncTask(usuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscasSolicitacoesCaronas(int ttViewsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","1.5");
        progressDialog.show();// Mostra a barra de dialogo
        new BuscaSolicitacaoAsyncTask(ttViewsAtuais, ttBuscar, usuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscarComentariosCarona(int ttComents, int ttBuscar, int idCarona, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        //progressDialog.show();// Mostra a barra de dialogo.
        new buscarComentariosAsyncTask(ttComents, ttBuscar, idCarona, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void gravaCarona(Carona carona, int idUsuario, GetRetorno retorno) {
        // progressDialog.show();
        new armazenaCaronaAsyncTask(carona, idUsuario, retorno).execute();

    }

    public void gravarComentarioCarona(int IdCarona, int idUsuario, String texto, GetRetorno retorno) {
        //progressDialog.show();
        new gravarComentarioCaronaAsyncTask(idUsuario, IdCarona, texto, retorno).execute();
    }

    public void recuperarSenha(String email, GetRetorno retorno) {
        Log.e("esse:","2");
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
        new solicitaCaronaAsyncTask(carona, usuario, retorno).execute();
    }

    public void fecharCaronaOferecida(int idCarona, int idUsuario, int tipo, GetRetorno retorno) {
        Log.e("esse:","3");
        progressDialog.show();
        new fecharCaronaOferecidaAsyncTask(idCarona, idUsuario, tipo, retorno).execute();
    }

    public void exibirMinhasSolicitações(int ttVsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {
        Log.e("esse:","4");
        progressDialog.show();// Mostra a barra de dialogo.
        new exibirMinhasSolicitaçõesAsyncTask(ttVsAtuais, ttBuscar, usuario, retorno).execute();
    }

    public void exibirSolicitacoesCaronas(Usuario usuario, GetRetorno retorno) {
        Log.e("esse:","5");
        progressDialog.show();// Mostra a barra de dialogo.
        new exibirUsuariosSolicitantesAsyncTask(usuario, retorno).execute();
    }

    public void aceitarRecusarCaronas(Usuario usuario, String resposta, GetRetorno retorno) {
        //progressDialog.show();// Mostra a barra de dialogo.
        new aceitaOuRecusaCaronaAsyncTask(usuario, resposta, retorno).execute();
    }

    public void alteraStatusCarona(int idCarona, int valor, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","6");
        progressDialog.show();// Mostra a barra de dialogo.
        new alteraStatusCaronaAsyncTask(idCarona, valor, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void aguardaRespostaCarona(int idUser, int idCar, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","7");
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaCaronaAsyncTask(idUser, idCar, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }


    public void desistirCarona(int idUsuario, int idCarona, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","8");
        progressDialog.show();// Mostra a barra de dialogo.
        new cancelarCaronaAsyncTask(idCarona, idUsuario, retorno).execute();    //Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    public void buscaCaronas(Usuario usuario, int ultimoValor, int totalViews, GetRetorno retorno) {    //Método que busca a classe que vai receber os dados do usuario.
        Log.e("esse:","9");
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
            Map<String, String> dataToSend = new HashMap<>();
            if (usuario.getNome() != null) dataToSend.put("nome", usuario.getNome());
            if (usuario.getSobrenome() != null) dataToSend.put("sobrenome", usuario.getSobrenome());
            if (usuario.getMatricula() != null) dataToSend.put("matricula", usuario.getMatricula());
            if (usuario.getTelefone() != null) dataToSend.put("telefone", usuario.getTelefone());
            if (usuario.getEmail() != null) dataToSend.put("email", usuario.getEmail());
            if (usuario.getSexo() != null) dataToSend.put("sexo", usuario.getSexo());
            dataToSend.put("ativo", usuario.getAtivo() + "");
            if (usuario.getSenha() != null) dataToSend.put("senha", usuario.getSenha());
            if (usuario.getFoto() != null) dataToSend.put("foto", usuario.getFoto());
            if (usuario.getExtFoto() != null) dataToSend.put("extencao", usuario.getExtFoto());
            String aquivoPhp = "Registros.php";
            if (usuario.getEdicao()) {
                //dadosParaEnvio.add(new BasicNameValuePair("id_edicao", usuario.getId() + ""));
                dataToSend.put("id_edicao", usuario.getId() + "");
                aquivoPhp = "Edicao.php";
            }

            if (usuario.isCnh()) {
                //dadosParaEnvio.add(new BasicNameValuePair("cnh", "1"));
                dataToSend.put("cnh", "1");

            } else {
                //dadosParaEnvio.add(new BasicNameValuePair("cnh", "0"));
                dataToSend.put("cnh", "0");
            }
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            String teste = "Sem conexão!";

            try {
                while (running) {
                    HttpURLConnection con = modoCon(aquivoPhp);
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((teste = reader.readLine())!= null) {
                        sb.append(teste + "\n");
                    }
                    JSONObject jObjeto = new JSONObject(sb.toString());
                    teste = jObjeto.getString("teste");
                    return teste;
                }
            } catch (Exception e) {
                Log.e(TAG, "erro registro " + e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
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
            String label[] = {"id_carona", "id_user", "texto_comentario"};
            String valores[] = {idCarona + "", idUsuario + "", this.texto};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Sem conexão";
            try {
                HttpURLConnection con = modoCon("Registros.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                JSONObject jObjeto = new JSONObject(sb.toString());
                teste = jObjeto.getString("teste");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
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

    public class BuscaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        int idUser;
        int idCar;
        GetRetorno retornoUsuario;

        public BuscaCaronaAsyncTask(int idU, int idC, GetRetorno retorno) {
            idUser = idU;
            idCar = idC; //O campo usuário recebe o parâmetro de usuário.
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
        protected Carona doInBackground(Void... params) {
            String label[] = {"id_usuario", "id_carona"};
            String valores[] = {idUser + "", idCar + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            Carona car = null;
            try {
                while (running) {
                    HttpURLConnection con = modoCon("aguardaConfirmacaoCarona.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String teste = null;
                    while ((teste = reader.readLine()) != null) {
                        sb.append(teste + "\n");
                    }
                    JSONObject jObj = new JSONObject(sb.toString());
                    if (jObj.length() == 0) {
                        return car;
                    } else {
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
                        carona.setId(idCar);
                        carona.setStatus(1);
                        carona.setStatusUsuario(statusUsuario);
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
                        carona.setProprietario(usuario);
                        car = carona;
                        return car;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "doInBackground " + e.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return car;
        }

        @Override
        protected void onPostExecute(Object car) {
            progressDialog.dismiss();
            retornoUsuario.concluido(car);
            super.onPostExecute(car);
        }
    }

    public class alteraStatusCaronaAsyncTask extends AsyncTask<Void, Void, String> {
        int idCarona;
        int status;
        GetRetorno retornoUsuario;

        public alteraStatusCaronaAsyncTask(int idCarona, int status, GetRetorno retorno) {
            this.idCarona = idCarona;
            this.status = status;
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
            String label[] = {"status3", "id_carona3"};
            String valores[] = {this.status + "", this.idCarona + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String mensagem = "Sem conexão!";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("RetornaDados.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((mensagem = reader.readLine()) != null) {
                        sb.append(mensagem + "\n");
                    }
                    JSONObject jObj = new JSONObject(sb.toString());
                    if (jObj.length() == 0) {
                    } else {
                        mensagem = jObj.getString("mensagem");
                    }
                    return mensagem;
                }
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
        protected JSONObject doInBackground(Void... params) {
            String label[] = {"id_comentario_carona", "tt_buscar", "tt_coments"};
            String valores[] = {this.idCarona + "", this.ttBuscar + "", this.ttComents + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            JSONObject jObjeto = null;
            try {
                HttpURLConnection con = modoCon("RetornaDados.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jObjeto = new JSONObject(sb.toString());
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            } finally {
                if (reader != null) {
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
        }
    }

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
            String label[] = {"id_user_close", "id_carona_close", "tipo"};
            String valores[] = {this.idUsuario + "", this.idCarona + "", this.tipo + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String mensagem = "Verifique sua conexão!";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("RetornaDados.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((mensagem = reader.readLine()) != null) {
                        sb.append(mensagem + "\n");
                    }
                    JSONObject jObj = new JSONObject(sb.toString());
                    mensagem = jObj.getString("mensagem");
                    return mensagem;
                }
            } catch (Exception e) {
                Log.e(TAG, "vamo lá " + e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return mensagem;
        }

        @Override
        protected void onPostExecute(String mensagem) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(mensagem);
            super.onPostExecute(mensagem);
        }
    }


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
            String label[] = {"id", "resposta"};
            String valores[] = {usuario.getId() + "", this.resposta};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Sem conexão!";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("aceitaRecusaCarona.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((teste = reader.readLine()) != null) {
                        sb.append(teste + "\n");
                    }
                    JSONObject jObjeto = new JSONObject(sb.toString());
                    teste = jObjeto.getString("teste");
                    return teste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
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
            //progressDialog.dismiss();
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }

    public class exibirUsuariosSolicitantesAsyncTask extends AsyncTask<Void, Void, List<Usuario>> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        public exibirUsuariosSolicitantesAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;
        }

        @Override
        protected List<Usuario> doInBackground(Void... params) {
            String label[] = {"id"};
            String valores[] = {this.usuario.getId() + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            List<Usuario> usuarios = new LinkedList<Usuario>();    //Variável que irá receber os dados do usuário.
            try {
                HttpURLConnection con = modoCon("buscarSolicitantesCaronas.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String mensagem = null;
                while ((mensagem = reader.readLine()) != null) {
                    sb.append(mensagem + "\n");
                }
                JSONObject jObj = new JSONObject(sb.toString());
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
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
            String label[] = {"matricula", "senha"};
            String valores[] = {this.usuario.getMatricula(), this.usuario.getSenha()};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            Usuario usuarioRetornado = null;
            try {
                HttpURLConnection con = modoCon("buscaDadosUsuario.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String teste = null;
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                JSONObject jObj = new JSONObject(sb.toString());
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
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return usuarioRetornado;
        }

        @Override
        protected void onPostExecute(Object usuarioRetornado) {
            Log.e("okkkk", "sim");
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
        protected List<Usuario> doInBackground(Void... params) {
            String label[] = {"id_usuario2", "status"};
            String valores[] = {this.usuario.getId() + "", this.status};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            List<Usuario> usuarios = null;
            try {
                HttpURLConnection con = modoCon("RetornaDados.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String teste = null;
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                JSONObject jObj = new JSONObject(sb.toString());
                usuarios = new LinkedList<Usuario>();
                if (jObj.getInt("retorno") > 0) {
                    for (int i = 0; i < jObj.getInt("tamanho"); i++) {
                        String nome = jObj.getString("nome_" + i);
                        String foto = jObj.getString("foto_" + i);
                        Integer id = jObj.getInt("id_" + i);
                        String telefone = jObj.getString("telefone_" + i);
                        String sexo = jObj.getString("sexo_" + i);
                        String cnh = jObj.getString("cnh_" + i);
                        String sobrenome = jObj.getString("sobrenome_" + i);
                        String matricula = jObj.getString("matricula_" + i);
                        String email = jObj.getString("email_" + i);
                        boolean cnhAdd;
                        if (cnh.equals("1")) {
                            cnhAdd = true;
                        } else {
                            cnhAdd = false;
                        }
                        Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnhAdd);
                        usuario.setId(id);
                        usuario.setFoto(foto);
                        usuario.setStatus(status);
                        usuarios.add(usuario);
                    }
                    return usuarios;
                }
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return usuarios;
        }

        @Override
        protected void onPostExecute(List<Usuario> usuariosRetornado) {
            retornoUsuario.concluido(usuariosRetornado);
            super.onPostExecute(usuariosRetornado);
        }
    }

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
            String label[] = {"id_carona", "id_usuario"};
            String valores[] = {carona + "", usuario + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Sem conexão!";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("cancelarCarona.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    teste = null;
                    while ((teste = reader.readLine()) != null) {
                        sb.append(teste + "\n");
                    }
                    JSONObject jObjeto = new JSONObject(sb.toString());
                    teste = jObjeto.getString("desistencia");
                    return teste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
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
            progressDialog.dismiss();
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }


    public class armazenaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Carona carona;
        int usuario;
        GetRetorno retorno;

        public armazenaCaronaAsyncTask(Carona carona, int idUsuario, GetRetorno retorno) {
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


        @Override
        protected Object doInBackground(Void... params) {
            String label[] = {"origem", "destino", "horario", "tipoVeiculo", "ponto", "restricao", "vagas", "id_usuario"};
            String valores[] = {carona.getOrigem(), carona.getDestino(), carona.getHorario(), carona.getTipoVeiculo(), carona.getPonto(), carona.getRestricao(), carona.getVagas() + "", usuario + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Verifique sua conexão!";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("Registros.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((teste = reader.readLine()) != null) {
                        sb.append(teste + "\n");
                    }
                    JSONObject jObjeto = new JSONObject(sb.toString());
                    teste = jObjeto.getString("teste");
                    int valor = Integer.parseInt(teste);
                    if (valor > 0) {
                        String retornoId = jObjeto.getString("idCar");
                        return retornoId;
                    }
                    return teste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
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
            progressDialog.dismiss();
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
            String label[] = {"id_carona", "id_usuario"};
            String valores[] = {idCarona + "", usuario.getId() + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            int teste = -100;
            Usuario usuario = null;
            try {
                HttpURLConnection con = modoCon("RetornaDados.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject jObjeto = new JSONObject(sb.toString());
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
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
        protected List<Carona> doInBackground(Void... params) {
            String label[] = {"id", "ttVsAtuais", "ttBuscar"};
            String valores[] = {this.usuario.getId() + "", this.ttVsAtuais + "", this.ttBuscar + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            List<Carona> caronas = new LinkedList<Carona>();

            try {
                HttpURLConnection con = modoCon("caronasAceitas.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String mensagem = null;
                while ((mensagem = reader.readLine()) != null) {
                    sb.append(mensagem + "\n");
                }
                JSONObject jObj = new JSONObject(sb.toString());
                if (jObj.length() == 0 || jObj.getInt("tamanho") == 0) {
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
                e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return caronas;
        }

        @Override
        protected void onPostExecute(List<Carona> Caronas) {
            progressDialog.dismiss();
            retornoUsuario.concluido(Caronas);
            super.onPostExecute(Caronas);
        }
    }

    public class BuscaCaronasAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        int ultimoValor;
        int totalViews;
        GetRetorno retornoUsuario;

        public BuscaCaronasAsyncTask(Usuario usuario, int ultimoValor, int totalViews, GetRetorno retorno) {
            this.usuario = usuario;
            this.ultimoValor = ultimoValor;
            this.totalViews = totalViews;
            this.retornoUsuario = retorno;
            Log.e("dadoskk:","_sx:"+this.usuario.getSexo()+"_id:"+this.usuario.getId()+"_ultVl:"+this.ultimoValor+"_ttVw:"+this.totalViews);
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
            String label[] = {"sexoUsuario", "idUser", "ultimoValor", "totalViews"};
            String valores[] = {usuario.getSexo(), usuario.getId() + "", ultimoValor + "", totalViews + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Não foi possível se conectar";
            JSONObject jObjeto = null;
            try {
                while (running) {
                    HttpURLConnection con = modoCon("Listas.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while ((teste = reader.readLine()) != null) {
                        sb.append(teste + "\n");
                    }
                    jObjeto = new JSONObject(sb.toString());
                    return jObjeto;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
            progressDialog.dismiss();
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

        @Override
        protected Object doInBackground(Void... params) {
            String label[] = {"email_user"};
            String valores[] = {emailUser + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Não foi possível se conectar";
            try {
                HttpURLConnection con = modoCon("RetornaDados.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                JSONObject jObjeto = new JSONObject(sb.toString());
                teste = jObjeto.getString("teste");
            } catch (Exception e) {
                Log.e(TAG, "erro registro " + e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
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
            progressDialog.dismiss();
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }
    public class BuscaSolicitacaoAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        int ttViewsAtuais, ttBuscar;
        GetRetorno retornoUsuario;

        public BuscaSolicitacaoAsyncTask(int ttVsAtuais, int ttBuscar, Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;
            this.ttViewsAtuais = ttVsAtuais;
            this.ttBuscar = ttBuscar;
        }

        @Override
        protected Object doInBackground(Void... params) {
            String label[] = {"id_user", "ttViewsAtuais", "ttBuscar"};
            String valores[] = {usuario.getId() + "", ttViewsAtuais + "", ttBuscar + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String teste = "Não foi possível se conectar";
            JSONObject jObjeto = null;
            try {
                HttpURLConnection con = modoCon("buscaSolicitacoes.php");
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                jObjeto = new JSONObject(sb.toString());
                if (jObjeto == null || jObjeto.getInt("tamanho") == -1) {
                    jObjeto = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return jObjeto;
        }

        @Override
        protected void onPostExecute(Object objeto) {
            JSONObject jObjeto = (JSONObject) objeto;
            List<Carona> caronas = new LinkedList<Carona>();
            if(caronas!=null) {
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
            }
            progressDialog.dismiss();
            retornoUsuario.concluido(caronas);
            super.onPostExecute(objeto);
        }
    }

    public class BuscaUltimaCaronasAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;
        int idUltimaCarona;

        public BuscaUltimaCaronasAsyncTask(Usuario usuario, int idUltimaCarona, GetRetorno retorno) {
            this.usuario = usuario;
            this.idUltimaCarona = idUltimaCarona;
            this.retornoUsuario = retorno;
        }

        @Override
        protected Object doInBackground(Void... params) {
            String label[] = {"sexoUsuario", "id_user", "id"};
            String valores[] = {usuario.getSexo(), usuario.getId() + "", this.idUltimaCarona + ""};
            Log.e("Dados:", usuario.getSexo() + " " + usuario.getId() + " " + this.idUltimaCarona);
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String arquivoServ = "UltimasCaronas.php";
            String teste = "Erro de conexão";
            JSONObject jObjeto = null;
            try {
                HttpURLConnection con = modoCon(arquivoServ);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((teste = reader.readLine()) != null) {
                    sb.append(teste + "\n");
                }
                jObjeto = new JSONObject(sb.toString());
                return jObjeto;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    if (mDados.getCaronaSolicitada() != null && mDados.getCaronaSolicitada().getId() == jObjeto.getInt("id_" + i)) {
                        continue;
                    }

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
                    car.setAtivo(ativo);
                    car.setVagasOcupadas(vagasOcupadas);
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
                    Log.e("okok1", "sim");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

        @Override
        protected String[] doInBackground(Void... params) {
            String label[] = {"idCaronaSolicita", "idUsuarioSolicita"};
            String valores[] = {carona.getId() + "", usuario.getId() + ""};
            String encodedStr = getEncodedData(mapaValores(label, valores));
            BufferedReader reader = null;
            String res[] = new String[2];
            res[0] = "Erro 404";
            try {
                while (running) {
                    HttpURLConnection con = modoCon("Registros.php");
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(encodedStr);
                    writer.flush();
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String mensagem = null;
                    while ((mensagem = reader.readLine()) != null) {
                        sb.append(mensagem + "\n");
                    }
                    JSONObject jObjeto = new JSONObject(sb.toString());
                    res[0] = jObjeto.getString("teste");
                    res[1] = jObjeto.getString("v_oculpadas");
                    return res;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

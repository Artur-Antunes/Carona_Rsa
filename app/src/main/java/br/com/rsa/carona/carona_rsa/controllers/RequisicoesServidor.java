package br.com.rsa.carona.carona_rsa.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class RequisicoesServidor {

    ProgressDialog progressDialog;//componente que mostra circulo de progresso
    public static final int TEMPO_CONEXAO = 1000 * 35; //tempo maximo de conex�o
    public static final String ENDERECO_SERVIDOR = "http://10.0.2.2/Caronas/";//local onde esta meu projeto php que salva e busca dados no banco

    //contrutor executa o circulo que pede pra aquardar at� que a conex�o seja terminada
    public RequisicoesServidor(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage("Aguarde por favor...");
    }

    //metodo para guardar usuario no banco usando uma classe que economiza recursos do celular
    public void gravaDadosDoUsuario(Usuario usuario, GetRetorno retorno) {
        progressDialog.show();
        new armazenaDadosUsuarioAsyncTask(usuario, retorno).execute();
    }

    public void buscaDadosDoUsuario(Usuario usuario,GetRetorno retorno){	//Método que busca a classe que vai receber os dados do usuario.
        Log.e("entrou","boraaa");
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaDadosUsuarioAsyncTask(usuario,retorno).execute();	//Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }

    //classe que armazena dados de usuario no banco de modo  que economiza recursos do celular
    public class armazenaDadosUsuarioAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public armazenaDadosUsuarioAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;

        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            //adicionado dados no arraylist para ser enviado

            dadosParaEnvio.add(new BasicNameValuePair("nome", usuario.getNome()));
            dadosParaEnvio.add(new BasicNameValuePair("sobrenome", usuario.getSobrenome()));
            dadosParaEnvio.add(new BasicNameValuePair("matricula", usuario.getMatricula()));
            dadosParaEnvio.add(new BasicNameValuePair("telefone", usuario.getTelefone()));
            dadosParaEnvio.add(new BasicNameValuePair("email", usuario.getEmail()));
            dadosParaEnvio.add(new BasicNameValuePair("sexo", usuario.getSexo()));
            dadosParaEnvio.add(new BasicNameValuePair("ativo", usuario.getAtivo() + ""));
            dadosParaEnvio.add(new BasicNameValuePair("senha", usuario.getSenha()));
            if (usuario.isCnh()) {
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "TRUE"));
            } else {
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "FALSE"));
            }

            Log.e("se vai gravar", usuario.getNome());
            //delara��o de variaveis http (params, cliente, post) para enviar dados
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Registros.php");
            String teste = "não";
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse

                JSONObject jObjeto = new JSONObject(resultado);
                teste = jObjeto.getString("teste");
                Log.e("FOOOIII????", teste);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("erro serv", e + "");
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

        public class BuscaDadosUsuarioAsyncTask extends AsyncTask<Void, Void, Object> {
            //Campos da classe.
            Usuario usuario;
            GetRetorno retornoUsuario;

            public BuscaDadosUsuarioAsyncTask(Usuario usuario, GetRetorno retorno) {
                this.usuario = usuario; //O campo usuário recebe o parâmetro de usuário.
                this.retornoUsuario = retorno;    //O campo retornoUsuario recebe o parâmetro de retorno.
                Log.e("matricula",this.usuario.getMatricula());
                Log.e("senha",this.usuario.getSenha());
            }

            @Override
            protected Usuario doInBackground(Void... params) { //Implementação obrigatória.

                ArrayList<NameValuePair> dados = new ArrayList();
                dados.add(new BasicNameValuePair("matricula", this.usuario.getMatricula()));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.
                dados.add(new BasicNameValuePair("senha", this.usuario.getSenha()));        //Adicionando a senha do usuário a o array dados com a chave 'senha'.

/**
 * HppParams:interface que representa um conjunto de valores imutáveis ​​
 * que define um comportamento de tempo de execução de um componente.
 */

                HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.

                HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
                HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.


                HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
                HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "buscaDadosUsuario.php");    //Fazer uma requisição tipo Post no WebService.
                //Página de registro

                Log.e("aqui 2","ok");

                Usuario usuarioRetornado = null;    //Variável que irá receber os dados do usuário.


                try {

                    Log.e("aqui 3","ok");

                    post.setEntity(new UrlEncodedFormEntity(dados));    //Configurando a entidade na requisição post.
                    HttpResponse httpResposta = cliente.execute(post);    //Executando a requisição post e armazenando na variável.


                    // Recebendo a resposta do servidor após a execução do HTTP POST.
                    HttpEntity entidade = httpResposta.getEntity();
                    String resultado = EntityUtils.toString(entidade);
                    //

                    Log.e("aqui 4","ok");
                    JSONObject jObj = new JSONObject(resultado);    //Recebendo a string da resposta no objeto 'jObj' e os valores dele.

                    Log.e("aqui 5","ok");

                    if (jObj.length() == 0) {    //Se o tamanho de jObj for igual a zero.
                        usuarioRetornado = null;
                        Log.e("obj nulo", " nada ");
                    } else {            //Senão,se o tamanho de jObj for diferente de zero.
                        Log.e("obj ", " passo 1 ok ");
                        String nome = jObj.getString("nome");        // Pegando o nome do usuário.
                        String sobrenome = jObj.getString("sobrenome");
                        String matricula = jObj.getString("matricula");
                        String email = jObj.getString("email");
                        String telefone = jObj.getString("telefone");
                        String sexo = jObj.getString("sexo");
                        Integer cnh = jObj.getInt("cnh");
                        boolean cnh1=false;
                        if(cnh==1){
                            cnh1=true;
                        }
                        Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh1);    //Novo obj de usuário.
                        usuarioRetornado=usuario;
                    }

                } catch (Exception e) {
                    e.getMessage();// se não der certo:mensagem de erro
                }

                return usuarioRetornado;    //Retorno para o método 'onPostExecute'.
            }//Fim método.

            @Override
            protected void onPostExecute(Object usuarioRetornado) {

                progressDialog.dismiss(); //Finalizar
                retornoUsuario.concluido(usuarioRetornado);
                super.onPostExecute(usuarioRetornado);


            }//Fim método.

        }//Fim classe.
    }

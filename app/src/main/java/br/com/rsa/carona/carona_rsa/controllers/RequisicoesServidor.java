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
import java.util.LinkedList;
import java.util.List;

import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class RequisicoesServidor {

    ProgressDialog progressDialog;//componente que mostra circulo de progresso
    public static final int TEMPO_CONEXAO = 1000 * 10; //tempo maximo de conex�o
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
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaDadosUsuarioAsyncTask(usuario,retorno).execute();	//Criando um novo obj de de BDU passando dois objetos como parâmetro.
    }
    public void gravaCarona(Carona carona,Usuario usuario, GetRetorno retorno) {
        progressDialog.show();
        new armazenaCaronaAsyncTask(carona,usuario, retorno).execute();
    }
    public void solicitaCarona(Carona carona,Usuario usuario, GetRetorno retorno) {
        progressDialog.show();
        new solicitaCaronaAsyncTask(carona,usuario, retorno).execute();
    }

    public void exibirSolicitacoesCaronas(Usuario usuario,GetRetorno retorno){
        progressDialog.show();// Mostra a barra de dialogo.
        new exibirUsuariosSolicitantesAsyncTask(usuario,retorno).execute();
    }

    public void aceitarRecusarCaronas(Usuario usuario,String resposta,GetRetorno retorno){
        progressDialog.show();// Mostra a barra de dialogo.
        new aceitaOuRecusaCaronaAsyncTask(usuario,resposta,retorno).execute();
    }

    public void buscaCaronas(Usuario usuario,GetRetorno retorno){	//Método que busca a classe que vai receber os dados do usuario.
        progressDialog.show();// Mostra a barra de dialogo.
        new BuscaCaronasAsyncTask(usuario,retorno).execute();	//Criando um novo obj de de BDU passando dois objetos como parâmetro.
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


    public class aceitaOuRecusaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;
        String resposta;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public aceitaOuRecusaCaronaAsyncTask(Usuario usuario,String resposta ,GetRetorno retorno) {
            this.usuario = usuario;
            this.resposta=  resposta;
            this.retornoUsuario = retorno;
        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            //adicionado dados no arraylist para ser enviado

            dadosParaEnvio.add(new BasicNameValuePair("id", usuario.getId()+""));
            dadosParaEnvio.add(new BasicNameValuePair("resposta", this.resposta));


            //delara��o de variaveis http (params, cliente, post) para enviar dados
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "aceitaRecusaCarona.php");
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


    public class exibirUsuariosSolicitantesAsyncTask extends AsyncTask<Void, Void, List<Usuario>> {
        //Campos da classe.
        Usuario usuario;
        GetRetorno retornoUsuario;

        public exibirUsuariosSolicitantesAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario; //O campo usuário recebe o parâmetro de usuário.
            this.retornoUsuario = retorno;    //O campo retornoUsuario recebe o parâmetro de retorno.
            Log.e(" id: ",this.usuario.getId()+"<-id aqui caralho");

        }

        @Override
        protected List<Usuario> doInBackground(Void... params) { //Implementação obrigatória.

            ArrayList<NameValuePair> dados = new ArrayList();
            dados.add(new BasicNameValuePair("id", this.usuario.getId()+""));    //Adicionando o nome do usuário a o array dados com a chave 'nome'.

/**
 * HppParams:interface que representa um conjunto de valores imutáveis ​​
 * que define um comportamento de tempo de execução de um componente.
 */

            HttpParams httpParametros = new BasicHttpParams();  //Configurar os timeouts de conexão.

            HttpConnectionParams.setConnectionTimeout(httpParametros, TEMPO_CONEXAO); // Configura o timeout da conexão em milisegundos até que a conexão seja estabelecida.
            HttpConnectionParams.setSoTimeout(httpParametros, TEMPO_CONEXAO);  // Configura o timeout do socket em milisegundos do tempo que será utilizado para aguardar os dados.


            HttpClient cliente = new DefaultHttpClient(httpParametros);    //Cria um novo cliente HTTP a partir de parâmetros.
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "buscarSolicitantesCaronas.php");    //Fazer uma requisição tipo Post no WebService.
            //Página de registro

            Log.e("aqui 2","ok");
            List<Usuario> usuarios = new LinkedList<Usuario>();    //Variável que irá receber os dados do usuário.

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
                    usuarios = null;
                    Log.e("obj nulo", " nada ");
                } else {            //Senão,se o tamanho de jObj for diferente de zero.
                    JSONObject jObjeto=jObj;
                        for (int i = 0; i <= jObjeto.getInt("tamanho"); i++) {
                            String telefone = jObjeto.getString("telefone_" + i);
                            String nome = jObjeto.getString("nome_" + i);
                            String sobrenome = jObjeto.getString("sobrenome_" + i);
                            String email = jObjeto.getString("email_" + i);
                            int cnh1 = jObjeto.getInt("cnh_" + i);
                            boolean cnh=true;
                            if(cnh1==0){
                                cnh=false;
                            }
                            Log.e("oioioiokkkk", nome);
                            String matricula = jObjeto.getString("matricula_" + i);
                            String sexo = jObjeto.getString("sexo_" + i);
                            int idUser = Integer.parseInt(jObjeto.getString("id_" + i));
                            Usuario usuario = new Usuario(nome,sobrenome,matricula,email,telefone,sexo,cnh);
                            usuario.setId(idUser);
                            usuarios.add(usuario);
                            Log.e("usuario add","ok");
                        }

                }

            } catch (Exception e) {
                e.getMessage();// se não der certo:mensagem de erro
            }

            return usuarios;    //Retorno para o método 'onPostExecute'.
        }//Fim método.

        @Override
        protected void onPostExecute(List<Usuario> usuarios) {
            progressDialog.dismiss(); //Finalizar
            retornoUsuario.concluido(usuarios);
            super.onPostExecute(usuarios);
        }//Fim método.
    }//Fim classe.

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
                    Integer id = jObj.getInt("id");
                    Integer id_carona=jObj.getInt("id_carona");
                    boolean cnh1=false;
                    if(cnh==1){
                        cnh1=true;
                    }
                    Usuario usuario = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh1);    //Novo obj de usuário.
                    usuario.setSenha(this.usuario.getSenha());
                    usuario.setIdCaronaSolicitada(id_carona);
                    usuario.setId(id);
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


    public class armazenaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Carona carona;
        Usuario usuario;
        GetRetorno retorno;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public armazenaCaronaAsyncTask(Carona carona,Usuario usuario, GetRetorno retorno) {
            this.carona = carona;
            this.retorno = retorno;
            this.usuario = usuario;

        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            //adicionado dados no arraylist para ser enviado

            dadosParaEnvio.add(new BasicNameValuePair("origem", carona.getOrigem()));
            dadosParaEnvio.add(new BasicNameValuePair("destino", carona.getDestino()));
            dadosParaEnvio.add(new BasicNameValuePair("horario", carona.getHorario()));
            dadosParaEnvio.add(new BasicNameValuePair("tipoVeiculo", carona.getTipoVeiculo()));
            dadosParaEnvio.add(new BasicNameValuePair("ponto", carona.getPonto()));
            dadosParaEnvio.add(new BasicNameValuePair("restricao", carona.getRestricao()));
            dadosParaEnvio.add(new BasicNameValuePair("vagas", carona.getVagas() + ""));

            dadosParaEnvio.add(new BasicNameValuePair("id_usuario", usuario.getId() + ""));
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

    public class BuscaCaronasAsyncTask extends AsyncTask<Void, Void, Object> {
        Usuario usuario;
        GetRetorno retornoUsuario;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public BuscaCaronasAsyncTask(Usuario usuario, GetRetorno retorno) {
            this.usuario = usuario;
            this.retornoUsuario = retorno;

        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            //adicionado dados no arraylist para ser enviado
            dadosParaEnvio.add(new BasicNameValuePair("sexoUsuario", "Masculino"));


            //delara��o de variaveis http (params, cliente, post) para enviar dados
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Listas.php");
            String teste = "não";
            JSONObject jObjeto = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse;

                jObjeto = new JSONObject(resultado);


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("erro serv", e + "");
            }

            return jObjeto;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object objeto)  {
            JSONObject jObjeto=(JSONObject)objeto;
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
                    int status = jObjeto.getInt("status_" + i);
                    int ativo = jObjeto.getInt("ativo_" + i);
                    int id = jObjeto.getInt("id_" + i);

                    String dataCriacao = jObjeto.getString("datacriacao_" + i);
                    Carona car = new Carona(origem, destino, horario, tipoVeiculo, restricao, vagas, ponto);
                    car.setId(id);
                    car.setStatus(status);
                    car.setAtivo(ativo);
                    car.setDataCriacao(dataCriacao);
                    caronas.add(car);

                    String telefone = jObjeto.getString("telefone_" + i);
                    String nome = jObjeto.getString("nome_" + i);
                    String sobrenome = jObjeto.getString("sobrenome_" + i);
                    String email = jObjeto.getString("email_" + i);
                    int cnh1 = jObjeto.getInt("cnh_" + i);
                    boolean cnh=true;
                    if(cnh1==0){
                        cnh=false;
                    }
                    Log.e("oioioioioioioio", nome);
                    String matricula = jObjeto.getString("matricula_" + i);
                    String sexo = jObjeto.getString("sexo_" + i);
                    Usuario usuario = new Usuario(nome,sobrenome,matricula,email,telefone,sexo,cnh);
                    usuarios.add(usuario);

                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("erro serv", e + "");
            }
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(caronas,usuarios);
            super.onPostExecute(objeto);
        }
    }

    public class solicitaCaronaAsyncTask extends AsyncTask<Void, Void, Object> {
        Carona carona;
        Usuario usuario;
        GetRetorno retorno;

        //contrutor requer um usuario uma interface com metodo previamente escrito.
        public solicitaCaronaAsyncTask(Carona carona,Usuario usuario, GetRetorno retorno) {
            this.carona = carona;
            this.retorno = retorno;
            this.usuario = usuario;

        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
            //adicionado dados no arraylist para ser enviado

            dadosParaEnvio.add(new BasicNameValuePair("idCaronaSolicita", carona.getId()+""));
            dadosParaEnvio.add(new BasicNameValuePair("idUsuarioSolicita", usuario.getId()+""));
            Log.e("não entedi", carona.getId()+""+usuario.getId());
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
            retorno.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }
    }

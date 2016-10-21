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
    public static final String ENDERECO_SERVIDOR = "http://192.168.0.157/Caronas/";//local onde esta meu projeto php que salva e busca dados no banco
     //contrutor executa o circulo que pede pra aquardar at� que a conex�o seja terminada
    public RequisicoesServidor(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processando...");
        progressDialog.setMessage("Aguarde por favor...");
    }
      //metodo para guardar usuario no banco usando uma classe que economiza recursos do celular
    public void gravaDadosDoUsuario(Usuario usuario, GetRetorno retorno){
        progressDialog.show();
        new armazenaDadosUsuarioAsyncTask(usuario, retorno).execute();
    }
       //classe que armazena dados de usuario no banco de modo  que economiza recursos do celular
    public class armazenaDadosUsuarioAsyncTask extends AsyncTask<Void, Void, Object>{
        Usuario usuario;
        GetRetorno retornoUsuario;
      //contrutor requer um usuario uma interface com metodo previamente escrito.
        public armazenaDadosUsuarioAsyncTask(Usuario usuario, GetRetorno retorno){
            this.usuario = usuario;
            this.retornoUsuario = retorno;

        }

        @Override //metodo que � execudado em segundo plano para economia de recursos
        protected Object doInBackground(Void... params) {
            ArrayList<NameValuePair> dadosParaEnvio = new ArrayList();//list que sera passada para o aquivo php atraves do httpPost
           //adicionado dados no arraylist para ser enviado

            dadosParaEnvio.add(new BasicNameValuePair("nome", usuario.getNome() ));
            dadosParaEnvio.add(new BasicNameValuePair("sobrenome", usuario.getSobrenome()));
            dadosParaEnvio.add(new BasicNameValuePair("matricula", usuario.getMatricula() ));
            dadosParaEnvio.add(new BasicNameValuePair("telefone", usuario.getTelefone()));
            dadosParaEnvio.add(new BasicNameValuePair("email", usuario.getEmail()));
            dadosParaEnvio.add(new BasicNameValuePair("sexo", usuario.getSexo()));
            dadosParaEnvio.add(new BasicNameValuePair("ativo", usuario.getAtivo()+""));
            dadosParaEnvio.add(new BasicNameValuePair("senha", usuario.getSenha()));
            if(usuario.isCnh()) {
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "TRUE"));
            }else{
                dadosParaEnvio.add(new BasicNameValuePair("cnh", "FALSE"));
            }

            Log.e("se vai gravar", usuario.getNome());
             //delara��o de variaveis http (params, cliente, post) para enviar dados 
            HttpParams httpRequestsParametros = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestsParametros, TEMPO_CONEXAO);
            HttpConnectionParams.setSoTimeout(httpRequestsParametros, TEMPO_CONEXAO);

            HttpClient cliente = new DefaultHttpClient(httpRequestsParametros);
            HttpPost post = new HttpPost(ENDERECO_SERVIDOR + "Registros.php");
            String teste="não";
            try {
                post.setEntity(new UrlEncodedFormEntity(dadosParaEnvio));
                HttpResponse httpResposta = cliente.execute(post);//declara httpResponse para pegar dados
                HttpEntity entidade = httpResposta.getEntity();
                String resultado = EntityUtils.toString(entidade);//resultado que veio graças ao httpResponse

                JSONObject jObjeto = new JSONObject(resultado);
                teste = jObjeto.getString("teste");
                Log.e("FOOOIII????",teste);

            }catch(Exception e){
                e.printStackTrace();
                Log.e("erro serv", e+"");
            }

            return teste;
        }

        @Override //metodo que � executado quando o post for exetutado/enviado
        protected void onPostExecute(Object resultado){
            progressDialog.dismiss();//encerra o circulo de progresso
            retornoUsuario.concluido(resultado);
            super.onPostExecute(resultado);
        }
    }
}
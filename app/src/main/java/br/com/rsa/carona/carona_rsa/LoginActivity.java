package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

/**
 * A tela de login que oferece o login via matricula / Senha.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mSenhaView;
    private EditText mMatriculaView;
    ManipulaDados mDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDados = new ManipulaDados(LoginActivity.this);
        new Funcoes().apagarNotificacoes(LoginActivity.this);

        //mDados.limparDados();
        if (mDados.getUsuario() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        mMatriculaView = (EditText) findViewById(R.id.matricula_login);
        mSenhaView = (EditText) findViewById(R.id.senha_login);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //buscar dados para o usuário logar....
    public void logar(View view) {
        if (isOnline()) {
            String usuarioInformado = this.mMatriculaView.getText().toString(); //String recebe o valor do campo de classe "usuario".
            String senhaInformada = this.mSenhaView.getText().toString(); //String recebe o valor do campo de classe "senha".
            Usuario usuarioLogado = new Usuario(usuarioInformado, senhaInformada); //Instanciando um objeto do tipo usuario com o nome e a senha !
            autenticar(usuarioLogado);
        } else {
            semCon();
        }
    }

    public void autenticar(Usuario usuario) {

        RequisicoesServidor rs = new RequisicoesServidor(LoginActivity.this);
        rs.buscaDadosDoUsuario(usuario, new GetRetorno() {

            @Override
            public void concluido(Object object) {
                Usuario user= (Usuario) object;
                if (user == null) {
                    Toast.makeText(LoginActivity.this,R.string.alert_sem_conexao,Toast.LENGTH_LONG).show();
                } else if(user.getId()==0){
                    mostrarMensagemErro();
                }else{
                    logarUsuario(object);
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });

    }

    private void mostrarMensagemErro() {    //Quando não existir nenhum usuário com o nome e senha repassados.
        Toast.makeText(LoginActivity.this, "Usuario não encontrado", Toast.LENGTH_SHORT).show();
    }

    public void recSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Senha");
        final EditText emailAtual = new EditText(this);
        emailAtual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailAtual.setHint("E-Mail");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 10, 10, 10);
        ll.addView(emailAtual);
        builder.setView(ll);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequisicoesServidor rs = new RequisicoesServidor(LoginActivity.this);
                rs.recuperarSenha(emailAtual.getText().toString(), new GetRetorno() {
                    @Override
                    public void concluido(Object object) {
                        if (object.toString().equals("1")) {
                            Toast.makeText(LoginActivity.this, "Uma nova senha foi enviada  para seu E-Mail", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (object.toString().equals("-1")) {
                            Toast.makeText(LoginActivity.this, "E-Mail não encontrado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Tente mais tarde! Erro:" + object.toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void concluido(Object object, Object object2) {

                    }
                });


            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void logarUsuario(Object object) {    //Usuário existente.
        Usuario usuario = (Usuario) object;
        mDados.gravarDados(usuario);
        mDados.setLogado(true);
        Servico.cntVerificaNovasCaronas=false;
        Toast.makeText(LoginActivity.this, "Bem-Vindo", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void salvarDadosExtras(Usuario user) {//não utilizado
        RequisicoesServidor rs = new RequisicoesServidor(LoginActivity.this);
        rs.exibirMinhasSolicitações(0, 6, user, new GetRetorno() {
            @Override
            public void concluido(Object object) {
                final List<Carona> caronas = (List<Carona>) object;
                if (caronas != null) {
                    for (int i = 0; i < caronas.size(); i++) {


                    }
                }
            }

            @Override
            public void concluido(Object object, Object object2) {

            }
        });
    }


    public void selecionarOpcao(View view) {
        switch (view.getId()) {
            case R.id.link_cadastro:
                if (isOnline()) {
                    startActivity(new Intent(this, Registro1Activity.class));
                } else {
                    semCon();
                }
                break;
        }
    }

    private void semCon() {
        Toast.makeText(LoginActivity.this, R.string.alert_sem_conexao, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cadastre_se) {
            if (isOnline()) {
                startActivity(new Intent(LoginActivity.this, Registro1Activity.class));
            } else {
                semCon();
            }
            return true;
        } else if (id == R.id.action_termos) {
            Toast.makeText(LoginActivity.this, R.string.alert_versao, Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_esqueceu) {
            if (isOnline()) {
                recSenha();
            } else {
                semCon();
            }
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // A activity não está mais visível mas permanece em memória
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // A activity está prestes a ser destruída (removida da memória)
    }

}



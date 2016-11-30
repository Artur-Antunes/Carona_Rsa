package br.com.rsa.carona.carona_rsa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;


import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.inputmethod.EditorInfo;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

/**
 * A tela de login que oferece o login via matricula / Senha.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mSenhaView;
    private EditText mMatriculaView;
    private Button BtnLogar;
    ManipulaDados mDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDados = new ManipulaDados(LoginActivity.this);
        //mDados.limparDados();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mDados.getUsuario() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        mMatriculaView = (EditText) findViewById(R.id.matricula_login);//matrucula usuario
        mSenhaView = (EditText) findViewById(R.id.senha_login);//senha usuario
    }


    /**
     * As tentativas de login ou registre a conta especificada pelo formulário de login.
     * Se houver erros de formulário (e-mail inválido, campos em falta, etc.), os
     * erros são apresentados e nenhuma tentativa de login real é feita
     */


    //buscar dados para o usuário logar....
    public void logar(View view) {
        String usuarioInformado = this.mMatriculaView.getText().toString(); //String recebe o valor do campo de classe "usuario".
        String senhaInformada = this.mSenhaView.getText().toString(); //String recebe o valor do campo de classe "senha".
        Usuario usuarioLogado = new Usuario(usuarioInformado, senhaInformada); //Instanciando um objeto do tipo usuario com o nome e a senha !
        autenticar(usuarioLogado);
    }

    public void autenticar(Usuario usuario) {

        RequisicoesServidor rs = new RequisicoesServidor(LoginActivity.this);
        rs.buscaDadosDoUsuario(usuario, new GetRetorno() {

            @Override
            public void concluido(Object object) {
                if (object == null) {    //Se não existir o usuário informado.
                    mostrarMensagemErro();
                } else {
                    logarUsuario(object);    //Realizar o login.
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

    private void logarUsuario(Object object) {    //Usuário existente.
        Usuario usuario = (Usuario) object;

        mDados.gravarDados(usuario);    //Guardando os dados do usuário logado.
        mDados.setLogado(true);

        Toast.makeText(LoginActivity.this, "Bem-Vindo", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void selecionarOpcao(View view) {
        switch (view.getId()) {
            case R.id.link_cadastro:
                startActivity(new Intent(this, registroActivity.class));
                break;
            case R.id.link_senha_esquecida:
                startActivity(new Intent(this, recuperarSenhaActivity.class));
                break;

            case R.id.link_termos_uso:
                //criar activity para mostrar os termos de uso.
                //startActivity(new Intent(this, recuperarSenhaActivity.class));
        }
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



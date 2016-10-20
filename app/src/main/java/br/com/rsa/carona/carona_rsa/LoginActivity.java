package br.com.rsa.carona.carona_rsa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A tela de login que oferece o login via matricula / Senha.
 */
public class LoginActivity extends AppCompatActivity{

    private EditText mSenhaView;
    private EditText mMatriculaView;
    private Button BtnLogar;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set login form.

        mMatriculaView = (EditText) findViewById(R.id.matricula_login);//matrucula usuario
        mSenhaView = (EditText) findViewById(R.id.senha_login);//senha usuario
        mSenhaView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }




    /**
     * As tentativas de login ou registre a conta especificada pelo formulário de login.
     * Se houver erros de formulário (e-mail inválido, campos em falta, etc.), os
     * erros são apresentados e nenhuma tentativa de login real é feita
     */

    private void attemptLogin() {
        // Repor erros.
        mMatriculaView.setError(null);
        mSenhaView.setError(null);

        //armazenar valores no momento da tentativa de login.
        String password = mSenhaView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verifique se há uma senha válida, se o usuário entrou um.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mSenhaView.setError(getString(R.string.error_invalid_password));
            focusView = mSenhaView;
            cancel = true;
        }

        if (cancel) {
            // Havia um erro; não tente login e focar o primeiro
            // campo de formulário com um erro.
            focusView.requestFocus();
        }
    }

    //buscar dados para o usuário logar....
    public void logar(View view) {


    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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


    /**
     * Mostra a UI progresso e esconde o formulário de login.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // Em Honeycomb MR2 temos as APIs ViewPropertyAnimator, que permitem
        // para animações muito fácil. Se estiver disponível, usar esses aplicativos para fade-in
        // o spinner progresso.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            //mostra tão simples  e ocultar os componentes de interface do usuário relevantes.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}



package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Mask;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Registro2Activity extends AppCompatActivity {

    private Spinner sexoRegistro;
    private Switch cnhRegistro;
    private EditText emailRegistro;
    private EditText matriculaRegistro;
    private EditText senhaRegistro;
    private EditText senha2Registro;
    public static Usuario usuario = null;
    private EditText telefoneRegistro;
    private Button btnCadastrar;
    private ImageView fotoRg;
    private TextView nomeSobrenome;
    private boolean verificaCamposInvalidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        verificaCamposInvalidos=true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario, android.R.layout.simple_spinner_dropdown_item);
        sexoRegistro = (Spinner) findViewById(R.id.sexo_registro);
        sexoRegistro.setAdapter(adapter);
        matriculaRegistro = (EditText) findViewById(R.id.matricula_registro);
        nomeSobrenome = (TextView) findViewById(R.id.nome_sobrenome_rg);
        nomeSobrenome.setText(usuario.getNome().toString() + " " + usuario.getSobrenome().toString());
        fotoRg = (ImageView) findViewById(R.id.foto_rg);
        fotoRg.setImageDrawable(new Funcoes().imagemArredondada(usuario.getFoto(), getResources()));
        matriculaRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (matriculaRegistro.length() <= 7) {
                        matriculaRegistro.setError(" Digite todos os números !");
                        verificaCamposInvalidos=false;
                    }else{
                        verificaCamposInvalidos=true;
                    }
                }
            }
        });
        telefoneRegistro = (EditText) findViewById(R.id.telefone_registro);
        telefoneRegistro.addTextChangedListener(Mask.insert("(##)#####-####", telefoneRegistro));
        telefoneRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (telefoneRegistro.length() < 13) {
                        telefoneRegistro.setError(" Digite todos os números !");
                        verificaCamposInvalidos=false;
                    }else{
                        verificaCamposInvalidos=true;
                    }
                }
            }
        });
        emailRegistro = (EditText) findViewById(R.id.email_registro);
        emailRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!new Funcoes().isEmailValid(emailRegistro.getText().toString())) {
                        emailRegistro.setError(" Formato inválido !");
                        verificaCamposInvalidos=false;
                    }else{
                        verificaCamposInvalidos=true;
                    }
                }
            }
        });
        senhaRegistro = (EditText) findViewById(R.id.senha_registro);
        senha2Registro = (EditText) findViewById(R.id.senha2_registro);
        btnCadastrar = (Button) findViewById(R.id.b_cadastrar);
        cnhRegistro = (Switch) findViewById(R.id.cnh_registro);
        cnhRegistro.setChecked(true);
        //PEGAR VALORES APÓS CLICAR NO BOTÃO CADASTRAR E SALVAR NO BANCO.
        btnCadastrar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (!matriculaRegistro.getText().toString().trim().equals("") &&
                        !telefoneRegistro.getText().toString().trim().equals("") &&
                        !emailRegistro.getText().toString().trim().equals("") &&
                        !senhaRegistro.getText().toString().trim().equals("") &&
                        !senha2Registro.getText().toString().trim().equals("") && verificaCamposInvalidos
                        ) {
                    if (senha2Registro.getText().toString().trim().equals(senhaRegistro.getText().toString().trim())) {
                        String matricula = matriculaRegistro.getText().toString();
                        String telefone = telefoneRegistro.getText().toString();
                        String email = emailRegistro.getText().toString();
                        String senha = senhaRegistro.getText().toString();
                        boolean cnh = cnhRegistro.isChecked();
                        String sexo = sexoRegistro.getSelectedItem().toString();
                        final Usuario usuario1 = new Usuario(usuario.getNome(), usuario.getSobrenome(), matricula, email, telefone, sexo, cnh);
                        usuario1.setSenha(senha);
                        usuario1.setFoto(usuario.getFoto());
                        usuario1.setExtFoto(usuario.getExtFoto());
                        usuario1.setAtivo(1);

                        RequisicoesServidor rs = new RequisicoesServidor(Registro2Activity.this);
                        rs.gravaDadosDoUsuario(usuario1, new GetRetorno() {
                            @Override
                            public void concluido(Object object) {
                                if (object.toString().equals("1")) {
                                    Toast.makeText(Registro2Activity.this, "Dados Salvos com Sucesso", Toast.LENGTH_SHORT).show();
                                    RequisicoesServidor rs = new RequisicoesServidor(Registro2Activity.this);
                                    rs.buscaDadosDoUsuario(usuario1, new GetRetorno() {
                                        @Override
                                        public void concluido(Object object) {
                                            logarUsuario(object);
                                        }

                                        @Override
                                        public void concluido(Object object, Object object2) {

                                        }
                                    });
                                } else if (object.toString().equals("2")) {
                                    Toast.makeText(Registro2Activity.this, "Email já existe", Toast.LENGTH_SHORT).show();
                                } else if (object.toString().equals("0")) {
                                    Toast.makeText(Registro2Activity.this, "Matricula já existe", Toast.LENGTH_SHORT).show();
                                } else if (object.toString().equals("-1")) {
                                    Toast.makeText(Registro2Activity.this, "Erro de comunicação", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void concluido(Object object, Object object2) {

                            }
                        });
                    } else {
                        Toast.makeText(Registro2Activity.this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Registro2Activity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logarUsuario(Object object) {    //Usuário existente.
        ManipulaDados mDados;
        mDados = new ManipulaDados(Registro2Activity.this);
        Usuario usuario = (Usuario) object;
        mDados.gravarDados(usuario);    //Guardando os dados do usuário logado.
        mDados.setCaronaSolicitada(-1);
        mDados.setLogado(true);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void voltar(View view) {
        startActivity(new Intent(Registro2Activity.this,LoginActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

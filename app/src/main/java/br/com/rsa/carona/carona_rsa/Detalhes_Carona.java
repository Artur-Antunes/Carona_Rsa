package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Detalhes_Carona extends AppCompatActivity {
    private TextView tv_origem, tv_destino, tv_vagas, tv_horario, tv_nome, tv_ponto, tv_tipoVeiculo, tv_status, tv_matricula, tv_telefone, tv_cnh, tv_email,tv_link_mais;
    private Button b_salvar;
    private LinearLayout ll;
    private RelativeLayout rl;
    AlertDialog.Builder dialog;
    public static Carona carona = null;
    public static Usuario usuario = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ManipulaDados md = new ManipulaDados(Detalhes_Carona.this);
        setContentView(R.layout.activity_detalhes__carona);
        ll = (LinearLayout) findViewById(R.id.caixa_participantes);
        rl = (RelativeLayout) findViewById(R.id.titulo_geral);
        tv_origem = (TextView) findViewById(R.id.tv_origem2);
        tv_destino = (TextView) findViewById(R.id.tv_destino2);
        tv_vagas = (TextView) findViewById(R.id.tv_vagas2);
        tv_link_mais = (TextView) findViewById(R.id.link_mais);
        tv_horario = (TextView) findViewById(R.id.tv_horario2);
        tv_ponto = (TextView) findViewById(R.id.tv_ponto);
        tv_tipoVeiculo = (TextView) findViewById(R.id.tv_tipoVeiculo);
        tv_nome = (TextView) findViewById(R.id.tv_nome);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_matricula = (TextView) findViewById(R.id.tv_matricula);
        tv_telefone = (TextView) findViewById(R.id.tv_telefone);
        tv_cnh = (TextView) findViewById(R.id.tv_cnh);
        tv_nome = (TextView) findViewById(R.id.tv_nome);
        tv_email = (TextView) findViewById(R.id.tv_email);
        b_salvar = (Button) findViewById(R.id.b_solicitar);
        dialog = new AlertDialog.Builder(Detalhes_Carona.this);
        if ((md.getCaronaSolicitada() == carona.getId()) || (md.getUsuario().getId() == usuario.getId())) {
            b_salvar.setText("CANCELAR");
            b_salvar.setTextColor(Color.parseColor("#936c66"));
        } else {
            b_salvar.setBackgroundResource(R.drawable.cor_botao2);
            b_salvar.setTextColor(Color.WHITE);
        }

        tv_origem.setText(carona.getOrigem());
        tv_destino.setText(carona.getDestino());
        tv_vagas.setText(carona.getVagas() + "");
        tv_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
        tv_ponto.setText(carona.getPonto());
        tv_tipoVeiculo.setText(carona.getTipoVeiculo());
        if (carona.getStatus() == 1) {
            tv_status.setText("Disponível");
        } else {
            tv_status.setText("Indisponível");
        }

        tv_matricula.setText(usuario.getMatricula());
        tv_telefone.setText(usuario.getTelefone());
        tv_nome.setText(usuario.getNome());
        tv_email.setText(usuario.getEmail());
        if (usuario.isCnh()) {
            tv_cnh.setText("CNH:Sim");
        } else {
            tv_cnh.setText("CNH:Não");
        }
        ll.removeAllViews();
        Log.e("tamanho paticipantes", carona.getParticipantes().size() + "");
        for (int i = 0; i < carona.getParticipantes().size(); i++) {
            final RelativeLayout modelo = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.modelo_participantes, null);
            TextView nome = (TextView) modelo.findViewById(R.id.tv_nome);
            TextView status = (TextView) modelo.findViewById(R.id.tv_status);
            nome.setText(carona.getParticipantes().get(i).getNome());
            status.setText(carona.getParticipantesStatus().get(i).toString());
            if (carona.getParticipantesStatus().get(i).toString().equals("Aceito")) {
                status.setTextColor(getResources().getColor(R.color.verde));
            } else {
                status.setTextColor(getResources().getColor(R.color.color1));
            }
            ll.addView(modelo, 0);
        }

        b_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (b_salvar.getText().toString().equals("CANCELAR")) {
                    if (md.getUsuario().getId() == usuario.getId()) {//O dono vai cancelar sua carona!
                        Log.e("usuario:", "dono_cancela");
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Detalhes_Carona.this);
                        dialog.setTitle(R.string.title_confirmacao)
                                .setMessage(R.string.alert_cancelar_carona)
                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        // startActivity(new Intent(Detalhes_Carona.this, ExibirDadosUsuarioActivity.class));
                                    }
                                })
                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        RequisicoesServidor rs = new RequisicoesServidor(Detalhes_Carona.this);
                                        rs.alteraStatusCarona(carona.getId(), 0, new GetRetorno() {
                                            @Override
                                            public void concluido(Object object) {
                                                Toast.makeText(Detalhes_Carona.this, (String) object, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Detalhes_Carona.this, MainActivity.class));
                                            }

                                            @Override
                                            public void concluido(Object object, Object object2) {

                                            }
                                        });
                                    }
                                }).show();
                    } else if (md.getCaronaSolicitada() == carona.getId()) {//O usuário vai cancelar sua solicitação!
                        Log.e("usuario:", "caroneiro_cancelando!");
                        RequisicoesServidor rserv = new RequisicoesServidor(Detalhes_Carona.this);
                        Usuario userLocal = new Usuario((md.getUsuario().getId()));
                        Log.e("id-carona", carona.getId() + "");
                        Log.e("id-usuario", userLocal.getId() + "");
                        rserv.desistirCarona(userLocal, carona, new GetRetorno() {
                            @Override
                            public void concluido(Object object) {
                                Toast.makeText(Detalhes_Carona.this, object.toString(), Toast.LENGTH_LONG).show();
                                md.setCaronaSolicitada(-1);
                                startActivity(new Intent(Detalhes_Carona.this, MainActivity.class));
                            }

                            @Override
                            public void concluido(Object object, Object object2) {

                            }
                        });

                    }
                } else if (b_salvar.getText().toString().equals(("Me leva !"))) {
                    if (md.getCaronaSolicitada() == -1) {
                        Log.e("usuario:", "caroneiro_pedindo!");
                        dialog.setTitle(R.string.title_confirmacao)
                                .setMessage(R.string.alert_solicitar_carona)
                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {

                                    }
                                })
                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        Usuario eu = md.getUsuario();
                                        RequisicoesServidor rs = new RequisicoesServidor(Detalhes_Carona.this);
                                        rs.solicitaCarona(carona, eu, new GetRetorno() {
                                            @Override
                                            public void concluido(Object object) {
                                                if (object.toString().trim().equals("1")) {
                                                    Toast.makeText(Detalhes_Carona.this, "CARONA SOLICITADA!", Toast.LENGTH_SHORT).show();
                                                    md.setCaronaSolicitada(carona.getId());
                                                    startActivity(new Intent(Detalhes_Carona.this, MainActivity.class));
                                                }
                                            }


                                            @Override
                                            public void concluido(Object object, Object object2) {

                                            }
                                        });

                                    }
                                }).show();

                    } else {
                        Toast.makeText(Detalhes_Carona.this, " Você já tem uma carona solicitada ! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void exibirParticipantes(View view) {
        Log.e("123", "okkk");
        rl.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);
        //tv_link_mais.;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

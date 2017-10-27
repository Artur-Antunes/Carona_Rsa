package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class DetalhesCarona extends AppCompatActivity {
    private TextView tv_origem;
    private TextView tv_destino;
    private TextView tv_vagas;
    private TextView tv_horario;
    private TextView tv_nome;
    private TextView tv_ponto;
    private TextView tv_tipoVeiculo;
    private TextView tv_status;
    private TextView tv_matricula;
    private TextView tv_telefone;
    private TextView tv_cnh;
    private TextView tv_email;
    private Button b_salvar;
    private RelativeLayout rl;
    private LinearLayout ll;
    private AlertDialog.Builder dialog;
    public static Carona carona = null;
    public static Usuario usuario = null;
    private String intencao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_carona);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ManipulaDados md = new ManipulaDados(DetalhesCarona.this);
        dialog = new AlertDialog.Builder(DetalhesCarona.this);
        //solCar2 = (FloatingActionButton) findViewById(R.id.b_solicita2);
        //ll = (LinearLayout) findViewById(R.id.caixa_participantes);
        //rl = (RelativeLayout) findViewById(R.id.titulo);
        tv_origem = (TextView) findViewById(R.id.tv_origem2);
        tv_destino = (TextView) findViewById(R.id.tv_destino2);
        tv_vagas = (TextView) findViewById(R.id.tv_vagas2);
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

        if ((md.getCaronaSolicitada() != null && md.getCaronaSolicitada().getId() == carona.getId()) || (md.getUsuario().getId() == usuario.getId())) {
            intencao = "CANCELAR";
            b_salvar.setText("CANCELAR");
        } else {
            intencao = "Me Leva!";
            //b_salvar.setBackgroundResource(R.drawable.cor_botao2);
        }

        tv_origem.setText(carona.getOrigem());
        tv_destino.setText(carona.getDestino());

        Bundle b = getIntent().getExtras();
        tv_vagas.setText(b.get("vagas") + "");
        if (carona.getHorario().length() == 4 || carona.getHorario().length() == 5) {
            tv_horario.setText(carona.getHorario());
        } else {
            tv_horario.setText(new Funcoes().horaSimples(carona.getHorario()));
        }
        tv_ponto.setText(carona.getPonto());
        tv_tipoVeiculo.setText(carona.getTipoVeiculo());
        if (carona.getStatus() == 1) {
            tv_status.setText("Ativa");
        } else {
            tv_status.setText("Inativa");
        }
        tv_matricula.setText(usuario.getMatricula());
        tv_telefone.setText(usuario.getTelefone());
        tv_nome.setText(usuario.getNome());
        tv_email.setText(usuario.getEmail());
        if (usuario.isCnh()) {
            tv_cnh.setText("Possui CNH");
        } else {
            tv_cnh.setText("Não Possui CNH");
        }
        //ll.removeAllViews();
        /**
         for (int i = 0; i < carona.getParticipantes().size(); i++) {
         final RelativeLayout modelo = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.modelo_participantes, null);
         TextView nome = (TextView) modelo.findViewById(R.id.tv_nome);
         TextView status = (TextView) modelo.findViewById(R.id.tv_status);
         nome.setText(carona.getParticipantes().get(i).getNome());
         status.setText(carona.getParticipantesStatus().get(i).toString());
         if (carona.getParticipantesStatus().get(i).toString().equals("ACEITO")) {
         status.setTextColor(getResources().getColor(R.color.verde));
         } else {
         status.setTextColor(getResources().getColor(R.color.color1));
         }
         ll.addView(modelo, 0);
         }

         **/
        b_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intencao.equals("CANCELAR")) {
                    if (md.getUsuario().getId() == usuario.getId()) {//O dono vai cancelar sua carona!
                        Log.e("usuario:", "dono_cancela");
                        dialog.setTitle(R.string.title_conf)
                                .setMessage(R.string.alert_cnl_car)
                                .setNegativeButton(R.string.n, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        // startActivity(new Intent(DetalhesCarona.this, UsuarioDetalhesActivity.class));
                                    }
                                })
                                .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        RequisicoesServidor rs = new RequisicoesServidor(DetalhesCarona.this);
                                        rs.alteraStatusCarona(carona.getId(), 0, new GetRetorno() {
                                            @Override
                                            public void concluido(Object object) {
                                                md.clearAtualCarOf();
                                                Toast.makeText(DetalhesCarona.this, (String) object, Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(DetalhesCarona.this, MainActivity.class));
                                            }

                                            @Override
                                            public void concluido(Object object, Object object2) {

                                            }
                                        });
                                    }
                                }).show();
                    } else if (md.getCaronaSolicitada().getId() == carona.getId()) {//O usuário vai cancelar sua solicitação!
                        Log.e("usuario:", "caroneiro_cancelando!");
                        RequisicoesServidor rserv = new RequisicoesServidor(DetalhesCarona.this);
                        Usuario userLocal = new Usuario((md.getUsuario().getId()));
                        rserv.desistirCarona(userLocal.getId(), carona.getId(), new GetRetorno() {
                            @Override
                            public void concluido(Object object) {
                                Toast.makeText(DetalhesCarona.this, object.toString(), Toast.LENGTH_LONG).show();
                                md.setCaronaSolicitada(new Carona(-1));
                                startActivity(new Intent(DetalhesCarona.this, MainActivity.class));
                            }

                            @Override
                            public void concluido(Object object, Object object2) {

                            }
                        });

                    }
                } else if (intencao.equals(("Me Leva!"))) {
                    if (md.getCaronaSolicitada().getId() == -1) {
                        dialog.setTitle(R.string.title_conf)
                                .setMessage(R.string.alert_slt_car)
                                .setNegativeButton(R.string.n, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {

                                    }
                                })
                                .setPositiveButton(R.string.s, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        Usuario eu = md.getUsuario();
                                        RequisicoesServidor rs = new RequisicoesServidor(DetalhesCarona.this);
                                        rs.solicitaCarona(carona, eu, new GetRetorno() {
                                            @Override
                                            public void concluido(Object object) {
                                                if (object.toString().trim().equals("1")) {
                                                    Toast.makeText(DetalhesCarona.this,R.string.alert_car_slt, Toast.LENGTH_SHORT).show();
                                                    md.setCaronaSolicitada(carona);
                                                    startActivity(new Intent(DetalhesCarona.this, MainActivity.class));
                                                }
                                            }


                                            @Override
                                            public void concluido(Object object, Object object2) {

                                            }
                                        });

                                    }
                                }).show();

                    } else {
                        Toast.makeText(DetalhesCarona.this,R.string.alert_car_slt, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void show(View v) {
        String usuarios = "";
        if ((carona.getParticipantes() != null) && (carona.getParticipantes().size() > 0)) {
            for (int i = 0; i < carona.getParticipantes().size(); i++) {
                String nome = carona.getParticipantes().get(i).getNome();
                String sobrenome = carona.getParticipantes().get(i).getSobrenome();
                usuarios += nome + " " + sobrenome + "\n";
            }
            getParticipantes(usuarios);
        } else {
            Toast.makeText(DetalhesCarona.this, "Nenhum participante", Toast.LENGTH_LONG).show();
        }
    }


    private void getParticipantes(String usuarios) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(DetalhesCarona.this);
        dialog.setCancelable(false);
        dialog.setTitle("PARTICIPANTES");
        dialog.setMessage(usuarios);
        dialog.setPositiveButton(R.string.k, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void exibir() {
        rl.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

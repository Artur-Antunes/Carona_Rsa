package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Detalhes_Carona extends AppCompatActivity {
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
    private LinearLayout ll;
    public static Carona carona = null;
    public static Usuario usuario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detalhes__carona);
        ll = (LinearLayout) findViewById(R.id.caixa_participantes);
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
        b_salvar = (Button) findViewById(R.id.b_salvar);

        tv_origem.setText(carona.getOrigem());
        tv_destino.setText(carona.getDestino());
        tv_vagas.setText(carona.getVagas() + "");
        tv_horario.setText(carona.getHorario());
        tv_ponto.setText(carona.getPonto());
        tv_tipoVeiculo.setText(carona.getTipoVeiculo());
        if (carona.getStatus() == 1) {
            tv_status.setText("DISPONÍVEL");
        } else {
            tv_status.setText("INDISPONÍVEL");
        }
        Log.e("testteddddd", usuario.getNome());
        tv_matricula.setText(usuario.getMatricula());
        tv_telefone.setText(usuario.getTelefone());
        tv_nome.setText(usuario.getNome());
        tv_email.setText(usuario.getEmail());
        if (usuario.isCnh()) {
            tv_cnh.setText("POSSUI CNH");
        } else {
            tv_cnh.setText("NÃO POSSUI CNH");
        }
        ll.removeAllViews();
        Log.e("tamanho paticipantes", carona.getParticipantes().size() + "");
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ExibirDadosUsuarioActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

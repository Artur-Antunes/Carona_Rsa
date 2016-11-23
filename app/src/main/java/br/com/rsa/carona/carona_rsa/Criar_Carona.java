package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.InputFilterMinMax;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;

public class Criar_Carona extends AppCompatActivity {
    private Spinner origem;
    private Spinner destino;
    private Spinner tipoVeiculo;
    private Button salvar;
    private ImageButton bMais;
    private ImageButton bMenos;
    private EditText ponto;
    private EditText vagas;
    private RadioGroup restricoes;
    private TimePicker horario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar__carona);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locais,android.R.layout.simple_spinner_dropdown_item);
        origem = (Spinner) findViewById(R.id.sp_origem);
        destino = (Spinner) findViewById(R.id.sp_destino);
        horario = (TimePicker) findViewById(R.id.tp_horario);
        horario.setIs24HourView(true);
        tipoVeiculo = (Spinner) findViewById(R.id.sp_tipos_veiculo);
        tipoVeiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(tipoVeiculo.getSelectedItem().equals("MOTOCICLETA")){
                    vagas.setText("1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        salvar = (Button) findViewById(R.id.b_salvar);
        bMais = (ImageButton) findViewById(R.id.b_mais);
        bMenos = (ImageButton) findViewById(R.id.b_menos);
        ponto = (EditText) findViewById(R.id.c_ponto);
        vagas = (EditText) findViewById(R.id.tv_vagas2);

        vagas.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});
        vagas.setText("1");
        restricoes = (RadioGroup) findViewById(R.id.rd_restricoes);
        origem.setAdapter(adapter);
        destino.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.tipo_veiculos,android.R.layout.simple_spinner_dropdown_item);
        tipoVeiculo.setAdapter(adapter);
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vagas.getText().toString().equals("")) {
                    if (!ponto.getText().toString().equals("")) {
                        if (!origem.getSelectedItem().toString().equals(destino.getSelectedItem().toString())) {
                            if(!(tipoVeiculo.getSelectedItem().equals("MOTOCICLETA") && !vagas.getText().toString().equals("1"))) {
                                String origemValor = origem.getSelectedItem().toString();
                                String destinoValor = destino.getSelectedItem().toString();
                                String tipoVeiculoValor = tipoVeiculo.getSelectedItem().toString();
                                int vagasValor = Integer.parseInt(vagas.getText().toString());
                                String pontoValor = ponto.getText().toString();
                                RadioButton radioButton = (RadioButton) findViewById(restricoes.getCheckedRadioButtonId());
                                String resticaoValor = radioButton.getText().toString();
                                String horarioValor = horario.getCurrentHour() + ":" + horario.getCurrentMinute();
                                Carona carona = new Carona(origemValor, destinoValor, horarioValor, tipoVeiculoValor, resticaoValor, vagasValor, pontoValor);
                                RequisicoesServidor rs = new RequisicoesServidor(Criar_Carona.this);
                                ManipulaDados md = new ManipulaDados(Criar_Carona.this);
                                rs.gravaCarona(carona, md.getUsuario(), new GetRetorno() {
                                    @Override
                                    public void concluido(Object object) {
                                        Toast.makeText(Criar_Carona.this, object.toString(), Toast.LENGTH_SHORT).show();
                                        Intent it = new Intent(Criar_Carona.this, MainActivity.class);
                                        startActivity(it);
                                    }

                                    @Override
                                    public void concluido(Object object, Object object2) {

                                    }
                                });
                            }else{
                                Toast.makeText(Criar_Carona.this, " MOTOCICLETA SÓ ACEITA 1 VAGA! ", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(Criar_Carona.this, " ORIGEM E DESTINO PRECISAM SER DISTINTOS ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Criar_Carona.this, " PREENCHA O PONTE DE ENCONTRO ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Criar_Carona.this, " PREENCHA A QUANTIDADE DE VAGAS ", Toast.LENGTH_SHORT).show();
                }
            }

        });

       bMenos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (!vagas.getText().toString().equals("")) {
                   int valor = Integer.parseInt(vagas.getText().toString());
                   vagas.setText((valor - 1) + "");
               }else{
                   vagas.setText(1+"");
               }
           }
       });
        bMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vagas.getText().toString().equals("")) {
                    int valor = Integer.parseInt(vagas.getText().toString());
                    vagas.setText((valor + 1) + "");
                }else{
                    vagas.setText(1+"");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_criar__carona, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }
}

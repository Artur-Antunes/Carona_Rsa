package br.com.rsa.carona.carona_rsa;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;

public class Criar_Carona extends AppCompatActivity implements NumberPicker.OnValueChangeListener{
    private Spinner origem;
    private Spinner destino;
    private Spinner tipoVeiculo;
    private Button salvar;
    //private ImageButton bMais;
    //private ImageButton bMenos;
    private EditText ponto;
    private Spinner vagas;
    private RadioGroup restricoes;
    private Button horario;
    AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_criar__carona);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locais, android.R.layout.simple_spinner_dropdown_item);
        restricoes = (RadioGroup) findViewById(R.id.rd_restricoes);
        horario = (Button) findViewById(R.id.tp_horario);
        origem = (Spinner) findViewById(R.id.sp_origem);
        destino = (Spinner) findViewById(R.id.sp_destino);
        origem.setAdapter(adapter);
        destino.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(this, R.array.tipo_veiculos, android.R.layout.simple_spinner_dropdown_item);
        tipoVeiculo = (Spinner) findViewById(R.id.sp_tipos_veiculo);
        tipoVeiculo.setAdapter(adapter);
        dialog = new AlertDialog.Builder(Criar_Carona.this);
        salvar = (Button) findViewById(R.id.b_salvar);
        ponto = (EditText) findViewById(R.id.c_ponto);
        ponto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (ponto.getText().toString().equals("")) {
                        ponto.setError(" Campo obrigatório !");
                    }
                }
            }
        });

        vagas = (Spinner) findViewById(R.id.tv_vagas2);
        adapter = ArrayAdapter.createFromResource(this, R.array.quant_vagas, android.R.layout.simple_spinner_dropdown_item);
        vagas.setAdapter(adapter);
        vagas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tipoVeiculo.getSelectedItem().equals("MOTO")) {
                    vagas.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Funcoes().validaHora(horario.getText().toString())) {
                    if (!vagas.getSelectedItem().toString().equals("")) {
                        if (!origem.getSelectedItem().toString().equals(destino.getSelectedItem().toString())) {
                            if (!(tipoVeiculo.getSelectedItem().equals("MOTOCICLETA") && !vagas.getSelectedItem().toString().equals("1"))) {
                                dialog.setTitle(R.string.title_confirmacao)
                                        .setMessage(R.string.alert_criar_carona)
                                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {


                                            }
                                        })
                                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                String origemValor = origem.getSelectedItem().toString();
                                                String destinoValor = destino.getSelectedItem().toString();
                                                String tipoVeiculoValor = tipoVeiculo.getSelectedItem().toString();
                                                int vagasValor = Integer.parseInt(vagas.getSelectedItem().toString());
                                                String pontoValor = ponto.getText().toString();
                                                RadioButton radioButton = (RadioButton) findViewById(restricoes.getCheckedRadioButtonId());
                                                String resticaoValor = radioButton.getText().toString();
                                                Carona carona = new Carona(origemValor, destinoValor, horario.getText().toString(), tipoVeiculoValor, resticaoValor, vagasValor, pontoValor);
                                                RequisicoesServidor rs = new RequisicoesServidor(Criar_Carona.this);
                                                ManipulaDados md = new ManipulaDados(Criar_Carona.this);
                                                // Log.e("hora?",carona.get);
                                                rs.gravaCarona(carona, md.getUsuario(), new GetRetorno() {
                                                    @Override
                                                    public void concluido(Object object) {
                                                        Toast.makeText(Criar_Carona.this, object.toString(), Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        criaBroadcastHome("nova(s)Carona(s)");
                                                        criaBroadcast(0,"myCarona");
                                                    }
                                                    @Override
                                                    public void concluido(Object object, Object object2) {

                                                    }
                                                });

                                            }
                                        }).show();

                            } else {
                                Toast.makeText(Criar_Carona.this, " MOTOCICLETA SÓ ACEITA 1 VAGA! ", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(Criar_Carona.this, " ORIGEM E DESTINO PRECISAM SER DISTINTOS ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Criar_Carona.this, " PREENCHA A QUANTIDADE DE VAGAS ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Criar_Carona.this, " O HORÁRIO DE SAÍDA É INVÁLIDO ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         bMenos.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if (!vagas.getText().toString().equals("")) {
        int valor = Integer.parseInt(vagas.getText().toString());
        vagas.setText((valor - 1) + "");
        } else {
        vagas.setText(1 + "");
        }
        }
        });
         bMais.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if (!vagas.getText().toString().equals("")) {
        int valor = Integer.parseInt(vagas.getText().toString());
        vagas.setText((valor + 1) + "");
        } else {
        vagas.setText(1 + "");
        }
        }
        });**/
    }

    /**
     public void show(View v) {
     final Dialog d = new Dialog(Criar_Carona.this);
     d.setTitle("Nº Vagas");
     d.setContentView(R.layout.testador);
     Button b1 = (Button) d.findViewById(R.id.button1);
     final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
     np.setMaxValue(5); // max value 100
     np.setMinValue(1);   // min value 0
     np.setWrapSelectorWheel(false);
     np.setOnValueChangedListener(this);
     b1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    Log.e("valor:",np.getValue()+"");
    vagas.setText(np.getValue() + ""); //set the value to textview
    d.dismiss();
    }
    });
     d.show();
     }

     **/
    public void showTimePickerDialog(View v) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(Criar_Carona.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timePicker.is24HourView();
                String selectMinuteFinal;
                if(selectedMinute<10){
                    selectMinuteFinal="0"+selectedMinute;
                }else{
                    selectMinuteFinal=selectedMinute+"";
                }
                horario.setText(selectedHour + ":" + selectMinuteFinal);
            }
        }, hour, minute, true);
        mTimePicker.setTitle("HORÁRIO SAÍDA");
        mTimePicker.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_criar__carona, menu);
        return true;
    }

    public void criaBroadcastHome(String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abcHome");
        dialogIntent.putExtra("mensagem", tipo);
        sendBroadcast(dialogIntent);
    }

    public void criaBroadcast(int valor, String tipo) {//Enviar dados para MainActivity
        Intent dialogIntent = new Intent();
        dialogIntent.setAction("abc");
        dialogIntent.putExtra("mensagem", tipo);
        dialogIntent.putExtra("valor", valor + "");
        sendBroadcast(dialogIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            finish();
            startActivity(new Intent(this, UsuarioDetalhesActivity.class));
            return true;
        }else if(id == R.id.action_home){
            finish();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}

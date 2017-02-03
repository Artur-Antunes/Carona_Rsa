package br.com.rsa.carona.carona_rsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;

public class testador extends AppCompatActivity {
    AlertDialog actions;
    private int valor = -1;
    ManipulaDados mDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testador);

    }

    DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // Delete
                    DateFormat dformato = new SimpleDateFormat("HH:mm");
                    String horaAtualString = dformato.format(Calendar.getInstance().getTime());

                    String horaEscolhidaString="13:51";

                    final DateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                    try {
                        final Date dateAtual = df.parse(horaAtualString);
                        final Date dateEscolhida = df.parse(horaEscolhidaString);
                        Log.e("teste->",dateAtual+"");
                        Log.e("teste->",dateEscolhida+"");
                        if(dateAtual.getTime()>dateEscolhida.getTime()){
                            Log.e("resultado:","a data atual é maior.");
                        }else{
                            Log.e("resultado:","a data atual é menor.");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(testador.this,"bora : "+dateString, Toast.LENGTH_LONG).show();
                    break;
                case 1: // Copy
                    startActivity(new Intent(testador.this, LoginActivity.class));
                    break;
                case 2: // Edit
                    break;
                default:
                    break;
            }
        }
    };
    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actions.show();
        }
    };

    public void ok() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(testador.this);
        dialog.setTitle(R.string.title_confirmacao)
                .setMessage("teste")
                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        startActivity(new Intent(testador.this, ExibirDadosUsuarioActivity.class));
                    }
                })
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                    }
                }).show();
    }

}

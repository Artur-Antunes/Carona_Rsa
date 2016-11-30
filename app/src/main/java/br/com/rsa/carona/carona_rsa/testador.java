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

import br.com.rsa.carona.carona_rsa.entidades.Servico;

public class testador extends AppCompatActivity {
    AlertDialog actions;
    private int valor = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Activity");

        Button button = new Button(this);
        button.setText("Click for Options");
        button.setOnClickListener(buttonListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de Perfil");
        String[] options = {"GALERIA", "CÂMERA", "REMOVER TUDO"};
        builder.setItems(options, actionListener);
        builder.setNegativeButton("Cancel", null);
        actions = builder.create();

        setContentView(button);
    }

    DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // Delete
                    valor = -8;
                    Toast.makeText(testador.this, valor + "sdfsd", Toast.LENGTH_LONG).show();
                    break;
                case 1: // Copy
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

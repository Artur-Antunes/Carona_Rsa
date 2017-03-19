package br.com.rsa.carona.carona_rsa.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import br.com.rsa.carona.carona_rsa.entidades.Servico;

/**
 * Created by Artur Antunes on 06/03/2017.
 */
public class StarMeLeva extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent it = new Intent(context, Servico.class);
        context.startService(it);
    }
}

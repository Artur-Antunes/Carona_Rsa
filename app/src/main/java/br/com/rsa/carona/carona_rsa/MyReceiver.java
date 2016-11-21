package br.com.rsa.carona.carona_rsa;
/*
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {
    static ArrayList<MeusDados> arrOnNewLocationListener = new ArrayList<MeusDados>();
    public MyReceiver() {

    }
        public static void setOnNewLocationListener(MeusDados listener) {
        arrOnNewLocationListener.add(listener);
    }
    public static void clearOnNewLocationListener(MeusDados listener) {
        arrOnNewLocationListener.remove(listener);
    }

    private static void OnNewLocationReceived(String Valor) {
        // Check if the Listener was set, otherwise we'll get an Exception when
        // we try to call it
        if (arrOnNewLocationListener != null) {
            // Only trigger the event, when we have any listener
            for (int i = arrOnNewLocationListener.size() - 1; i >= 0; i--) {
                arrOnNewLocationListener.get(i).onDadosChegam(Valor);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("oioioiooio", "onReceive ");
        String teste = intent.getStringExtra("teste");
        Log.e("foi recebico", "onReceive " + teste);

    }


    public interface MeusDados {
        public abstract void onDadosChegam(String  dados);

    }



}
*/
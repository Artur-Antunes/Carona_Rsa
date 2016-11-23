package br.com.rsa.carona.carona_rsa.entidades;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import br.com.rsa.carona.carona_rsa.DetalheUsuario;
import br.com.rsa.carona.carona_rsa.MainActivity;
import br.com.rsa.carona.carona_rsa.R;

public class Funcoes {

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String getExtencaoImagem(String imgPath) {
        String[] aux = imgPath.split("\\.");
        return aux[aux.length - 1];
    }
    public void notificacaoFechado(Bitmap imagem, String titulo, String texto, Context contexto, int numero){
        if(!checkApp(contexto)) {
            final Intent emptyIntent = new Intent(contexto, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(contexto)
                            .setSmallIcon(R.mipmap.icon)
                            .setContentTitle(titulo)
                            .setContentText(texto)
                            .setLargeIcon(imagem)
                            .setContentIntent(pendingIntent);

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(numero, mBuilder.build());
        }else{
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(contexto, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void notificacaoAbertoFechado(Bitmap imagem, String titulo, String texto, Context contexto, int numero){

            final Intent emptyIntent = new Intent(contexto, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(contexto)
                            .setSmallIcon(R.mipmap.icon)
                            .setContentTitle(titulo)
                            .setContentText(texto)
                            .setLargeIcon(imagem)
                            .setContentIntent(pendingIntent);

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(numero, mBuilder.build());

    }

    public List<Usuario> removeUsuarioRepitidos(List<Usuario>lista){
        List nomes=new LinkedList();
        List<Usuario> users=new LinkedList<Usuario>();
        for(int i=0;i<lista.size();i++){
            Log.e("oioioioio ", lista.get(i).getId() + " "+i);
            if(!verIgualdade(lista.get(i).getId()+"",nomes)){
                users.add(lista.get(i));
                nomes.add(lista.get(i).getId()+"");
            }

        }

       return users;
    }
    public boolean verIgualdade(String num, List<String> lista){
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).equals(num)){
                return true;
            }
        }
        return false;
    }
    public boolean checkApp(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("br.com.rsa.carona.carona_rsa")) {
            Log.e("ooooooooo", "aberto ");
            return true;
        } else {
            Log.e("ooooooooo", "fechado ");
            return false;
        }
    }
}

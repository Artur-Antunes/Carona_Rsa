package br.com.rsa.carona.carona_rsa.entidades;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public RoundedBitmapDrawable imagemArredondada(String foto,Resources res){
        byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bitmap);
        dr.setCircular(true);
        return dr;
    }

    public String getExtencaoImagem(String imgPath) {
        String[] aux = imgPath.split("\\.");
        return aux[aux.length - 1];
    }

    public boolean validaHora(String horaSaida){
        DateFormat dformato = new SimpleDateFormat("HH:mm");
        String horaAtualString = dformato.format(Calendar.getInstance().getTime());
        final DateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        try {
            final Date dateAtual = df.parse(horaAtualString);
            final Date dateEscolhida = df.parse(horaSaida);
            if(dateEscolhida.getTime()>dateAtual.getTime()){
                return true;
            }else{
                return false;            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
                return false;
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
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
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

    public void notificacaoAbertoFechado(Bitmap imagem, String titulo, String texto,Context contexto, int numero){

            final Intent emptyIntent = new Intent(contexto, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(contexto)
                            .setSmallIcon(R.mipmap.icon)
                            .setContentTitle(titulo)
                            .setContentText(texto)
                            .setLargeIcon(imagem)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(numero, mBuilder.build());
        //mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
    }

    public void apagarNotificacoes(Context contexto){
        NotificationManager notifManager= (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    public void apagarNotificacaoEspecifica(Context contexto, int id){
        NotificationManager notifManager= (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(id);
    }

    public List<Usuario> removeUsuarioRepitidos(List<Usuario>lista){
        List nomes=new LinkedList();
        List<Usuario> users=new LinkedList<Usuario>();
        for(int i=0;i<lista.size();i++){
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

    public String retornaSimbolo(String sexo){
        if(sexo.equals("Masculino")){
            return "M";
        }else{
            return "F";
        }
    }

    public String converteBooleanStringnCnh(boolean cnh){
        if(cnh){
            return "SIM";
        }else{
            return "NÃO";
        }

    }

    public boolean getConecxao(){
        try{
            URL myUrl = new URL("http://10.0.2.2/Caronas");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String horaSimples(String hora){
        String horaRetorno=hora.substring(hora.length() - 8);
        horaRetorno=horaRetorno.substring(0,horaRetorno.length()-3);
        return horaRetorno;
    }

    public boolean isEmailValid(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public boolean checkApp(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("br.com.rsa.carona.carona_rsa")) {
            return true;
        } else {
            return false;
        }
    }
}

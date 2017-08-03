package br.com.rsa.carona.carona_rsa;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import br.com.rsa.carona.carona_rsa.entidades.BadgeView;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;


public class MainActivity extends AppCompatActivity {

    public int numNovasCaronas = 0;
    public int numNovasSolicitacoes = 0;
    public int numCarAceita=0;
    MyReceiver receiver;
    View v1,v2,v3;
    PagerAdapter adapter;
    public static BadgeView badge1,badge2,badge3;
    TabLayout tabLayout;
    IntentFilter filter = new IntentFilter();
    ManipulaDados md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        md=new ManipulaDados(MainActivity.this);
        //Intent it = new Intent(this, Servico.class);
        //startService(it);
        receiver = new MyReceiver(new Handler());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        TabLayout.Tab tab1 = tabLayout.newTab();
        TabLayout.Tab tab2 = tabLayout.newTab();
        TabLayout.Tab tab3 = tabLayout.newTab();
        tab1.setCustomView(R.layout.tab);
        tab2.setCustomView(R.layout.tab);
        tab3.setCustomView(R.layout.tab);
        TextView txt1 = (TextView) tab1.getCustomView().findViewById(R.id.text1);
        TextView txt2 = (TextView) tab2.getCustomView().findViewById(R.id.text1);
        TextView txt3 = (TextView) tab3.getCustomView().findViewById(R.id.text1);
        txt1.setText("HOME");
        txt1.setTypeface(null,Typeface.BOLD);
        txt2.setText("RECEBIDAS");
        txt3.setText("OFERECIDAS");

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        v1 = tabLayout.getTabAt(0).getCustomView();
        v2 = tabLayout.getTabAt(1).getCustomView();
        v3 = tabLayout.getTabAt(2).getCustomView();

        badge1 = new BadgeView(this, v1);
        badge2 = new BadgeView(this, v2);
        badge3 = new BadgeView(this, v3);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.NORMAL);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        filter.addAction("abc");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        mNotificationManager.cancel(2);
        mNotificationManager.cancel(3);
        if(!isMyServiceRunning(Servico.class)){
            Intent it = new Intent(this, Servico.class);
            startService(it);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                Log.w("oiooi", "Tried to unregister the reciver when it's not registered");
            } else {
                throw e;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            if(Home.load.getVisibility()==View.INVISIBLE) {
                if (md.getUsuario().getIdCaronaSolicitada() == -1 && md.getCaronaOferecida() == null) {
                    startActivity(new Intent(this, Criar_Carona.class));
                } else if (md.getUsuario().getIdCaronaSolicitada() != -1) {
                    Toast.makeText(MainActivity.this, R.string.alert_car_solicitada, Toast.LENGTH_LONG).show();
                } else if (md.getCaronaOferecida() != null) {
                    Toast.makeText(MainActivity.this, R.string.alert_car_oferecida, Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MainActivity.this, R.string.alert_sem_conexao, Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.action_perfil) {
            startActivity(new Intent(this, UsuarioDetalhesActivity.class));
            return true;
        } else if (id == R.id.action_sair) {
            if(md.getCaronaOferecida()==null) {
                Servico.ativo = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                md.limparDados();
                LimparBadge(badge1, 1);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Atenção!")
                        .setMessage("A ação não pode ser concluída! Para deslogar do sistema cancele a carona que você ofertou e tente novamente.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {

                            }
                        }).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void mostraBadge(String valor, BadgeView badge, int tipo) {
        switch (tipo) {
            case 1:
                numNovasCaronas += Integer.parseInt(valor);
                badge.setText(numNovasCaronas + "");
                badge.setBadgeBackgroundColor(R.color.color1);
                badge.show();
                break;
            case 2:
                numNovasSolicitacoes += Integer.parseInt(valor);
                badge.setText(numNovasSolicitacoes + "");
                badge.setBadgeBackgroundColor(R.color.color1);
                badge.show();
                break;
            case 3:
                numCarAceita += Integer.parseInt(valor);
                badge.setText(numCarAceita + "");
                badge.setBadgeBackgroundColor(R.color.color1);
                badge.show();
                break;
        }
    }

    public void LimparBadge(BadgeView badge, int valor) {
        badge.hide();
        if (valor == 1) {
            numNovasCaronas = 0;
        } else if(valor==2){
            numNovasSolicitacoes = 0;
        }else {
            numCarAceita=0;
        }
    }


    public void decrementarBadge(BadgeView badge, int valor) {
        badge.hide();
        if (valor == 1) {
            if (numNovasCaronas > 0) {
                --numNovasCaronas;
            }
        } else {
            if (numNovasSolicitacoes > 0) {
                --numNovasSolicitacoes;
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            String mensagem = intent.getStringExtra("mensagem");
            final String valor = intent.getStringExtra("valor");
            switch (mensagem) {
                case "solicitacao":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mostraBadge(valor, badge3, 2);
                        }
                    });

                    break;

                case "novaCarona":
                    Log.e("foi recebico", valor + " novas caronas");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mostraBadge(valor, badge1, 1);
                            //Home.load.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "solicitacao_expirou":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("aqui_será","sim");
                            Home.load.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case "solicitacao_aceita":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mostraBadge(valor, badge2, 3);
                        }
                    });
                    break;

                case "sem_conexao":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Home.load.setVisibility(View.VISIBLE);
                            Home.newCarona.setVisibility(View.INVISIBLE);
                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.no_con_icon);
                            Home.load.setImageBitmap(bm);
                        }
                    });
                    break;
                case "conexao_ok":
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                               Home.load.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
            }
        }
    }
}

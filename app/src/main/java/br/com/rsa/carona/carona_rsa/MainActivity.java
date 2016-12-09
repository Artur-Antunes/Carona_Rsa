package br.com.rsa.carona.carona_rsa;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.com.rsa.carona.carona_rsa.entidades.BadgeView;
import br.com.rsa.carona.carona_rsa.entidades.Carona;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Servico;


public class MainActivity extends AppCompatActivity {
    public int numNovasCaronas = 0;
    public int numNovasSolicitacoes = 0;
    MyReceiver receiver;
    View v1, v3;
    PagerAdapter adapter;
    public static BadgeView badge1, badge3;
    TabLayout tabLayout;
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent it = new Intent(this, Servico.class);
        startService(it);
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
        txt2.setText("RECEBIDAS");
        txt3.setText("OFERECIDAS");

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        v1 = tabLayout.getTabAt(0).getCustomView();
        v3 = tabLayout.getTabAt(2).getCustomView();
        badge1 = new BadgeView(this, v1);
        badge3 = new BadgeView(this, v3);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

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
        Log.e("registro", "registrado");

    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
        mNotificationManager.cancel(2);
        mNotificationManager.cancel(3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
                Log.w("oiooi", "Tried to unregister the reciver when it's not registered");
            } else {
                // unexpected, re-throw
                throw e;
            }
        }
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
            startActivity(new Intent(this, Criar_Carona.class));
            return true;
        } else if (id == R.id.action_perfil) {
            startActivity(new Intent(this, ExibirDadosUsuarioActivity.class));
            return true;
        } else if (id == R.id.action_sair) {
            ManipulaDados mDados;
            mDados = new ManipulaDados(MainActivity.this);
            if(mDados.limparDados()){
                startActivity(new Intent(this, LoginActivity.class));
                finish();
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
        }


    }

     public void LimparBadge(BadgeView badge, int valor) {
        badge.hide();
        if (valor == 1) {
            numNovasCaronas = 0;
        } else {
            numNovasSolicitacoes = 0;
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
                            Home.load.setVisibility(View.VISIBLE);
                        }
                    });
                    break;

            }


        }
    }
}

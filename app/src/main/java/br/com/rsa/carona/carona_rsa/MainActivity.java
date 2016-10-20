package br.com.rsa.carona.carona_rsa;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * //{@link } que irá fornecer
     *       * Fragmentos para cada uma das secções. Nós usamos um
     *       * {FragmentPagerAdapter @link} derivado, que irá manter todos os
     *       * Fragmento carregado na memória. Se isso se torna muito intensivo de memória, ele
     *       * Pode ser melhor para mudar para um
     *       * {@link} Android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * O {@link ViewPager} que irá hospedar o conteúdo da seção.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Cria o adaptador que irá retornar um fragmento de cada um dos três
        // Secções principais da actividade.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Configure o ViewPager com o adaptador seções.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar o menu; isto acrescenta itens à barra de ação se ela estiver presente.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Item de ação Handle bar clica aqui. A barra de ação será
        // Tratar automaticamente os cliques no botão Início / Up, contanto
        // Como você especificar uma atividade pai em AndroidManifest.xml.
        int id = item.getItemId();

        // Noinspection simplificado se Statement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Uma {FragmentPagerAdapter @link} que retorna um fragmento correspondente aos
     * Uma das seções / guias / páginas.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // GetItem é chamada para instanciar o fragmento para a página fornecida.
            // Retorna um PlaceholderFragment (definida como uma classe interna estática abaixo).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     *       * Um fragmento espaço reservado contendo uma visão simples.
     *      
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         *           * O argumento fragmento representando o número da seção para este
         *           * Fragmento.
         *          
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         *          * Retorna uma nova instância deste fragmento para a seção de dados
         *           * número.
         *          
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}

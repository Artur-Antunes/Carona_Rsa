package br.com.rsa.carona.carona_rsa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.rsa.carona.carona_rsa.Caronas_Recebidas;
import br.com.rsa.carona.carona_rsa.Home;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Home tab1 = new Home();
                return tab1;
            case 1:
                Caronas_Recebidas tab2 = new Caronas_Recebidas();
                return tab2;
            case 2:
                Coronas_oferecidas tab3 = new Coronas_oferecidas();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


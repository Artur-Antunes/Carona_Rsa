package br.com.rsa.carona.carona_rsa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import br.com.rsa.carona.carona_rsa.Caronas_Recebidas;
import br.com.rsa.carona.carona_rsa.Home;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        Log.e("numero de tabs", mNumOfTabs+" SSSSS00000000000000000");
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position) {
            case 0:
                frag = new Home();
                Log.e("0000000", "home 00000000000000000");
                break;
            case 1:
                frag= new Caronas_Recebidas();
                Log.e("000000", "recebidas 000000000000000000000");
                break;
            case 2:
                frag = new Coronas_oferecidas();
                Log.e("00000", "oferecidas 0000000000000000000000");
                break;
            default:
                Log.e("000000", "default 0000000000000000000000000");
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


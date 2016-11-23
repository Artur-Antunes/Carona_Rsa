package br.com.rsa.carona.carona_rsa;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position) {
            case 0:
                frag = new Home();
                break;
            case 1:
                frag= new Caronas_Recebidas();
                break;
            case 2:
                frag = new Coronas_oferecidas();
                break;
            default:
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


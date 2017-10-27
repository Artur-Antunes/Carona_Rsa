package br.com.rsa.carona.carona_rsa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
                frag= new CaronasRecebidas();
                break;
            case 2:
                frag = new CoronasOferecidas();
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


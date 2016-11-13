package br.com.rsa.carona.carona_rsa;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Caronas_Recebidas extends Fragment {
View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("sadasd", "Recebidas ");
       view= inflater.inflate(R.layout.fragment_caronas__recebidas, container, false);

        return view;

    }
}

package com.example.kamil.cukrowag.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.util.logger;

/**
 * Created by kamil on 11.06.17.
 */

public class fragment_scale extends Fragment {
    Button mButton;
    TextView mTextView;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scale, container, false);
        mContext = view.getContext();
        mButton = (Button)view.findViewById(R.id.button);
        mTextView = (TextView)view.findViewById(R.id.textView);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                refresh();
            }
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setEnabled(false);
    }

    public void refresh() {
        String str;
        if ( MainActivity.mScale == null ) {
            str = "Waga jest niepodłączona.\n\n";
        } else {
            str = "Waga jest podłączona.\n";
            try {
                double waga = MainActivity.mScale.getWeightReport().getWeight();
                str += "Wskazanie wagi wynosi: " +String.format("%.2f", waga)+" g\n";
            } catch(Exception e) {
                str += "Odczyt wskazania wagi nie powiódł się. \nKod błędy: "+e.toString()+"n";
            }
        }
        logger.t(mContext, str);
        mTextView.setText(str);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}

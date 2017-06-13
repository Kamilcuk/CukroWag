package com.example.kamil.cukrowag.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;

/**
 * Created by kamil on 11.06.17.
 */

public class fragment_menu extends Fragment {
    Button mButtonInformation, mButtonWeight;
    TextView mTextViewScale, mTextViewWeight;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mContext = view.getContext();
        mTextViewScale = (TextView)view.findViewById(R.id.fragment_menu_scaleinfo);
        mTextViewWeight = (TextView)view.findViewById(R.id.fragment_menu_textView_weight);

        mButtonWeight = (Button)view.findViewById(R.id.fragment_menu_button_weight);
        mButtonWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( MainActivity.mScale != null ) {
                    try {
                        mTextViewWeight.setText(String.format("%.2f", MainActivity.mScale.getWeightReport().getWeight())+" g");
                    } catch(Exception e) {
                        mTextViewWeight.setText("Odczyt wskazania wagi nie powiódł się. \nKod błędy: "+e.toString()+"\n");
                    }
                }
                onResume();
            }
        });

        mButtonInformation = (Button)view.findViewById(R.id.fragment_menu_button_information);
        mButtonInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, ActivityInformation.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( MainActivity.mScale == null ) {
            mTextViewScale.setText("Waga jest niepodłączona.");
            mTextViewWeight.setText("");
            mButtonWeight.setEnabled(false);
        } else {
            mTextViewScale.setText("Waga jest podłączona.");
            mButtonWeight.setEnabled(true);
        }
    }
}

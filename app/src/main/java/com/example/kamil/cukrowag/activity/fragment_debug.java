package com.example.kamil.cukrowag.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.util.logger;
import com.example.kamil.cukrowag.util.Consumer0;

/**
 * Created by kamil on 09.06.17.
 */
public class fragment_debug extends Fragment {
    private TextView logText;
    private ScrollView mScrollView;
    private Button testScale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug, container, false);

        testScale = (Button)view.findViewById(R.id.testScale) ;
        testScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( MainActivity.mScale == null ) {
                    logger.l(view.getContext(), "No scale connected");
                    return;
                }
                try {
                    logger.l(view.getContext(), MainActivity.mScale.getWeightReport().statusMessage());
                } catch (Exception e) {
                    logger.l(view.getContext(), e.toString());
                }
            }
        });

        // set l text
        logText = (TextView)view.findViewById(R.id.logText);
        // scroll l text to the bottom
        mScrollView = (ScrollView)view.findViewById(R.id.logText_scroller);

        logRefresh();

        logger.setLoggerRefresher(new Consumer0(){
            @Override
            public void accept() {
                logRefresh();
            }
        });

        return view;
    }

    public void logRefresh() {
        logText.setText(logger.getLogs());
        // scroll l text to the bottom
        mScrollView.post(new Runnable()
        {
            public void run()
            {
                mScrollView.smoothScrollTo(0, logText.getBottom());
            }
        });
    }
}

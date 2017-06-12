package com.example.kamil.cukrowag.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Meal;
import com.example.kamil.cukrowag.util.logger;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kamil on 09.06.17.
 */

public class fragment_meal extends Fragment {
    ListView mList;
    FoodDatabase mFoodDatabase;
    Context mContext;
    ArrayAdapter<Meal> mListAdapter;
    TextView mInfo, mInfoRight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        mContext = view.getContext();
        mList = (ListView) view.findViewById(R.id.fragment_food_list);
        mFoodDatabase = MainActivity.mFoodDatabase;
        mInfo = (TextView)view.findViewById(R.id.header_textview_left);
        mInfo.setText("Posiłki");
        mInfoRight = (TextView)view.findViewById(R.id.header_textview_right);

        mListAdapter = new ArrayAdapter<Meal>(mContext, R.layout.abstract_row_left_right, mFoodDatabase.mMeals) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Meal m = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_left_right, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.abstract_row_left)).setText(m.name);
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText(new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss").format(m.creationDate));
                return convertView;
            }
        };
        mList.setAdapter(mListAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                edutujPosiłek( mListAdapter.getItem(position) );
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Meal i = mListAdapter.getItem(position);
                new AlertDialog.Builder(mContext)
                        .setTitle("Co zrobić z posiłkiem?")
                        .setMessage(i.toString())
                        .setPositiveButton("Kopiuj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                Meal nowyposilek = new Meal(i);
                                mFoodDatabase.add(nowyposilek);
                                onResume();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Usuń", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mFoodDatabase.remove(i);
                                onResume();
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Anuluj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.cancel();
                            }
                        }).create().show();
                return true;
            }
        });

        return view;
    }

    public void edutujPosiłek(final Meal i) {
        ActivityAddMeal.mMeal = i;
        Intent intent = new Intent(mContext, ActivityAddMeal.class);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        logger.l();
        super.onResume();

        Collections.sort(mFoodDatabase.mMeals, new Comparator<Meal>() {
            @Override
            public int compare(Meal m2, Meal m1) {
                return m1.creationDate.compareTo(m2.creationDate);
            }
        });

        mListAdapter.notifyDataSetChanged();
        mInfoRight.setText(String.format("Ilość: %d", mListAdapter.getCount()));
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setEnabled(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}

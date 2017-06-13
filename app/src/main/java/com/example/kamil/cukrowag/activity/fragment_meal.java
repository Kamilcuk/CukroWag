package com.example.kamil.cukrowag.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Meal;
import com.example.kamil.cukrowag.util.logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kamil on 09.06.17.
 */

public class fragment_meal extends FragmentListSearch<Meal> {
    FoodDatabase mFoodDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFoodDatabase = MainActivity.mFoodDatabase;

        super.setListAdapter(new ArrayAdapter<Meal>(mContext, R.layout.abstract_row_left_right, new ArrayList<Meal>(mFoodDatabase.mMeals)) {
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
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                editMeal(mListAdapter.getItem(position));
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
                                Meal newmeal = new Meal(i);
                                mFoodDatabase.add(newmeal);
                                logger.l("mFoodDatabase.mMeals.size()="+mFoodDatabase.mMeals.size()+" mQuerystring="+mQuerystring);
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

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMeal();
            }
        });

        return view;
    }

    private void editMeal(final Meal i) {
        ActivityAddMeal.mMeal = new Meal(i);
        Intent intent = new Intent(mContext, ActivityAddMeal.class);
        startActivity(intent);
    }

    private void newMeal() {
        ActivityAddMeal.mMeal = null;
        Intent intent = new Intent(mContext, ActivityAddMeal.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        logger.l("mFoodDatabase.mMeals.size()="+mFoodDatabase.mMeals.size()+" mQuerystring="+mQuerystring);
        super.onResume();
        Collections.sort(mFoodDatabase.mMeals, new Comparator<Meal>() {
            @Override
            public int compare(Meal m2, Meal m1) {
                return m1.creationDate.compareTo(m2.creationDate);
            }
        });
        super.filterRefreshListAdapter(mFoodDatabase.mMeals);
    }
}

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

/**
 * Created by kamil on 09.06.17.
 */

public class fragment_meal extends FragmentListSearch<Meal> {
    FoodDatabase mFoodDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFoodDatabase = MainActivity.mFoodDatabase;

        super.setListAdapter(new ArrayAdapter<Meal>(mContext, R.layout.abstract_row_title_left_right, new ArrayList<Meal>(mFoodDatabase.mMeals)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Meal m = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_title_left_right, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.abstract_row_title)).setText(m.name);
                ((TextView) convertView.findViewById(R.id.abstract_row_left)).setText(m.sum().toStringNoName());
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText(new SimpleDateFormat("yyyy/MM/dd\nHH:mm:ss").format(m.creationDate));
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setMaxLines(2);
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
                final Meal i = mFoodDatabase.findMeal(mListAdapter.getItem(position).getId());
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
        Intent intent = new Intent(mContext, ActivityAddMeal.class);
        intent.putExtra("id", i.getId());
        startActivity(intent);
    }

    private void newMeal() {
        Intent intent = new Intent(mContext, ActivityAddMeal.class);
        intent.putExtra("id", -1);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        logger.l("mFoodDatabase.mMeals.size()="+mFoodDatabase.mMeals.size()+" mQuerystring="+mQuerystring);
        super.onResume();
        super.filterRefreshListAdapter(mFoodDatabase.mMeals);
    }
}

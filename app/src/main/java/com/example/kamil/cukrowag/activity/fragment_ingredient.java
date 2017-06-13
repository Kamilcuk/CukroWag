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
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.util.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kamil on 10.06.17.
 */

public class fragment_ingredient extends FragmentListSearch<Ingredient> {
    FoodDatabase mFoodDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFoodDatabase = MainActivity.mFoodDatabase;

        super.setListAdapter(new ArrayAdapter<Ingredient>(mContext, R.layout.abstract_row_left_right, new ArrayList<Ingredient>(mFoodDatabase.mIngredients)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Ingredient i = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_left_right, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.abstract_row_left)).setText(i.toString());
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText("");
                return convertView;
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                editIngredient(mListAdapter.getItem(position));
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Ingredient i = mListAdapter.getItem(position);
                new AlertDialog.Builder(mContext)
                        .setTitle("Usunąć składnik z bazy?")
                        .setMessage(i.toString())
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                mFoodDatabase.mIngredients.remove(i);
                                onResume();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
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
                newIngredient();
            }
        });

        return view;
    }

    private void newIngredient() {
        ActivityAddIngredient.mIngredient = null;
        Intent intent = new Intent(mContext, ActivityAddIngredient.class);
        startActivity(intent);
    }

    private void editIngredient(final Ingredient i) {
        ActivityAddIngredient.mIngredient = i;
        Intent intent = new Intent(mContext, ActivityAddIngredient.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        logger.l("mFoodDatabase.mIngredients.size()="+mFoodDatabase.mIngredients.size()+" mQuerystring="+mQuerystring);
        super.onResume();
        Collections.sort(mFoodDatabase.mIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient m2, Ingredient m1) {
                return m2.name.compareTo(m1.name);
            }
        });
        super.filterRefreshListAdapter(mFoodDatabase.mIngredients);
    }
}

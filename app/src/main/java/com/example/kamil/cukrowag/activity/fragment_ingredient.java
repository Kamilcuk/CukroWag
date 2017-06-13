package com.example.kamil.cukrowag.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.util.logger;

import java.util.ArrayList;

/**
 * Created by kamil on 10.06.17.
 */

public class fragment_ingredient extends FragmentListSearch<Ingredient> {
    FoodDatabase mFoodDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFoodDatabase = MainActivity.mFoodDatabase;

        super.setListAdapter(new ArrayAdapterIngredient(mContext, R.layout.abstract_row_title_left_right, new ArrayList<Ingredient>(mFoodDatabase.mIngredients)));

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
                final Ingredient i = mFoodDatabase.findIngredient(mListAdapter.getItem(position).getId());
                new AlertDialog.Builder(mContext)
                        .setTitle("Czy usunąć składnik?")
                        .setMessage(i.toString())
                        .setNegativeButton("Usuń", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mFoodDatabase.mIngredients.remove(i);
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
                newIngredient();
            }
        });

        return view;
    }

    private void editIngredient(final Ingredient i) {
        Intent intent = new Intent(mContext, ActivityAddIngredient.class);
        intent.putExtra("id", i.getId());
        startActivity(intent);
    }

    private void newIngredient() {
        Intent intent = new Intent(mContext, ActivityAddIngredient.class);
        intent.putExtra("id", -1);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        logger.l("mFoodDatabase.mIngredients.size()="+mFoodDatabase.mIngredients.size()+" mQuerystring="+mQuerystring);
        super.onResume();
        super.filterRefreshListAdapter(mFoodDatabase.mIngredients);
    }
}

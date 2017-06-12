package com.example.kamil.cukrowag.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.util.logger;

/**
 * Created by kamil on 12.06.17.
 */

public class ActivityAddIngredient extends AppCompatActivity {
    TextView WW, WBT;
    EditText name, calories, protein, fat, carbs, fiber;
    Context mContext;
    FoodDatabase mFoodDatabase;
    Ingredient mIngredient = new Ingredient();
    Button cancel, ok, refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.l("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        mContext = this;
        mFoodDatabase = MainActivity.mFoodDatabase;

        name = (EditText) findViewById(R.id.activity_add_ingredient_name);
        calories = (EditText) findViewById(R.id.activity_add_ingredient_calories);
        protein = (EditText) findViewById(R.id.activity_add_ingredient_protein);
        fat = (EditText) findViewById(R.id.activity_add_ingredient_fat);
        carbs = (EditText) findViewById(R.id.activity_add_ingredient_carbs);
        fiber = (EditText) findViewById(R.id.activity_add_ingredient_fiber);

        WW = (TextView) findViewById(R.id.activity_add_ingredient_WW);
        WBT = (TextView) findViewById(R.id.activity_add_ingredient_WBT);

        cancel = (Button) findViewById(R.id.activity_add_ingredient_cancel);
        ok = (Button) findViewById(R.id.activity_add_ingredient_ok);
        refresh = (Button) findViewById(R.id.activity_add_ingredient_refresh);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValues();
                refreshTextView();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTextView();
                if ( checkValues() ) {
                    mFoodDatabase.add(mIngredient);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTextView();
    }

    public void refreshTextView() {
        WW.setText(String.format("%.1f", mIngredient.getWW()));
        WBT.setText(String.format("%.1f", mIngredient.getWBT()));
    }

    public boolean checkValues() {
        mIngredient.name = name.getText().toString();
        for(Ingredient i : mFoodDatabase.mIngredients) {
            if ( i.name == mIngredient.name ) {
                logger.t(mContext, "Składnik z taką nazwą już istnieje");
                return false;
            }
        }
        try {
            double temp = Double.valueOf(calories.getText().toString());
            if ( temp <= 0 ) {
                throw new NumberFormatException("temp<=0");
            }
            mIngredient.calories = temp;
        } catch(NumberFormatException nfe) {
            logger.t(mContext, "Kalorie musza być liczbą większą od zera.\n" + nfe.toString());
            return false;
        }
        try {
            double temp = Double.valueOf(protein.getText().toString());
            if ( temp <= 0 ) {
                throw new NumberFormatException("temp<=0");
            }
            mIngredient.protein = temp;
        } catch(NumberFormatException nfe) {
            logger.t(mContext, "Białka musza być liczbą większą od zera.\n" + nfe.toString());
            return false;
        }
        try {
            double temp = Double.valueOf(fat.getText().toString());
            if ( temp <= 0 ) {
                throw new NumberFormatException("temp<=0");
            }
            mIngredient.fat = temp;
        } catch(NumberFormatException nfe) {
            logger.t(mContext, "Tłuszcze musza być liczbą większą od zera.\n" + nfe.toString());
            return false;
        }
        try {
            double temp = Double.valueOf(carbs.getText().toString());
            if ( temp <= 0 ) {
                throw new NumberFormatException("temp<=0");
            }
            mIngredient.carbs = temp;
        } catch(NumberFormatException nfe) {
            logger.t(mContext, "Węglowodany musza być liczbą większą od zera.\n" + nfe.toString());
            return false;
        }
        try {
            double temp = Double.valueOf(fiber.getText().toString());
            if ( temp <= 0 ) {
                throw new NumberFormatException("temp<=0");
            }
            mIngredient.fiber = temp;
        } catch(NumberFormatException nfe) {
            logger.t(mContext, "Białka musza być liczbą większą od zera.\n" + nfe.toString());
            return false;
        }
        return true;
    }

}
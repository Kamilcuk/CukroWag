package com.example.kamil.cukrowag.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.food.IngredientPart;
import com.example.kamil.cukrowag.food.Meal;
import com.example.kamil.cukrowag.util.logger;

/**
 * Created by kamil on 11.06.17.
 */

public class ActivityAddMeal extends AppCompatActivity  {
    Button mAddIngredientButton, mOkButton, mCancelButton;
    TextView mInfo;
    EditText mName;
    Context mContext;
    public static Meal mMeal = null;
    static FoodDatabase mFoodDatabase;
    ListView mIngredientsList;
    ArrayAdapter<IngredientPart> mIngredientsListAdapter;
    boolean addnew;
    Dialog mDialog;
    Button mDialogWaga;
    EditText mDialogEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.l("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        mContext = this;
        mFoodDatabase = MainActivity.mFoodDatabase;

        addnew = mMeal == null;
        if ( addnew ) {
            mMeal = new Meal(-1);
            if ( mMeal == null ) {
                throw new RuntimeException("mMeal == null");
            }
        }

        mName = (EditText)findViewById(R.id.add_meal_name);
        mName.setText(mMeal.name);

        mInfo = (TextView)findViewById(R.id.add_meal_info);

        mIngredientsList = (ListView)findViewById(R.id.add_meal_ingredients_info);
        mIngredientsListAdapter = new ArrayAdapter<IngredientPart>(this, R.layout.abstract_row_left_right, mMeal.ingredients) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                IngredientPart i = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_left_right, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.abstract_row_left)).setText( i.toString() );
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText("");
                return convertView;
            }
        };
        mIngredientsList.setAdapter(mIngredientsListAdapter);

        mIngredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final IngredientPart ip = mIngredientsListAdapter.getItem(position);

                View DialogView = getLayoutInflater().inflate(R.layout.add_meal_add_ingredient_dialog, null);

                mDialog = new AlertDialog.Builder(ActivityAddMeal.this)
                        .setTitle("Edytuj składnik")
                        .setMessage(ip.toString())
                        .setView(DialogView)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                mDialogPositiveButton(ip);

                            }
                        })
                        .setNegativeButton("usuń", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mMeal.ingredients.remove(ip);
                                dialog.cancel();
                            }
                        }).create();

                mDialogWaga = (Button)DialogView.findViewById(R.id.add_ingredient_dialog_waga_button);
                mDialogWaga.setEnabled(MainActivity.mScale != null);
                mDialogWaga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        mDialogWagaOnClick(arg0);
                    }
                });

                mDialogEditText = (EditText)DialogView.findViewById(R.id.add_ingredient_dialog_waga_edittext);
                mDialogEditText.post(new Runnable() {
                    public void run() {
                        mDialogEditText.requestFocusFromTouch();
                        InputMethodManager lManager = (InputMethodManager)ActivityAddMeal.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        lManager.showSoftInput(mDialogEditText, 0);
                    }
                });

                mDialog.show();
            }
        });

        mAddIngredientButton = (Button)findViewById(R.id.add_meal_add_ingredient);
        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivityAddIngredient();
            }
        });

        mOkButton = (Button)findViewById(R.id.add_meal_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( mName.getText() == null || mName.getText().toString().isEmpty() ) {
                    Toast.makeText(mContext, "Posiłek musi miec nazwę", Toast.LENGTH_LONG).show();
                    return;
                }
                if ( mMeal.ingredients.size() == 0 ) {
                    Toast.makeText(mContext, "Posiłek musi miec składniki", Toast.LENGTH_LONG).show();
                    return;
                }
                mMeal.name = mName.getText().toString();
                if ( addnew ) {
                    mFoodDatabase.add(mMeal);
                }
                mMeal = null;
                finish();
            }
        });

        mCancelButton = (Button)findViewById(R.id.add_meal_remove);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mFoodDatabase.remove(mMeal);
                mMeal = null;
                finish();
            }
        });
    }


    private void mDialogWagaOnClick(View arg0) {
        if ( MainActivity.mScale != null ) {
            try {
                mDialogEditText.setText(String.format("%2f", MainActivity.mScale.getWeightReport().getWeight()));
            } catch(Exception e) {
                Toast.makeText(this, "Blad wewnetrzny: "+e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void mDialogPositiveButton(final IngredientPart ip) {
        if ( mDialogEditText == null ) {
            Toast.makeText(this, "mDialogEditText == null", Toast.LENGTH_LONG).show();
            return;
        }
        if ( mDialogEditText.getText() == null ) {
            Toast.makeText(this, "mDialogEditText.getText() == null", Toast.LENGTH_LONG).show();
            return;
        }
        String str = mDialogEditText.getText().toString().replace(',','.');
        double howmuch;
        try {
            howmuch = Double.parseDouble(str);
        } catch(NumberFormatException mfe) {
            Toast.makeText(this, "Waga składnika musi być liczbą zmiennoprzecinkową", Toast.LENGTH_LONG).show();
            return;
        }
        if ( howmuch <= 0 ) {
            Toast.makeText(this, "Waga składnika ("+String.format("%.2f", howmuch)+") musi być większa od zera", Toast.LENGTH_LONG).show();
            return;
        }
        ip.howmuch = howmuch;
        mDialog.cancel();
        finish();
    }

    @Override
    public void onResume() {
        logger.l("");
        super.onResume();

        if ( addnew ) {
            getSupportActionBar().setTitle("Dodaj posiłek");
        } else {
            getSupportActionBar().setTitle("Edytuj posiłek");
        }


        mMeal.name = mName.getText().toString();
        Ingredient mealsum = mMeal.sum();
        String str =
                String.format("Posiłek: %s\n", mMeal.toString()) +
                String.format("Ilosć składników: %d\n", mMeal.ingredients.size()) +
                String.format("Wartości odżywcze posiłku:\n%s", mealsum.toString());
        mInfo.setText(str);

        mIngredientsListAdapter.notifyDataSetChanged();
        mIngredientsList.setAdapter(mIngredientsListAdapter);
    }

    public void startActivityAddIngredient() {
        Intent intent = new Intent(this, ActivityAddMealAddIngredient.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
        }
    }
}

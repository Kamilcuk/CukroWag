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

import java.text.SimpleDateFormat;

/**
 * Created by kamil on 11.06.17.
 */

public class ActivityAddMeal extends AppCompatActivity {
    Meal mMeal;
    FoodDatabase mFoodDatabase;
    Button mAddIngredientButton, mOkButton, mCancelButton;
    TextView mInfo;
    EditText mName;
    Context mContext;
    ListView mIngredientsList;
    ArrayAdapter<IngredientPart> mIngredientsListAdapter;
    boolean addnew;
    Dialog mDialog;
    Button mDialogWaga;
    EditText mDialogEditText;
    static int idbuffer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.l("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        mContext = this;
        mFoodDatabase = MainActivity.mFoodDatabase;

        {
            Bundle b = getIntent().getExtras();
            if ( b == null || b.containsKey("id") == false ) {
            } else {
                idbuffer = b.getInt("id");
            }
            addnew = idbuffer < 0;
            logger.l(this, "addnew="+addnew+" id="+idbuffer);
            if (addnew) {
                mMeal = new Meal();
            } else {
                mMeal = mFoodDatabase.findMeal( idbuffer );
            }
        }
        if ( mMeal == null ) {
            throw new RuntimeException("mMeal == null");
        }

        mName = (EditText) findViewById(R.id.add_meal_name);
        if ( mName == null ) {
            throw new RuntimeException("mName == null");
        }
        mName.setText(mMeal.name);

        mInfo = (TextView) findViewById(R.id.add_meal_info);

        mIngredientsList = (ListView) findViewById(R.id.add_meal_ingredients_info);
        mIngredientsListAdapter = new ArrayAdapter<IngredientPart>(this, R.layout.abstract_row_title_left_right, mMeal.ingredients) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_title_left_right, parent, false);
                }
                IngredientPart ip = getItem(position);
                Ingredient i = ip.ingredient;
                ((TextView) convertView.findViewById(R.id.abstract_row_title)).setText(i.name);
                ((TextView) convertView.findViewById(R.id.abstract_row_left)) .setText(String.format("Kalorie: %.1f kcal\nBiałka: %.1f g\nTłuszcze: %.1f g\nWęglowodany: %.1f g\nBłonnik: %.1f g", i.calories, i.protein, i.fat, i.carbs, i.fiber));
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText(String.format("%.1f g\nWBT: %.2f\nWW: %.2f", ip.howmuch, i.getWBT(), i.getWW()));
                ((TextView) convertView.findViewById(R.id.abstract_row_right)).setMaxLines(3);
                return convertView;
            }
        };
        mIngredientsList.setAdapter(mIngredientsListAdapter);

        mIngredientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final IngredientPart ip = mIngredientsListAdapter.getItem(position);
                final Ingredient i = ip.ingredient;

                View DialogView = getLayoutInflater().inflate(R.layout.add_meal_add_ingredient_dialog, null);

                mDialog = new AlertDialog.Builder(ActivityAddMeal.this)
                        .setTitle("Edytuj wagę składnika ")
                        .setView(DialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                mDialogPositiveButton(ip);
                                onResume();

                            }
                        })
                        .setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mMeal.ingredients.remove(ip);
                                onResume();
                                dialog.cancel();
                            }
                        }).create();

                ((TextView) DialogView.findViewById(R.id.abstract_row_title)).setText(i.name);
                ((TextView) DialogView.findViewById(R.id.abstract_row_left)) .setText(String.format("Kalorie: %.1f kcal\nBiałka: %.1f g\nTłuszcze: %.1f g\nWęglowodany: %.1f g\nBłonnik: %.1f g", i.calories, i.protein, i.fat, i.carbs, i.fiber));
                ((TextView) DialogView.findViewById(R.id.abstract_row_right)).setText(String.format("%.1f g\nWBT: %.2f\nWW: %.2f", ip.howmuch, i.getWBT(), i.getWW()));
                ((TextView) DialogView.findViewById(R.id.abstract_row_right)).setMaxLines(3);

                mDialogWaga = (Button) DialogView.findViewById(R.id.add_ingredient_dialog_waga_button);
                mDialogWaga.setEnabled(MainActivity.mScale != null);
                mDialogWaga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        mDialogWagaOnClick(arg0);
                    }
                });

                mDialogEditText = (EditText) DialogView.findViewById(R.id.add_ingredient_dialog_waga_edittext);
                mDialogEditText.post(new Runnable() {
                    public void run() {
                        mDialogEditText.requestFocusFromTouch();
                        InputMethodManager lManager = (InputMethodManager) ActivityAddMeal.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        lManager.showSoftInput(mDialogEditText, 0);
                    }
                });
                mDialogEditText.setText(String.format("%.1f", ip.howmuch));

                mDialog.show();
            }
        });

        mAddIngredientButton = (Button) findViewById(R.id.add_meal_add_ingredient);
        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivityAddIngredient();
            }
        });

        mOkButton = (Button) findViewById(R.id.add_meal_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mName.getText() == null || mName.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "Posiłek musi miec nazwę", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mMeal.ingredients.size() == 0) {
                    Toast.makeText(mContext, "Posiłek musi miec składniki", Toast.LENGTH_LONG).show();
                    return;
                }
                mMeal.name = mName.getText().toString();
                if (addnew) {
                    mFoodDatabase.add(new Meal(mMeal));
                }
                mMeal = null;
                finish();
            }
        });

        mCancelButton = (Button) findViewById(R.id.add_meal_remove);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mFoodDatabase.remove(mMeal);
                mMeal = null;
                finish();
            }
        });


        if (addnew) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Dodaj posiłek");
            }
            mCancelButton.setText(R.string.cancel);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edytuj posiłek");
            }
            mCancelButton.setText(R.string.remove);
        }
    }

    @Override
    protected void onDestroy() {
        mMeal = null;
        super.onDestroy();
    }

    private void mDialogWagaOnClick(View arg0) {
        if (MainActivity.mScale != null) {
            try {
                mDialogEditText.setText(String.format("%.1f", MainActivity.mScale.getWeightReport().getWeight()));
            } catch (Exception e) {
                Toast.makeText(this, "Blad wewnetrzny: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void mDialogPositiveButton(final IngredientPart ip) {
        if (mDialogEditText == null) {
            Toast.makeText(this, "mDialogEditText == null", Toast.LENGTH_LONG).show();
            return;
        }
        if (mDialogEditText.getText() == null) {
            Toast.makeText(this, "mDialogEditText.getText() == null", Toast.LENGTH_LONG).show();
            return;
        }
        String str = mDialogEditText.getText().toString().replace(',', '.');
        double howmuch;
        try {
            howmuch = Double.parseDouble(str);
        } catch (NumberFormatException mfe) {
            Toast.makeText(this, "Waga składnika musi być liczbą zmiennoprzecinkową", Toast.LENGTH_LONG).show();
            return;
        }
        if (howmuch <= 0) {
            Toast.makeText(this, "Waga składnika (" + String.format("%.1f", howmuch) + ") musi być większa od zera", Toast.LENGTH_LONG).show();
            return;
        }
        ip.howmuch = howmuch;
        mDialog.cancel();
    }

    @Override
    public void onResume() {
        logger.l("");
        super.onResume();

        mMeal.name = mName.getText().toString();
        Ingredient s = mMeal.sum();
        String str = String.format("Utworzony: %s\n", new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(mMeal.creationDate)) +
                        String.format("Ilosć składników: %d\n", mMeal.ingredients.size()) +
                        String.format("Wartości odżywcze posiłku:\n") +
                        String.format("Kalorie: %.1f kcal;  Białka: %.1f g;  Tłuszcze: %.1f g;\n", s.calories, s.protein, s.fat)+
                        String.format("Węglowodany: %.1f g;  Błonnik: %.1f g; \nWBT: %.2f;   WW: %.2f\n", s.carbs, s.fiber, s.getWBT(), s.getWW());
        mInfo.setText(str);

        mIngredientsListAdapter.notifyDataSetChanged();
    }

    public void startActivityAddIngredient() {
        Intent intent = new Intent(this, ActivityAddMealAddIngredient.class);
        ActivityAddMealAddIngredient.mMeal = mMeal;
        startActivity(intent);
    }
}

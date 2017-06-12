package com.example.kamil.cukrowag.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.food.Meal;

/**
 * Created by kamil on 11.06.17.
 */

public class ActivityAddMealAddIngredient extends AppCompatActivity implements SearchView.OnQueryTextListener {
    FoodDatabase mFoodDatabase;
    MenuItem searchMenuItem;
    SearchView searchView;
    ListView mList;
    ArrayAdapter<Ingredient> mListAdapter;

    AlertDialog dialog;
    Button mDialogWaga;
    EditText mDialogEditText;
    private Meal mMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_add_ingredient);
        mFoodDatabase = MainActivity.mFoodDatabase;
        mMeal = ActivityAddMeal.mMeal;
        mList = (ListView)findViewById(R.id.add_ingredient_list);

        mListAdapter = new ArrayAdapter<Ingredient>(this, R.layout.abstract_row_left_right, mFoodDatabase.mIngredients) {
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
        };
        mList.setAdapter(mListAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                addIngredientToMealDialog( mListAdapter.getItem(position) );
            }
        });
    }

    private void addIngredientToMealDialog(final Ingredient i) {
        View DialogView = getLayoutInflater().inflate(R.layout.add_meal_add_ingredient_dialog, null);
        dialog = new AlertDialog.Builder(this)
                .setMessage(i.toString()).setTitle("Dodaj składnik do posiłku")
                .setView(DialogView)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        mDialogPositiveButton(i);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
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
                InputMethodManager lManager = (InputMethodManager)ActivityAddMealAddIngredient.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(mDialogEditText, 0);
            }
        });

        dialog.show();

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

    private void mDialogPositiveButton(final Ingredient i) {
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
        try {
            mMeal.add(i, howmuch);
        } catch (Exception e) {
            Toast.makeText(this, "Błąd dodawania składnika", Toast.LENGTH_LONG).show();
        }
        dialog.cancel();
        finish();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_ingredient, menu);

        {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Dodaj składnik do posiłku");
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchMenuItem = menu.findItem(R.id.add_ingredient_search);
            searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
            searchView.setEnabled(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.setIconifiedByDefault(false);
            searchView.requestFocusFromTouch();
            searchMenuItem.expandActionView();
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mListAdapter.getFilter().filter(newText.trim());
        return true;
    }

}
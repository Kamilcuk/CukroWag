package com.example.kamil.cukrowag.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.SearchView;
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
import com.example.kamil.cukrowag.food.Ingredient;
import com.example.kamil.cukrowag.util.logger;

/**
 * Created by kamil on 10.06.17.
 */

public class fragment_ingredient extends Fragment implements SearchView.OnQueryTextListener {
    FoodDatabase mFoodDatabase;
    ListView mList;
    Context mContext;

    ArrayAdapter<Ingredient> mListAdapter;
    AlertDialog.Builder mBuilder;
    Dialog mDialog;
    TextView mInfo, mInfoRight;
    SearchView searchView;

    public fragment_ingredient() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        mContext = view.getContext();
        mFoodDatabase = MainActivity.mFoodDatabase;
        mList = (ListView)view.findViewById(R.id.fragment_ingredient_listview);
        mInfo = (TextView)view.findViewById(R.id.header_textview_left);
        mInfo.setText("Składniki");
        mInfoRight = (TextView)view.findViewById(R.id.header_textview_right);


        mListAdapter = new ArrayAdapter<Ingredient>(mContext, R.layout.abstract_row_left_right, mFoodDatabase.mIngredients) {
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

        {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) view.findViewById(R.id.fragment_ingredient_searchview);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
            searchView.setEnabled(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.setIconifiedByDefault(false);
            searchView.requestFocusFromTouch();
        }

        return view;
    }

    @Override
    public void onResume() {
        logger.l();
        super.onResume();

        mListAdapter.notifyDataSetChanged();
        mInfoRight.setText(String.format("Ilość: %d", mListAdapter.getCount()));
    }

    private void editIngredient(final Ingredient i) {
        mBuilder = (new AlertDialog.Builder(mContext))
                .setMessage(i.toString()).setTitle("Chcesz usunąć ten skladnik")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mFoodDatabase.remove(i);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
        mDialog = mBuilder.create();
        mDialog.show();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mListAdapter.getFilter().filter(newText.trim());
        return false;
    }
}

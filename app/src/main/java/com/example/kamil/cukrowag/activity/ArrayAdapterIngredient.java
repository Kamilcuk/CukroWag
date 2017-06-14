package com.example.kamil.cukrowag.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.Ingredient;

import java.util.List;

/**
 * Created by kamil on 13.06.17.
 */

public class ArrayAdapterIngredient extends ArrayAdapter<Ingredient> {
    ArrayAdapterIngredient(Context context, int resource, List<Ingredient> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ingredient i = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.abstract_row_title_left_right, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.abstract_row_title)).setText(i.name);
        ((TextView) convertView.findViewById(R.id.abstract_row_left)) .setText(String.format("Kalorie: %.1f kcal\nBiałka: %.1f g\nTłuszcze: %.1f g\nWęglowodany: %.1f g\nBłonnik: %.1f g", i.calories, i.protein, i.fat, i.carbs, i.fiber));
        ((TextView) convertView.findViewById(R.id.abstract_row_right)).setText(String.format("WBT: %.2f\nWW: %.2f", i.getWBT(), i.getWW()));
        ((TextView) convertView.findViewById(R.id.abstract_row_right)).setMaxLines(2);
        return convertView;
    }
}

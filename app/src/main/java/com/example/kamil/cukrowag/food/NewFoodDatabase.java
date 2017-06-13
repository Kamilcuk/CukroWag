package com.example.kamil.cukrowag.food;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kamil.cukrowag.util.logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by kamil on 13.06.17.
 */

public class NewFoodDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "CUKROWAG.sqlite3";
    public static final int DB_VERSION = 1;
    Context mContext;

    public NewFoodDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(""+
                "CREATE TABLE ingredients_categories ( " +
                " id INTEGER PRIMATY KEY, " +
                " name TEXT " +
                ");\n" +
                "CREATE TABLE ingredients (" +
                " id INTEGER PRIMARY KEY, " +
                " category_id INTEGER," +
                " name TEXT," +
                " calories REAL," +
                " protein REAL," +
                " fat REAL," +
                " carbs REAL," +
                " fiber REAL," +
                " FOREIGN KEY(category_id) REFERENCES ingredients_categories(id)" +
                ");\n" +
                "CREATE TABLE meals ("+
                " id INTEGER PRIMATY KEY, "+
                " name TEXT,"+
                " creationdate TEXT"+
                ");\n" +
                "CREATE_TABLE meals_ingredients (" +
                " meal_id INTEGER, ingredient_id INTEGER, howmuch REAL" +
                " FOREIGN KEY(meal_id) REFERENCES meal(id)" +
                " FOREIGN KEY(ingredient_id) REFERENCES ingridients(id)" +
                ");\n"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(""+
                "DROP TABLE IF EXISTS meals_ingredients;" +
                "DROP TABLE IF EXISTS meals;" +
                "DROP TABLE IF EXISTS ingredients;" +
                "DROP TABLE IF EXISTS ingredients_categories;" );
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Ingredient findIngredient(int id) {
        Ingredient temp = null;

        final Cursor c = getReadableDatabase().rawQuery("SELECT * FROM ingredients WHERE ingredients.id = " + id, null);
        if ( c != null ) {
            if (c.moveToFirst()) {
                try {
                    temp = new Ingredient(
                            c.getInt(c.getColumnIndexOrThrow("id")),
                            null,
                            c.getString(c.getColumnIndexOrThrow("name")),
                            c.getDouble(c.getColumnIndexOrThrow("calories")),
                            c.getDouble(c.getColumnIndexOrThrow("protein")),
                            c.getDouble(c.getColumnIndexOrThrow("fat")),
                            c.getDouble(c.getColumnIndexOrThrow("carbs")),
                            c.getDouble(c.getColumnIndexOrThrow("fiber"))
                    );
                } catch (Exception e) {
                    logger.l(e.toString());
                }
            }
            c.close();
        }

        return temp;
    }

    public Meal findMeal(int id) {
        Meal temp = null;

        final Cursor c = getReadableDatabase().rawQuery("SELECT * FROM meals JOIN meals_ingredients a ON meals.meal_id = a.meal_id WHERE meals.meal_id = " + id, null);
        if ( c != null ) {
            if (c.moveToFirst()) {
                temp = new Meal();
                temp.setId(c.getInt(c.getColumnIndex("meals.id")));
                temp.name = c.getString(c.getColumnIndex("meals.name"));
                try {
                    temp.creationDate = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS").parse(c.getString(c.getColumnIndex("meals.creationDate")));
                } catch(ParseException e) {
                }
                do {
                    try {
                        temp.add(findIngredient(c.getInt(c.getColumnIndex("a.ingredient_id"))), c.getDouble(c.getColumnIndex("a.howmuch")));
                    } catch (Exception e) {

                    }
                } while (c.moveToNext());
            }
            c.close();
        }

        return temp;
    }
}

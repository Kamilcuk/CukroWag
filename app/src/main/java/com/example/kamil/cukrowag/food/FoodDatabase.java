package com.example.kamil.cukrowag.food;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kamil.cukrowag.util.Consumer1;
import com.example.kamil.cukrowag.util.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static com.example.kamil.cukrowag.activity.MainActivity.mFoodDatabase;

/**
 * Created by kamil on 11.06.17.
 */

public class FoodDatabase implements Serializable {
    static final long serialVersionUID = 42L;
    final static String table_categories = "categories";
    final static String table_ingredients = "ingridients";
    final static String table_meals_ingredients = "meals_ingredients";
    final static String table_meals = "meals";

    public ArrayList<Ingredient> mIngredients = new ArrayList<Ingredient>();
    public ArrayList<IngredientCategory> mIngredientCategories = new ArrayList<IngredientCategory>();
    public ArrayList<Meal> mMeals = new ArrayList<Meal>();
    int mIngredientsId = 0;
    int mIngredientCategoriesId = 0;
    int mMealsId = 0;

    public void databaseImport(Context context) throws Exception {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        if (db == null) {
            logger.l("Database does not exist!");
            return;
        }

        DatabaseHelper.rawQueryCursorDo(db, "select * from sqlite_master",
                new Consumer1<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        String[] columnNames = cursor.getColumnNames();
                        String tableString = new String();
                        for (String name : columnNames) {
                            tableString += String.format("%s: %s\n", name,
                                    cursor.getString(cursor.getColumnIndex(name)));
                        }
                        tableString += "\n";
                        logger.l(tableString);
                    }
                }
        );

        if (DatabaseHelper.checkTableExists(db, table_categories)) {
            DatabaseHelper.rawQueryCursorDo(db, "select * from " + table_categories,
                    new Consumer1<Cursor>() {
                        @Override
                        public void accept(Cursor cursor) {
                            mIngredientCategories.add(new IngredientCategory(
                                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
                            ));
                        }
                    }
            );
        } else {
            logger.l("table " + table_categories + " does not exists!");
        }

        if (DatabaseHelper.checkTableExists(db, table_ingredients)) {
            DatabaseHelper.rawQueryCursorDo(db, "select * from " + table_ingredients,
                    new Consumer1<Cursor>() {
                        @Override
                        public void accept(Cursor cursor) {
                            try {
                                mIngredients.add(new Ingredient(
                                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                        findObj(mIngredientCategories, cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                                        cursor.getDouble(cursor.getColumnIndexOrThrow("calories")),
                                        cursor.getDouble(cursor.getColumnIndexOrThrow("protein")),
                                        cursor.getDouble(cursor.getColumnIndexOrThrow("fat")),
                                        cursor.getDouble(cursor.getColumnIndexOrThrow("carbs")),
                                        cursor.getDouble(cursor.getColumnIndexOrThrow("fiber"))
                                ));
                            } catch (Exception e) {
                                logger.l(e.toString());
                            }
                        }
                    }
            );
        } else {
            logger.l("table " + table_ingredients + " does not exists!");
        }

        if (DatabaseHelper.checkTableExists(db, table_meals)) {
            DatabaseHelper.rawQueryCursorDo(db, "select * from " + table_meals,
                    new Consumer1<Cursor>() {
                        @Override
                        public void accept(Cursor cursor) {
                            Meal m = new Meal();
                            m.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                            mMeals.add(m);
                        }
                    }
            );
        } else {
            logger.l("table " + table_meals + " does not exists!");
        }

        if (DatabaseHelper.checkTableExists(db, "meals") && DatabaseHelper.checkTableExists(db, "ingredients")) {
            if (DatabaseHelper.checkTableExists(db, table_meals_ingredients)) {
                DatabaseHelper.rawQueryCursorDo(db, "select * from " + table_meals_ingredients,
                        new Consumer1<Cursor>() {
                            @Override
                            public void accept(Cursor cursor) {
                                try {
                                    findObj(mMeals, cursor.getInt(cursor.getColumnIndexOrThrow("meal_id")))
                                            .add(findObj(mIngredients,
                                                    cursor.getInt(cursor.getColumnIndexOrThrow("ingredient_id"))),
                                                    cursor.getInt(cursor.getColumnIndexOrThrow("how_much"))
                                            );
                                } catch (Exception e) {
                                    logger.l(e.toString());
                                }
                            }
                        }
                );
            } else {
                logger.l("table meals_ingredients does not exists!");
            }
        }

        db.close();

        logger.l("new FoodDatabase imported");

        changeNotifyConsumer(true, new Ingredient());
        changeNotifyConsumer(true, new IngredientCategory());
        changeNotifyConsumer(true, new Meal());
    }

    public Meal add(Meal m) {
        m.setId(getNextId(mMeals));
        m.creationDate = new Date();
        logger.l(m.getId() + " " + m.name);
        mMeals.add(m);
        changeNotifyConsumer(true, m);
        return m;
    }
    public Ingredient add(Ingredient m)   {
        logger.l(m.getId() + " " + m.name);
        m.setId(getNextId(mIngredients));
        mIngredients.add(m);
        changeNotifyConsumer(true, m);
        return m;
    }
    public IngredientCategory add(IngredientCategory m){
        logger.l(m.getId() + " " + m.name);
        m.setId(getNextId(mIngredientCategories));
        mIngredientCategories.add(m);
        changeNotifyConsumer(true, m);
        return m;
    }
    public void remove(Meal m) {
        logger.l(m.getId() + " " + m.name);
        mMeals.remove(m);
        changeNotifyConsumer(false, m);
    }

    public void remove(Ingredient m) {
        logger.l(m.getId() + " " + m.name);
        mIngredients.remove(m);
        changeNotifyConsumer(false, m);
    }

    public void remove(IngredientCategory m) {
        logger.l("");
        mIngredientCategories.remove(m);
        changeNotifyConsumer(false, m);
    }

    public Meal findMeal(int id) { return findObj(mMeals, id); }
    public Ingredient findIngredient(int id) { return findObj(mIngredients, id); }
    public <T extends HasIdName> T findObj(ArrayList<T> list, int list_id) {
        for(T i : list) {
            if ( i.getId() == list_id ) {
                return i;
            }
        }
        return null;
    }

    public <T extends HasIdName> void update(ArrayList<T> list, T newObject, int list_id) throws Exception  {
        for (int i = 0; i < list.size(); ++i) {
            if ( list.get(i).getId() == list_id ) {
                list.set(i, newObject);
                return;
            }
        }
        throw new RuntimeException("update: list_id="+list_id+" not found");
    }

    public <T extends HasIdName> int getNextId(ArrayList<T> list) {
        int ret;
        if (list.isEmpty()) {
            return 0;
        } else {
            ret = -1;
            for (T l : list) {
                if (l.getId() > ret) {
                    ret = l.getId();
                }
            }
        }
        return ret + 1;
    }

    public ArrayList<Ingredient> getIngredients() {
        return mIngredients;
    }

    public ArrayList<IngredientCategory> getIngredientCategories() {
        return mIngredientCategories;
    }

    public ArrayList<Meal> getMeals() {
        return mMeals;
    }

    private void changeNotifyConsumer(boolean dir, Meal unused) {
        Collections.sort(mFoodDatabase.mMeals, new Comparator<Meal>() {
            @Override
            public int compare(Meal m2, Meal m1) {
                return m1.creationDate.compareTo(m2.creationDate);
            }
        });
    }
    private void changeNotifyConsumer(boolean dir, Ingredient unused) {
        Collections.sort(mFoodDatabase.mIngredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient m2, Ingredient m1) {
                return m2.name.compareTo(m1.name);
            }
        });
    }
    private void changeNotifyConsumer(boolean dir, IngredientCategory unused) {
    }
}

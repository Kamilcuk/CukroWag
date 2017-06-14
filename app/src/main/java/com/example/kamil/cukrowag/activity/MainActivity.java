package com.example.kamil.cukrowag.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.FoodDatabase;
import com.example.kamil.cukrowag.food.IngredientPart;
import com.example.kamil.cukrowag.food.Meal;
import com.example.kamil.cukrowag.scale.Scale;
import com.example.kamil.cukrowag.scale.UsbScale;
import com.example.kamil.cukrowag.util.logger;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ViewPager mViewPager;
    private PagerAdapter mViewPagerAdapter;
    static public Scale mScale = null;
    SharedPreferences prefs = null;
    static public FoodDatabase mFoodDatabase;
    static final String mFoodDatabaseFileName = "mFoodDatabase.dat";
    TabLayout mTabLayout;
    static final Integer mViewPagetPosDefault = 1;
    Integer mViewPagerPos = mViewPagetPosDefault;
    static final String mViewPagerPosFileName = "mViewPagerPos.dat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.activity_main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.menu);
        mTabLayout.getTabAt(1).setIcon(R.drawable.meal);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ingredients);

        // Set the intent filter for the broadcast receiver, and register.
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbDetachReceiver, filter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if ( HandleSerializable.canLoad(this, mFoodDatabaseFileName) == false || prefs.getBoolean("firstrun", true) == true) {
            // Do first run stuff here then set 'firstrun' as false
            logger.l("First run!");
            this.importFoodDatabase(this);
            prefs.edit().putBoolean("firstrun", false).commit();
        } else {
            try {
                logger.l("Importing food database from filename");
                FoodDatabase temp;
                temp = (FoodDatabase) HandleSerializable.load(this, mFoodDatabaseFileName);
                mFoodDatabase = temp;
            } catch(IOException | ClassNotFoundException e) {
                logger.a(this, e.toString());
                logger.l("Error: Importing food database from filename");
                this.importFoodDatabase(this);
            }
        }
        if ( mFoodDatabase == null ) {
            logger.l("ERRORRR mFoodDatabase == null");
        }
        logger.l(this, "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if ( HandleSerializable.canLoad(this, mViewPagerPosFileName) ) {
                Integer temp;
                temp = (Integer) HandleSerializable.load(this, mViewPagerPosFileName);
                mViewPagerPos = temp;
            }
        } catch(IOException | ClassNotFoundException e) {
            logger.l(this, e.toString());
            mViewPagerPos = -1;
        }
        if(mViewPager != null && mViewPagerPos >= 0 && mViewPagerPos < mViewPagerAdapter.getCount() ) {
            mViewPager.setCurrentItem(mViewPagerPos);
        } else {
            mViewPager.setCurrentItem(mViewPagetPosDefault);
        }
    }

    @Override
    protected void onStop() {
        mViewPagerPos = mViewPager.getCurrentItem();
        try {
            HandleSerializable.save(this, mViewPagerPos, mViewPagerPosFileName);
        } catch(IOException e) {
            logger.a(this, e.toString());
        }
        super.onStop();
    }

    static public void importFoodDatabase(Context context) {
        try {
            FoodDatabase temp;
            temp = new FoodDatabase();
            temp.databaseImport(context);
            logger.t(context, "Zaimportowano nową bazę danych");
            mFoodDatabase = temp;

            try {
                Meal m = new Meal();
                m.ingredients.add(new IngredientPart(mFoodDatabase.findObj(mFoodDatabase.getIngredients(), 1), 100));
                m.name = "test posilek1";
                mFoodDatabase.add(m);
            } catch (Exception e) {
                logger.a(context, e.toString());
            }
            try {
                Meal m = new Meal();
                m.ingredients.add(new IngredientPart(mFoodDatabase.findObj(mFoodDatabase.getIngredients(), 2), 100));
                m.name = "test posilek2";
                mFoodDatabase.add(m);
            } catch (Exception e) {
                logger.a(context, e.toString());
            }

            saveFoodDatabase(context);

            logger.l("TUTAJ1");
        } catch(Exception e) {
            logger.l("TUTAJ2");
            mFoodDatabase = new FoodDatabase();
            logger.a(context, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        saveFoodDatabase(this);
        unregisterReceiver(mUsbDetachReceiver);
        logger.l(this, "");
    }

    public static void saveFoodDatabase(Context context) {
        try {
            HandleSerializable.save(context, mFoodDatabase, mFoodDatabaseFileName);
        } catch (IOException e) {
            logger.a(context, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_meal: {
                Intent intent = new Intent(this, ActivityAddMeal.class);
                intent.putExtra("id", -1);
                startActivity(intent);
            }   return true;
            case R.id.action_add_ingredient: {
                Intent intent = new Intent(this, ActivityAddIngredient.class);
                intent.putExtra("id", -1);
                startActivity(intent);
            }    return true;
            case R.id.action_menu:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.action_meals:
                mViewPager.setCurrentItem(1);
                return true;
            case R.id.action_ingredients:
                mViewPager.setCurrentItem(2);
                return true;
            case R.id.action_information:{
                Intent intent = new Intent(this, ActivityInformation.class);
                startActivity(intent);
            }    return true;
            case R.id.action_settings: {
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
            }    return true;
            case R.id.action_exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scaleConnect(UsbDevice usbDevice) {
        logger.l(this, "");
        if ( prefs.getBoolean("enable_usb_weight_scale", true) == true ) {
            try {
                mScale = new UsbScale(this, usbDevice);
            } catch (Exception e) {
                logger.a(this, e.toString());
            }
        } else {
            scaleDisconnect();
        }
    }

    private void scaleDisconnect() {
        logger.l(this, "");
        mScale = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null) {
            logger.l(this, "intent: " + intent.toString());
            if (intent.getAction() != null) {
                if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) { // active intent
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (usbDevice != null) {
                        logger.l(this, "USB device attached: name: " + usbDevice.getDeviceName());
                        scaleConnect(usbDevice);
                    }
                }
            }
        }

        saveFoodDatabase(this);
    }

    // Catch a USB detach broadcast intent.
    BroadcastReceiver mUsbDetachReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) { // braodcast intent
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                }
                scaleDisconnect();
                onResume();
            }
        }
    };
}
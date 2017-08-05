package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    private SharedPreferences sharedpreferences;

    private String TAG = MainActivity.class.getSimpleName();

    private static String url = "http://chr724.ir/services/v3/EasyCharge/initializeData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("خرید شارژ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();

        toolbar.setNavigationOnClickListener(v -> {
            if (fragmentManager.getBackStackEntryCount() > 0)
                fragmentManager.popBackStack();
            else if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
            else
                drawer.openDrawer(GravityCompat.START);
        });

        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ChargeFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedpreferences = getSharedPreferences("KiooskData", Context.MODE_PRIVATE);

        new GetInitializeData().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("خروج");
            builder.setMessage("آیا می خواهید از برنامه خارج شوید؟");
            builder.setPositiveButton("بلی", (dialog, which) -> {
                finish();
            });
            builder.setNegativeButton("خیر", (dialog, which) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.charge) {
            fragment = new ChargeFragment();
        } else if (id == R.id.packages) {
            fragment = new PackageFragment();
        } else if (id == R.id.giftcard) {
            fragment = new GiftcardFragment();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container_wrapper, fragment);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private class GetInitializeData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (isNetworkConnected()) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);

                if (jsonStr != null) {
                    ArrayList<Package> packages;
                    ArrayList<Package> giftcards;
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        JSONObject packageObj = jsonObj.getJSONObject("products").getJSONObject("internetPackage").getJSONObject("mtn");
                        packages = new ArrayList<Package>();

                        for (Iterator<String> iter = packageObj.keys(); iter.hasNext(); ) {
                            String key = iter.next();
                            JSONArray tempArray = packageObj.getJSONArray(key);
                            for (int i = 0; i < tempArray.length(); i++) {
                                JSONObject j = tempArray.getJSONObject(i);
                                Package p = new Package(j.getString("id"), j.getString("name"), j.getString("price"));
                                packages.add(p);
                            }
                        }

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        Type listOfPackages = new TypeToken<ArrayList<Package>>() {
                        }.getType();
                        String strPackages = new Gson().toJson(packages, listOfPackages);

                        editor.putString("packages", strPackages);

                        JSONObject giftcardObj = jsonObj.getJSONObject("products").getJSONObject("giftCard");
                        giftcards = new ArrayList<>();

                        for (Iterator<String> iter = giftcardObj.keys(); iter.hasNext(); ) {
                            String key = iter.next();
                            JSONArray tempArray = giftcardObj.getJSONArray(key);
                            for (int i = 0; i < tempArray.length(); i++) {
                                JSONObject j = tempArray.getJSONObject(i);
                                Package p = new Package(j.getString("id"), j.getString("name"), j.getString("price"));
                                giftcards.add(p);
                            }
                        }

                        Type listOfGiftcards = new TypeToken<ArrayList<Package>>() {
                        }.getType();
                        String strGiftcards = new Gson().toJson(giftcards, listOfGiftcards);

                        editor.putString("giftcards", strGiftcards);

                        editor.apply();

                    } catch (final JSONException e) {
                        Log.e("", "Json parsing error: " + e.getMessage());
                    }


                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(() -> Log.e(TAG, "Couldn't get json from server. Check LogCat for possible errors!"));

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL checkUrl = new URL(url);
                HttpURLConnection urlc = (HttpURLConnection) checkUrl.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;
    }
}

package com.amalkjose.whatsappassistant;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SheduleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    EditText editText;
    String[] items;
    SQLiteDatabase db;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView= (ListView) findViewById(R.id.listview);
        editText=(EditText)findViewById(R.id.txtsearch);
        load_shedules();

        db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS shedule_msg(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,phone VARCHAR,datetime VARCHAR,message VARCHAR,image VARCHAR,status INTEGER);");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    load_shedules();
                }
                else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(SheduleActivity.this,SheduleContacts.class);
                startActivity(in);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void searchItem(String text){
        load_shedules();
        for(String item:items){
            if(!item.toLowerCase().contains(text.toLowerCase())){
                listItems.remove(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

        if (id == R.id.reset) {
            Toast.makeText(getApplicationContext(),"hai1",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.settings) {

        } else if (id == R.id.signout) {
            Intent intent=new Intent(SheduleActivity.this,Main2Activity.class);
            startActivity(intent);
        } else if (id == R.id.share) {

        } else if (id == R.id.about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    public void load_shedules(){
        try {
            db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
            Cursor cur=db.rawQuery("SELECT * FROM shedule_msg where status=1 order by id DESC", null);
            if(cur.getCount()==0){
                Toast.makeText(this,"No shedules found..!",Toast.LENGTH_SHORT).show();
            }
            else {
                items = new String[cur.getCount()];
                int i = 0;
                cur.moveToFirst();
                do {
                    String nm = cur.getString(cur.getColumnIndex("name"));
                    String ph = cur.getString(cur.getColumnIndex("phone"));
                    String dt_tm = cur.getString(cur.getColumnIndex("datetime"));
                    items[i] = nm + "\n" + ph + "\n" + dt_tm;
                    i++;
                } while (cur.moveToNext());
                cur.close();
                listItems = new ArrayList<>(Arrays.asList(items));
                adapter = new ArrayAdapter<String>(this, R.layout.three_line_list, R.id.textitem, listItems);
                listView.setAdapter(adapter);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"No Shedules found..!",Toast.LENGTH_SHORT).show();
        }
    }

}

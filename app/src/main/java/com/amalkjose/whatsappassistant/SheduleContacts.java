package com.amalkjose.whatsappassistant;

import android.content.Intent;
import android.database.Cursor;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SheduleContacts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    List<Person> cont;
    ListView listView;
    EditText editText;
    String[] items;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedule_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView=(ListView)findViewById(R.id.listview);
        editText=(EditText)findViewById(R.id.txtsearch);
        cont = new ArrayList<Person>();
        load_contacts();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    load_contacts();
                }
                else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       // Toast.makeText(getApplicationContext(),cont.get(139).name,Toast.LENGTH_SHORT).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void searchItem(String text){
        load_contacts();
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
        getMenuInflater().inflate(R.menu.shedule_contacts, menu);
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
            Intent intent=new Intent(SheduleContacts.this,Main2Activity.class);
            startActivity(intent);
        } else if (id == R.id.share) {

        } else if (id == R.id.about) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void load_contacts(){
        String[] projection    = new String[] {
                ContactsContract.RawContacts._ID,
                ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor people = getContentResolver().query(  ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" },
                null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int indexUid=people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        if(people != null   && people.moveToFirst()) {
            items=new String[people.getCount()];
            int i=0;
            do{
                Person p = new Person();
                p.name = people.getString(indexName);
                p.phone = people.getString(indexNumber);
                p.cid = people.getString(indexUid);
                cont.add(p);
                items[i]=people.getString(indexName);
                i++;
            }while (people.moveToNext());
        }
        people.close();
        Arrays.sort(items);
        listItems=new ArrayList<>(Arrays.asList(items));
        adapter=new ArrayAdapter<String>(this,R.layout.list_item,R.id.textitem,listItems);
        listView.setAdapter(adapter);
    }

}
class Person{
    String name;
    String phone;
    String cid;
}

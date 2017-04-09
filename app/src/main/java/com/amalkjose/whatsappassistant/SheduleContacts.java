package com.amalkjose.whatsappassistant;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class SheduleContacts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    List<Person> cont;
    ListView listView;
    EditText editText,dt,tm,ctext,msg;
    String[] items,numbers;
    String contact,img_path,s_time;
    Button ok,cancel;
    ArrayAdapter<String> adapter;
    RadioGroup rg;
    Boolean isPic=false;
    ArrayList<String> listItems;
    SQLiteDatabase db;

    //Image Start-1

    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private final static int ALL_PERMISSIONS_RESULT = 107;

    //Image End-1

    private int mYear, mMonth, mDay, mHour, mMinute;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),(String)parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
                contact=parent.getItemAtPosition(position).toString();
                final AlertDialog.Builder builder = new AlertDialog.Builder(SheduleContacts.this);
                LayoutInflater inflater = (SheduleContacts.this).getLayoutInflater();
                View dialogView=inflater.inflate(R.layout.dialog, null);
                builder.setCancelable(false);
                builder.setView(dialogView);
                ctext= (EditText) dialogView.findViewById(R.id.sel_contact);
                ctext.setText(contact);
                dt= (EditText) dialogView.findViewById(R.id.date);
                msg= (EditText) dialogView.findViewById(R.id.msg);
                tm= (EditText) dialogView.findViewById(R.id.time);
                rg = (RadioGroup) dialogView.findViewById(R.id.mtype);
                imageview = (ImageView) dialogView.findViewById(R.id.iv_send);
                ok=(Button)dialogView.findViewById(R.id.shedule);
                cancel=(Button)dialogView.findViewById(R.id.cancel);




                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        switch(checkedId){
                            case R.id.mtype_p:
                                msg.setHint("Image Caption");
                                isPic=true;
                                imageview.setVisibility(View.VISIBLE);
                                Integer w= (int)(150 * getApplicationContext().getResources().getDisplayMetrics().density);
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w,w);
                                parms.gravity= Gravity.CENTER;
                                imageview.setLayoutParams(parms);
                                break;
                            case R.id.mtype_t:
                                msg.setHint("Message");
                                isPic=false;
                                imageview.setVisibility(View.INVISIBLE);
                                LinearLayout.LayoutParams parms2 = new LinearLayout.LayoutParams(0,0 );
                                parms2.gravity= Gravity.CENTER;
                                imageview.setLayoutParams(parms2);
                                break;
                        }
                    }
                });
                //Image Start-2
                imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });
                //Image End-2

                dt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(SheduleContacts.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        dt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    }
                });
                tm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(SheduleContacts.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        s_time=hourOfDay+":" + minute;
                                        String AM_PM ;
                                        if(hourOfDay < 12) {
                                            AM_PM = "AM";
                                            if(hourOfDay==0){
                                                hourOfDay=12;
                                            }
                                        } else {
                                            AM_PM = "PM";
                                            hourOfDay=hourOfDay-12;
                                            if(hourOfDay==0){
                                                hourOfDay=12;
                                            }
                                        }
                                        tm.setText(hourOfDay + ":" + minute+ "  " +AM_PM);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                final AlertDialog alertDialog = builder.create();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date=dt.getText().toString();
                        String time=s_time;
                        String message=msg.getText().toString();
                        String[] separated = contact.split("\n");
                        String cname = separated[0];
                        String cphone = separated[1];
                        String image_path=img_path;
                        db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
                        db.execSQL("CREATE TABLE IF NOT EXISTS shedule_msg(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,phone VARCHAR,datetime VARCHAR,message VARCHAR,image VARCHAR);");

                        if(TextUtils.isEmpty(date)) {
                            Toast.makeText(getApplicationContext(),"Please select a valid Date",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(TextUtils.isEmpty(time)) {
                            Toast.makeText(getApplicationContext(),"Please select a valid Time",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(isPic){
                            if(TextUtils.isEmpty(image_path)) {
                                Toast.makeText(getApplicationContext(),"Please select a valid Image",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            db.execSQL("insert into shedule_msg(name,phone,datetime,message,image) values('"+cname+"','"+cphone+"','"+date+" "+time+"','"+message+"','"+image_path+"')");
                        }
                        else{
                            if(TextUtils.isEmpty(message)) {
                                Toast.makeText(getApplicationContext(),"Please type your message",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            db.execSQL("insert into shedule_msg(name,phone,datetime,message,image) values('"+cname+"','"+cphone+"','"+date+" "+time+"','"+message+"','null')");
                        }
                        Toast.makeText(getApplicationContext(),"Message Saved..!",Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
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
        try {
            String[] projection = new String[]{
                    ContactsContract.RawContacts._ID,
                    ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor people = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                    new String[]{"com.whatsapp"},
                    null);
            int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int indexUid = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            if (people != null && people.moveToFirst()) {
                items = new String[people.getCount()];
                int i = 0;
                do {
                    Person p = new Person();
                    p.name = people.getString(indexName);
                    p.phone = people.getString(indexNumber);
                    p.cid = people.getString(indexUid);
                    cont.add(p);
                    items[i] = people.getString(indexName) + "\n" + people.getString(indexNumber);
                    i++;
                } while (people.moveToNext());
                Arrays.sort(items);
                numbers = new String[people.getCount()];
                for (int j = 0; j < people.getCount(); j++) {
                    numbers[j] = items[j];
                }
            }
            people.close();
            listItems = new ArrayList<>(Arrays.asList(items));
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.textitem, listItems);
            listView.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"No Whatsapp Contacts Found..!",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(SheduleContacts.this,MainActivity.class);
            startActivity(intent);
        }
    }

    //image start-3
    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SheduleContacts.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                imageview.setImageBitmap(bitmap);
                img_path=String.valueOf(destination);
                Toast.makeText(getApplicationContext(),"destination"+String.valueOf(destination),Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String copy_path = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg").toString();

                try {
                    FileUtils.copyFile(new File(destination.toString()), new File(copy_path));
                    img_path=String.valueOf(copy_path);
                    Toast.makeText(getApplicationContext(),"Copypath"+String.valueOf(copy_path),Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }


                imageview.setImageBitmap(bitmap);
                Toast.makeText(getApplicationContext(),"destinati"+String.valueOf(destination),Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //image End-3

}
class Person{
    String name;
    String phone;
    String cid;
}

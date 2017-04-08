package com.amalkjose.whatsappassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    SQLiteDatabase db;
    Button a2contbtn,a2forgbtn;
    EditText a2pin;
    Integer pin;
    boolean isInstalled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        PackageManager pm = getApplicationContext().getPackageManager();
        isInstalled = isPackageInstalled("com.whatsapp", pm);
        if(!isInstalled){
            Toast.makeText(this,"This app required Whatsapp installed..!",Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(this,"Whatsapp identified..!",Toast.LENGTH_SHORT).show();
        }

        db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS loginpin(email VARCHAR,pin VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM loginpin", null);
        if(c.getCount()==0){
            Toast.makeText(this,"Register here..!",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Main2Activity.this,Main3Activity.class);
            startActivity(intent);
            finish();
        }

        a2contbtn=(Button) findViewById(R.id.a2_cont_btn);
        a2forgbtn=(Button) findViewById(R.id.a2_forg_btn);
        a2pin=(EditText) findViewById(R.id.a2_pin);

        a2contbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    pin = Integer.parseInt(a2pin.getText().toString());
                    if(TextUtils.isEmpty(pin.toString())) {
                        a2pin.setError("Please enter PIN");
                        return;
                    }
                    Cursor c=db.rawQuery("SELECT * FROM loginpin where pin='"+pin.toString()+"'", null);
                    if(c.getCount()==0)
                    {
                        Toast.makeText(getApplicationContext(),"Wrong PIN",Toast.LENGTH_SHORT).show();
                        a2pin.setText("");
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Please enter valid inputs..!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        a2forgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Main2Activity.this,Resetpw.class);
                startActivity(in);
            }
        });
    }
    @Override
    public void onBackPressed() {

    }
    public boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}

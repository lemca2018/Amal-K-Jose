package com.amalkjose.whatsappassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaCodec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main3Activity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText a3mail,a3pin1,a3pin2;
    Button a3regbtn;
    String mail;
    Integer pin1,pin2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS loginpin(email VARCHAR,pin VARCHAR);");
        Cursor c = db.rawQuery("select * from loginpin", null);
        if(c.getCount() > 0){
            Intent intent=new Intent(Main3Activity.this,Main2Activity.class);
            startActivity(intent);
        }
        a3regbtn=(Button) findViewById(R.id.a3_reg_btn);
        a3mail=(EditText)findViewById(R.id.a3_email);
        a3pin1=(EditText)findViewById(R.id.a3_pin1);
        a3pin2=(EditText)findViewById(R.id.a3_pin2);

        a3regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mail = a3mail.getText().toString();
                    pin1 = Integer.parseInt(a3pin1.getText().toString());
                    pin2 = Integer.parseInt(a3pin2.getText().toString());

                    if(!emailValidator(mail)){
                        Toast.makeText(getApplicationContext(),"Invalid Email address..!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(pin1 - pin2 !=0){
                        Toast.makeText(getApplicationContext(),"PIN not matching..!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(pin1.toString().length()!=6){
                        Toast.makeText(getApplicationContext(),"PIN must have 6 digits..!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        db.execSQL("INSERT INTO loginpin VALUES('"+mail+"','"+pin1.toString()+"')");
                        Toast.makeText(getApplicationContext(),"PIN Generated..!",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Please enter valid inputs..!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(Main3Activity.this,Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onBackPressed() {

    }
}

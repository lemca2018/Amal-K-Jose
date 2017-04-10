package com.amalkjose.whatsappassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity{
    SQLiteDatabase db;
    Button a2contbtn,a2forgbtn;
    EditText a2pin;
    Integer pin;
    boolean isInstalled;
    private SparseIntArray mErrorString;
    private static final int REQUEST_PERMISSIONS = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mErrorString = new SparseIntArray();

        PackageManager pm = getApplicationContext().getPackageManager();
        isInstalled = isPackageInstalled("com.whatsapp", pm);
        if(!isInstalled){
            Toast.makeText(this,"This app required WhatsApp installed..!",Toast.LENGTH_SHORT).show();
            finish();
        }
        requestAppPermissions(new
                        String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        R.string.runtime_permissions_txt,
                        REQUEST_PERMISSIONS);

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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            Toast.makeText(this,"All permissions are required",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void requestAppPermissions(final String[] requestedPermissions,
                                      final int stringId, final int requestCode) {
        mErrorString.put(requestCode, stringId);
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                ActivityCompat.requestPermissions(Main2Activity.this, requestedPermissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, requestedPermissions, requestCode);
            }
        } else {
            onPermissionsGranted(requestCode);
        }
    }
    public void onPermissionsGranted(int requestCode){
    };
}


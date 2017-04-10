package com.amalkjose.whatsappassistant;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

public class Resetpw extends Activity implements OnClickListener{
    SQLiteDatabase db;
    Session session = null;
    ProgressDialog pdialog = null;
    Context context = null;
    String rec, subject, textMessage,pin;
    Boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpw);
        context = this;
        db = openOrCreateDatabase("wp_assistant",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS loginpin(email VARCHAR,pin VARCHAR);");
        Cursor c = db.rawQuery("select * from loginpin", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            pin=c.getString(c.getColumnIndex("pin"));
            rec=c.getString(c.getColumnIndex("email"));
        }

        Button login = (Button) findViewById(R.id.submit_btn);
        login.setOnClickListener(this);

    }
    @Override

    public void onClick(View v) {
        subject = "WhatsApp Assistant : PIN Reset";
        textMessage ="Hi User, Please note that your 6 digit pin number of WhatsApp Assistant is <br><br><center><b> <font color='red' size='20px'>"+pin+"</font></b></center>";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("info.waassistant@gmail.com", "sh1lj@0652");
            }
        });
        pdialog = ProgressDialog.show(context, "", "Sending Mail...", true);
        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("info.waassistant@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
                flag=true;
            } catch(MessagingException e) {
                flag=false;
                e.printStackTrace();
            } catch(Exception e) {
                flag=false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pdialog.dismiss();
            if(flag) {
                Toast toast = Toast.makeText(getApplicationContext(), "PIN number sent to registerd email id \n" + rec, Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }else {
                Toast.makeText(getApplicationContext(), "Network error..!", Toast.LENGTH_LONG).show();
            }
        }
    }
}

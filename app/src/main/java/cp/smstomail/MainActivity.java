package cp.smstomail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private MsgReceiver msgReceiver;
    String email_s = "";
    String passwd = "";
    String email_r = "";
    Button button;
    EditText send_e;
    EditText passwd_e ;
    EditText recevied_e ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.send_button);
        send_e = findViewById(R.id.send_email);
        passwd_e = findViewById(R.id.passwd_input);
        recevied_e = findViewById(R.id.recevied_email);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hit", "yes");
                prepare();
            }
        });
    }

    private void initString(){
            email_s = send_e.getText().toString();
            passwd = passwd_e.getText().toString();
            email_r = recevied_e.getText().toString();
    }

    private void prepare(){
               initString();
               intentFilter = new IntentFilter();
               intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
               msgReceiver = new MsgReceiver(email_s,passwd,email_r);
               registerReceiver(msgReceiver,intentFilter);

    }
}

class MsgReceiver extends BroadcastReceiver{
    private String send;
    private String receive;
    private String key;

    MsgReceiver(String send_mail,String passwd,String receive_mail)
    {
     send = send_mail;
     receive=receive_mail;
     key=passwd;
     send("init");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"receive msg",Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                String msg_content = "number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis();
                Log.d("send",msg_content);
                send(msg_content);
            }
        }
    }

    private void send(final String message_l){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.put("mail.host", "smtp.126.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.setProperty("mail.smtp.quitwait", "false");
                Session session = Session.getInstance(props, null);
                Log.d("apple","mail");
                try {
                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(send);
                    msg.setRecipients(Message.RecipientType.TO, receive);
                    msg.setSubject(message_l);
                    msg.setSentDate(new Date());
                    msg.setText(message_l);
                    Log.d("send","success");
                    Transport.send(msg,send,key);
                } catch (MessagingException mex) {
                    System.out.println("send failed, exception: " + mex);
                }
            }
        }).start();
    }
}
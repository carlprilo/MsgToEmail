package cp.smstomail;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

class MsgReceiver extends BroadcastReceiver {
    private String send;
    private String receive;
    private String key;
    private String smtp;

    MsgReceiver(String send_mail, String passwd, String receive_mail) {
        send = send_mail;
        receive = receive_mail;
        key = passwd;
        smtp = "smtp." + receive.split("@")[1];
        send(Build.MODEL, "init success!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "receive msg", Toast.LENGTH_SHORT).show();
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
                        + "   body:" + msg.getDisplayMessageBody();
                Log.d("send", msg_content);
                String subject = msg.getOriginatingAddress();
                String content = msg.getDisplayMessageBody() + "\nfrom: " + Build.MODEL;
                send(subject, content);
            }
        }
    }


    private void send(final String message_s, final String message_c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", smtp);
                props.put("mail.host", "smtp.126.com");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.setProperty("mail.smtp.quitwait", "false");
                Session session = Session.getInstance(props, null);
                Log.d("apple", "mail");
                try {
                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(send);
                    msg.setRecipients(Message.RecipientType.TO, receive);
                    msg.setSubject(message_s);
                    msg.setSentDate(new Date());
                    msg.setText(message_c);
                    Log.d("send", "success");
                    Transport.send(msg, send, key);
                } catch (MessagingException mex) {
                    System.out.println("send failed, exception: " + mex);
                }
            }
        }).start();
    }
}
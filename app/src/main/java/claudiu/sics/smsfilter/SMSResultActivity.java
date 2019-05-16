package claudiu.sics.smsfilter;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sisc.claudiu.smsfilter.R;

public class SMSResultActivity extends AppCompatActivity {

    final String CHANNEL_ID = "channel_id_1";
    final String CHANNEL_NAME = "channel_name_1";


    final String SMS_URI_ALL = "content://sms/"; // 所有短信
    final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
    final String SMS_URI_SEND = "content://sms/sent"; // 已发送
    final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
    final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
    final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
    final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表

    class WillView extends android.support.v7.widget.AppCompatTextView {
        public WillView(Context context) {
            super(context);
        }


    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        TextView tv = new TextView(this);
//        tv.setText(getSmsInPhone());
//
//        ScrollView sv = new ScrollView(this);
//        sv.addView(tv);
//
//        setContentView(sv);
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] fromColumns = {"body", "person"};
        int[] toViews = {R.id.body, R.id.from};

        Uri uri = Uri.parse(SMS_URI_ALL);

        String[] projection = new String[] { "_id", "address", "person",
                "body", "date", "type", };
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, "date desc"); // 获取手机内部短信
//
//
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
//                R.id.cc, cursor, fromColumns, toViews, 0);
//        System.out.println("Got " + findViewById(R.id.ll).toString());
//        ListView lv = (ListView) findViewById(R.id.ll);
//        TextView x = new TextView(SMSResultActivity.this);
//        x.setText("这个是代码添加的");
//        lv.addFooterView(x);
////        System.out.println("Got " + lv.toString());
////        lv.setAdapter(adapter);
//        setContentView(R.layout.activity_smsresult);
        ListView listView = new ListView(this);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.single_sms, cursor, fromColumns, toViews, 0);
        listView.setAdapter(adapter);
        setContentView(listView);
    }

    private List<String> getData(){

        List<String> data = new ArrayList<String>();
        data.add("测试数据1");
        data.add("测试数据2");
        data.add("测试数据3");
        data.add("测试数据4");

        return data;
    }





    private void testNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                .setContentTitle("通知测试")
                .setContentText("这里是通知内容")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SMSResultActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SMSResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        System.out.println(mNotificationManager);
    }

    @SuppressLint("LongLogTag")
    public String getSmsInPhone() {
        StringBuilder smsBuilder = new StringBuilder();

        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person",
                    "body", "date", "type", };
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    System.out.println(strbody);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
                    if (intType == 1) {
                        strType = "接收";
                    } else if (intType == 2) {
                        strType = "发送";
                    } else if (intType == 3) {
                        strType = "草稿";
                    } else if (intType == 4) {
                        strType = "发件箱";
                    } else if (intType == 5) {
                        strType = "发送失败";
                    } else if (intType == 6) {
                        strType = "待发送列表";
                    } else if (intType == 0) {
                        strType = "所以短信";
                    } else {
                        strType = "null";
                    }
                    if (strbody.indexOf("加") >0 ) {
                        smsBuilder.append("[ ");
                        smsBuilder.append(strAddress + ", ");
                        smsBuilder.append(intPerson + ", ");
                        smsBuilder.append(strbody + ", ");
                        smsBuilder.append(strDate + ", ");
                        smsBuilder.append(strType);
                        smsBuilder.append(" ]\n\n");
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }

            smsBuilder.append("getSmsInPhone has executed!");

        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        }

        return smsBuilder.toString();
    }

}

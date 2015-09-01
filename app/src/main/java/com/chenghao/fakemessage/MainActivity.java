package com.chenghao.fakemessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends Activity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button bt_send = null;
    private LinearLayout ll_date = null;
    private LinearLayout ll_time = null;
    private EditText et_phone;
    private EditText et_time;
    private EditText et_content;
    private LinearLayout ll_new;
    private LinearLayout ll_old;

    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private TextView tv_date;
    private TextView tv_time;

    //private final long TIME = 5000;

    private final int OLD_MESSAGE = 0;
    private final int NEW_MESSAGE = 1;
    private final int SEND_MESSAGE = 2;
    private int mode = OLD_MESSAGE;
    private RadioGroup rg_mode;
    private RadioButton rb1, rb2, rb3;

    private String str_phone;
    private String str_content = null;

    private long time = 0;

    private Notification note = null;

    private void init() {
        bt_send = (Button) findViewById(R.id.bt_send);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_time = (EditText) findViewById(R.id.et_time);
        et_content = (EditText) findViewById(R.id.et_content);
        ll_new = (LinearLayout) findViewById(R.id.ll_new_message);
        ll_old = (LinearLayout) findViewById(R.id.ll_old_message);
        ll_new.setVisibility(View.GONE);

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        update();

        rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
    }

    private void update() {
        tv_date.setText(dateFormat.format(calendar.getTime()));
        tv_time.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        bt_send.setOnClickListener(this);
        ll_date.setOnClickListener(this);
        ll_time.setOnClickListener(this);

        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb1.getId() == checkedId) {
                    mode = OLD_MESSAGE;
                    et_phone.setHint("来自哪个号码");
                    ll_old.setVisibility(View.VISIBLE);
                    ll_new.setVisibility(View.GONE);
                }
                else if (rb2.getId() == checkedId) {
                    mode = NEW_MESSAGE;
                    et_phone.setHint("来自哪个号码");
                    ll_new.setVisibility(View.VISIBLE);
                    ll_old.setVisibility(View.GONE);
                }
                else if (rb3.getId() == checkedId) {
                    mode = SEND_MESSAGE;
                    et_phone.setHint("发给哪个号码");
                    ll_old.setVisibility(View.VISIBLE);
                    ll_new.setVisibility(View.GONE);
                }
            }
        });
    }

    private void sendNotification() {
        NotificationManager noteMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
//        intent.setType("vnd.android-dir/mms-sms");
//        intent.setData(Uri.parse("content://mms-sms/conversations/10/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this).setTicker("您有一条新短信").setSmallIcon(R.drawable.message);
        note = builder.setContentIntent(pendingIntent).setContentTitle(str_phone).setContentText(str_content).build();
        //默认提示音
        note.defaults |= Notification.DEFAULT_SOUND;
        //默认震动模式
        note.defaults |= Notification.DEFAULT_VIBRATE;
        //默认LED提示模式
        note.defaults |= Notification.DEFAULT_LIGHTS;
        //重复上面的各种提示模式
        // note.flags |= Notification.FLAG_INSISTENT;
        // 点击查看后跳转
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        //状态栏无法清除
        note.flags |= Notification.FLAG_NO_CLEAR;

        noteMng.notify(110, note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            //return true;
            new AlertDialog.Builder(this)
                    .setTitle("关于")
                    .setMessage("部分机型可能无法使用。\n使用本软件若造成任何后果自负。\n\n微博：@友人帐上的友人A")
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("观看引导",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //实例化出一个SharedPreferences对象
                            SharedPreferences mySharedPreferences = getSharedPreferences("config_welcome", Activity.MODE_PRIVATE);
                            //实例化SharedPreferences.Editor对象
                            SharedPreferences.Editor editor = mySharedPreferences.edit();
                            //用putString的方法保存数据
                            editor.putString("isFirst", "first");
                            //提交当前数据
                            editor.commit();
                            finish();
                            startActivity(new Intent(MainActivity.this, ProductTourActivity.class));
                        }
                    }).create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        str_phone = et_phone.getText().toString().trim();
        str_content = et_content.getText().toString().trim();

        switch (v.getId()) {

            case R.id.bt_send:
                if (str_phone.equals("")) {
                    Toast.makeText(this, "请填写发送号码", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (str_content.equals("")) {
                    Toast.makeText(this, "请填写发送内容", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (mode == NEW_MESSAGE) {
                    if (et_time.getText().toString().trim().equals("")) {
                        Toast.makeText(this, "请填写延迟时间", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    time = Integer.valueOf(et_time.getText().toString().trim()) * 1000;
                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(time);
                                //创建一个接收对象
                                ContentResolver resolver = getContentResolver();
                                Uri uri = Uri.parse("content://sms");
                                ContentValues values = new ContentValues();
                                values.put("address", str_phone);
                                values.put("type", 1);
                                values.put("read", 0);
                                values.put("date", System.currentTimeMillis());
                                //values.put("service_center", "+8613010112500");
                                values.put("body", str_content);
                                resolver.insert(uri, values);
                                //消息栏提示
                                sendNotification();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }.start();
                    Toast.makeText(this, "短信将在" + et_time.getText().toString().trim() + "秒后发送", Toast.LENGTH_SHORT).show();
                } else {
                    ContentResolver resolver = getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    ContentValues values = new ContentValues();
                    values.put("address", str_phone);
                    if (mode == OLD_MESSAGE) {
                        values.put("type", 1);
                        values.put("read", 1);
                    } else {
                        values.put("type", 2);
                    }
                    values.put("date", calendar.getTimeInMillis());
                    //values.put("service_center", "+8613010112500");
                    values.put("body", str_content);
                    resolver.insert(uri, values);
                    Toast.makeText(this, "短信已生成", Toast.LENGTH_SHORT).show();
                }
                //关闭页面
                //this.finish();
                break;
            case R.id.ll_date:
                new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ll_time:
                new TimePickerDialog(this, this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
    }
}

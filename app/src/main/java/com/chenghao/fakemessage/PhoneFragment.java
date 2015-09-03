package com.chenghao.fakemessage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zhongya on 15/9/1.
 */
public class PhoneFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText et_phone;
    private RadioGroup rg_mode;
    private RadioButton rb1, rb2, rb3;

    private int mode = CallLog.Calls.OUTGOING_TYPE;

    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private TextView tv_date;
    private TextView tv_time;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_fragment, null);
        init(view);
        return view;
    }

    private void init(View view) {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        rg_mode = (RadioGroup) view.findViewById(R.id.rg_mode);
        rb1 = (RadioButton) view.findViewById(R.id.rb1);
        rb2 = (RadioButton) view.findViewById(R.id.rb2);
        rb3 = (RadioButton) view.findViewById(R.id.rb3);

        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        update();

        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb1.getId() == checkedId) {
                    mode = CallLog.Calls.OUTGOING_TYPE;
                } else if (rb2.getId() == checkedId) {
                    mode = CallLog.Calls.INCOMING_TYPE;
                } else if (rb3.getId() == checkedId) {
                    mode = CallLog.Calls.MISSED_TYPE;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_c:
                addCall(et_phone.getText().toString().trim(), mode);
                Toast.makeText(getActivity(), "记录已添加", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_a:
                new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.action_b:
                new TimePickerDialog(getActivity(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                break;
        }
    }

    //添加通话记录
    private void addCall(String phone, int type) {
        ContentValues content = new ContentValues();
        content.put(CallLog.Calls.TYPE, type);
        content.put(CallLog.Calls.NUMBER, phone);
        content.put(CallLog.Calls.DATE, calendar.getTimeInMillis());
        content.put(CallLog.Calls.NEW, "1");
        getActivity().getContentResolver().insert(CallLog.Calls.CONTENT_URI, content);
    }

    private void update() {
        tv_date.setText(dateFormat.format(calendar.getTime()));
        tv_time.setText(timeFormat.format(calendar.getTime()));
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

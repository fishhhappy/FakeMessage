package com.chenghao.fakemessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> items;
    private List<String> title;
    private MyFragmentAdapter adapter;
    private Toolbar mToolbar;
    private FloatingActionButton fab_a, fab_b, fab_c;

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        items = new ArrayList<Fragment>();
        title = new ArrayList<String>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        fab_a = (FloatingActionButton) findViewById(R.id.action_a);
        fab_b = (FloatingActionButton) findViewById(R.id.action_b);
        fab_c = (FloatingActionButton) findViewById(R.id.action_c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        final MessageFragment messageFragment = new MessageFragment();
        final PhoneFragment phoneFragment = new PhoneFragment();
        items.add(messageFragment);
        items.add(phoneFragment);
        title.add("伪造短信");
        title.add("伪造通话");

        fab_a.setOnClickListener(messageFragment);
        fab_b.setOnClickListener(messageFragment);
        fab_c.setOnClickListener(messageFragment);

        adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private FloatingActionButton fab_c = (FloatingActionButton) (MainActivity.this).findViewById(R.id.action_c);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fab_a.setOnClickListener(messageFragment);
                    fab_b.setOnClickListener(messageFragment);
                    fab_c.setOnClickListener(messageFragment);
                    fab_c.setTitle("发送短信");
                } else {
                    fab_a.setOnClickListener(phoneFragment);
                    fab_b.setOnClickListener(phoneFragment);
                    fab_c.setOnClickListener(phoneFragment);
                    fab_c.setTitle("增加记录");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("伪造短信"));
        tabLayout.addTab(tabLayout.newTab().setText("伪造通话"));
//        tabLayout.addTab(tabLayout.newTab().setText("目录"));
        tabLayout.setupWithViewPager(viewPager);
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
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
                    .setMessage("部分机型可能无法使用。\n使用本软件若造成任何后果自负")
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("观看引导", new DialogInterface.OnClickListener() {
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

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

}

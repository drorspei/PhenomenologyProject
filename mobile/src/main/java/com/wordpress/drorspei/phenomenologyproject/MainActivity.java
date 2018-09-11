package com.wordpress.drorspei.phenomenologyproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Log.d("MainActivity", "WRITE_EXTERNAL_STORAGE ok");
        }

        if (checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.RECEIVE_BOOT_COMPLETED}, 1);
        } else {
            Log.d("MainActivity", "RECEIVE_BOOT_COMPLETED ok");
        }

        new PhenomenonNotificationManager(this).setRandomTimeAll();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            List<Phenomenon> phenomena = FileUtils.loadPhenomena();
            ArrayList<String> titlesArr = new ArrayList<>(phenomena.size());

            for (Phenomenon phenomenon : phenomena) {
                titlesArr.add(phenomenon.title);
            }

            return PhenomenonFragment.newInstance(position, titlesArr);
        }

        @Override
        public int getCount() {
            return FileUtils.loadPhenomena().size() + 1;
        }

    }

    @Subscribe
    public void onInvalidateSectionsPagerAdapter(InvalidateSectionsPagerAdapter event) {
        Log.d("MainActivity", "onInvalidateSectionsPagerAdapter called");
        List<Phenomenon> phenomena = FileUtils.loadPhenomena();
        ArrayList<String> titlesArr = new ArrayList<>(phenomena.size());

        for (Phenomenon phenomenon : phenomena) {
            titlesArr.add(phenomenon.title);
        }

        SetFragmentTitlesEvent setFragmentTitlesEvent = new SetFragmentTitlesEvent();
        setFragmentTitlesEvent.titlesArr = titlesArr;
        EventBus.getDefault().post(setFragmentTitlesEvent);

        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    static class InvalidateSectionsPagerAdapter { }
    static class SetFragmentTitlesEvent { ArrayList<String> titlesArr = null; }
}

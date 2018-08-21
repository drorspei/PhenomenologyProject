package com.wordpress.drorspei.phenomenologyproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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

    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras != null){
            if (extras.containsKey("title") && extras.containsKey("button"))
            {
                String title = extras.getString("title");
                String button = extras.getString("button");
                Toast.makeText(this, String.format("Clicked: %s, %s", title, button),
                        Toast.LENGTH_LONG).show();
            }
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_TITLES = "titles";

        private int phenomenonIndex = -1;
        private List<String> titles = null;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<String> titles) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            args.putStringArrayList(ARG_TITLES, titles);

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            List<Phenomenon> phenomena = FileUtils.loadPhenomena();

            Bundle bundle = getArguments();
            if (bundle != null) {
                phenomenonIndex = bundle.getInt(ARG_SECTION_NUMBER);

                ArrayList<String> preTitles = bundle.getStringArrayList(ARG_TITLES);
                if (preTitles != null) {
                    titles = new ArrayList<>(preTitles);
                    titles.add(0, "");
                }
            }

            int[] spinnerSelections = new int[3];

            if (phenomenonIndex >= 0 && phenomenonIndex < phenomena.size()) {
                Phenomenon phenomenon = phenomena.get(phenomenonIndex);

                EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
                phenomenonTitle.setText(phenomenon.title);

                if (titles != null) {
                    titles.remove(phenomenonIndex + 1);
                }

                EditText phenomenonButton1 = rootView.findViewById(R.id.phenomenonButton1);
                phenomenonButton1.setText(phenomenon.button1);

                EditText phenomenonButton2 = rootView.findViewById(R.id.phenomenonButton2);
                phenomenonButton2.setText(phenomenon.button2);

                EditText phenomenonButton3 = rootView.findViewById(R.id.phenomenonButton3);
                phenomenonButton3.setText(phenomenon.button3);

                int i = 0;
                for (String conn : new String[] {phenomenon.conn1, phenomenon.conn2, phenomenon.conn3}) {
                    spinnerSelections[i++] = Math.max(titles.indexOf(conn), 0);
                }

                Button doNowBtn = rootView.findViewById(R.id.phenomenonDoNow);
                doNowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle(phenomenon.title)
                                .setContentText(phenomenon.title)
                                .setAutoCancel(true);

                        for (String button : new String[] {phenomenon.button1, phenomenon.button2, phenomenon.button3}) {
                            if (!button.isEmpty()) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("title", phenomenon.title);
                                intent.putExtra("button", button);

                                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1,
                                        intent, PendingIntent.FLAG_ONE_SHOT);
                                NotificationCompat.Action action = new NotificationCompat.Action
                                        .Builder(android.R.drawable.ic_menu_add, button, pendingIntent).build();
                                builder.addAction(action);
                            }
                        }

                        Notification notification = builder.build();

                        NotificationManager notificationManager = (NotificationManager) getContext()
                                .getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, notification);
                    }
                });
            }

            int[] spinnerIds = new int[] {R.id.phenomenonConn1, R.id.phenomenonConn2, R.id.phenomenonConn3};
            Context context = getContext();

            if (titles != null && context != null) {
                int i= 0;

                for (int spinnerId : spinnerIds) {
                    Spinner spinner = rootView.findViewById(spinnerId);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, titles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(spinnerSelections[i++]);
                }
            }

            Button saveBtn  = rootView.findViewById(R.id.phenomenonSave);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Phenomenon> phenomena = FileUtils.loadPhenomena();

                    if (phenomenonIndex < 0 || phenomenonIndex >= phenomena.size()) {
                        phenomena.add(new Phenomenon());
                        phenomenonIndex = phenomena.size() - 1;
                    }

                    Phenomenon phenomenon = phenomena.get(phenomenonIndex);

                    EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
                    phenomenon.title = phenomenonTitle.getText().toString();

                    EditText phenomenonButton1 = rootView.findViewById(R.id.phenomenonButton1);
                    phenomenon.button1 = phenomenonButton1.getText().toString();

                    EditText phenomenonButton2 = rootView.findViewById(R.id.phenomenonButton2);
                    phenomenon.button2= phenomenonButton2.getText().toString();

                    EditText phenomenonButton3 = rootView.findViewById(R.id.phenomenonButton3);
                    phenomenon.button3 = phenomenonButton3.getText().toString();

                    Spinner phenomenonConn1 = rootView.findViewById(R.id.phenomenonConn1);
                    phenomenon.conn1 = phenomenonConn1.getSelectedItem().toString();

                    Spinner phenomenonConn2 = rootView.findViewById(R.id.phenomenonConn2);
                    phenomenon.conn2 = phenomenonConn2.getSelectedItem().toString();

                    Spinner phenomenonConn3 = rootView.findViewById(R.id.phenomenonConn3);
                    phenomenon.conn3 = phenomenonConn3.getSelectedItem().toString();

                    phenomenon.starttime = 0;
                    phenomenon.endtime = 1;
                    phenomenon.howmany = 1;

                    try{
                        FileUtils.savePhenomena(phenomena);
                        Toast.makeText(getActivity(), "Saved",
                                Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new InvalidateSectionsPagerAdapter());
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Failed to save",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            List<Phenomenon> phenomena = FileUtils.loadPhenomena();
            ArrayList<String> titlesArr = new ArrayList<String>(phenomena.size());

            for (Phenomenon phenomenon : phenomena) {
                titlesArr.add(phenomenon.title);
            }

            return PlaceholderFragment.newInstance(position, titlesArr);
        }

        @Override
        public int getCount() {
            return FileUtils.loadPhenomena().size() + 1;
        }
    }

    public static class InvalidateSectionsPagerAdapter { }

    @Subscribe
    public void onInvalidateSectionsPagersAdapter(InvalidateSectionsPagerAdapter event) {
        mSectionsPagerAdapter.notifyDataSetChanged();
        System.out.println("notified dataset changed");
    }
}

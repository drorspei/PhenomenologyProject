package com.wordpress.drorspei.phenomenologyproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhenomenonFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TITLES = "titles";

    private int phenomenonIndex = -1;
    private List<String> titles = null;
    private String myTitle = null;

    public PhenomenonFragment() {
        EventBus.getDefault().register(this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    static PhenomenonFragment newInstance(int sectionNumber, ArrayList<String> titles) {
        PhenomenonFragment fragment = new PhenomenonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        args.putStringArrayList(ARG_TITLES, titles);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<Phenomenon> phenomena = FileUtils.loadPhenomena();

        Bundle bundle = getArguments();
        if (bundle != null) {
            phenomenonIndex = bundle.getInt(ARG_SECTION_NUMBER);

            ArrayList<String> preTitles = bundle.getStringArrayList(ARG_TITLES);
            if (preTitles != null) {
                titles = new ArrayList<>(preTitles);
                titles.add(0, "None");
            }
        }

        int[] spinnerSelections = new int[3];

        Spinner phenomenonStarttime = rootView.findViewById(R.id.phenomenonStarttime);
        phenomenonStarttime.setSelection(0);
        Spinner phenomenonEndtime = rootView.findViewById(R.id.phenomenonEndtime);
        phenomenonEndtime.setSelection(23);
        Spinner phenomenonHowmany = rootView.findViewById(R.id.phenomenonHowmany);
        phenomenonHowmany.setSelection(0);

        if (phenomenonIndex >= 0 && phenomenonIndex < phenomena.size()) {
            Phenomenon phenomenon = phenomena.get(phenomenonIndex);
            myTitle = phenomenon.title;

            EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
            phenomenonTitle.setText(phenomenon.title);

            if (titles != null) {
                if (phenomenonIndex + 1 < titles.size()) {
                    titles.remove(phenomenonIndex + 1);
                }
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
            doNowBtn.setOnClickListener(v -> {
                MainActivity mainActivity = (MainActivity) getContext();
                if (mainActivity != null) {
                    new PhenomenonNotificationManager(mainActivity).setNotification(phenomenonIndex, phenomenon,
                            new GregorianCalendar().getTimeInMillis());
                }
            });

            phenomenonStarttime.setSelection(phenomenon.starttime);
            phenomenonEndtime.setSelection(phenomenon.endtime);
            phenomenonHowmany.setSelection(phenomenon.howmany);
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
        saveBtn.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getContext();
            PhenomenonNotificationManager phenomenonNotificationManager = null;
            if (mainActivity != null) {
                phenomenonNotificationManager = new PhenomenonNotificationManager(mainActivity);
            }
            ArrayList<Phenomenon> phenomena1 = FileUtils.loadPhenomena();
            Phenomenon phenomenon;

            if (phenomenonIndex < 0 || phenomenonIndex >= phenomena1.size()) {
                phenomenon = new Phenomenon();
                phenomena1.add(phenomenon);
                phenomenonIndex = phenomena1.size() - 1;
            } else {
                phenomenon = phenomena1.get(phenomenonIndex);
                if (phenomenonNotificationManager != null) {
                    phenomenonNotificationManager.cancelNotification(phenomenonIndex, phenomenon);
                }
            }

            boolean isEmpty = true;

            EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
            phenomenon.title = phenomenonTitle.getText().toString();
            myTitle = phenomenon.title;
            //noinspection ConstantConditions
            isEmpty &= phenomenon.title.isEmpty();

            EditText phenomenonButton1 = rootView.findViewById(R.id.phenomenonButton1);
            phenomenon.button1 = phenomenonButton1.getText().toString();
            isEmpty &= phenomenon.button1.isEmpty();

            EditText phenomenonButton2 = rootView.findViewById(R.id.phenomenonButton2);
            phenomenon.button2= phenomenonButton2.getText().toString();
            isEmpty &= phenomenon.button2.isEmpty();

            EditText phenomenonButton3 = rootView.findViewById(R.id.phenomenonButton3);
            phenomenon.button3 = phenomenonButton3.getText().toString();
            isEmpty &= phenomenon.button3.isEmpty();

            Spinner phenomenonConn1 = rootView.findViewById(R.id.phenomenonConn1);
            phenomenon.conn1 = phenomenonConn1.getSelectedItem().toString();
            isEmpty &= phenomenon.conn1.equals("None");

            Spinner phenomenonConn2 = rootView.findViewById(R.id.phenomenonConn2);
            phenomenon.conn2 = phenomenonConn2.getSelectedItem().toString();
            isEmpty &= phenomenon.conn2.equals("None");

            Spinner phenomenonConn3 = rootView.findViewById(R.id.phenomenonConn3);
            phenomenon.conn3 = phenomenonConn3.getSelectedItem().toString();
            isEmpty &= phenomenon.conn3.equals("None");

            phenomenon.starttime = phenomenonStarttime.getSelectedItemPosition();
            phenomenon.endtime = phenomenonEndtime.getSelectedItemPosition();
            phenomenon.howmany = phenomenonHowmany.getSelectedItemPosition();

            if (isEmpty) {
                phenomena1.remove(phenomenonIndex);
            } else {
                if (phenomenonNotificationManager != null) {
                    phenomenonNotificationManager.setNotification(phenomenonIndex, phenomenon,
                            new GregorianCalendar().getTimeInMillis());
                }
            }

            try{
                FileUtils.savePhenomena(phenomena1);
                Toast.makeText(getActivity(), "Saved",
                        Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new MainActivity.InvalidateSectionsPagerAdapter());
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Failed to save",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        return rootView;
    }

    @Subscribe
    public void onSetFragmentTitlesEvent (MainActivity.SetFragmentTitlesEvent event) {
        Log.d("Fragment", String.format("got event %d", phenomenonIndex));

        View rootView = getView();
        Context context = getContext();
        if (rootView != null && context != null) {
            titles = new ArrayList<>(event.titlesArr);
            titles.add(0, "None");
            titles.remove(myTitle);

            for (int spinnerId : new int[] {R.id.phenomenonConn1, R.id.phenomenonConn2, R.id.phenomenonConn3}) {
                Spinner spinner = rootView.findViewById(spinnerId);
                String selectionText = spinner.getSelectedItem().toString();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, titles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setSelection(titles.indexOf(selectionText));
            }

            Log.d("Fragment", String.format("updated titles %d", phenomenonIndex));
        }
    }
}

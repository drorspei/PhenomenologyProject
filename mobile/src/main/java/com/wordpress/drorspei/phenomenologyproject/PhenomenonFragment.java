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
import com.wordpress.drorspei.phenomenologyproject.data.IPhenomenaDb;
import com.wordpress.drorspei.phenomenologyproject.data.IScheduleDb;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.IPhenomenonTimeDistribution;
import com.wordpress.drorspei.phenomenologyproject.timedistributions.PhenomenonTimePoissonDistribution;
import jsondbs.JsonPhenomenaDb;
import jsondbs.JsonScheduleDb;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhenomenonFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private Phenomenon phenomenon;

    public PhenomenonFragment() {
        EventBus.getDefault().register(this);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    static PhenomenonFragment newInstance(Phenomenon phenomenon) {
        PhenomenonFragment fragment = new PhenomenonFragment();
        Bundle args = new Bundle();
        args.putParcelable("phenomenon", phenomenon);

        fragment.setArguments(args);
        return fragment;
    }

    private static List<String> getTitles(String excludeTitle) {
        IPhenomenaDb phenomenaDb = new JsonPhenomenaDb();
        ArrayList<String> titles = new ArrayList<>();
        titles.add("No continuation");

        for (Phenomenon phenomenon : phenomenaDb.getAll()) {
            if (!phenomenon.title.equals(excludeTitle)) {
                titles.add(phenomenon.title);
            }
        }

        return titles;
    }

    private void updateContinuationSpinners(Context context, View rootView) {
        List<String> titles = getTitles(phenomenon != null ? phenomenon.title : null);
        String[] continuations = (phenomenon != null ? phenomenon.continuations : new String[] {"", "", ""});

        int continuationIndex = 0;
        for (int spinnerId : new int[] {R.id.phenomenonConn1, R.id.phenomenonConn2, R.id.phenomenonConn3}) {
            Spinner spinner = rootView.findViewById(spinnerId);
            String selectionText = continuations[continuationIndex];

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, titles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setSelection(titles.indexOf(selectionText));

            continuationIndex++;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            phenomenon = bundle.getParcelable("phenomenon");
        }

        Spinner phenomenonStarttime = rootView.findViewById(R.id.phenomenonStarttime);
        phenomenonStarttime.setSelection(0);
        Spinner phenomenonEndtime = rootView.findViewById(R.id.phenomenonEndtime);
        phenomenonEndtime.setSelection(23);
        Spinner phenomenonHowmany = rootView.findViewById(R.id.phenomenonHowmany);
        phenomenonHowmany.setSelection(0);

        if (phenomenon != null) {
            EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
            phenomenonTitle.setText(phenomenon.title);

            EditText phenomenonButton1 = rootView.findViewById(R.id.phenomenonButton1);
            phenomenonButton1.setText(phenomenon.buttons[0]);

            EditText phenomenonButton2 = rootView.findViewById(R.id.phenomenonButton2);
            phenomenonButton2.setText(phenomenon.buttons[1]);

            EditText phenomenonButton3 = rootView.findViewById(R.id.phenomenonButton3);
            phenomenonButton3.setText(phenomenon.buttons[2]);

            Button doNowBtn = rootView.findViewById(R.id.phenomenonDoNow);
            doNowBtn.setOnClickListener(v -> {
                MainActivity mainActivity = (MainActivity) getContext();
                if (mainActivity != null) {
                    NotificationService.showNotification(mainActivity, phenomenon);
                }
            });

            phenomenonStarttime.setSelection(phenomenon.starttime);
            phenomenonEndtime.setSelection(phenomenon.endtime);
            phenomenonHowmany.setSelection(phenomenon.howmany);
        }

        Context context = getContext();

        if (context != null) {
            updateContinuationSpinners(context, rootView);
        }

        Button saveBtn  = rootView.findViewById(R.id.phenomenonSave);
        saveBtn.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getContext();

            boolean isEmpty = true;

            EditText phenomenonTitle = rootView.findViewById(R.id.phenomenonTitle);
            String newTitle = phenomenonTitle.getText().toString();
            //noinspection ConstantConditions
            isEmpty &= newTitle.isEmpty();

            String[] newButtons = new String[] {"", "", ""};
            int buttonIndex = 0;

            for (int buttonId : new int[] {R.id.phenomenonButton1, R.id.phenomenonButton2, R.id.phenomenonButton3}) {
                EditText phenomenonButton = rootView.findViewById(buttonId);
                String newButton = phenomenonButton.getText().toString();
                newButtons[buttonIndex] = newButton;
                isEmpty &= newButton.isEmpty();
                buttonIndex++;
            }

            String[] newContinuations = new String[] {"No continuation", "No continuation", "No continuation"};
            int continuationIndex = 0;

            for (int continuationId : new int[] {R.id.phenomenonConn1, R.id.phenomenonConn2, R.id.phenomenonConn3}) {
                Spinner phenomenonContinuation = rootView.findViewById(continuationId);
                String continuation = phenomenonContinuation.getSelectedItem().toString();
                newContinuations[continuationIndex] = continuation;
                isEmpty &= continuation.equals("No continuation");
                continuationIndex++;
            }

            int newStarttime = phenomenonStarttime.getSelectedItemPosition();
            int newEndtime = phenomenonEndtime.getSelectedItemPosition();
            int newHowmany = phenomenonHowmany.getSelectedItemPosition();

            IPhenomenaDb phenomenaDb = new JsonPhenomenaDb();
            IScheduleDb scheduleDb = new JsonScheduleDb();

            if (phenomenon != null) {
                phenomenaDb.remove(phenomenon);
                scheduleDb.remove(phenomenon);
            }

            if (isEmpty) {
                phenomenon = null;
            } else {
                phenomenon = new Phenomenon(newTitle, newButtons, newContinuations,
                        newStarttime, newEndtime, newHowmany);

                // Add to database.
                phenomenaDb.add(phenomenon);

                // Show it now.
                NotificationService.showNotification(mainActivity, phenomenon);

                // Schedule it and update next notification time.
                IPhenomenonTimeDistribution timeDistribution = new PhenomenonTimePoissonDistribution();
                scheduleDb.add(phenomenon, timeDistribution.nextTime(phenomenon, new Date()));
                new PhenomenonNotificationManager(mainActivity).setNextNotification();
            }

            // Post event to update all fragments.
            EventBus.getDefault().post(new MainActivity.InvalidateSectionsPagerAdapter());
        });

        return rootView;
    }

    @Subscribe
    public void onSetFragmentTitlesEvent(MainActivity.SetFragmentTitlesEvent event) {
        Log.d("PhenomenologyProject", "onSetFragmentTitlesEvent called");

        View rootView = getView();
        Context context = getContext();
        if (rootView != null && context != null) {
            updateContinuationSpinners(context, rootView);
        }
    }
}

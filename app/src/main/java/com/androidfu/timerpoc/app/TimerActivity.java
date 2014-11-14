package com.androidfu.timerpoc.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TimerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements TimerView.OnTimerStateChanged {

        private static final String TAG = PlaceholderFragment.class.getSimpleName();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            final TimerView clock1 = (TimerView) rootView.findViewById(R.id.clock1);
            clock1.setListener(this);
            clock1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        clock1.setDuration(10000).setInterval(500).setRepetitions(3).start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
            final TimerView clock2 = (TimerView) rootView.findViewById(R.id.clock2);
            clock2.setListener(this);
            clock2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        clock2.setDuration(30000).setInterval(500).setRepetitions(1).start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
            final TimerView clock3 = (TimerView) rootView.findViewById(R.id.clock3);
            clock3.setListener(this);
            clock3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        clock3.setDuration(60000).setInterval(500).setRepetitions(3).start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
            final TimerView clock4 = (TimerView) rootView.findViewById(R.id.clock4);
            clock4.setListener(this);
            clock4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        clock4.setDuration(10000).setInterval(500).setRepetitions(10).start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onFinished(TimerView timerView) {
            timerView.setClickable(true);
            Log.v(TAG, String.format("TimerView %1$s Finished.", timerView.toString()));
        }

        @Override
        public void onStarted(TimerView timerView) {
            timerView.setClickable(false);
        }
    }
}

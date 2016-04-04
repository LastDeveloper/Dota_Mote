package com.example.phnx.dmote;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrefsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrefsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.mypreferences);

        }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
       // view.setBackgroundColor(getResources().getColor(android.R.color.black));
        addPreferencesFromResource(R.xml.mypreferences);
        return view;

    }
    */
}
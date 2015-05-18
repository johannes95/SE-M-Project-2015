package group8.com.application.UI.mainView.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group8.com.application.Application.Controller;
import group8.com.application.Application.Session;
import group8.com.application.Model.ConstantData;
import group8.com.application.R;
import group8.com.application.UI.DrivingView.DrivingView;
import group8.com.application.UI.MedalsView;
import group8.com.application.UI.RankingView;
import group8.com.application.UI.ResultsView;

public class start_menu_fragment extends Fragment implements View.OnClickListener {
    Button start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_menu_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        start = (Button) getView().findViewById(R.id.startButton);
        start.setOnClickListener(this);
        Controller.eventGetCustomToast(getActivity(), customLayout());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startButton:
                Controller.startGrading();

                Intent intent = new Intent(v.getContext(), DrivingView.class);
                startActivityForResult(intent, 0);
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menuViewMenuRanking:
                Intent rankingInt = new Intent(getView().getContext(), RankingView.class);
                startActivityForResult(rankingInt, 0);
                return true;
            case R.id.menuViewMenuGraphs:
                Intent graphsInt = new Intent(getView().getContext(), ResultsView.class);
                startActivityForResult(graphsInt, 0);
                return true;
            case R.id.menuViewMenuMedals:
                Intent intentMedals = new Intent(getView().getContext(), MedalsView.class);
                startActivityForResult(intentMedals, 0);
                return true;

            case R.id.menuViewMenuLogout:
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(getView().getContext());
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", "");
                edit.apply();
                Session.restart();

                //Goes back to the login view.
                Fragment fragment;
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                fragment = new login_reg_menu_fragment();
                transaction.replace(R.id.menuViewFrame, fragment, ConstantData.TAG_LOGINREGISTERFRAGMENT);
                transaction.commit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View customLayout(){

        LayoutInflater myInflator = getActivity().getLayoutInflater();
        return myInflator.inflate(R.layout.toast_layout, (ViewGroup) getView().findViewById(R.id.toast_layout_root));

    }
}

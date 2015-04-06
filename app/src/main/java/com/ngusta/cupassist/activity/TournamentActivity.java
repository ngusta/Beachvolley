package com.ngusta.cupassist.activity;

import com.google.gson.Gson;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TeamAdapter;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.domain.Tournament;
import com.ngusta.cupassist.io.TournamentListCache;
import com.ngusta.cupassist.service.TournamentService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class TournamentActivity extends Activity {

    public static final String TOURNAMENT_INTENT = "tournament";

    private Tournament tournament;

    public static void startActivity(Context context, Tournament tournament) {
        Intent intent = new Intent(context, TournamentActivity.class);
        String json = new Gson().toJson(tournament);
        intent.putExtra(TOURNAMENT_INTENT, json);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_view);
        Gson gson = new Gson();
        tournament = gson.fromJson(getIntent().getStringExtra("tournament"), Tournament.class);
        initInfo();
        new RequestTournamentDetailsTask().execute();
    }

    private void initInfo() {
        getActionBar().setTitle(tournament.getName());
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                TournamentListAdapter.getLevelIndicatorResource(tournament.getLevel()))));
        ((TextView) findViewById(R.id.club)).setText(tournament.getClub());
        ((TextView) findViewById(R.id.start_date)).setText(tournament.getFormattedStartDate());
    }

    private class RequestTournamentDetailsTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new TournamentService(TournamentActivity.this).loadTournamentDetails(tournament);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            initClazzSpinner();
            initCALink();
            updateTeams(tournament.getClazzes().get(0));
        }
    }

    private void initClazzSpinner() {
        Spinner clazzSpinner = (Spinner) findViewById(R.id.clazz_spinner);
        ArrayAdapter<Tournament.TournamentClazz> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(tournament.getClazzes());
        clazzSpinner.setAdapter(adapter);
        clazzSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTeams((Tournament.TournamentClazz) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initCALink() {
        final ImageButton button = (ImageButton) findViewById(R.id.open_cupassist);
        button.setImageResource(R.drawable.link_icon_enabled);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri cupAssistUri = Uri.parse(TournamentListCache.CUP_ASSIST_TOURNAMENT_URL + tournament.getRegistrationUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
                startActivity(browserIntent);
            }
        });
    }

    private void updateTeams(Tournament.TournamentClazz clazz) {
        ListView teamListView = (ListView) findViewById(R.id.teamList);
        List<Tournament.TeamGroupPosition> teams = tournament.getTeamGroupPositionsForClazz(clazz);
        final ArrayAdapter<Tournament.TeamGroupPosition> adapter = new TeamAdapter(this, R.layout.team_list_item, teams, clazz.getMaxNumberOfTeams());
        teamListView.setAdapter(adapter);
    }
}

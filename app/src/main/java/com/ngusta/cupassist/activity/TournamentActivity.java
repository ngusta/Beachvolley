package com.ngusta.cupassist.activity;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ngusta.beachvolley.domain.Team;
import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.R;
import com.ngusta.cupassist.adapters.TeamAdapter;
import com.ngusta.cupassist.adapters.TournamentListAdapter;
import com.ngusta.cupassist.io.TournamentListDownloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TournamentActivity extends AppCompatActivity {

    public static final String TOURNAMENT_INTENT = "tournament";

    private Toolbar mToolbar;

    private View mProgressContainer;

    private RecyclerView mTeamList;

    private boolean mListShown;

    private Tournament mTournament;

    private Spinner mClazzSpinner;

    public static void startActivity(Context context, Tournament tournament) {
        Intent intent = new Intent(context, TournamentActivity.class);
        intent.putExtra(TOURNAMENT_INTENT, tournament);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_view);
        mTournament = (Tournament) getIntent().getSerializableExtra(TOURNAMENT_INTENT);
        mProgressContainer = findViewById(R.id.progressContainer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mTeamList = (RecyclerView) findViewById(R.id.teamList);
        mTeamList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mTeamList.setLayoutManager(linearLayoutManager);
        mTeamList.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                mClazzSpinner.setSelection(Math.max(0, mClazzSpinner.getSelectedItemPosition() - 1));
                updateAfterClazzChange();
            }

            @Override
            public void onSwipeRight() {
                mClazzSpinner.setSelection(Math.min(mTournament.getClazzes().size() - 1, mClazzSpinner.getSelectedItemPosition() + 1));
                updateAfterClazzChange();
            }
        });
        setListShown(false, false);
        initInfo();
        initCALink();
        initClazzSpinner();
        MyApplication.getTeamsFirebase().child(mTournament.uniqueIdentifier()).addValueEventListener(getTeamsListener());
    }

    private void updateAfterClazzChange() {
        Tournament.TournamentClazz clazz = (Tournament.TournamentClazz) mClazzSpinner.getSelectedItem();
        setTeamsInfo(clazz);
        updateTeams(clazz);
    }

    private ValueEventListener getTeamsListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Team> teams = new ArrayList<>();
                for (DataSnapshot teamSnapshot : snapshot.getChildren()) {
                    teams.add(teamSnapshot.getValue(Team.class));
                }
                mTournament.setTeams(teams);
                updateAfterClazzChange();
                setListShown(true, true);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        };
    }

    private void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mTeamList
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mTeamList.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer
                        .startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                mTeamList.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mTeamList.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mTeamList.setVisibility(View.GONE);
        }
    }

    private void initInfo() {
        ActionBar appBar = getSupportActionBar();

        if (appBar != null) {
            appBar.setTitle(mTournament.getName());
            appBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                    TournamentListAdapter.getLevelIndicatorResource(mTournament))));
        }

        ((TextView) findViewById(R.id.club)).setText(mTournament.getClub());
        ((TextView) findViewById(R.id.start_date)).setText(mTournament.getFormattedStartDate());
    }

    private void initClazzSpinner() {
        mClazzSpinner = (Spinner) findViewById(R.id.clazz_spinner);
        ArrayAdapter<Tournament.TournamentClazz> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(mTournament.getClazzes());
        mClazzSpinner.setAdapter(adapter);
        mClazzSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Tournament.TournamentClazz clazz = (Tournament.TournamentClazz) parent.getSelectedItem();
                setTeamsInfo(clazz);
                updateTeams(clazz);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initCALink() {
        final ImageButton button = (ImageButton) findViewById(R.id.open_cupassist);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri cupAssistUri = Uri.parse(TournamentListDownloader.CUP_ASSIST_TOURNAMENT_URL + mTournament.getRegistrationUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, cupAssistUri);
                startActivity(browserIntent);
            }
        });
    }

    private void setTeamsInfo(Tournament.TournamentClazz clazz) {
        ((TextView) findViewById(R.id.teams_info)).setText(
                getResources().getString(R.string.registered) + " " + mTournament.getNumberOfCompleteTeamsForClazz(clazz) + "/" + clazz
                        .getMaxNumberOfTeams());
    }

    private void updateTeams(Tournament.TournamentClazz clazz) {
        List<Tournament.TeamGroupPosition> teams = mTournament.getTeamGroupPositionsForClazz(clazz);
        TeamAdapter teamAdapter = new TeamAdapter(teams, clazz.getMaxNumberOfTeams());
        mTeamList.setAdapter(teamAdapter);
    }

    private class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;

            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}

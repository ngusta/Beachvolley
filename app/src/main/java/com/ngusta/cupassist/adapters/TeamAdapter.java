package com.ngusta.cupassist.adapters;

import com.ngusta.beachvolley.domain.Tournament;
import com.ngusta.cupassist.R;

import android.graphics.Color;
import android.support.v4.view.NestedScrollingChild;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamHolder> {

    private final List<Tournament.TeamGroupPosition> teams;

    private final int maxNumberOfTeams;

    public TeamAdapter(List<Tournament.TeamGroupPosition> teams, int maxNumberOfTeams) {
        this.teams = teams;
        this.maxNumberOfTeams = maxNumberOfTeams;
    }

    @Override
    public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_list_item, parent, false);
        return new TeamHolder(v);
    }

    @Override
    public void onBindViewHolder(TeamHolder holder, int position) {
        Tournament.TeamGroupPosition team = teams.get(position);
        if (team.team.hasPaid()) {
            holder.team.setBackgroundColor(0x2000c800);
        } else {
            holder.team.setBackgroundColor(Color.WHITE);
        }
        if (position < maxNumberOfTeams) {
            holder.group.setText("" + team.group);
        } else {
            holder.group.setText("");
        }
        holder.playerAName.setText(team.team.getPlayerA().getName());
        holder.playerBName.setText(team.team.getPlayerB().getName());
        holder.entryPoints.setText(String.valueOf(team.team.getEntryPoints()));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public class TeamHolder extends RecyclerView.ViewHolder {

        RelativeLayout team;
        TextView group;
        TextView playerAName;
        TextView playerBName;
        TextView entryPoints;

        public TeamHolder(View itemView) {
            super(itemView);
            team = (RelativeLayout) itemView.findViewById(R.id.team);
            group = (TextView) itemView.findViewById(R.id.group);
            playerAName = (TextView) itemView.findViewById(R.id.playerA_name);
            playerBName = (TextView) itemView.findViewById(R.id.playerB_name);
            entryPoints = (TextView) itemView.findViewById(R.id.entry_points);
        }
    }
}

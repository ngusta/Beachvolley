package com.ngusta.beachvolley.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

import static java.lang.Double.compare;

public class Team implements Serializable, Comparable<Team> {

    private Player playerA;

    private Player playerB;

    private Date registrationTime;

    private Clazz clazz;

    private boolean paid;

    private boolean completeTeam;

    public Team() {
    }

    public Team(Player playerA, Player playerB, Date registrationTime, Clazz clazz, boolean paid) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.registrationTime = registrationTime;
        this.clazz = clazz;
        this.completeTeam = true;
        this.paid = paid;
    }

    public Team(Player playerA, Date registrationDate, Clazz clazz, boolean paid) {
        this.playerA = playerA;
        this.playerB = new Player("Partner sökes", "", "");
        this.registrationTime = registrationDate;
        this.clazz = clazz;
        this.completeTeam = false;
        this.paid = paid;
    }

    @JsonIgnore
    public String getNames() {
        return playerA.getName() + "/" + playerB.getName();
    }

    @JsonIgnore
    public String getClubs() {
        return playerA.getClub() + "/" + playerB.getClub();
    }

    @JsonIgnore
    public int getEntryPoints() {
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getEntryPoints() * 0.1) + playerA.getMixEntryPoints() + Math.round(playerB.getEntryPoints() * 0.1)
                    + playerB.getMixEntryPoints());
        }
        return playerA.getEntryPoints() + playerB.getEntryPoints();
    }

    @JsonIgnore
    public int getRankingPoints() {
        if (clazz == Clazz.MIXED) {
            return (int) (Math.round(playerA.getRankingPoints() * 0.1) + playerA.getMixRankingPoints() + Math
                    .round(playerB.getRankingPoints() * 0.1) + playerB.getMixRankingPoints());
        }
        return playerA.getRankingPoints() + playerB.getRankingPoints();
    }

    private int getHighestEntryPoints() {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixEntryPoints(), playerB.getMixEntryPoints());
        }
        return Math.max(playerA.getEntryPoints(), playerB.getEntryPoints());
    }

    private int getHighestRankingPoints() {
        if (clazz == Clazz.MIXED) {
            return Math.max(playerA.getMixRankingPoints(), playerB.getMixRankingPoints());
        }
        return Math.max(playerA.getRankingPoints(), playerB.getRankingPoints());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public boolean isCompleteTeam() {
        return completeTeam;
    }

    public boolean hasPaid() {
        return paid;
    }

    @Override
    public int compareTo(Team another) {
        if (completeTeam && !another.completeTeam) {
            return -1;
        } else if (!completeTeam && another.completeTeam) {
            return 1;
        }

        int cmp = compare(another.getEntryPoints(), this.getEntryPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getRankingPoints(), this.getRankingPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getHighestEntryPoints(), this.getHighestEntryPoints());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compare(another.getHighestRankingPoints(), this.getHighestRankingPoints());
        return cmp;
    }

    @Override
    public String toString() {
        return playerA.getName() + "/" + playerB.getName() +
                ", entry: " + getEntryPoints() +
                ", registrationTime=" + registrationTime +
                ", clazz=" + clazz +
                ", completeTeam=" + completeTeam +
                ", paid=" + paid;
    }
}

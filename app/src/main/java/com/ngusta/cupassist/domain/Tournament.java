package com.ngusta.cupassist.domain;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tournament implements Serializable {
    public static final String TAG = Tournament.class.getSimpleName();
    private Date startDate;
    private CompetitionPeriod competitionPeriod;
    private String club;
    private String name;
    private String url;
    private Level level;
    private String levelString;
    private List<Clazz> clazzes;
    private String registrationUrl;
    private Map<Clazz, Integer> maxNumberOfTeams;
    private Map<Clazz, List<Team>> teams;

    public Tournament(Date startDate, String period, String club, String name, String url, String level, String clazzes) {
        this.startDate = startDate;
        this.competitionPeriod = CompetitionPeriod.findByName(period);
        this.club = club;
        this.name = name;
        this.url = url;
        this.levelString = level;
        this.level = Level.parse(level);
        this.clazzes = parseClazzes(clazzes);
    }

    private List<Clazz> parseClazzes(String clazzes) {
        ArrayList<Clazz> parsedClazzes = new ArrayList<>();
        if (clazzes != null) {
            String[] clazzArray = clazzes.split(", ");
            for (String clazzString : clazzArray) {
                parsedClazzes.add(Clazz.parse(clazzString));
            }
        }
        return parsedClazzes;
    }

    public String getFormattedStartDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(startDate);
    }

    public List<Team> getSeededTeamsForClazz(Clazz clazz) {
        if (getTeams().get(clazz) == null) {
            return Collections.emptyList();
        }
        List<Team> seeded = getTeams().get(clazz);
        Collections.sort(seeded, level.getComparator());
        int maxNumberOfTeams = getMaxNumberOfTeamsForClazz(clazz);
        if (seeded.size() > maxNumberOfTeams) {
            seeded = seeded.subList(0, maxNumberOfTeams);
        }
        Collections.sort(seeded);
        return seeded;
    }

    public int getNumberOfGroupsForClazz(Clazz clazz) {
        int maxNumberOfTeams = getMaxNumberOfTeamsForClazz(clazz);
        if (maxNumberOfTeams == 12) {
            return 4;
        } else {
            return maxNumberOfTeams / 4;
        }
    }

    public CompetitionPeriod getCompetitionPeriod() {
        return competitionPeriod;
    }

    public String getClub() {
        return club;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLevelString() {
        return levelString;
    }

    public List<Clazz> getClazzes() {
        return clazzes != null ? clazzes : Collections.<Clazz>emptyList();
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public int getMaxNumberOfTeamsForClazz(Clazz clazz) {
        if (maxNumberOfTeams.containsKey(clazz)) {
            return maxNumberOfTeams.get(clazz);
        } else {
            int guess = this.level == Level.OPEN ? 16 : 12;
            Log.i(TAG, String.format("Guessing number of teams in '%s' for clazz %s: %d", name, clazz, guess));
            return guess;
        }
    }

    public String getClassesWithMaxNumberOfTeamsString() {
        if (getClazzes().isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Clazz clazz : getClazzes()) {
            sb.append(clazz.toString()).append("(").append(getMaxNumberOfTeamsForClazz(clazz)).append(")").append(", ");
        }
        return sb.delete(sb.length() - 2, sb.length()).append("]").toString();
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    public Map<Clazz, List<Team>> getTeams() {
        return teams != null ? teams : Collections.<Clazz, List<Team>>emptyMap();
    }

    public List<TeamGroupPosition> getTeamGroupPositionsForClazz(Clazz clazz) {
        int group = 0;
        int operator = 1;
        int numberOfGroups = getNumberOfGroupsForClazz(clazz);
        List<TeamGroupPosition> teamGroupPositions = new ArrayList<>(getMaxNumberOfTeamsForClazz(clazz));

        for (Team team : getSeededTeamsForClazz(clazz)) {
            group += operator;
            if (group == (numberOfGroups + 1)) {
                group = numberOfGroups;
                operator = -1;
            } else if (group == 0) {
                group = 1;
                operator = 1;
            }
            teamGroupPositions.add(new TeamGroupPosition(group, team));
        }

        return teamGroupPositions;
    }

    public void setMaxNumberOfTeams(Map<Clazz, Integer> maxNumberOfTeams) {
        this.maxNumberOfTeams = new HashMap<>(maxNumberOfTeams);
    }

    public void setTeams(List<Team> teamList) {
        teams = new HashMap<>();
        for (Team team : teamList) {
            if (!teams.containsKey(team.getClazz())) {
                teams.put(team.getClazz(), new ArrayList<Team>());
            }
            teams.get(team.getClazz()).add(team);
        }
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "startDate=" + getFormattedStartDate() +
                ", period='" + competitionPeriod.getName() + '\'' +
                ", club='" + club + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", level='" + level + '\'' +
                ", classes='" + clazzes + '\'' +
                ", redirectUrl='" + registrationUrl + '\'' +
                '}';
    }

    public enum Level {
        OPEN, OPEN_GREEN, CHALLENGER, SWEDISH_BEACH_TOUR, YOUTH, VETERAN, UNKNOWN;

        private static final Comparator<Team> REGISTRATION_TIME_COMPARATOR = new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                if (lhs.getRegistrationTime() == null) {
                    return rhs.getRegistrationTime() == null ? 0 : -1;
                }
                return lhs.getRegistrationTime().compareTo(rhs.getRegistrationTime());
            }
        };

        private static final Comparator<Team> ENTRY_POINTS_COMPARATOR = new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                return lhs.compareTo(rhs);
            }
        };

        public static Level parse(String levelString) {
            if (levelString == null) {
                return UNKNOWN;
            }
            if (levelString.contains("Open (Svart)")) {
                return OPEN;
            } else if (levelString.contains("Open (Grön)")) {
                return OPEN_GREEN;
            } else if (levelString.contains("Challenger")) {
                return CHALLENGER;
            } else if (levelString.contains("Swedish Beach Tour")) {
                return SWEDISH_BEACH_TOUR;
            } else if (levelString.contains("Veteran")) {
                return VETERAN;
            } else if (levelString.contains("Ungdom") || levelString.contains("3-beach") || levelString.contains("Kidsvolley")) {
                return YOUTH;
            }
            return UNKNOWN;
        }

        public Comparator<Team> getComparator() {
            switch (this) {
                case OPEN:
                case OPEN_GREEN:
                    return REGISTRATION_TIME_COMPARATOR;
                default:
                    return ENTRY_POINTS_COMPARATOR;
            }
        }
    }

    public static class TeamGroupPosition {
        public final int group;
        public final Team team;

        public TeamGroupPosition(int group, Team team) {
            this.group = group;
            this.team = team;
        }
    }
}
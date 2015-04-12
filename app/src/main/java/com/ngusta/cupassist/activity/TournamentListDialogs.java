package com.ngusta.cupassist.activity;

import com.ngusta.cupassist.R;
import com.ngusta.cupassist.domain.Clazz;
import com.ngusta.cupassist.domain.Tournament;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TournamentListDialogs {

    private static final String CLAZZ_PREFERENCES_KEY = "defaultClazzIndexes";

    private static final String LEVEL_PREFERENCES_KEY = "defaultLevelIndexes";

    private static final int MEN_INDEX = 0;

    private static final int WOMEN_INDEX = 1;

    private static final int MIXED_INDEX = 2;

    private static final int YOUTH_INDEX = 3;

    private static final int VETERAN_INDEX = 4;

    private static final int OPEN_GREEN_INDEX = 0;

    private static final int OPEN_BLACK_INDEX = 1;

    private static final int CHALLENGER_INDEX = 2;

    private static final int SBT_INDEX = 3;

    private static final int YOUTH_LEVEL_INDEX = 4;

    private static final int VETERAN_LEVEL_INDEX = 5;

    static List<Clazz> getDefaultClazzes(SharedPreferences preferences) {
        Set<String> savedClazzIndexes = getSavedClazzPreferences(preferences);
        ArrayList<Clazz> clazzesToFilter = new ArrayList<>();
        for (String clazzIndex : savedClazzIndexes) {
            int i = Integer.parseInt(clazzIndex);
            addClazzesFromIndex(clazzesToFilter, i);
        }
        return clazzesToFilter;
    }

    private static Set<String> getSavedClazzPreferences(SharedPreferences preferences) {
        Set<String> defaultClazzIndexes = new HashSet<>();
        defaultClazzIndexes.add("" + MEN_INDEX);
        defaultClazzIndexes.add("" + WOMEN_INDEX);
        defaultClazzIndexes.add("" + MIXED_INDEX);
        return preferences.getStringSet(CLAZZ_PREFERENCES_KEY, defaultClazzIndexes);
    }

    private static void addClazzesFromIndex(ArrayList<Clazz> clazzesToFilter, int i) {
        switch (i) {
            case MEN_INDEX:
                clazzesToFilter.add(Clazz.MEN);
                break;
            case WOMEN_INDEX:
                clazzesToFilter.add(Clazz.WOMEN);
                break;
            case MIXED_INDEX:
                clazzesToFilter.add(Clazz.MIXED);
                break;
            case YOUTH_INDEX:
                clazzesToFilter.addAll(Clazz.getYouthClazzes());
                break;
            case VETERAN_INDEX:
                clazzesToFilter.addAll(Clazz.getVeteranClazzes());
                break;

        }
    }

    static AlertDialog.Builder createClazzFilterDialog(final TournamentListActivity activity, final SharedPreferences preferences) {
        Set<String> savedClazzIndexes = getSavedClazzPreferences(preferences);
        final ArrayList<Integer> selectedItemsIndexList = new ArrayList<>();
        boolean[] defaultSelected = {false, false, false, false, false};
        for (String clazzIndex : savedClazzIndexes) {
            int i = Integer.parseInt(clazzIndex);
            defaultSelected[i] = true;
            selectedItemsIndexList.add(i);
        }
        String[] clazzNames = {"Herr", "Dam", "Mixed", "Ungdom", "Veteran"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.show_clazzes)
                .setMultiChoiceItems(clazzNames, defaultSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItemsIndexList.add(which);
                        } else if (selectedItemsIndexList.contains(which)) {
                            selectedItemsIndexList.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.show, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<Clazz> clazzesToFilter = new ArrayList<>();
                        HashSet<String> clazzIndexesToSave = new HashSet<>();
                        for (Integer selectedClazzIndex : selectedItemsIndexList) {
                            clazzIndexesToSave.add(selectedClazzIndex.toString());
                            addClazzesFromIndex(clazzesToFilter, selectedClazzIndex);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(CLAZZ_PREFERENCES_KEY, clazzIndexesToSave);
                        editor.apply();
                        activity.setClazzesToFilter(clazzesToFilter);
                        activity.updateList();
                    }
                });
        return dialogBuilder;
    }

    static List<Tournament.Level> getDefaultLevels(SharedPreferences preferences) {
        Set<String> savedLevelIndexes = getSavedLevelPreferences(preferences);
        ArrayList<Tournament.Level> levelsToFilter = new ArrayList<>();
        for (String levelIndex : savedLevelIndexes) {
            int i = Integer.parseInt(levelIndex);
            addLevelsFromIndex(levelsToFilter, i);
        }
        return levelsToFilter;
    }

    private static Set<String> getSavedLevelPreferences(SharedPreferences preferences) {
        Set<String> defaultLevelIndexes = new HashSet<>();
        defaultLevelIndexes.add("" + OPEN_GREEN_INDEX);
        defaultLevelIndexes.add("" + OPEN_BLACK_INDEX);
        defaultLevelIndexes.add("" + CHALLENGER_INDEX);
        defaultLevelIndexes.add("" + SBT_INDEX);
        return preferences.getStringSet(LEVEL_PREFERENCES_KEY, defaultLevelIndexes);
    }

    private static void addLevelsFromIndex(ArrayList<Tournament.Level> levelsToFilter, int i) {
        switch (i) {
            case OPEN_GREEN_INDEX:
                levelsToFilter.add(Tournament.Level.OPEN_GREEN);
                break;
            case OPEN_BLACK_INDEX:
                levelsToFilter.add(Tournament.Level.OPEN);
                break;
            case CHALLENGER_INDEX:
                levelsToFilter.add(Tournament.Level.CHALLENGER);
                break;
            case SBT_INDEX:
                levelsToFilter.add(Tournament.Level.SWEDISH_BEACH_TOUR);
                break;
            case YOUTH_LEVEL_INDEX:
                levelsToFilter.add(Tournament.Level.YOUTH);
                levelsToFilter.add(Tournament.Level.UNKNOWN);
                break;
            case VETERAN_LEVEL_INDEX:
                levelsToFilter.add(Tournament.Level.VETERAN);
                levelsToFilter.add(Tournament.Level.UNKNOWN);
                break;
        }
    }

    static AlertDialog.Builder createLevelFilterDialog(final TournamentListActivity activity, final SharedPreferences preferences) {
        Set<String> savedLevelIndexes = getSavedLevelPreferences(preferences);
        final ArrayList<Integer> selectedItemsIndexList = new ArrayList<>();
        boolean[] defaultSelected = {false, false, false, false, false, false};
        for (String levelIndex : savedLevelIndexes) {
            int i = Integer.parseInt(levelIndex);
            defaultSelected[i] = true;
            selectedItemsIndexList.add(i);
        }
        String[] levelNames = {"Open grön", "Open svart", "Challenger", "Swedish Beach Tour", "Ungdom", "Veteran"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.show_levels)
                .setMultiChoiceItems(levelNames, defaultSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItemsIndexList.add(which);
                        } else if (selectedItemsIndexList.contains(which)) {
                            selectedItemsIndexList.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton(R.string.show, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<Tournament.Level> levelsToFilter = new ArrayList<>();
                        HashSet<String> levelIndexesToSave = new HashSet<>();
                        for (Integer selectedLevelIndex : selectedItemsIndexList) {
                            levelIndexesToSave.add(selectedLevelIndex.toString());
                            addLevelsFromIndex(levelsToFilter, selectedLevelIndex);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(LEVEL_PREFERENCES_KEY, levelIndexesToSave);
                        editor.apply();
                        activity.setLevelsToFilter(levelsToFilter);
                        activity.updateList();
                    }
                });
        return dialogBuilder;
    }
}
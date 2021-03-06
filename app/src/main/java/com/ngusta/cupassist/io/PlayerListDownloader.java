package com.ngusta.cupassist.io;

import com.ngusta.beachvolley.domain.CompetitionPeriod;
import com.ngusta.beachvolley.domain.Player;
import com.ngusta.beachvolley.domain.PlayerList;
import com.ngusta.cupassist.parser.PlayerListParser;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerListDownloader {

    private static String CUP_ASSIST_PLAYERS_RANKING_BASE_URL = "http://www.cupassist.com/pa/ranking_beach/index.php";

    private static String CUP_ASSIST_PLAYERS_RANKING_MEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=H";

    private static String CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=D";

    private static String CUP_ASSIST_PLAYERS_RANKING_MIXED_URL = "http://www.cupassist.com/pa/ranking_beach/visrank.php?k=M";

    private PlayerListParser playerListParser;

    private SourceCodeRequester sourceCodeRequester;

    private PlayerList playerList;

    public PlayerListDownloader() {
        playerListParser = new PlayerListParser();
        sourceCodeRequester = new SourceCodeRequester();
    }

    public Map<String, Player> getPlayers() {
        if (playerList != null) {
            return playerList.getPlayers();
        }
        try {
            sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_BASE_URL);
            playerList = new PlayerList(CompetitionPeriod.findPeriodByDate(new Date()));

            Set<Player> men = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MEN_URL));
            Set<Player> women = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_WOMEN_URL));
            Set<Player> mix = playerListParser.parsePlayerList(sourceCodeRequester.getSourceCode(CUP_ASSIST_PLAYERS_RANKING_MIXED_URL));

            Map<String, Player> players = new HashMap<>();
            for (Player man : men) {
                if (!players.containsKey(man.uniqueIdentifier())) {
                    players.put(man.uniqueIdentifier(), man);
                }
            }
            for (Player woman : women) {
                if (!players.containsKey(woman.uniqueIdentifier())) {
                    players.put(woman.uniqueIdentifier(), woman);
                }
            }

            for (Player mixPlayer : mix) {
                Player player = players.get(mixPlayer.uniqueIdentifier());
                if (player != null) {
                    player.setMixEntryPoints(mixPlayer.getEntryPoints());
                    player.setMixRankingPoints(mixPlayer.getRankingPoints());
                } else {
                    mixPlayer.setMixEntryPoints(mixPlayer.getEntryPoints());
                    mixPlayer.setMixRankingPoints(mixPlayer.getRankingPoints());
                    mixPlayer.setEntryPoints(0);
                    mixPlayer.setRankingPoints(0);
                    players.put(mixPlayer.uniqueIdentifier(), mixPlayer);
                }
            }
            playerList.setPlayers(players);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return playerList.getPlayers();
    }
}

package net.zomis.spring.games.generic.v2;

import net.zomis.spring.games.generic.PlayerInGame;
import net.zomis.spring.games.generic.TokenGenerator;
import net.zomis.spring.games.messages.GameInfo;
import net.zomis.spring.games.messages.JoinGameResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyGame<G> {

    private final String id;
    private final List<PlayerInGame> playerKeys = Collections.synchronizedList(new ArrayList<>());
    private final TokenGenerator tokenGenerator;
    private final GameHelper2<G> gameHelper;
    private final Object gameConfiguration;

    public LobbyGame(String id, TokenGenerator tokenGenerator, GameHelper2<G> gameHelper, Object gameConfiguration) {
        this.id = id;
        this.tokenGenerator = tokenGenerator;
        this.gameHelper = gameHelper;
        this.gameConfiguration = gameConfiguration;
    }

    public String getId() {
        return id;
    }

    public JoinGameResponse joinRequest(String playerName, Object playerConfiguration) {
        ActionResult joinResult = gameHelper.playerJoin(this, playerConfiguration);
        if (joinResult.isOk()) {
            String privateKey = tokenGenerator.generateToken();
            addPlayer(playerName, privateKey, playerConfiguration);
            return new JoinGameResponse(privateKey);
        } else {
            // TODO: Handle this gracefully. Return correct HTTP Status, with message explaining why.
            // Could possibly return an ActionResult here
            return new JoinGameResponse((String) null);
        }
    }

    private void addPlayer(String playerName, String privateKey, Object playerConfiguration) {
        // TODO: Store player configuration in PlayerInGame object probably
        PlayerInGame pig = new PlayerInGame(playerName, playerKeys.size(), privateKey);
        this.playerKeys.add(pig);
    }

    public GameInfo getGameInfo() {
        return new GameInfo(id, playerKeys.stream().map(PlayerInGame::getName).collect(Collectors.toList()),
                Instant.now().toEpochMilli(),
                playerKeys.size(), false);
    }

    public Object getGameConfiguration() {
        return gameConfiguration;
    }

    public List<PlayerInGame> getPlayers() {
        return playerKeys;
    }

    public RunningGame<G> startGame() {
        G game = gameHelper.startGame(this);
        return new RunningGame<>(this.gameHelper, this.id, this.playerKeys, game);
    }



}

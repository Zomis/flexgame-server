package net.zomis.spring.games.generic;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import net.zomis.spring.games.generic.v2.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DBMongo<G> implements DatabaseInterface<G> {

    private static final Logger logger = LoggerFactory.getLogger(DBMongo.class);

    private final MongoCollection<Document> games;

    public DBMongo(MongoClient client) {
        MongoDatabase db = client.getDatabase("flexgame_server");
        this.games = db.getCollection("games");
    }

    @Override
    public void startGame(LobbyGame<G> lobbyGame, RunningGame<G> running) {
        Document game = new Document()
            .append("gameId", running.getGameInfo().getId())
            .append("lastActivity", running.getGameInfo().getLastActivity())
            .append("players", running.getGameInfo().getPlayers())
            .append("moves", Collections.emptyList());
//            .append("startState", );
        games.insertOne(game);
        logger.info("Inserted game: " + game.toJson());
    }

    @Override
    public void action(RunningGame<G> game, ActionV2 action, InternalActionResult result) {
        Document document = new Document()
            .append("type", action.getName())
            .append("result", result.getResultData());
        if (action.getActionData() != null && !action.getActionData().isNull()) {
            document = document.append("data", action.getActionData());
        }
        UpdateResult updateResult = games.updateOne(Filters.eq("gameId", game.getGameInfo().getId()),
            Updates.push("moves", document));
        logger.info("Added to moves in game {}: {} {} --> {}", game.getGameInfo().getId(), action.getName(),
            action.getActionData(), updateResult.toString());
    }



}

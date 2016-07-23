package net.zomis.monopoly.model.actions;

import net.zomis.monopoly.model.*;

import java.util.Arrays;
import java.util.Random;

public class RollDiceAction implements GameAction {

    private final int[] values;

    public RollDiceAction(int... values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    public int[] getValues() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public boolean isAllowed(Player player) {
        return player.getGame().isEmptyStack() && player.getGame().getCurrentPlayer() == player;
    }

    @Override
    public GameActionResult perform(Player player) {
        // TODO: Find out if player has rolled doubles three times a row, then go directly to Jail

        Game game = player.getGame();
        int previousTile = player.getPosition();
        int tileCount = game.getTileCount();
        boolean passGo = (previousTile + getTotal()) / tileCount > 0;
        int nextTile = (previousTile + getTotal()) % tileCount;
        player.setPosition(nextTile);

        if (passGo) {
            player.collect(200);
        }

        Property property = player.getPositionProperty();
        property.land(player, this);

        // if player has rolled doubles, then take turn again
        if (!isDoubles()) {
            player.getGame().nextPlayer();
        }
        return null;
    }

    private boolean isDoubles() {
        return Arrays.stream(values).distinct().count() == 1;
    }

    public static RollDiceAction roll(Random random) {
        return new RollDiceAction(random.nextInt(6) + 1, random.nextInt(6) + 1);
    }

    public int getTotal() {
        return Arrays.stream(values).sum();
    }
}

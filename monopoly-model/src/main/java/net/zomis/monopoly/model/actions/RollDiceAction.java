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
        Game game = player.getGame();
        if (isDoubles()) {
            player.addDoublesRolled();
        }
        if (player.getDoublesRolled() == 3) {
            player.gotoJail();
            Property property = player.getPositionProperty();
            property.land(player, this);
            return new GameActionResult(true, player.getName() + " went to " + property.getName());
        }
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

        return new GameActionResult(true, player.getName() + " landed on " + property.getName());
    }

    public boolean isDoubles() {
        return Arrays.stream(values).distinct().count() == 1;
    }

    public static RollDiceAction roll(Random random) {
        return new RollDiceAction(random.nextInt(6) + 1, random.nextInt(6) + 1);
    }

    public int getTotal() {
        return Arrays.stream(values).sum();
    }
}

package net.zomis.spring.games.impls;

import net.zomis.spring.games.generic.v2.ActionV2;

public class ActionScore {

    private final ActionV2 action;
    private final double score;

    public ActionScore(ActionV2 action, double score) {
        this.action = action;
        this.score = score;
    }

    public ActionV2 getAction() {
        return action;
    }

    public double getScore() {
        return score;
    }

}

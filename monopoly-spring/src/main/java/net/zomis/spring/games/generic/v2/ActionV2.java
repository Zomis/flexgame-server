package net.zomis.spring.games.generic.v2;

import com.fasterxml.jackson.databind.JsonNode;

public class ActionV2 {

    private final String name;
    private final JsonNode actionData;

    public ActionV2(String name, JsonNode actionData) {
        this.name = name;
        this.actionData = actionData;
    }

    public JsonNode getActionData() {
        return actionData;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ActionV2{" +
                "name='" + name + '\'' +
                ", actionData=" + actionData +
                '}';
    }

}

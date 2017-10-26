package net.zomis.spring.games.generic;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AIInvite {

    private String aiName;
    private ObjectNode aiSpecification;
    private ObjectNode playerConfiguration;

    public String getAiName() {
        return aiName;
    }

    public void setAiName(String aiName) {
        this.aiName = aiName;
    }

    public ObjectNode getAiSpecification() {
        return aiSpecification;
    }

    public void setAiSpecification(ObjectNode aiSpecification) {
        this.aiSpecification = aiSpecification;
    }

    public ObjectNode getPlayerConfiguration() {
        return playerConfiguration;
    }

    public void setPlayerConfiguration(ObjectNode playerConfiguration) {
        this.playerConfiguration = playerConfiguration;
    }

}

// entity/ConversationResponse.java
package eu.esupec.ttj_mobile.entity;

import java.util.List;

public class ConversationResponse {
    private User correspondant;
    private List<Message> messages;

    public User getCorrespondant() { return correspondant; }
    public List<Message> getMessages() { return messages; }
}
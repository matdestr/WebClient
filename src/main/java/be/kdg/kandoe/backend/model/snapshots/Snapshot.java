package be.kdg.kandoe.backend.model.snapshots;

import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.List;

/**
 * Created by Vincent on 7/02/2016.
 */
public class Snapshot {
    private Session session;
    private List<ChatMessageSnapshot> chatMessageSnapshots;
    private List<CardSnapshot> cardSnapshots;
}

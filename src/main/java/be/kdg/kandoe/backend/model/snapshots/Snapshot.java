package be.kdg.kandoe.backend.model.snapshots;

import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.List;

public class Snapshot {
    private Session session;
    private List<ChatMessageSnapshot> chatMessageSnapshots;
    private List<CardSnapshot> cardSnapshots;
}

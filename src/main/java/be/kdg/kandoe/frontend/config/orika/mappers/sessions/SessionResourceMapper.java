package be.kdg.kandoe.frontend.config.orika.mappers.sessions;

import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SynchronousSessionResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class SessionResourceMapper extends CustomMapper<Session, SessionResource> {
    @Override
    public void mapBtoA(SessionResource sessionResource, Session session, MappingContext context) {
        super.mapBtoA(sessionResource, session, context);
    }

    @Override
    public void mapAtoB(Session session, SessionResource sessionResource, MappingContext context) {
        sessionResource.setCategoryId(session.getCategory().getCategoryId());

        if (session.getTopic() != null)
            sessionResource.setTopicId(session.getTopic().getTopicId());

        if (session.getCurrentParticipantPlaying() != null)
            sessionResource.setCurrentParticipantPlayingUserId(session.getCurrentParticipantPlaying().getParticipant().getUserId());

        if (session instanceof SynchronousSession) {
            // Needed to prevent NullPointerException
            if (((SynchronousSession) session).getStartDateTime() != null) {
                ((SynchronousSessionResource) sessionResource).setStartDateTime(((SynchronousSession) session).getStartDateTime());
            }
        } else if (session instanceof AsynchronousSession) {
            ((AsynchronousSessionResource) sessionResource).setSecondsBetweenMoves(((AsynchronousSession) session).getSecondsBetweenMoves());
        }
    }
}

package be.kdg.kandoe.frontend.config.orika.mappers.sessions;

import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionListItemResource;
import lombok.val;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

/**
 * Orika mapper for Session and SessionListItemResource
 */

@Component
public class SessionListItemResourceMapper extends CustomMapper<Session, SessionListItemResource> {
    @Override
    public void mapBtoA(SessionListItemResource sessionListItemResource, Session session, MappingContext context) {
        super.mapBtoA(sessionListItemResource, session, context);
    }

    @Override
    public void mapAtoB(Session session, SessionListItemResource sessionListItemResource, MappingContext context) {

        val category = session.getCategory();

        if (category != null) {
            sessionListItemResource.setCategoryTitle(session.getCategory().getName());

            if (category.getOrganization() != null)
                sessionListItemResource.setOrganizationTitle(session.getCategory().getOrganization().getName());

        }

        if (session.getTopic() != null)
            sessionListItemResource.setTopicTitle(session.getTopic().getName());

        if (session.getParticipantInfo() != null)
            sessionListItemResource.setParticipantAmount(session.getParticipantInfo().size());
    }
}

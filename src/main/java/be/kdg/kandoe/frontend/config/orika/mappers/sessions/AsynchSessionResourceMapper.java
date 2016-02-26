package be.kdg.kandoe.frontend.config.orika.mappers.sessions;

import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public class AsynchSessionResourceMapper extends CustomMapper<AsynchronousSession, AsynchronousSessionResource> {
    @Override
    public void mapBtoA(AsynchronousSessionResource asynchronousSessionResource, AsynchronousSession asynchronousSession, MappingContext context) {

    }

    @Override
    public void mapAtoB(AsynchronousSession asynchronousSession, AsynchronousSessionResource asynchronousSessionResource, MappingContext context) {
        asynchronousSessionResource.setTopicId(asynchronousSession.getTopic().getTopicId());
    }
}

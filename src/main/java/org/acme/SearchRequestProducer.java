package org.acme;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/search")
public class SearchRequestProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger( SearchRequestProducer.class );
    @Channel("request-producer")
    Emitter<SearchRequest> requestEmitter;

    @POST
    public void handle(SearchRequest request) {
        requestEmitter.send(request);
        LOGGER.info("produce request {}", request);
    }
}

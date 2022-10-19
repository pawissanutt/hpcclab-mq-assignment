package org.acme;

import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Path("/results")
public class SearchRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger( SearchRequestConsumer.class );

    ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();

    @Incoming("request-consumer")
    public void consume(JsonObject jsonRequest) {
        var request = jsonRequest.mapTo(SearchRequest.class);
        LOGGER.info("receive request {}", request);
        // TODO
        results.put(request.id, "TODO");
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getResult(String id) {
        if (results.containsKey(id))
            return results.get(id);
        throw new NotFoundException();
    }
}

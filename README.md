# Introduction

This project is for learning how to use RabbitMQ with Quarkus. There are
a lot of bad practices in here.

# Prerequisites

  - [JDK 17+](https://adoptium.net/installation)

  - Set `JAVA_HOME` to JDK location
    
    ``` bash
    export JAVA_HOME="<replace with your jdk location>"
    ```

  - [quarkus cli](https://quarkus.io/guides/cli-tooling)

  - [Docker Runtime](https://www.docker.com/products/docker-desktop/)

# Instructions

1.  Create a new project via quarkus cli
    
    ``` bash
    quarkus create org.acme:cloud-a3-mq --extension="smallrye-reactive-messaging-rabbitmq,resteasy-reactive-jackson,smallrye-openapi"
    ```

2.  Copy the following placeholder files:
    
    1.  [src/main/java/org/acme/SearchRequest.java](src/main/java/org/acme/SearchRequest.java)
        
        ``` java
        package org.acme;
        
        import io.quarkus.runtime.annotations.RegisterForReflection;
        
        @RegisterForReflection
        public class SearchRequest{
            public String id;
            public String keyword;
        
            @Override
            public String toString() {
                final StringBuffer sb = new StringBuffer("SearchRequest{");
                sb.append("id='").append(id).append('\'');
                sb.append(", keyword='").append(keyword).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
        ```
    
    2.  [src/main/java/org/acme/SearchRequestConsumer.java](src/main/java/org/acme/SearchRequestConsumer.java)
        
        ``` java
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
        ```
    
    3.  [src/main/java/org/acme/SearchRequestProducer.java](src/main/java/org/acme/SearchRequestProducer.java)
        
        ``` java
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
        ```
    
    4.  [src/main/resources/application.properties](src/main/resources/application.properties)
        
        ``` properties
        mp.messaging.outgoing.request-producer.connector=smallrye-rabbitmq
        mp.messaging.outgoing.request-producer.exchange.name=search
        mp.messaging.incoming.request-consumer.connector=smallrye-rabbitmq
        mp.messaging.incoming.request-consumer.exchange.name=search
        mp.messaging.incoming.request-consumer.queue.name=search
        ```

3.  Start quarkus server in dev mode
    
    ``` bash
    quarkus dev
    ```
    
    If every thing work correctly, you should be able to open the dev
    console via <http://localhost:8080/q/dev/>. Also, you will notice
    that quarkus will start RabbitMQ container for you automatically.
    
    <div class="note">
    
    The dev mode that is provided quarkus will reload your application
    every time you change your code. However, it might make RabbitMQ not
    response to the new message. You can fix this by restarting your
    Quarkus process.
    
    </div>

4.  Go to the swagger UI at <http://localhost:8080/q/swagger-ui/>
    
    1.  You will find the list of exposing API. You can try send the
        following request:
        
        ``` httprequest
        POST /search
        content-type: application/json
        
        {
          "id": "1",
          "keyword": "test"
        }
        ```
        
        Then you will see the response in the console log.
        
            2022-10-18 20:30:58,182 INFO  [org.acm.SearchRequestProducer] (executor-thread-0) produce request SearchRequest{id='1', keyword='test'}
            2022-10-18 20:30:58,265 INFO  [org.acm.SearchRequestConsumer] (vert.x-eventloop-thread-2) receive request SearchRequest{id='1', keyword='test'}
    
    2.  To get the result of your request, you have to send the HTTP GET
        as the following example :
        
        ``` httprequest
        GET /results/1
        ```
        
        With the replace holder code, it will response with `TODO`
        message.

5.  Complete this search application by editing
    [src/main/java/org/acme/SearchRequestConsumer.java](src/main/java/org/acme/SearchRequestConsumer.java)
    in `consume` method.

6.  Write a simple client side application to sending the request into
    queue directly.

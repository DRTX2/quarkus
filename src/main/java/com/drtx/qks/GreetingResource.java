package com.drtx.qks;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/greeting")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Greeting", description = "Greeting operations")
public class GreetingResource {

    @Inject
    GreetingConfig greetingConfig;

    @GET
    @Operation(summary = "Get greeting message", description = "Returns a greeting message from configuration")
    public Response hello() {
        return Response.ok(Map.of(
            "message", greetingConfig.message(),
            "timestamp", System.currentTimeMillis()
        )).build();
    }

    @GET
    @Path("/{name}")
    @Operation(summary = "Get personalized greeting", description = "Returns a personalized greeting message")
    public Response helloName(@PathParam("name") String name) {
        return Response.ok(Map.of(
            "message", greetingConfig.message() + ", " + name + "!",
            "name", name,
            "timestamp", System.currentTimeMillis()
        )).build();
    }

    @POST
    @Operation(summary = "Create custom greeting", description = "Creates a custom greeting with provided data")
    public Response createGreeting(GreetingRequest request) {
        String customMessage = String.format("%s, %s!",
            request.greeting() != null ? request.greeting() : greetingConfig.message(),
            request.name() != null ? request.name() : "Guest"
        );

        return Response.ok(Map.of(
            "message", customMessage,
            "receivedData", request
        )).build();
    }
}


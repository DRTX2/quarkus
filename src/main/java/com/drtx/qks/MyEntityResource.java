package com.drtx.qks;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/entities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Entities", description = "Entity CRUD operations")
public class MyEntityResource {

    @Inject
    MyEntityRepository repository;

    @GET
    @Operation(summary = "Get all entities", description = "Returns all entities from database")
    public List<MyEntity> getAll() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get entity by ID", description = "Returns a single entity by its ID")
    public Response getById(@PathParam("id") Long id) {
        MyEntity entity = repository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Entity not found with id: " + id))
                .build();
        }
        return Response.ok(entity).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create entity", description = "Creates a new entity")
    public Response create(@Valid MyEntityRequest request) {
        MyEntity entity = new MyEntity();
        entity.field = request.field();
        repository.persist(entity);
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update entity", description = "Updates an existing entity")
    public Response update(@PathParam("id") Long id, @Valid MyEntityRequest request) {
        MyEntity entity = repository.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Entity not found with id: " + id))
                .build();
        }
        entity.field = request.field();
        repository.persist(entity);
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete entity", description = "Deletes an entity by its ID")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Entity not found with id: " + id))
                .build();
        }
        return Response.noContent().build();
    }
}


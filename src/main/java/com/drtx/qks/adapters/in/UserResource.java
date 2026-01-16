package com.drtx.qks.adapters.in;


import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.UserUseCase;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@ApplicationScoped
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Users operations")
public class UserResource {
    @Inject
    UserUseCase userUseCase;

    @GET
    @Operation(summary = "Get users", description = "Returns all users with pagination and filtering")
    public Page<User> findAll(
            @QueryParam("username") String username,
            @QueryParam("email") String email,
            @QueryParam("role") String role,
            @QueryParam("enabled") Boolean enabled,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        UserFilter filter = new UserFilter(username, email, role, enabled);
        return userUseCase.findAll(filter, page, size);
    }

    @GET
    @Path("/{id}")
    public User findById(@PathParam("id") Long id) {
        return userUseCase.findById(id).
                orElseThrow(()-> new NotFoundException("User not found with id: " + id));
    }

    @PUT
    @Path("/{id}")
    public User update(@PathParam("id") Long id, User user){
        user.setId(id);
        return userUseCase.update(user);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteById(@PathParam("id") Long id) {
        userUseCase.deleteById(id);
        return Response.noContent().build();
    }
}

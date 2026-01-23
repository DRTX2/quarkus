package com.drtx.qks.adapters.in.rest.user;

import com.drtx.qks.domain.model.User;
import com.drtx.qks.domain.ports.in.user.UserUseCase;
import com.drtx.qks.domain.valueObjects.Page;
import com.drtx.qks.domain.valueObjects.UserFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@ApplicationScoped
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Users operations")
@jakarta.annotation.security.RolesAllowed(com.drtx.qks.domain.constants.Roles.ADMIN)
@org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement(name = "bearer-jwt")
public class UserResource {
    UserUseCase userUseCase;

    @Inject
    public UserResource(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GET
    @Operation(summary = "Get users", description = "Returns all users with pagination and filtering")
    public Page<User> findAll(
            @QueryParam("username") String username,
            @QueryParam("email") String email,
            @QueryParam("role") String role,
            @QueryParam("enabled") Boolean enabled,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        if (page < 0 || size <= 0)
            throw new BadRequestException("Invalid pagination parameters");

        UserFilter filter = new UserFilter(username, email, role, enabled);
        return userUseCase.findAll(filter, page, size);
    }

    @GET
    @Path("/{uuid}")
    public User findById(@PathParam("uuid") UUID uuid) {
        return userUseCase.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("User not found with uuid: " + uuid));
    }

    @PUT
    @Path("/{uuid}")
    public User update(@PathParam("uuid") UUID uuid, User user) {
        user.setUuid(uuid);
        return userUseCase.update(user);
    }

    @DELETE
    @Path("/{uuid}")
    public Response deleteByUuid(@PathParam("uuid") UUID uuid) {
        userUseCase.deleteByUuid(uuid);
        return Response.noContent().build();
    }
}

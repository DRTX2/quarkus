package com.drtx.qks.adapters.in.rest.chaos;

import com.drtx.qks.application.dtos.chaos.ChaosRequest;
import com.drtx.qks.domain.constants.Roles;
import com.drtx.qks.domain.model.ChaosExperiment;
import com.drtx.qks.domain.ports.in.chaos.ExecuteChaosUseCase;
import com.drtx.qks.domain.ports.in.chaos.GetChaosExperimentUseCase;
import com.drtx.qks.domain.ports.in.chaos.ListChaosExperimentsUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/v1/chaos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Chaos Engineering & AIOps", description = "Endpoints for executing chaos experiments and analyzing resilience with AI")
public class ChaosController {

    private final ExecuteChaosUseCase executeChaosUseCase;
    private final GetChaosExperimentUseCase getChaosExperimentUseCase;
    private final ListChaosExperimentsUseCase listChaosExperimentsUseCase;

    @Inject
    public ChaosController(ExecuteChaosUseCase executeChaosUseCase,
                           GetChaosExperimentUseCase getChaosExperimentUseCase,
                           ListChaosExperimentsUseCase listChaosExperimentsUseCase) {
        this.executeChaosUseCase = executeChaosUseCase;
        this.getChaosExperimentUseCase = getChaosExperimentUseCase;
        this.listChaosExperimentsUseCase = listChaosExperimentsUseCase;
    }

    @POST
    @Path("/inject")
    @RolesAllowed({Roles.SRE_ADMIN, Roles.ADMIN})
    @Operation(summary = "Inject chaos into a Kubernetes deployment", description = "Requires SRE_ADMIN or ADMIN role. Executes chaos, measures recovery and generates an ISO 25010 resilience report.")
    public Response injectChaos(@Valid ChaosRequest request, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "system";

        ChaosExperiment experiment = executeChaosUseCase.executeExperiment(
                request.getTargetNamespace(),
                request.getTargetDeployment(),
                request.getType(),
                username
        );

        return Response.accepted(experiment).build();
    }

    @GET
    @Path("/experiments")
    @RolesAllowed({Roles.SRE_ADMIN, Roles.ADMIN, Roles.USER})
    @Operation(summary = "List recent chaos experiments", description = "Returns historical experiments, logs and scores.")
    public Response listExperiments(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("namespace") String namespace) {

        List<ChaosExperiment> experiments;
        if (namespace != null && !namespace.isBlank()) {
            experiments = listChaosExperimentsUseCase.listExperimentsByNamespace(namespace);
        } else {
            experiments = listChaosExperimentsUseCase.listRecentExperiments(limit);
        }

        return Response.ok(experiments).build();
    }

    @GET
    @Path("/experiments/{id}")
    @RolesAllowed({Roles.SRE_ADMIN, Roles.ADMIN, Roles.USER})
    @Operation(summary = "Get chaos experiment by ID", description = "Retrieves the full report and metrics for a specific experiment.")
    public Response getExperimentById(@Parameter(description = "Experiment UUID") @PathParam("id") String id) {
        return getChaosExperimentUseCase.getExperimentById(id)
                .map(exp -> Response.ok(exp).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Chaos experiment not found with ID: " + id + "\"}")
                        .build());
    }
}

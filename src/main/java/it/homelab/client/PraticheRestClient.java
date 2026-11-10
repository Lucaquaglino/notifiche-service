
package it.homelab.client;

import it.homelab.dto.PraticaDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/pratiche")
@RegisterRestClient(configKey = "pratiche-api")
public interface PraticheRestClient {

    @GET
    @Path("/{id}")
    PraticaDTO getPraticaById(@PathParam("id") Long id);
}

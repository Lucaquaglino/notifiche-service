
package it.homelab.kafka;

import it.homelab.client.PraticheRestClient;
import it.homelab.dto.PraticaCreataEvent;
import it.homelab.dto.PraticaDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PraticheConsumer {

    private static final Logger LOG =
            Logger.getLogger(PraticheConsumer.class);

    @Inject
    @RestClient
    PraticheRestClient praticheRestClient;

    @Incoming("pratiche-events")
    public void riceviEvento(PraticaCreataEvent evento) {

        LOG.infof(
                "Evento Kafka ricevuto: pratica ID=%d",
                evento.praticaId()
        );

        try {
            PraticaDTO pratica =
                    praticheRestClient.getPraticaById(evento.praticaId());

            LOG.infof(
                    "Pratica recuperata via REST: ID=%d, codice=%s, stato=%s",
                    pratica.id(),
                    pratica.codice(),
                    pratica.stato()
            );

        } catch (Exception e) {
            LOG.errorf(
                    e,
                    "Errore nella chiamata REST per la pratica %d",
                    evento.praticaId()
            );
        }
    }
}

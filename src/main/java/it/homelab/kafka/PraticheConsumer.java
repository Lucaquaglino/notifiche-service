
package it.homelab.kafka;

import it.homelab.dto.PraticaCreataEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PraticheConsumer {

    private static final Logger LOG =
            Logger.getLogger(PraticheConsumer.class);

    @Incoming("pratiche-events")
    public void riceviEvento(PraticaCreataEvent evento) {

        LOG.infof(
                "Pratica ricevuta da Kafka: ID=%d, codice=%s, stato=%s",
                evento.praticaId(),
                evento.codice(),
                evento.stato()
        );
    }
}

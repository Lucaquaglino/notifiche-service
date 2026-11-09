
package it.homelab.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import it.homelab.dto.PraticaCreataEvent;

public class PraticaCreataDeserializer
        extends ObjectMapperDeserializer<PraticaCreataEvent> {

    public PraticaCreataDeserializer() {
        super(PraticaCreataEvent.class);
    }
}

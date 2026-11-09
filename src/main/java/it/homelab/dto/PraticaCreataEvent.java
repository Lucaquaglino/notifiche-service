
package it.homelab.dto;

public record PraticaCreataEvent(
        String eventType,
        Long praticaId,
        String codice,
        String stato
) {}

package br.com.freela.contrato.domain.event;
import br.com.freela.contrato.domain.model.Contrato;
import br.com.freela.contrato.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record EntregaRegistrada(UUID eventId, UUID contratoId, UUID clienteId,
                                UUID freelancerId, Instant occurredAt) implements DomainEvent {
    public static EntregaRegistrada novo(Contrato contrato) {
        return new EntregaRegistrada(
                UUID.randomUUID(),
                contrato.id().id(),
                contrato.clienteId().id(),
                contrato.freelancerId().id(),
                Instant.now()
        );
    }

    @Override
    public String eventType() {
        return "EntregaRegistrada";
    }
}

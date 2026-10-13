package br.com.freela.contrato.domain.event;

import br.com.freela.contrato.domain.model.*;
import br.com.freela.contrato.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ContratoCriado(UUID eventId, Instant occurredAt, UUID contratoId,
                             ClienteId clienteId, FreelancerId freelancerId, Titulo titulo, ValorMonetario valor)
        implements DomainEvent {
    public static ContratoCriado novo(Contrato c) {
        return new ContratoCriado(UUID.randomUUID(), Instant.now(), c.id().id(), c.clienteId(), c.freelancerId(), c.titulo(), c.valor());
    }

    @Override
    public String eventType() {
        return "ContratoCriado";
    }
}

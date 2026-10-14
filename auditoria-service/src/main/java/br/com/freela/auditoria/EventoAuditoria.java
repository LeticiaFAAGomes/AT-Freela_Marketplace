package br.com.freela.auditoria;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auditoria_eventos")
@com.fasterxml.jackson.annotation.JsonAutoDetect(
        fieldVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)
class EventoAuditoria {
    @Id
    UUID id;
    @Column(unique = true, nullable = false)
    UUID eventId;
    UUID aggregateId;
    String eventType;
    String correlationId;
    @Column(columnDefinition = "text")
    String payload;
    Instant recebidoEm;

    protected EventoAuditoria() {
    }
    EventoAuditoria(UUID eventId, UUID aggregateId, String eventType, String correlationId, String payload) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.payload = payload;
        this.recebidoEm = Instant.now();
    }
}

package br.com.freela.contrato.infrastructure.persistence.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@Getter
public class OutboxEvent {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant occurredAt;

    private Instant publishedAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            UUID eventId,
            UUID aggregateId,
            String eventType,
            String payload,
            Instant occurredAt
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public void marcarComoPublicado() {
        this.publishedAt = Instant.now();
    }
}

package br.com.freela.reputacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "eventos_processados")
public class EventoProcessado {
    @Id
    @Column(nullable = false, unique = true)
    private UUID eventId;

    protected EventoProcessado() {
    }

    public EventoProcessado(UUID eventId) {
        this.eventId = eventId;
    }
}

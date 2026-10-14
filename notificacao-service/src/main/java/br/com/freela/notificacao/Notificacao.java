package br.com.freela.notificacao;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
class Notificacao {
    @Id
    UUID id;
    @Column(nullable = false)
    private UUID eventoId;
    private UUID contratoId;
    private UUID destinatarioId;
    private String tipo;
    @Column(length = 500)
    private String mensagem;
    private Instant criadaEm;

    protected Notificacao() {
    }

    Notificacao(UUID eventoId, UUID contratoId, UUID destinatarioId, String tipo, String mensagem) {
        this.id = UUID.randomUUID();
        this.eventoId = eventoId;
        this.contratoId = contratoId;
        this.destinatarioId = destinatarioId;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.criadaEm = Instant.now();
    }
}

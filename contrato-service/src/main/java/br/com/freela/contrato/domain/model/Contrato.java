package br.com.freela.contrato.domain.model;

import br.com.freela.contrato.domain.event.ContratoCriado;
import br.com.freela.contrato.domain.shared.DomainEvent;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Contrato {
    private final ContratoId id;
    private final ClienteId clienteId;
    private final FreelancerId freelancerId;
    private final Titulo titulo;
    private final ValorMonetario valor;
    private StatusContrato status;
    private final Instant criadoEm;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Contrato(
            ContratoId id,
            ClienteId clienteId,
            FreelancerId freelancerId,
            Titulo titulo,
            ValorMonetario valor,
            StatusContrato status,
            Instant criadoEm
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.freelancerId = freelancerId;
        this.titulo = titulo;
        this.valor = valor;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    public static Contrato criar(ClienteId clienteId, FreelancerId freelancerId, Titulo titulo, ValorMonetario valor) {
        var contrato = new Contrato(ContratoId.de(UUID.randomUUID()), clienteId, freelancerId, titulo, valor,
                StatusContrato.ATIVO, Instant.now());
        contrato.domainEvents.add(ContratoCriado.novo(contrato));
        return contrato;
    }

    public static Contrato restaurar(ContratoId id, ClienteId clienteId, FreelancerId freelancerId, Titulo titulo,
                                     ValorMonetario valor, StatusContrato status, Instant criadoEm) {
        return new Contrato(id, clienteId, freelancerId, titulo, valor, status, criadoEm);
    }

    public void registrarEntrega() {
        if (status != StatusContrato.ATIVO) throw new IllegalStateException("Somente contratos ativos recebem entrega");
        status = StatusContrato.ENTREGA_REGISTRADA;
    }

    public void concluir() {
        if (status != StatusContrato.ENTREGA_REGISTRADA)
            throw new IllegalStateException("A entrega precisa estar registrada");
        status = StatusContrato.CONCLUIDO;
    }

    public void cancelar() {
        if (status == StatusContrato.CONCLUIDO)
            throw new IllegalStateException("Contrato concluído não pode ser cancelado");
        status = StatusContrato.CANCELADO;
    }

    public List<DomainEvent> pullDomainEvents() {
        var copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public ContratoId id() {
        return id;
    }

    public ClienteId clienteId() {
        return clienteId;
    }

    public FreelancerId freelancerId() {
        return freelancerId;
    }

    public Titulo titulo() {
        return titulo;
    }

    public ValorMonetario valor() {
        return valor;
    }

    public StatusContrato status() {
        return status;
    }

    public Instant criadoEm() {
        return criadoEm;
    }
}

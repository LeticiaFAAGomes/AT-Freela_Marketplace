package br.com.freela.contrato.application;

import br.com.freela.contrato.domain.model.*;
import br.com.freela.contrato.domain.repository.ContratoRepository;
import br.com.freela.contrato.domain.shared.DomainEvent;
import br.com.freela.contrato.domain.shared.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarContratoService {

    private static final Logger log = LoggerFactory.getLogger(CriarContratoService.class);

    @Autowired
    private ContratoRepository repository;
    @Autowired
    private DomainEventPublisher publisher;

 @Transactional
    public Contrato criar(CriarContratoCommand cmd) {
        log.info(
                "contrato.criacao.inicio clienteId={} freelancerId={} titulo={} valor={}",
                cmd.clienteId(), cmd.freelancerId(), cmd.titulo(), cmd.valor()
        );

        Contrato contrato = Contrato.criar(
                ClienteId.de(cmd.clienteId()),
                FreelancerId.de(cmd.freelancerId()),
                new Titulo(cmd.titulo()),
                ValorMonetario.de(cmd.valor())
        );

        log.info(
                "contrato.dominio.criado contratoId={} status={} domainEvents={}",
                contrato.id(), contrato.status(), contrato.domainEvents().size()
        );

        Contrato salvo = repository.salvar(contrato);
        var eventos = contrato.pullDomainEvents();

        for (DomainEvent evento : eventos) {
            log.info(
                    "contrato.evento.outbox contratoId={} eventId={} eventType={} occurredAt={}",
                    contrato.id(), evento.eventId(),
                    evento.eventType(), evento.occurredAt()
            );
        }

        publisher.publicar(eventos);
        log.info(
                "contrato.criacao.sucesso contratoId={} clienteId={} freelancerId={} status={}",
                salvo.id(), salvo.clienteId(), salvo.freelancerId(), salvo.status()
        );

        return salvo;
    }
}

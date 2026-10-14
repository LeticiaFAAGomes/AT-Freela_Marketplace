package br.com.freela.contrato.application;

import br.com.freela.contrato.domain.model.Contrato;
import br.com.freela.contrato.domain.repository.ContratoRepository;
import br.com.freela.contrato.domain.shared.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ConcluirContratoService {

    @Autowired
    private ContratoRepository repository;

    @Autowired
    private DomainEventPublisher publisher;

    @Transactional
    public Contrato concluir(UUID id) {
        Contrato contrato = repository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contrato não encontrado: " + id));
        contrato.concluir();
        Contrato salvo = repository.salvar(contrato);
        var eventos = contrato.pullDomainEvents();
        publisher.publicar(eventos);

        return salvo;
    }
}

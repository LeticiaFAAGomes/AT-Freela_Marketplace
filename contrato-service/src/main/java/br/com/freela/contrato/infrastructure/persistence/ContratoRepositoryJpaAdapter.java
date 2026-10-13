package br.com.freela.contrato.infrastructure.persistence;

import br.com.freela.contrato.domain.model.*;
import br.com.freela.contrato.domain.repository.ContratoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ContratoRepositoryJpaAdapter implements ContratoRepository {

    private static final Logger log = LoggerFactory.getLogger(ContratoRepositoryJpaAdapter.class);

    @Autowired
    private SpringDataContratoRepository jpa;

    @Override
    public Contrato salvar(Contrato c) {
        log.info("contrato.persistence.save.inicio contratoId={} status={}", c.id(), c.status());
        var entity = new ContratoJpaEntity(c.id().id(), c.clienteId().id(), c.freelancerId().id(), c.titulo().titulo(), c.valor().valor(), c.status(), c.criadoEm());
        var saved = jpa.save(entity);
        log.info("contrato.persistence.save.sucesso contratoId={} status={}", saved.getId(), saved.getStatus());
        return toDomain(saved);
    }

    @Override
    public Optional<Contrato> buscarPorId(UUID id) {
        log.info("contrato.persistence.findById contratoId={}", id);
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Contrato> listar() {
        log.info("contrato.persistence.findAll");
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    private Contrato toDomain(ContratoJpaEntity e) {
        return Contrato.restaurar(
                ContratoId.de(e.getId()),
            ClienteId.de(e.getClienteId()),
            FreelancerId.de(e.getFreelancerId()),
            new Titulo(e.getTitulo()),
            ValorMonetario.de(e.getValor()),
            e.getStatus(),
            e.getCriadoEm());
    }
}

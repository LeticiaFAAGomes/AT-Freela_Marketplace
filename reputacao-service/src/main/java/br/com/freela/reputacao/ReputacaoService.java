package br.com.freela.reputacao;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ReputacaoService {
    private static final Logger log = LoggerFactory.getLogger(ReputacaoService.class);

    @Autowired
    private ReputacaoRepository repository;

    @Autowired
    private EventoProcessadoRepository eventosRepository;

    @Transactional
    public void registrarContratoConcluido(UUID contratoId, UUID freelancerId, BigDecimal valor) {
        log.info("reputacao.atualizacao.inicio contratoId={} freelancerId={} valor={}", contratoId, freelancerId, valor);
        var r = repository.findById(freelancerId).orElseGet(() -> new ReputacaoFreelancer(freelancerId));
        r.registrarContrato(valor);
        repository.save(r);
        log.info("reputacao.atualizacao.sucesso contratoId={} freelancerId={} contratosConcluidos={} valorTotal={}", contratoId, freelancerId, r.contratosConcluidos, r.valorTotal);
    }
    @Transactional
    public void processarContratoConcluido(
            UUID eventId,
            UUID contratoId,
            UUID freelancerId,
            BigDecimal valor
    ) {
        if (eventosRepository.existsById(eventId)) {
            log.info(
                    "kafka.evento.duplicado eventId={} contratoId={}",
                    eventId, contratoId
            );
            return;
        }

        var reputacao = repository.findById(freelancerId).orElseGet(() -> new ReputacaoFreelancer(freelancerId));
        reputacao.registrarContrato(valor);
        repository.save(reputacao);
        eventosRepository.save(new EventoProcessado(eventId));
        log.info(
                "reputacao.contrato.concluido.processado eventId={} contratoId={} freelancerId={} contratosConcluidos={} valorTotal={}",
                eventId, contratoId, freelancerId,
                reputacao.contratosConcluidos, reputacao.valorTotal
        );
    }
}

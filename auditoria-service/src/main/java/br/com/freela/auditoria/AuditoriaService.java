package br.com.freela.auditoria;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditoriaService {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    @Autowired
    private EventoAuditoriaRepository repository;

    @Transactional
    public void registrar(UUID eventId, UUID aggregateId, String eventType, String correlationId, String payload) {
        if (repository.existsByEventId(eventId)) {
            log.info("auditoria.evento.duplicado eventId={}", eventId);
            return;
        }
        log.info("auditoria.registro.inicio eventId={} aggregateId={} eventType={}",
                eventId, aggregateId, eventType
        );
        EventoAuditoria evento = repository.save(
                new EventoAuditoria(
                        eventId,
                        aggregateId,
                        eventType,
                        correlationId,
                        payload
                )
        );
        log.info("auditoria.registro.sucesso auditoriaId={} eventId={}",
                evento.id, eventId
        );
    }
}

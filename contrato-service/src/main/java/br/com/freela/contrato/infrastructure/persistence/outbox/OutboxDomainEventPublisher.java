package br.com.freela.contrato.infrastructure.persistence.outbox;

import br.com.freela.contrato.domain.event.ContratoCriado;
import br.com.freela.contrato.domain.shared.DomainEvent;
import br.com.freela.contrato.domain.shared.DomainEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void publicar(List<DomainEvent> eventos) {
        for (DomainEvent evento : eventos) {
            try {
                String payload = objectMapper.writeValueAsString(evento);

                if (evento instanceof ContratoCriado contratoCriado) {
                    outboxRepository.save(new OutboxEvent(
                            evento.eventId(),
                            contratoCriado.contratoId(),
                            evento.eventType(),
                            payload,
                            evento.occurredAt()
                    ));
                } else {
                    throw new IllegalArgumentException(
                            "Tipo de evento não suportado: " + evento.eventType()
                    );
                }
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(
                        "Não foi possível serializar o evento " + evento.eventId(), e
                );
            }
        }
    }
}

package br.com.freela.contrato.infrastructure.persistence.outbox;

import br.com.freela.contrato.domain.event.ContratoConcluido;
import br.com.freela.contrato.domain.event.ContratoCriado;
import br.com.freela.contrato.domain.shared.DomainEvent;
import br.com.freela.contrato.domain.shared.DomainEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private OutboxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void publicar(List<DomainEvent> eventos) {
        for (DomainEvent evento : eventos) {
            try {
                UUID contratoId;

                if (evento instanceof ContratoCriado criado) {
                    contratoId = criado.contratoId();
                } else if (evento instanceof ContratoConcluido concluido) {
                    contratoId = concluido.contratoId();
                } else {
                    throw new IllegalArgumentException(
                            "Tipo de evento não suportado: " + evento.eventType()
                    );
                }

                ObjectNode payload = objectMapper.valueToTree(evento);
                payload.put("eventType", evento.eventType());

                outboxRepository.save(new OutboxEvent(
                        evento.eventId(),
                        contratoId,
                        evento.eventType(),
                        objectMapper.writeValueAsString(payload),
                        evento.occurredAt()
                ));

            } catch (JsonProcessingException e) {
                throw new IllegalStateException(
                        "Não foi possível serializar o evento " + evento.eventId(), e
                );
            }
        }
    }
}

package br.com.freela.reputacao.consumer;

import br.com.freela.reputacao.ReputacaoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ContratoCriadoConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(ContratoCriadoConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReputacaoService reputacaoService;

    @KafkaListener(
            topics = "${topics.contrato-eventos:contrato.eventos}",
            groupId = "reputacao-service"
    )
    public void consumir(String mensagem) throws Exception {
        JsonNode evento = objectMapper.readTree(mensagem);
        String tipo = evento.path("eventType").asText("");

        if (!"ContratoConcluido".equals(tipo)) {
            log.debug("kafka.evento.ignorado eventType={}", tipo);
            return;
        }
        UUID eventId = UUID.fromString(evento.path("eventId").asText());
        UUID contratoId = UUID.fromString(evento.path("contratoId").asText());
        UUID freelancerId = UUID.fromString(evento.path("freelancerId").asText());
        BigDecimal valor = new BigDecimal(evento.path("valor").asText());
        reputacaoService.processarContratoConcluido(
                eventId, contratoId, freelancerId, valor
        );
    }
}

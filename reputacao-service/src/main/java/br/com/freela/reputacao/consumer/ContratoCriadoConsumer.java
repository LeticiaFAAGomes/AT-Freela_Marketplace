package br.com.freela.reputacao.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContratoCriadoConsumer {
    private static final Logger log =
            LoggerFactory.getLogger(ContratoCriadoConsumer.class);
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${topics.contrato-eventos:contrato.eventos}",
            groupId = "reputacao-service"
    )
    public void consumir(String mensagem) throws Exception {
        JsonNode evento = objectMapper.readTree(mensagem);

        log.info(
            "kafka.contrato.criado.recebido contratoId={} freelancerId={}",
            evento.path("contratoId").asText(),
            evento.path("freelancerId").toString()
        );

        log.info(
                "kafka.contrato.criado.recebido contratoId={} freelancerId={}",
                evento.path("contratoId").asText(),
                evento.path("freelancerId").toString()
        );
    }

}

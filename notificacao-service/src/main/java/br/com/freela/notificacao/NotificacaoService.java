package br.com.freela.notificacao;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificacaoService {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    @Autowired
    private NotificacaoRepository repository;

    @Transactional
    public void registrar(UUID eventoId, UUID contratoId, UUID destinatarioId, String tipo, String mensagem) {
        if (repository.existsByEventoIdAndDestinatarioId(eventoId, destinatarioId)) {
            log.info("notificacao.duplicada ignorada eventoId={} destinatarioId={}",
                    eventoId, destinatarioId);
            return;
        }

        Notificacao notificacao = repository.save(
                new Notificacao(eventoId, contratoId, destinatarioId, tipo, mensagem)
        );

        log.info("notificacao.registro.sucesso notificacaoId={} eventoId={} contratoId={}",
                notificacao.id, eventoId, contratoId);
    }
}

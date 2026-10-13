package br.com.freela.contrato.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ClienteId(UUID id) {
    public ClienteId {
        Objects.requireNonNull(id, "O ID do cliente do contrato não pode ser nulo.");
    }

    public static ClienteId novo() {
        return new ClienteId(UUID.randomUUID());
    }

    public static ClienteId de(UUID id) {
        return new ClienteId(id);
    }
}

package br.com.freela.contrato.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ContratoId(UUID id) {

    public ContratoId {
        Objects.requireNonNull(id, "O ID do contrato não pode ser nulo.");
    }

    public static ContratoId novo() {
        return new ContratoId(UUID.randomUUID());
    }

    public static ContratoId de(UUID id) {
        return new ContratoId(id);
    }
}

package br.com.freela.contrato.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ValorMonetario(BigDecimal valor) {
    public ValorMonetario {
        Objects.requireNonNull(valor, "O valor é obrigatorio.");
        valor = valor.setScale(2, RoundingMode.HALF_UP);
        if(valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
    }
    public static ValorMonetario de(String valor) {
        return new ValorMonetario(new BigDecimal(valor));
    }

    public static ValorMonetario de(BigDecimal valor) {
        return new ValorMonetario(valor);
    }
}

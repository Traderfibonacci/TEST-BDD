package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AgendamentoModel {
    @Expose
    private IdModel id;
    @Expose
    private String nomeCliente;
    @Expose
    private LocalDate dataAgendamento;
    @Expose
    private String tipoMaterial;
    @Expose
    private String descricao;

    @Data
    public static class IdModel {
        @Expose
        private String timestamp;
        @Expose
        private String date;
    }
}


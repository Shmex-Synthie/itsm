package br.simpliticket.sistemas.chamados.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historico_status")
public class HistoricoStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado statusNovo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data;

    public HistoricoStatus() {
    }

    public HistoricoStatus(Chamado chamado, StatusChamado anterior, StatusChamado novo) {
        this.chamado = chamado;
        this.statusAnterior = anterior;
        this.statusNovo = novo;
        this.data = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public StatusChamado getStatusAnterior() {
        return statusAnterior;
    }

    public StatusChamado getStatusNovo() {
        return statusNovo;
    }

    public LocalDateTime getData() {
        return data;
    }

}

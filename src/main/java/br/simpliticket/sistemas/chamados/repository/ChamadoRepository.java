package br.simpliticket.sistemas.chamados.repository;

import br.simpliticket.sistemas.chamados.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    @Query("SELECT c FROM Chamado c WHERE (:status IS NULL OR c.status=:status) AND (:prioridade IS NULL OR c.prioridade=:prioridade) AND (:clienteId IS NULL OR c.cliente.id=:clienteId)")
    Page<Chamado> filtrar(@Param("status") StatusChamado status, @Param("prioridade") Prioridade prioridade,
        @Param("clienteId") Long clienteId, Pageable pageable);
    Page<Chamado> findByClienteId(Long clienteId, Pageable pageable);
}

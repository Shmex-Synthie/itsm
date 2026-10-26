package br.simpliticket.sistemas.chamados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.simpliticket.sistemas.chamados.entity.HistoricoStatus;
public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {
    List<HistoricoStatus> findByChamadoIdOrderByDataAsc(Long chamadoId);
    void deleteByChamadoId(Long chamadoId);
}

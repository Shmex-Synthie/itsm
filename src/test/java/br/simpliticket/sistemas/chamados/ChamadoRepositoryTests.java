package br.simpliticket.sistemas.chamados;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.repository.*;

@DataJpaTest
class ChamadoRepositoryTests {
    @Autowired ChamadoRepository chamados;
    @Autowired ClienteRepository clientes;
    @Autowired CategoriaRepository categorias;

    @Test
    void filtraChamadosPorStatusPrioridadeECliente() {
        Cliente cliente = clientes.save(new Cliente("Ana", "ana@email.com", null));
        Categoria categoria = categorias.save(new Categoria("Rede", "Problemas de rede"));
        chamados.save(new Chamado("Internet", "Sem acesso", Prioridade.ALTA, cliente, categoria, null));
        chamados.save(new Chamado("Lentidão", "Internet lenta", Prioridade.BAIXA, cliente, categoria, null));
        assertEquals(1, chamados.filtrar(StatusChamado.ABERTO, Prioridade.ALTA, cliente.getId(), PageRequest.of(0, 10)).getTotalElements());
        assertEquals(2, chamados.findByClienteId(cliente.getId(), PageRequest.of(0, 10)).getTotalElements());
        assertEquals(0, chamados.filtrar(StatusChamado.FECHADO, null, null, PageRequest.of(0, 10)).getTotalElements());
    }

}

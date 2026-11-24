package br.simpliticket.sistemas.chamados.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.simpliticket.sistemas.chamados.dto.StatusChamadoRequest;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.repository.*;
import br.simpliticket.sistemas.chamados.service.ChamadoService;

/** Carrega dados demonstrativos somente quando o banco esta vazio. */
@Component
public class DadosIniciais implements ApplicationRunner {

    private final ClienteRepository clientes;
    private final CategoriaRepository categorias;
    private final TecnicoRepository tecnicos;
    private final ChamadoRepository chamados;
    private final ChamadoService chamadoService;

    public DadosIniciais(ClienteRepository clientes, CategoriaRepository categorias,
                         TecnicoRepository tecnicos, ChamadoRepository chamados,
                         ChamadoService chamadoService) {
        this.clientes = clientes;
        this.categorias = categorias;
        this.tecnicos = tecnicos;
        this.chamados = chamados;
        this.chamadoService = chamadoService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Evita acrescentar registros de exemplo em um banco que ja possui dados.
        if (clientes.count() > 0 || categorias.count() > 0 ||
            tecnicos.count() > 0 || chamados.count() > 0) return;

        List<Cliente> listaClientes = clientes.saveAll(List.of(
            new Cliente("Ana Souza", "ana.souza@exemplo.com", "47999990001"),
            new Cliente("Bruno Lima", "bruno.lima@exemplo.com", "47999990002"),
            new Cliente("Carla Mendes", "carla.mendes@exemplo.com", "47999990003"),
            new Cliente("Diego Martins", "diego.martins@exemplo.com", "47999990004"),
            new Cliente("Elisa Costa", "elisa.costa@exemplo.com", "47999990005")
        ));

        List<Categoria> listaCategorias = categorias.saveAll(List.of(
            new Categoria("Hardware", "Problemas com computadores, perifericos e equipamentos"),
            new Categoria("Software", "Instalacao, atualizacao e erros de programas"),
            new Categoria("Rede", "Conectividade, Wi-Fi e acesso aos sistemas")
        ));

        List<Tecnico> listaTecnicos = tecnicos.saveAll(List.of(
            new Tecnico("Marcos Oliveira", "marcos@exemplo.com", Perfil.ADMIN),
            new Tecnico("Patricia Rocha", "patricia@exemplo.com", Perfil.TECNICO),
            new Tecnico("Rafael Almeida", "rafael@exemplo.com", Perfil.TECNICO),
            new Tecnico("Sofia Pereira", "sofia@exemplo.com", Perfil.TECNICO)
        ));

        String[] titulos = {
            "Computador nao liga", "Impressora com papel preso", "Tela piscando",
            "Teclado com defeito", "Troca de monitor", "Falha ao abrir planilha",
            "Atualizacao do navegador", "Erro no sistema interno", "Instalacao de aplicativo",
            "Aplicativo fechando sozinho", "Internet instavel", "Sem acesso ao Wi-Fi",
            "VPN nao conecta", "Lentidao na rede", "Acesso a pasta compartilhada"
        };
        Prioridade[] prioridades = {
            Prioridade.ALTA, Prioridade.BAIXA, Prioridade.MEDIA, Prioridade.BAIXA,
            Prioridade.MEDIA, Prioridade.ALTA, Prioridade.BAIXA, Prioridade.CRITICA,
            Prioridade.MEDIA, Prioridade.ALTA, Prioridade.ALTA, Prioridade.MEDIA,
            Prioridade.CRITICA, Prioridade.MEDIA, Prioridade.BAIXA
        };

        for (int i = 0; i < titulos.length; i++) {
            Cliente cliente = listaClientes.get(i % listaClientes.size());
            Categoria categoria = listaCategorias.get(i / 5);
            Tecnico tecnico = i % 5 == 0 ? null : listaTecnicos.get(i % listaTecnicos.size());
            Chamado chamado = chamados.save(new Chamado(
                titulos[i], "Chamado demonstrativo: " + titulos[i] +
                ". Solicita-se avaliacao e atendimento da equipe de suporte.",
                prioridades[i], cliente, categoria, tecnico
            ));

            // As transicoes usam o servico real, gerando historico corretamente.
            switch (i % 4) {
                case 1 -> chamadoService.alterarStatus(chamado.getId(),
                    new StatusChamadoRequest(StatusChamado.EM_ATENDIMENTO));
                case 2 -> {
                    chamadoService.alterarStatus(chamado.getId(),
                        new StatusChamadoRequest(StatusChamado.EM_ATENDIMENTO));
                    chamadoService.alterarStatus(chamado.getId(),
                        new StatusChamadoRequest(StatusChamado.AGUARDANDO_CLIENTE));
                }
                case 3 -> chamadoService.alterarStatus(chamado.getId(),
                    new StatusChamadoRequest(StatusChamado.FECHADO));
                default -> { /* permanece ABERTO */ }
            }
        }
    }
}

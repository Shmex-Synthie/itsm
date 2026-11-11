package br.simpliticket.sistemas.chamados.web;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.*;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.service.*;
import br.simpliticket.sistemas.chamados.web.form.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/app/chamados")

public class ChamadoWebController {

    private final ChamadoService chamados;
    private final ClienteService clientes;
    private final CategoriaService categorias;
    private final TecnicoService tecnicos;
    private final ComentarioService comentarios;

    public ChamadoWebController(ChamadoService chamados, ClienteService clientes, CategoriaService categorias,
    TecnicoService tecnicos, ComentarioService comentarios) {
        this.chamados=chamados; this.clientes=clientes; this.categorias=categorias;
        this.tecnicos=tecnicos; this.comentarios=comentarios;
    }

    @GetMapping
    public String listar(@RequestParam(required=false) StatusChamado status,
                         @RequestParam(required=false) Prioridade prioridade,
                         @RequestParam(required=false) Long clienteId,
                         @RequestParam(defaultValue="0") int pagina, Model model) {
        int numeroPagina=Math.max(0,pagina);
        Page<ChamadoResponse> resultado=chamados.listar(status, prioridade, clienteId,
            PageRequest.of(numeroPagina, 15, Sort.by(Sort.Direction.DESC,"dataAbertura")));
        model.addAttribute("pagina",resultado);
        model.addAttribute("statusFiltro",status);
        model.addAttribute("prioridadeFiltro",prioridade);
        model.addAttribute("clienteFiltro",clienteId);
        model.addAttribute("statusDisponiveis",StatusChamado.values());
        model.addAttribute("prioridades",Prioridade.values());
        model.addAttribute("clientes",clientes.listar());
        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required=false) Long clienteId, Model model) {
        ChamadoForm f=new ChamadoForm(); f.setClienteId(clienteId); f.setPrioridade(Prioridade.MEDIA);
        model.addAttribute("form",f);
        return formulario(model,"Novo chamado","/app/chamados");
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ChamadoForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model,"Novo chamado","/app/chamados");
        try {
            ChamadoResponse c=chamados.criar(dto(form));
            redirect.addFlashAttribute("sucesso","Chamado criado.");
            return "redirect:/app/chamados/"+c.id();
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro",ex.getMessage());
            return formulario(model,"Novo chamado","/app/chamados");
        }

    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ChamadoResponse c=chamados.buscar(id); ChamadoForm f=new ChamadoForm();
        f.setTitulo(c.titulo()); f.setDescricao(c.descricao()); f.setPrioridade(c.prioridade());
        f.setClienteId(c.clienteId()); f.setCategoriaId(c.categoriaId()); f.setTecnicoId(c.tecnicoId());
        model.addAttribute("form",f);
        return formulario(model,"Editar chamado","/app/chamados/"+id+"/editar");
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ChamadoForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model,"Editar chamado","/app/chamados/"+id+"/editar");
        try {
            chamados.atualizar(id,dto(form));
            redirect.addFlashAttribute("sucesso","Chamado atualizado.");
            return "redirect:/app/chamados/"+id;
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro",ex.getMessage());
            return formulario(model,"Editar chamado","/app/chamados/"+id+"/editar");
        }

    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        ChamadoDetalheResponse detalhe=chamados.detalhe(id);
        model.addAttribute("detalhe",detalhe);
        model.addAttribute("tecnicos",tecnicos.listar());
        model.addAttribute("transicoes",proximos(detalhe.chamado().status()));
        if (!model.containsAttribute("comentarioForm")) model.addAttribute("comentarioForm",new ComentarioForm());
        return "chamados/detalhe";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id, @RequestParam StatusChamado status, RedirectAttributes redirect) {
        try {
            chamados.alterarStatus(id,new StatusChamadoRequest(status));
            redirect.addFlashAttribute("sucesso","Status atualizado.");
        }
        catch (RegraNegocioException ex) {redirect.addFlashAttribute("erro",ex.getMessage());}
        return "redirect:/app/chamados/"+id;
    }

    @PostMapping("/{id}/tecnico")
    public String atribuir(@PathVariable Long id, @RequestParam Long tecnicoId, RedirectAttributes redirect) {
        try {
            chamados.atribuir(id,new AtribuirChamadoRequest(tecnicoId));
            redirect.addFlashAttribute("sucesso","Técnico atribuído.");
        }
        catch (RegraNegocioException ex) {redirect.addFlashAttribute("erro",ex.getMessage());}
        return "redirect:/app/chamados/"+id;
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable Long id, @Valid @ModelAttribute("comentarioForm") ComentarioForm form, BindingResult errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            redirect.addFlashAttribute("erro","Preencha autor e comentário (máximo de 2000 caracteres).");
            return "redirect:/app/chamados/"+id;
        }
        comentarios.criar(id,new ComentarioRequest(form.getTexto(),form.getAutor()));
        redirect.addFlashAttribute("sucesso","Comentário adicionado.");
        return "redirect:/app/chamados/"+id;
    }

    private String formulario(Model model,String titulo,String acao) {
        model.addAttribute("titulo",titulo); model.addAttribute("acao",acao);
        model.addAttribute("clientes",clientes.listar()); model.addAttribute("categorias",categorias.listar());
        model.addAttribute("tecnicos",tecnicos.listar()); model.addAttribute("prioridades",Prioridade.values());
        return "chamados/form";
    }

    private ChamadoRequest dto(ChamadoForm f) {return new ChamadoRequest(f.getTitulo(),f.getDescricao(),f.getPrioridade(),f.getClienteId(),f.getCategoriaId(),f.getTecnicoId());}

    private List<StatusChamado> proximos(StatusChamado status) {
        return switch(status) {
            case ABERTO -> List.of(StatusChamado.EM_ATENDIMENTO,StatusChamado.FECHADO);
            case EM_ATENDIMENTO -> List.of(StatusChamado.AGUARDANDO_CLIENTE,StatusChamado.FECHADO);
            case AGUARDANDO_CLIENTE -> List.of(StatusChamado.EM_ATENDIMENTO,StatusChamado.FECHADO);
            case FECHADO -> List.of();
        };
    }

}

package br.simpliticket.sistemas.chamados.web;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.service.ClienteService;
import br.simpliticket.sistemas.chamados.web.form.ClienteForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/clientes")

public class ClienteWebController {

    private final ClienteService service;
    public ClienteWebController(ClienteService service) {this.service = service;}

    @GetMapping
    public String listar(Model model) {model.addAttribute("clientes", service.listar()); return "clientes/lista";}

    @GetMapping("/novo")
    public String novo(Model model) {model.addAttribute("form", new ClienteForm()); return formulario(model, "Novo cliente", "/app/clientes");}

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ClienteForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Novo cliente", "/app/clientes");
        try {
            service.criar(dto(form));
            redirect.addFlashAttribute("sucesso", "Cliente cadastrado.");
            return "redirect:/app/clientes";
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return formulario(model, "Novo cliente", "/app/clientes");
        }

    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {model.addAttribute("cliente", service.buscar(id)); return "clientes/detalhe";}

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ClienteResponse c=service.buscar(id); ClienteForm form=new ClienteForm();
        form.setNome(c.nome()); form.setEmail(c.email()); form.setTelefone(c.telefone()); model.addAttribute("form", form);
        return formulario(model, "Editar cliente", "/app/clientes/" + id + "/editar");
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ClienteForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Editar cliente", "/app/clientes/" + id + "/editar");
        try {
            service.atualizar(id, dto(form));
            redirect.addFlashAttribute("sucesso", "Cliente atualizado.");
            return "redirect:/app/clientes/" + id;
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return formulario(model, "Editar cliente", "/app/clientes/" + id + "/editar");
        }

    }

    private String formulario(Model model,String titulo,String acao) {
        model.addAttribute("titulo",titulo);
        model.addAttribute("acao",acao);
        return "clientes/form";
    }

    private ClienteRequest dto(ClienteForm f) {return new ClienteRequest(f.getNome(),f.getEmail(),f.getTelefone());}

}

package br.simpliticket.sistemas.chamados.web;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.entity.Perfil;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.service.TecnicoService;
import br.simpliticket.sistemas.chamados.web.form.TecnicoForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/tecnicos")

public class TecnicoWebController {

    private final TecnicoService service;
    public TecnicoWebController(TecnicoService service) {this.service = service;}

    @GetMapping
    public String listar(Model model) {model.addAttribute("tecnicos", service.listar()); return "tecnicos/lista";}

    @GetMapping("/novo")
    public String novo(Model model) {model.addAttribute("form", new TecnicoForm()); return formulario(model, "Novo técnico", "/app/tecnicos");}

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TecnicoForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Novo técnico", "/app/tecnicos");
        try {
            service.criar(dto(form));
            redirect.addFlashAttribute("sucesso", "Técnico cadastrado.");
            return "redirect:/app/tecnicos";
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return formulario(model, "Novo técnico", "/app/tecnicos");
        }

    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        TecnicoResponse t=service.buscar(id); TecnicoForm form=new TecnicoForm();
        form.setNome(t.nome()); form.setEmail(t.email()); form.setPerfil(t.perfil()); model.addAttribute("form", form);
        return formulario(model, "Editar técnico", "/app/tecnicos/" + id + "/editar");
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") TecnicoForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Editar técnico", "/app/tecnicos/" + id + "/editar");
        try {
            service.atualizar(id, dto(form));
            redirect.addFlashAttribute("sucesso", "Técnico atualizado.");
            return "redirect:/app/tecnicos";
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return formulario(model, "Editar técnico", "/app/tecnicos/" + id + "/editar");
        }

    }

    private String formulario(Model model,String titulo,String acao) {
        model.addAttribute("titulo",titulo);
        model.addAttribute("acao",acao);
        model.addAttribute("perfis",Perfil.values());
        return "tecnicos/form";
    }

    private TecnicoRequest dto(TecnicoForm f) {return new TecnicoRequest(f.getNome(),f.getEmail(),f.getPerfil());}

}

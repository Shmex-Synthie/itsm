package br.simpliticket.sistemas.chamados.web;

import br.simpliticket.sistemas.chamados.dto.*;
import br.simpliticket.sistemas.chamados.exception.RegraNegocioException;
import br.simpliticket.sistemas.chamados.service.CategoriaService;
import br.simpliticket.sistemas.chamados.web.form.CategoriaForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/categorias")

public class CategoriaWebController {

    private final CategoriaService service;
    public CategoriaWebController(CategoriaService service) {this.service = service;}

    @GetMapping
    public String listar(Model model) {model.addAttribute("categorias", service.listar()); return "categorias/lista";}

    @GetMapping("/nova")
    public String novo(Model model) {model.addAttribute("form", new CategoriaForm()); return formulario(model, "Nova categoria", "/app/categorias");}
    
    @PostMapping
    public String criar(@Valid @ModelAttribute("form") CategoriaForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Nova categoria", "/app/categorias");
        try {
            service.criar(dto(form)); 
            redirect.addFlashAttribute("sucesso", "Categoria cadastrada."); 
            return "redirect:/app/categorias";
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage()); 
            return formulario(model, "Nova categoria", "/app/categorias");
        }

    }
    
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CategoriaResponse c=service.buscar(id); CategoriaForm form=new CategoriaForm();
        form.setNome(c.nome()); form.setDescricao(c.descricao()); model.addAttribute("form", form);
        return formulario(model, "Editar categoria", "/app/categorias/" + id + "/editar");
    }
    
    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") CategoriaForm form, BindingResult errors, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) return formulario(model, "Editar categoria", "/app/categorias/" + id + "/editar");
        try {
            service.atualizar(id, dto(form)); 
            redirect.addFlashAttribute("sucesso", "Categoria atualizada."); 
            return "redirect:/app/categorias";
        }
        catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage()); 
            return formulario(model, "Editar categoria", "/app/categorias/" + id + "/editar");
        }

    }

    private String formulario(Model model,String titulo,String acao) {
        model.addAttribute("titulo",titulo); 
        model.addAttribute("acao",acao); 
        return "categorias/form";
    }
    
    private CategoriaRequest dto(CategoriaForm f) {return new CategoriaRequest(f.getNome(),f.getDescricao());}

}

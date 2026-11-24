package br.simpliticket.sistemas.chamados.config;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private record Janela(long inicioNanos, int quantidade) {}
    private final ConcurrentHashMap<String, Janela> contadores = new ConcurrentHashMap<>();
    private final AtomicLong requisicoes = new AtomicLong();
    private final int limite;
    private final long janelaNanos;
    private final boolean habilitado;

    public RateLimitFilter(
            @Value("${simpliticket.rate-limit.enabled:true}") boolean habilitado,
            @Value("${simpliticket.rate-limit.requests:10}") int limite,
            @Value("${simpliticket.rate-limit.window-seconds:60}") long janelaSegundos) {
        if (limite < 1 || janelaSegundos < 1 || janelaSegundos > 86400) {
            throw new IllegalArgumentException("Configuração de rate limiting inválida");
        }
        this.habilitado = habilitado;
        this.limite = limite;
        this.janelaNanos = Duration.ofSeconds(janelaSegundos).toNanos();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean apiChamados = path.equals("/chamados") || path.startsWith("/chamados/");
        // Apenas GET da listagem web: formulários e demais telas não são bloqueados.
        boolean listaWeb = "GET".equalsIgnoreCase(request.getMethod()) && path.equals("/app/chamados");
        return !habilitado || !(apiChamados || listaWeb);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        // Usa o endereço da conexão, sem confiar em X-Forwarded-For fornecido pelo cliente.
        String chave = request.getRemoteAddr();
        long agora = System.nanoTime();
        Janela atual = contadores.compute(chave, (ip, anterior) -> {
            if (anterior == null || agora - anterior.inicioNanos() >= janelaNanos) {
                return new Janela(agora, 1);
            }
            return new Janela(anterior.inicioNanos(), anterior.quantidade() + 1);
        });
        // Limpeza periódica de entradas inativas para impedir crescimento indefinido do mapa.
        if (requisicoes.incrementAndGet() % 200 == 0) {
            contadores.entrySet().removeIf(e -> agora - e.getValue().inicioNanos() >= janelaNanos);
        }
        if (atual.quantidade() > limite) {
            long restanteNanos = Math.max(0L, janelaNanos - (agora - atual.inicioNanos()));
            long segundos = Math.max(1L, (restanteNanos + 999_999_999L) / 1_000_000_000L);
            response.setHeader("Retry-After", Long.toString(segundos));
            response.setStatus(429);
            if ("/app/chamados".equals(request.getServletPath())) {
                // Encaminhamento interno à tela amigável sem redirecionar para outra URL.
                request.setAttribute("segundosEspera", segundos);
                request.getRequestDispatcher("/limite-requisicoes").forward(request, response);
            } else {
                // Mantém o contrato JSON anterior da API REST.
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\":429,\"erro\":\"Limite de requisições excedido. Tente novamente mais tarde.\"}");
            }
            return;
        }
        chain.doFilter(request, response);
    }
}

package br.simpliticket.sistemas.chamados.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTests {
    @Test
    void bloqueiaQuandoExcedeLimiteERetornaRetryAfter() throws Exception {
        var filtro = new RateLimitFilter(true, 2, 60);
        for (int i = 0; i < 2; i++) {
            var request = new MockHttpServletRequest("GET", "/chamados");
            request.setServletPath("/chamados");
            request.setRemoteAddr("192.0.2.10");
            var response = new MockHttpServletResponse();
            var chain = new MockFilterChain();
            filtro.doFilter(request, response, chain);
            assertNotNull(chain.getRequest());
            assertEquals(200, response.getStatus());
        }
        var request = new MockHttpServletRequest("GET", "/chamados");
        request.setServletPath("/chamados");
        request.setRemoteAddr("192.0.2.10");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();
        filtro.doFilter(request, response, chain);
        assertEquals(429, response.getStatus());
        assertNull(chain.getRequest());
        assertNotNull(response.getHeader("Retry-After"));
        assertTrue(response.getContentAsString().contains("Limite de requisições"));
    }

    @Test
    void naoInterfereEmOutrasRotas() throws Exception {
        var filtro = new RateLimitFilter(true, 1, 60);
        for (int i = 0; i < 3; i++) {
            var request = new MockHttpServletRequest("GET", "/clientes");
            var response = new MockHttpServletResponse();
            var chain = new MockFilterChain();
            filtro.doFilter(request, response, chain);
            assertNotNull(chain.getRequest());
        }
    }

    @Test
    void separaContagemPorEnderecoRemoto() throws Exception {
        var filtro = new RateLimitFilter(true, 1, 60);
        for (String ip : new String[]{"192.0.2.1", "192.0.2.2"}) {
            var request = new MockHttpServletRequest("GET", "/chamados");
            request.setServletPath("/chamados");
            request.setRemoteAddr(ip);
            var response = new MockHttpServletResponse();
            var chain = new MockFilterChain();
            filtro.doFilter(request, response, chain);
            assertNotNull(chain.getRequest());
        }
    }
    @Test
    void mostraTelaQuandoListaWebExcedeLimite() throws Exception {
        var filtro = new RateLimitFilter(true, 1, 60);
        var primeira = new MockHttpServletRequest("GET", "/app/chamados");
        primeira.setServletPath("/app/chamados");
        primeira.setRemoteAddr("192.0.2.20");
        var respostaInicial = new MockHttpServletResponse();
        var primeiraChain = new MockFilterChain();
        filtro.doFilter(primeira, respostaInicial, primeiraChain);
        assertNotNull(primeiraChain.getRequest());

        var segunda = new MockHttpServletRequest("GET", "/app/chamados");
        segunda.setServletPath("/app/chamados");
        segunda.setRemoteAddr("192.0.2.20");
        var respostaBloqueada = new MockHttpServletResponse();
        var segundaChain = new MockFilterChain();
        filtro.doFilter(segunda, respostaBloqueada, segundaChain);
        assertEquals(429, respostaBloqueada.getStatus());
        assertNotNull(respostaBloqueada.getHeader("Retry-After"));
        assertEquals("/limite-requisicoes", respostaBloqueada.getForwardedUrl());
        assertNull(segundaChain.getRequest());
    }

    @Test
    void naoBloqueiaPostDaInterfaceWeb() throws Exception {
        var filtro = new RateLimitFilter(true, 1, 60);
        for (int i = 0; i < 2; i++) {
            var request = new MockHttpServletRequest("POST", "/app/chamados");
            request.setServletPath("/app/chamados");
            var chain = new MockFilterChain();
            filtro.doFilter(request, new MockHttpServletResponse(), chain);
            assertNotNull(chain.getRequest());
        }
    }
}

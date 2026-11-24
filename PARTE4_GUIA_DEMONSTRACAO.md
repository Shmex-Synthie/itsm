# Parte 4 — Resiliência e proteção contra sobrecarga

## O que foi observado no projeto-base
- `ChamadoController`: GET `/chamados` já usa `Pageable` e tamanho padrão 20.
- `GlobalExceptionHandler`: centraliza tratamento de erros de negócio e validação.
- Não foi identificado filtro dedicado de rate limiting no código recebido.
- `application.properties` configura H2 em memória; ausência de métricas de disponibilidade impede inferir falha real de produção.

## O que foi acrescentado
- `config/RateLimitFilter.java`: janela fixa por endereço IP remoto, restrita à API `/chamados` e subrotas.
- Três parâmetros em `application.properties`, habilitação, quantidade e segundos.
- Resposta 429 com JSON e cabeçalho `Retry-After` quando exceder o limite.
- `RateLimitFilterTests.java`: testes de limite, escopo de rotas e isolamento por IP.

## Como demonstrar localmente
1. Na pasta do projeto, executar `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
2. Abrir um terminal **PowerShell** e executar:

```powershell
1..12 | ForEach-Object {
  $status = curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/chamados
  Write-Host "Requisição $_ => HTTP $status"
}
```

3. Esperado: até 10 respostas normais (tipicamente 200), seguidas de HTTP 429 dentro da mesma janela de 60 s, considerando que não houve requisições anteriores desse IP.
4. Para observar a mensagem e o `Retry-After`, execute `curl.exe -i http://localhost:8080/chamados` enquanto o limite estiver excedido.
5. Após terminar a janela, tente novamente para demonstrar recuperação automática.
6. Executar testes automatizados com `./mvnw test` (ou `mvnw.cmd test`).

**Importante:** saída esperada não equivale a execução comprovada. Registrar prints reais do seu ambiente. O teste de rate limiting não é um teste de carga.

## Limitações e decisões
- Armazenamento em memória por instância: cada servidor conta separadamente, portanto não há limite global em implantação horizontal.
- Identificação por IP: usuários atrás de NAT podem compartilhar limite e um proxy pode mascarar IPs.
- Janela fixa: permite rajadas próximas à virada; estratégia produtiva pode exigir token bucket/sliding window.
- O filtro não gerencia carga do banco de dados, não implementa timeout nem circuit breaker.
- Para produção: validar identidade da origem em proxy confiável, preferir gateway/distributed store (ex.: Redis), regras por endpoint/usuário, observabilidade e testes controlados.
- O protótipo aplica limite a GET e escritas de `/chamados` para uma prova simples; em produção, políticas diferentes para operações críticas podem ser necessárias.

## Pontos para o relatório (sem afirmar problemas não comprovados)
1. Risco potencial: ausência de limitação de solicitações pode aumentar pressão sobre aplicação e banco.
2. Risco potencial: consultas e integrações lentas podem consumir threads sem orçamento explícito por operação.
3. Oportunidade: contabilizar 429, latência p95/p99 e falhas para ajustar limites e identificar impactos em usuários legítimos.

## Recomendações priorizadas
- Curto prazo: rate limiting configurável e observabilidade básica; validar por teste controlado.
- Médio prazo: timeout por integração externa e medição de latência/erros antes de ajustar recursos.
- Longo prazo: rate limiting distribuído no gateway caso haja múltiplas instâncias, com limites por identidade e políticas de exceção.

## Fontes técnicas
- Spring Framework — OncePerRequestFilter: https://docs.spring.io/spring-framework/reference/web/webmvc/filters.html
- MDN — HTTP 429: https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Reference/Status/429
- MDN — Retry-After: https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Reference/Headers/Retry-After

## SimpliTicket - Sistema de Chamados

Sistema de chamados baseado inteiramente em Java, com o uso de APIs REST e banco de dados H2, utilizando-se do Spring Starter.

## Exemplos de consultas

A API utiliza `http://localhost:8080` como endereço base.

## Cadastro de técnico

`POST /tecnicos`

```json
{"nome":"Maria","email":"maria@exemplo.com","perfil":"TECNICO"}
```

## Abertura de chamado

`POST /chamados`

```json
{"titulo":"Computador não liga","descricao":"Equipamento não inicia","prioridade":"ALTA","clienteId":1,"categoriaId":1,"tecnicoId":null}
```

Os campos `clienteId` e `categoriaId` devem corresponder a registros cadastrados. `tecnicoId` é opcional.

## Atribuição

`PATCH /chamados/1/atribuir`

```json
{"tecnicoId":1}
```

## Mudança de status

`PATCH /chamados/1/status`

```json
{"status":"EM_ATENDIMENTO"}
```

Transições permitidas: `ABERTO` para `EM_ATENDIMENTO` ou `FECHADO`; `EM_ATENDIMENTO` para `AGUARDANDO_CLIENTE` ou `FECHADO`; `AGUARDANDO_CLIENTE` para `EM_ATENDIMENTO` ou `FECHADO`. Chamados em estado `FECHADO` não podem ser alterados.

## Filtros e paginação

- `GET /chamados?status=ABERTO&prioridade=ALTA&clienteId=1`
- `GET /chamados?status=EM_ATENDIMENTO&page=0&size=10&sort=dataAbertura,desc`
- `GET /chamados?prioridade=CRITICA&page=0&size=5&sort=prioridade,desc`
- `GET /chamados?clienteId=1&sort=titulo,asc`
- `GET /clientes/1/chamados?page=0&size=10&sort=dataAbertura,desc`
- `GET /chamados/1/detalhe`

Filtros são opcionais e podem ser combinados.

## Comentários

`POST /chamados/1/comentarios`

```json
{"texto":"Verificação iniciada","autor":"Maria"}
```

`GET /chamados/1/comentarios` retorna os comentários ordenados pela data. `GET /chamados/1/detalhe` retorna chamado, comentários e histórico de mudanças de status.

## Testes

Executar `./mvnw test` (Linux/macOS) ou `mvnw.cmd test` (Windows), com Java 21 e acesso às dependências Maven.

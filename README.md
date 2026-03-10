# URL Shortener

Encurtador de URLs com geração de QR Code, cache em Redis e rate limit por IP.

- **Front-end (Vercel / domínio próprio):** https://shortlink.planumlabs.com
- **Back-end (Render / subdomínio):** https://go.planumlabs.com (o domínio público do back-end também é configurado via `BASE_URL`, usado para montar o `shortUrl` retornado pela API).

## Principais features

- Criar short URL **aleatória** ou **customizada**
- Redirecionamento `302 Found` para a URL original
- QR Code (visualização e download)
- Cache em Redis para resolver URLs rapidamente
- Contador de cliques (armazenado no Redis)
- Rate limit por IP (padrão: **10 req/min**) usando Redis
- OpenAPI/Swagger

## Stack

- Java 17
- Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL
- Flyway (migrações em `src/main/resources/db/migration`)
- Redis (cache, cliques e rate limit)
- ZXing (QR Code)
- springdoc-openapi (Swagger)
- JUnit 5 + Mockito

## Arquitetura

O projeto segue **bem de perto** a ideia de Arquitetura Hexagonal (Ports & Adapters):

- **Entrada (Inbound adapters):** controllers REST em `urlshortener.adapters.in.*`
- **Aplicação (Use cases):** serviços em `urlshortener.application.service.*`
- **Domínio:** modelo e portas em `urlshortener.domain.*`
- **Saída (Outbound adapters):** persistência JPA em `urlshortener.adapters.out.persistence.*`
- **Infra:** segurança/CORS/Redis config/utilitários em `urlshortener.infrastructure.*`

### Observações (onde foge do “hexagonal puro”)

- `urlshortener.domain.model.ShortUrl` é uma entidade JPA (`@Entity`). Em uma hexagonal “estrita”, o domínio não teria dependência de frameworks (JPA/Hibernate).
- `urlshortener.application.service.ShortUrlService` depende de `urlshortener.infrastructure.util.ShortCodeGenerator`. Em um desenho mais estrito, isso poderia ser um componente da camada de aplicação ou um **port** (para facilitar testes/trocas).
- `urlshortener.application.service.RedisService` encapsula Redis (infra), mas está em `application/service`. Funciona, só é uma fronteira um pouco mais “flexível”.

Mesmo assim, a direção de dependências principal (controller → app → ports → adapters) está bem alinhada.

## API

A API expõe os endpoints abaixo (também documentados no Swagger).

### Criar short URL aleatória

`POST /short-urls`

Body:

```json
{
  "originalUrl": "https://example.com",
  "customShortCode": null
}
```

Response `201`:

```json
{
  "shortUrl": "https://<BASE_URL>/<code>",
  "code": "abc12",
  "originalUrl": "https://example.com"
}
```

### Criar short URL customizada

`POST /short-urls/custom`

Body:

```json
{
  "originalUrl": "https://example.com",
  "customShortCode": "meu-link"
}
```

Notas:
- O código é normalizado com `trim()`, `toLowerCase()` e remoção de espaços.

### Redirecionar

`GET /{code}`

- Retorna `302 Found` com header `Location: <originalUrl>`.
- Incrementa cliques e usa cache quando disponível.

### QR Code

- `GET /{code}/qr-code` → `image/png`
- `GET /{code}/qr-code/download` → `image/png` com `Content-Disposition: attachment`

## Erros (formato padrão)

Erros de validação e negócio retornam um JSON no formato:

```json
{
  "status": 400,
  "message": "Invalid URL",
  "timestamp": "2026-03-09T12:34:56"
}
```

Casos principais:
- `400` — URL inválida / código custom em branco
- `404` — short code não encontrado
- `409` — short code já existe
- `429` — rate limit excedido (texto simples: `Too many requests`)

## Redis (cache, cliques e rate limit)

O Redis é usado para:

- **Cache de resolução**: `url:{code}` → `<originalUrl>`
- **Cliques**: `clicks:{code}` → contador
- **Rate limit**: `ratelimit:{ip}` → contador com TTL de 1 minuto

Rate limit atual: **10 requisições por minuto por IP**.

> Em ambientes com proxy/CDN, o filtro usa `X-Forwarded-For` para resolver o IP do cliente.

## Swagger / OpenAPI

- Swagger UI: `GET /swagger-ui/index.html`
- OpenAPI JSON: `GET /v3/api-docs`

## Configuração (variáveis de ambiente)

A aplicação lê as configurações via variáveis de ambiente (recomendado para deploy no Render):

- `DB_URL_JDBC` — ex.: `jdbc:postgresql://.../dbname?sslmode=require`
- `DB_USER`
- `DB_PASSWORD`
- `REDIS_URL` — ex.: `redis://...` ou `rediss://...`
- `BASE_URL` — **URL pública do back-end** (Render), usada para compor o `shortUrl` retornado pela API (ex.: `https://go.planumlabs.com`)

## Rodando localmente

### 1) Maven (dev)

```bash
./mvnw test
./mvnw spring-boot:run
```

A API sobe por padrão em `http://localhost:8080`.

### 2) Docker

Build e run:

```bash
docker build -t url-shortener .
docker run -p 8080:8080 \
  -e DB_URL_JDBC="..." \
  -e DB_USER="..." \
  -e DB_PASSWORD="..." \
  -e REDIS_URL="..." \
  -e BASE_URL="http://localhost:8080" \
  url-shortener
```

> O `Dockerfile` faz `package -DskipTests`. Para validar testes, rode `./mvnw test` antes do build.

## Testes

A suíte inclui testes para controller, services e componentes de infra (rate limit):

```bash
./mvnw test
```

Arquivos de teste em `src/test/java`.

## Saúde (Healthcheck)

- `GET /actuator/health`

## CORS

Por padrão, o CORS permite origem:

- `https://shortlink.planumlabs.com`

Se você for rodar outro front-end/domínio, ajuste a lista em `SecurityConfig`.

## Contribuindo

Projeto **open source** — PRs são bem-vindos.

Sugestão de fluxo:

1. Abra uma issue descrevendo a mudança (bug/feature)
2. Faça fork e crie uma branch
3. Inclua/atualize testes quando fizer sentido
4. Abra um Pull Request com descrição e passos para reproduzir

## Segurança

- **Não comite segredos** (URLs com credenciais, senhas, tokens). Prefira sempre variáveis de ambiente.
- Revise permissões de CORS ao expor a API publicamente.

---

Feito com Spring Boot + Redis + PostgreSQL.

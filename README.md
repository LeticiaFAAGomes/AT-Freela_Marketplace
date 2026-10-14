# 💼 Freela Marketplace — Microsserviços com Kafka

### Marketplace de Freelancers com Spring Boot, Spring Cloud, Domain-Driven Design e Comunicação Assíncrona

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Microsserviços-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Eventos-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

O **Freela Marketplace** é uma aplicação distribuída para gerenciamento de contratos entre clientes e freelancers. Desenvolvido com Java, Spring Boot e Spring Cloud, o projeto utiliza arquitetura de microsserviços e Apache Kafka para comunicação assíncrona baseada em eventos.

A solução aplica conceitos de **Domain-Driven Design (DDD), Transactional Outbox, idempotência, particionamento por chave, tolerância a falhas, auditoria e rastreamento distribuído**, buscando garantir maior confiabilidade e desacoplamento entre os serviços.

## 📌 Objetivos

- Desenvolver uma aplicação distribuída com arquitetura de microsserviços.
- Separar as responsabilidades de contratos, notificações, reputação e auditoria.
- Implementar o ciclo de vida dos contratos com regras de domínio.
- Aplicar conceitos de Domain-Driven Design (DDD).
- Implementar comunicação assíncrona com Apache Kafka.
- Utilizar o padrão Transactional Outbox para publicação confiável de eventos.
- Identificar eventos por meio de UUIDs únicos.
- Utilizar o identificador do contrato como chave de particionamento.
- Implementar idempotência nos consumidores.
- Configurar tentativas de reprocessamento e Dead Letter Topic (DLT).
- Registrar eventos para auditoria e rastreabilidade.
- Atualizar informações agregadas sobre os freelancers.
- Utilizar Eureka para registro e descoberta de serviços.
- Centralizar o acesso HTTP por meio do API Gateway.
- Utilizar PostgreSQL com bancos separados por microsserviço.
- Aplicar logs estruturados, correlação de requisições e tracing distribuído.
- Executar a infraestrutura local com Docker Compose.

## 💼 Sobre o Sistema

O marketplace representa um cenário no qual clientes contratam freelancers para executar trabalhos. O `contrato-service` gerencia os contratos e publica eventos de domínio que são consumidos pelos demais serviços.

O sistema é composto por seis aplicações Spring Boot:

| Aplicação | Responsabilidade |
|---|---|
| `eureka-server` | Registro e descoberta dos microsserviços |
| `api-gateway` | Entrada HTTP e roteamento das requisições |
| `contrato-service` | Gerenciamento dos contratos e publicação de eventos |
| `notificacao-service` | Processamento e persistência de notificações |
| `reputacao-service` | Agregação dos contratos concluídos por freelancer |
| `auditoria-service` | Registro e consulta dos eventos processados |

A comunicação entre o produtor e os consumidores ocorre por meio do Kafka. Dessa forma, o processamento de notificações, reputação e auditoria não precisa ocorrer por chamadas HTTP síncronas ao serviço de contratos.

## 🏛️ Arquitetura

```text
                        CLIENTE
                           |
                           | REST / HTTP
                           v
                    API GATEWAY :8080
                           |
                           v
                   CONTRATO-SERVICE :8081
                           |
                    Transação de domínio
                           |
                 +---------+---------+
                 |                   |
                 v                   v
             Contrato           Outbox Events
                                     |
                                     v
                              OutboxPublisher
                                     |
                                     v
                            APACHE KAFKA :9092
                              contrato.eventos
                                     |
                   +-----------------+-----------------+
                   |                 |                 |
                   v                 v                 v
           NOTIFICACAO-SERVICE  REPUTACAO-SERVICE  AUDITORIA-SERVICE
                :8082               :8083              :8084
                   |                 |                 |
                   v                 v                 v
             notificacao_db      reputacao_db       auditoria_db

                 EUREKA SERVER :8761
                 Descoberta de serviços

                 POSTGRESQL :5432
                 Persistência relacional

                 ZIPKIN :9411
                 Rastreamento distribuído
```

A arquitetura separa as responsabilidades de cada serviço e utiliza eventos para propagar mudanças do domínio. O PostgreSQL mantém bancos independentes por serviço, enquanto o Kafka transporta os eventos de domínio.

## 🔌 Portas e Endpoints Locais

| Serviço ou ferramenta | Porta | Endereço |
|---|---:|---|
| API Gateway | 8080 | `http://localhost:8080` |
| Contrato Service | 8081 | `http://localhost:8081` |
| Notificação Service | 8082 | `http://localhost:8082` |
| Reputação Service | 8083 | `http://localhost:8083` |
| Auditoria Service | 8084 | `http://localhost:8084` |
| Eureka Server | 8761 | `http://localhost:8761` |
| Apache Kafka | 9092 | `localhost:9092` |
| Kafka UI | 8090 | `http://localhost:8090` |
| PostgreSQL | 5432 | `localhost:5432` |
| Zipkin | 9411 | `http://localhost:9411` |
| Grafana | 3000 | `http://localhost:3000`, se configurado |
| Loki | 3100 | `http://localhost:3100`, se configurado |

As portas correspondem ao ambiente local documentado. Os serviços de observabilidade devem estar definidos no Compose ou em outra configuração efetivamente utilizada para que seus endereços estejam disponíveis.

## 🧩 Componentes da Aplicação

### `eureka-server`

Fornece o registro e a descoberta dos microsserviços, permitindo que as aplicações se registrem e sejam localizadas pelo nome.

### `api-gateway`

Centraliza o acesso HTTP, encaminha requisições para os serviços e participa da propagação do cabeçalho `X-Correlation-Id`.

### `contrato-service`

Implementa as regras do domínio dos contratos, controla as transições de estado e registra os eventos na Transactional Outbox.

### `notificacao-service`

Consome eventos de domínio e persiste os registros de notificação no banco `notificacao_db`.

### `reputacao-service`

Consome eventos de contratos concluídos e atualiza os dados agregados dos freelancers, registrando os eventos processados para apoiar a idempotência.

### `auditoria-service`

Consome eventos do Kafka e mantém um histórico com identificadores, tipos, dados do contrato, payload e informações de correlação disponíveis.

## 📄 Contrato Service

O `contrato-service` é o núcleo do domínio da aplicação. Ele gerencia os contratos entre clientes e freelancers, aplica regras de negócio e produz eventos para os demais serviços.

### Responsabilidades

- Criar e consultar contratos.
- Registrar a entrega de um contrato.
- Concluir contratos respeitando as regras de domínio.
- Gerar eventos de domínio.
- Persistir eventos pendentes na Outbox.
- Publicar os eventos no Kafka.
- Utilizar o identificador do contrato como chave da mensagem.

### Ciclo de vida do contrato

Os estados documentados são:

- `ATIVO`
- `ENTREGA_REGISTRADA`
- `CONCLUIDO`
- `CANCELADO`

Fluxo principal:

```text
      ATIVO
        |
        v
ENTREGA_REGISTRADA
        |
        v
    CONCLUIDO
```

O cancelamento depende das regras de domínio implementadas.

### Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/contratos` | Cria um contrato |
| `GET` | `/api/contratos` | Lista contratos |
| `GET` | `/api/contratos/{id}` | Consulta um contrato |
| `PUT` | `/api/contratos/{id}/entrega` | Registra a entrega |
| `PUT` | `/api/contratos/{id}/concluir` | Conclui um contrato |

### Exemplo de criação de contrato

```json
{
  "clienteId": "11111111-1111-1111-1111-111111111111",
  "freelancerId": "22222222-2222-2222-2222-222222222222",
  "titulo": "Construção de API de pagamentos",
  "valor": 3500.00
}
```

Os identificadores são exemplos ilustrativos.

## 🏛️ Domain-Driven Design (DDD)

O `contrato-service` utiliza uma organização inspirada em Domain-Driven Design, separando regras de negócio, casos de uso e detalhes de infraestrutura.

Estrutura conceitual:

```text
contrato-service/
└── src/main/java/br/com/freela/contrato/
    ├── application/
    ├── domain/
    │   ├── event/
    │   ├── model/
    │   ├── repository/
    │   └── shared/
    └── infrastructure/
        ├── persistence/
        └── web/
```

### Conceitos aplicados

- **Domínio:** concentra regras de negócio e estados válidos.
- **Aggregate:** o agregado `Contrato` controla as transições do contrato.
- **Domain Events:** representam acontecimentos relevantes no domínio.
- **Application Services:** coordenam os casos de uso.
- **Repository:** abstrai o acesso aos dados.
- **Infrastructure:** implementa persistência, comunicação com Kafka e interfaces HTTP.

### Eventos de domínio

Os principais eventos do ciclo de vida são:

- `ContratoCriado`
- `EntregaRegistrada`
- `ContratoConcluido`

Esses eventos representam mudanças relevantes e permitem que os consumidores reajam de forma assíncrona.

## 🔄 Comunicação Assíncrona com Apache Kafka

O Kafka distribui eventos entre os serviços consumidores sem exigir chamadas HTTP diretas para cada processamento.

### Tópicos

| Tópico | Finalidade |
|---|---|
| `contrato.eventos` | Transporte dos eventos de domínio |
| `contrato.eventos.DLT` | Destino de mensagens que esgotam as tentativas de processamento, conforme configuração do consumidor |

O tópico principal está configurado no ambiente de desenvolvimento com três partições, conforme a configuração documentada do projeto.

Cada serviço consumidor utiliza seu próprio `group.id`, para que notificações, reputação e auditoria recebam independentemente os eventos relevantes.

### Chave de particionamento

A chave da mensagem é o identificador do contrato (`contratoId` ou `aggregateId`).

Isso permite encaminhar eventos de um mesmo contrato à mesma partição quando a chave e a estratégia de particionamento permanecem consistentes. A ordem de leitura é preservada dentro de uma partição, mas a ordem global entre partições distintas não é garantida.

### Especificação dos eventos

Os eventos compartilham identificadores e metadados para facilitar o processamento e a rastreabilidade.

| Campo | Descrição |
|---|---|
| `eventId` | Identificador único do evento |
| `eventType` | Tipo do evento de domínio |
| `contratoId` | Identificador do contrato relacionado |
| `occurredAt` | Instante em que o evento ocorreu |
| `correlationId` | Identificador de correlação, quando disponível |

Os campos específicos dependem do tipo de evento.

#### `ContratoCriado`

Consumidores: `notificacao-service` e `auditoria-service`.

Campos específicos esperados: `clienteId`, `freelancerId`, `titulo` e `valor`.

#### `EntregaRegistrada`

Consumidores: `notificacao-service` e `auditoria-service`.

Contém os identificadores do contrato, cliente e freelancer, além dos metadados comuns.

#### `ContratoConcluido`

Consumidores: `notificacao-service`, `reputacao-service` e `auditoria-service`.

Inclui o identificador do freelancer e o valor do contrato, necessários à atualização da reputação.

Os formatos definitivos devem corresponder aos eventos realmente serializados pelo produtor.

## 📤 Transactional Outbox

O padrão Transactional Outbox reduz o risco de inconsistência entre a gravação do contrato e a publicação do evento.

### Funcionamento

1. O serviço executa a operação de domínio.
2. O contrato e o evento pendente são gravados na mesma transação.
3. O `OutboxPublisher` consulta os registros ainda não publicados.
4. O evento é enviado ao Kafka.
5. O produtor aguarda a confirmação do envio.
6. Após a confirmação, o registro recebe o horário de publicação em `published_at`.

A tabela `outbox_events` mantém informações como:

- `eventId`
- `aggregateId`
- `eventType`
- `payload`
- `occurredAt`
- `publishedAt`

O campo `published_at` permanece nulo enquanto o evento estiver pendente.

O padrão evita depender exclusivamente de uma publicação imediata durante a requisição HTTP. Entretanto, uma falha após o envio ao Kafka e antes da atualização da Outbox pode provocar uma nova publicação. Por isso, os consumidores também precisam tratar mensagens duplicadas.

## 📨 Notificação Service

O `notificacao-service` processa eventos recebidos do Kafka e persiste notificações no banco `notificacao_db`.

### Responsabilidades

- Consumir eventos de `contrato.eventos`.
- Processar os tipos de evento relevantes para notificações.
- Persistir os registros de notificação.
- Identificar eventos já processados.
- Evitar duplicações de registros.
- Aplicar a configuração de tratamento de falhas do consumidor.

### Banco de dados

Banco: `notificacao_db`

A persistência permite consultar os registros gerados a partir dos eventos do marketplace.

### Tratamento de falhas

A configuração de consumo pode incluir tentativas de reprocessamento e encaminhamento de mensagens que continuam falhando para um tópico DLT.

A quantidade de tentativas, o intervalo, o tópico de destino e o comportamento após falhas devem corresponder à configuração efetiva do consumidor.

## ⭐ Reputação Service

O `reputacao-service` mantém informações agregadas sobre os freelancers com base nos contratos concluídos.

### Responsabilidades

- Consumir eventos `ContratoConcluido`.
- Identificar o freelancer associado ao contrato.
- Incrementar a quantidade de contratos concluídos.
- Acumular o valor dos contratos concluídos.
- Registrar os eventos processados para apoiar a idempotência.

### Dados agregados

A tabela `reputacoes` mantém:

| Campo | Descrição |
|---|---|
| `freelancer_id` | Identificador do freelancer |
| `contratos_concluidos` | Quantidade de contratos concluídos contabilizados |
| `valor_total` | Valor acumulado dos contratos concluídos |

A tabela `eventos_processados` mantém os identificadores dos eventos já processados.

### Endpoint

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/reputacoes` | Consulta os dados agregados de reputação |

### Verificação no PostgreSQL

```sql
SELECT freelancer_id, contratos_concluidos, valor_total
FROM reputacoes;

SELECT event_id
FROM eventos_processados;
```

Essas consultas permitem verificar as atualizações persistidas e os eventos registrados pelo serviço.

## 🧾 Auditoria Service

O `auditoria-service` registra os eventos recebidos do Kafka, oferecendo histórico e rastreabilidade do processamento.

### Responsabilidades

- Consumir eventos de domínio.
- Registrar o identificador único do evento.
- Armazenar o identificador do contrato relacionado.
- Registrar o tipo do evento e o payload recebido.
- Persistir a correlação quando disponível.
- Evitar duplicações de registros por evento.

### Dados de auditoria

A tabela `auditoria_eventos` armazena informações como:

| Campo | Descrição |
|---|---|
| `id` | Identificador do registro de auditoria |
| `aggregate_id` | Identificador do contrato |
| `event_id` | Identificador único do evento |
| `event_type` | Tipo do evento |
| `recebido_em` | Data e hora de recebimento |
| Payload | Conteúdo original do evento, conforme entidade |

### Endpoint

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/auditoria` | Consulta os registros de auditoria |

### Verificação no PostgreSQL

```sql
SELECT id, aggregate_id, event_id, event_type, recebido_em
FROM auditoria_eventos
ORDER BY recebido_em DESC;
```

Para consultar um contrato específico:

```sql
SELECT event_id, event_type, recebido_em
FROM auditoria_eventos
WHERE aggregate_id = 'ID_DO_CONTRATO'
ORDER BY recebido_em;
```

Substitua `ID_DO_CONTRATO` pelo identificador real.

## 🔁 Idempotência e Confiabilidade

Em sistemas distribuídos, mensagens podem ser entregues mais de uma vez. Os consumidores devem identificar eventos já processados antes de executar operações que alteram o estado.

O projeto utiliza `eventId` como identificador único para apoiar a idempotência.

### Estratégias aplicadas

- Identificação dos eventos por UUID.
- Verificação de eventos já processados.
- Persistência dos identificadores processados.
- Uso de restrições de unicidade, conforme configuração do banco.
- Registro dos eventos processados pela reputação.
- Tratamento de falhas e tentativas de reprocessamento.

A idempotência precisa ser garantida pela implementação transacional e pelas restrições de banco de dados. A simples existência de um registro de evento não comprova, por si só, que todas as operações relacionadas são atômicas.

O Kafka não garante que cada mensagem seja entregue uma única vez em todas as situações. A aplicação deve estar preparada para reentregas.

## 🔁 Tratamento de Falhas e Dead Letter Topic

O tratamento de falhas evita que mensagens com problemas sejam descartadas silenciosamente.

A configuração dos consumidores pode incluir:

1. Recebimento do evento.
2. Tentativa de processamento.
3. Repetição em caso de erro recuperável.
4. Encaminhamento para `contrato.eventos.DLT` após o esgotamento das tentativas.
5. Análise posterior da mensagem e da causa da falha.

O projeto documenta três tentativas com intervalo de um segundo e utilização de DLT. Esses parâmetros precisam ser conferidos nas configurações efetivas dos consumidores.

A DLT permite diagnosticar mensagens que não foram processadas normalmente. O reprocessamento posterior deve considerar a causa da falha e o controle de idempotência.

## 🆔 Correlação e Logs

O cabeçalho `X-Correlation-Id` identifica requisições relacionadas e pode ser propagado ao longo do fluxo.

Exemplo:

```http
X-Correlation-Id: teste-contrato-001
```

O identificador pode ser incluído no evento de domínio para permitir a associação entre a requisição original e os logs dos consumidores.

### Informações úteis para os logs

- Data e horário.
- Nível de severidade.
- Nome do serviço.
- Thread e logger.
- `correlationId`, quando disponível.
- `eventId`, quando disponível.
- `contratoId`, quando disponível.
- Resultado do processamento.

Os logs não devem registrar senhas, tokens ou outros dados sensíveis.

## 📈 Observabilidade com Zipkin

O projeto inclui configuração de tracing com Micrometer Tracing e Zipkin para apoiar o acompanhamento das requisições distribuídas.

Interface local:

`http://localhost:9411`

O rastreamento ajuda a analisar latência, chamadas entre componentes e falhas.

Como a publicação pela Outbox ocorre de forma assíncrona, o processamento HTTP e o processamento Kafka podem aparecer em traces distintos. O `correlationId` pode ajudar a relacionar os registros.

A presença do Zipkin não comprova, por si só, que todos os serviços estão enviando spans. É necessário verificar a configuração de exportação e a chegada dos traces na interface.

## 📊 Logs Centralizados com Loki e Grafana

Caso estejam habilitados na infraestrutura local, Loki e Grafana podem ser utilizados para centralizar e consultar logs dos serviços.

Endereços usuais:

- Grafana: `http://localhost:3000`
- Loki: `http://localhost:3100`

A consulta depende da configuração da fonte de dados Loki e dos rótulos efetivamente enviados pelos serviços.

Exemplo de consulta LogQL, caso o rótulo `app` esteja configurado:

```logql
{app=~".+"} |= "teste-contrato-001"
```

A configuração real de Loki, Grafana, appenders e rótulos deve ser conferida no Compose e nos arquivos de configuração antes de considerar essa funcionalidade concluída.

## 🐘 PostgreSQL

O PostgreSQL mantém bancos independentes para os microsserviços.

| Banco | Serviço |
|---|---|
| `contrato_db` | `contrato-service` |
| `notificacao_db` | `notificacao-service` |
| `reputacao_db` | `reputacao-service` |
| `auditoria_db` | `auditoria-service` |

### Configuração local padrão

| Propriedade | Valor |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Usuário | `freela` |
| Senha | `freela` |

Esses valores são destinados ao ambiente local documentado. Para outros ambientes, utilize variáveis de ambiente e credenciais apropriadas.

Script de inicialização:

```text
infra/postgres/init-databases.sql
```

### Consultas úteis

Verificar as tabelas de auditoria:

```sql
SELECT event_id, aggregate_id, event_type, recebido_em
FROM auditoria_eventos
ORDER BY recebido_em DESC;
```

Verificar reputações:

```sql
SELECT freelancer_id, contratos_concluidos, valor_total
FROM reputacoes;
```

Verificar eventos processados:

```sql
SELECT event_id
FROM eventos_processados;
```

## 🐳 Infraestrutura com Docker Compose

A infraestrutura local está definida em:

```text
infra/docker-compose.yml
```

O ambiente inclui PostgreSQL, Kafka, Kafka UI e Zipkin, conforme a configuração documentada. Outros componentes, como Loki e Grafana, dependem de estarem definidos no Compose ou configurados separadamente.

### Iniciar a infraestrutura

Na raiz do repositório:

```powershell
cd infra
docker compose up -d
```

### Verificar os containers

```powershell
docker compose ps
```

### Consultar logs

```powershell
docker compose logs -f
```

Para acompanhar o Kafka:

```powershell
docker compose logs -f kafka
```

### Encerrar os containers

```powershell
docker compose down
```

### Remover também os volumes persistentes

```powershell
docker compose down -v
```

**Atenção:** o último comando remove os volumes gerenciados pelo Compose e pode apagar os dados persistidos localmente.

## ▶️ Execução das Aplicações

Com a infraestrutura disponível, inicie os serviços a partir da raiz do repositório.

### Eureka Server

```powershell
mvn -pl eureka-server spring-boot:run
```

### API Gateway

```powershell
mvn -pl api-gateway spring-boot:run
```

### Contrato Service

```powershell
mvn -pl contrato-service spring-boot:run
```

### Notificação Service

```powershell
mvn -pl notificacao-service spring-boot:run
```

### Reputação Service

```powershell
mvn -pl reputacao-service spring-boot:run
```

### Auditoria Service

```powershell
mvn -pl auditoria-service spring-boot:run
```

Cada aplicação precisa ter configurações de conexão com PostgreSQL, Kafka e Eureka compatíveis com o ambiente em que está sendo executada.

Os comandos acima pressupõem que os módulos e seus POMs estejam organizados conforme os nomes indicados.

## 🧪 Teste do Fluxo Completo

Com a infraestrutura e os serviços em execução, o fluxo pode ser testado por meio do API Gateway.

Os comandos abaixo utilizam PowerShell.

### 1. Criar um contrato

```powershell
$h = @{ "X-Correlation-Id" = "demo-001" }

$body = @{
    clienteId    = "11111111-1111-1111-1111-111111111111"
    freelancerId = "22222222-2222-2222-2222-222222222222"
    titulo       = "Contrato de demonstração"
    valor        = 1500.00
} | ConvertTo-Json

$c = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/api/contratos" `
    -ContentType "application/json; charset=utf-8" `
    -Headers $h `
    -Body ([System.Text.Encoding]::UTF8.GetBytes($body))

$c
```

Guarde o identificador retornado no campo `id`.

### 2. Registrar a entrega

```powershell
$id = $c.id

Invoke-RestMethod `
    -Method Put `
    -Uri "http://localhost:8080/api/contratos/$id/entrega" `
    -Headers $h
```

### 3. Concluir o contrato

```powershell
Invoke-RestMethod `
    -Method Put `
    -Uri "http://localhost:8080/api/contratos/$id/concluir" `
    -Headers $h
```

### 4. Consultar a auditoria

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/auditoria" |
    ConvertTo-Json -Depth 10
```

A auditoria também pode ser verificada diretamente no PostgreSQL:

```powershell
docker exec -it freela-postgres psql -U freela -d auditoria_db -c "SELECT event_id, aggregate_id, event_type, recebido_em FROM auditoria_eventos WHERE aggregate_id = '$id' ORDER BY recebido_em;"
```

O fluxo esperado é a persistência dos eventos `ContratoCriado`, `EntregaRegistrada` e `ContratoConcluido`.

### 5. Consultar a reputação

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/reputacoes" |
    ConvertTo-Json -Depth 10
```

Para consultar diretamente o banco:

```powershell
docker exec -it freela-postgres psql -U freela -d reputacao_db -c "SELECT freelancer_id, contratos_concluidos, valor_total FROM reputacoes;"
```

O valor agregado pode incluir outros contratos de testes anteriores. Para validar o resultado, considere o histórico anterior do freelancer.

### 6. Consultar notificações

```powershell
Invoke-RestMethod `
    -Uri "http://localhost:8080/api/notificacoes" |
    ConvertTo-Json -Depth 10
```

A rota acima deve ser conferida com a configuração real do Gateway e do `notificacao-service`. Caso o endpoint de consulta seja diferente, utilize o caminho efetivamente configurado.

### 7. Conferir a publicação na Outbox

No banco `contrato_db`, consulte os registros da Outbox:

```powershell
docker exec -it freela-postgres psql -U freela -d contrato_db -c "SELECT * FROM outbox_events ORDER BY occurred_at DESC LIMIT 10;"
```

A consulta pressupõe que a tabela utilize a coluna `occurred_at`. Ajuste a ordenação se o nome da coluna real for diferente.

Os eventos publicados devem apresentar o horário de publicação preenchido no campo `published_at`.

## 🧪 Testes Automatizados

Na raiz do repositório, execute:

### Executar os testes

```powershell
mvn test
```

### Verificar compilação e ciclo de build

```powershell
mvn clean verify
```

O resultado desses comandos deve ser verificado no ambiente local. Testes que dependem de PostgreSQL, Kafka ou outros serviços externos podem exigir infraestrutura previamente iniciada.

## 🛠️ Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Cloud
- Spring Cloud Gateway
- Netflix Eureka
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Kafka UI
- Docker e Docker Compose
- Maven
- Micrometer Tracing
- Zipkin
- Loki e Grafana, caso habilitados no ambiente

## 📚 Conceitos Aplicados

- Arquitetura de microsserviços.
- Domain-Driven Design (DDD).
- Agregados e regras de domínio.
- Domain Events.
- Transactional Outbox.
- Comunicação assíncrona baseada em eventos.
- Apache Kafka e particionamento por chave.
- Idempotência no processamento de mensagens.
- Reprocessamento e Dead Letter Topic.
- Persistência independente por serviço.
- Service Discovery com Eureka.
- API Gateway.
- Auditoria de eventos.
- Identificadores de correlação.
- Logs estruturados e rastreamento distribuído.
- Conteinerização com Docker Compose.

## 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido para a disciplina de Domain-Driven Design (DDD) e Arquitetura de Softwares Escaláveis com Java do bloco de Desenvolvimento de Softwares Escaláveis, com foco em microsserviços, Domain-Driven Design, comunicação assíncrona com Kafka, confiabilidade no processamento de eventos e observabilidade em sistemas distribuídos.

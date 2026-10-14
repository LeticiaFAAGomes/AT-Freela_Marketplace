# 💼 Freela Marketplace — Microsserviços com Kafka

### Marketplace de Freelancers com Spring Boot, Spring Cloud, Domain-Driven Design e Comunicação Assíncrona

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Microservices-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-4-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

O **Freela Marketplace** é um sistema de contratação de freelancers desenvolvido com arquitetura de microsserviços utilizando Java, Spring Boot e Spring Cloud. A aplicação gerencia o ciclo de vida dos contratos e utiliza **Apache Kafka para comunicação assíncrona baseada em eventos**, integrando os serviços de notificações, reputação e auditoria.

O projeto aplica conceitos de **Domain-Driven Design (DDD), Transactional Outbox, idempotência, processamento assíncrono, tolerância a falhas e rastreabilidade**, buscando garantir maior confiabilidade na comunicação entre os serviços.

---

## 📌 Objetivos

- Desenvolver uma aplicação distribuída utilizando arquitetura de microsserviços.
- Separar as responsabilidades de contratos, notificações, reputação e auditoria.
- Implementar o ciclo de vida dos contratos com regras de domínio.
- Aplicar conceitos de Domain-Driven Design (DDD).
- Implementar comunicação assíncrona com Apache Kafka.
- Publicar eventos de domínio utilizando o padrão Transactional Outbox.
- Garantir a identificação dos eventos por meio de identificadores únicos.
- Implementar idempotência no processamento de eventos.
- Utilizar o identificador do contrato como chave de particionamento para preservar a ordem dos eventos relacionados.
- Implementar tratamento de falhas com tentativas de reprocessamento e Dead Letter Topic.
- Registrar eventos relevantes para auditoria.
- Atualizar informações agregadas de reputação dos freelancers.
- Utilizar Eureka para registro e descoberta de serviços.
- Centralizar o acesso HTTP por meio do API Gateway.
- Executar a infraestrutura local com Docker Compose.
- Trabalhar com logs estruturados e recursos de observabilidade.

---

# 💼 Sobre o Sistema

O marketplace representa um cenário em que clientes contratam freelancers para executar trabalhos. O `contrato-service` gerencia os contratos e publica eventos que são consumidos pelos demais serviços.

O sistema é composto por seis aplicações Spring Boot:

- **eureka-server** — registro e descoberta dos microsserviços.
- **api-gateway** — ponto de entrada HTTP e roteamento para os serviços.
- **contrato-service** — gerenciamento do ciclo de vida dos contratos e publicação de eventos.
- **notificacao-service** — processamento e armazenamento de notificações.
- **reputacao-service** — atualização das informações agregadas dos freelancers.
- **auditoria-service** — registro dos eventos relevantes do sistema.

A comunicação entre os serviços consumidores e o serviço de contratos ocorre de forma assíncrona por meio do Kafka, reduzindo o acoplamento direto entre os componentes.

---

## 🏛️ Arquitetura

```text
                         ┌──────────────────────┐
                         │        CLIENTE       │
                         │       REST / HTTP    │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      API GATEWAY     │
                         │        :8080         │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    CONTRATO-SERVICE  │
                         │        :8081         │
                         │                      │
                         │ DDD + Transactional   │
                         │       Outbox         │
                         └──────────┬───────────┘
                                    │
                                    │ Publicação
                                    ▼
                         ┌──────────────────────┐
                         │     APACHE KAFKA     │
                         │        :9092         │
                         │                      │
                         │   contrato.eventos   │
                         └──────────┬───────────┘
                                    │
                  ┌─────────────────┼─────────────────┐
                  │                 │                 │
                  ▼                 ▼                 ▼
       ┌──────────────────┐ ┌────────────────┐ ┌────────────────┐
       │ NOTIFICACAO-     │ │ REPUTACAO-     │ │ AUDITORIA-     │
       │ SERVICE          │ │ SERVICE        │ │ SERVICE        │
       │ :8082            │ │ :8083          │ │ :8084          │
       └────────┬─────────┘ └───────┬────────┘ └───────┬────────┘
                │                   │                  │
                ▼                   ▼                  ▼
       ┌──────────────────┐ ┌────────────────┐ ┌────────────────┐
       │ notificacao_db   │ │ reputacao_db   │ │ auditoria_db   │
       └──────────────────┘ └────────────────┘ └────────────────┘

                 ┌────────────────────────────────┐
                 │          EUREKA SERVER         │
                 │             :8761              │
                 └────────────────────────────────┘

                 ┌────────────────────────────────┐
                 │           POSTGRESQL           │
                 │             :5432              │
                 └────────────────────────────────┘
```

O diagrama representa a organização lógica da aplicação. O Kafka distribui os eventos para os consumidores, enquanto cada microsserviço mantém suas próprias responsabilidades e seus próprios dados.

---

## 🔌 Portas

| Serviço | Porta |
|---|---:|
| `api-gateway` | `8080` |
| `contrato-service` | `8081` |
| `notificacao-service` | `8082` |
| `reputacao-service` | `8083` |
| `auditoria-service` | `8084` |
| `eureka-server` | `8761` |
| Apache Kafka | `9092` |
| Kafka UI | `8090` |
| PostgreSQL | `5432` |
| Zipkin | `9411` |

---

# 🧩 Componentes da Aplicação

| Componente | Responsabilidade |
|---|---|
| `eureka-server` | Registro e descoberta de serviços |
| `api-gateway` | Entrada HTTP e roteamento das requisições |
| `contrato-service` | Regras de domínio, ciclo de vida e publicação de eventos |
| `notificacao-service` | Processamento e persistência de notificações |
| `reputacao-service` | Agregação dos contratos concluídos por freelancer |
| `auditoria-service` | Registro e rastreabilidade de eventos |
| Apache Kafka | Transporte assíncrono de eventos |
| PostgreSQL | Persistência dos dados dos microsserviços |

---

# 📄 contrato-service

O `contrato-service` é o núcleo do domínio da aplicação. Ele gerencia os contratos entre clientes e freelancers, aplica as regras de negócio e registra os eventos que devem ser publicados para os outros serviços.

### Responsabilidades

- Criar e consultar contratos.
- Registrar a entrega de um contrato.
- Concluir contratos respeitando as regras de domínio.
- Gerar eventos de domínio.
- Persistir os eventos pendentes na Outbox.
- Publicar os eventos no Kafka.
- Utilizar o identificador do contrato como chave de publicação dos eventos.

### Ciclo de vida do contrato

Os estados do contrato são:

- `ATIVO`
- `ENTREGA_REGISTRADA`
- `CONCLUIDO`
- `CANCELADO`

Fluxo principal:

```text
       ATIVO
         │
         ▼
 ENTREGA_REGISTRADA
         │
         ▼
     CONCLUIDO
```

Um contrato ativo também pode ser cancelado, conforme as regras de domínio implementadas.

### Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/contratos` | Cria um contrato |
| `GET` | `/api/contratos` | Lista os contratos |
| `GET` | `/api/contratos/{id}` | Consulta um contrato |
| `PUT` | `/api/contratos/{id}/entrega` | Registra a entrega |
| `PUT` | `/api/contratos/{id}/concluir` | Conclui um contrato |

### Exemplo de criação

```json
{
  "clienteId": "11111111-1111-1111-1111-111111111111",
  "freelancerId": "22222222-2222-2222-2222-222222222222",
  "titulo": "Construção de API de pagamentos",
  "valor": 3500.00
}
```

Os identificadores são exemplos ilustrativos. O formato exato da resposta depende da implementação do endpoint.

---

# 🏛️ Domain-Driven Design (DDD)

O `contrato-service` utiliza uma organização inspirada em Domain-Driven Design, separando o domínio, os casos de uso e os detalhes de infraestrutura.

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

- **Domínio:** concentra as regras de negócio e os estados válidos do contrato.
- **Aggregate:** o agregado `Contrato` controla as transições de estado.
- **Domain Events:** representam acontecimentos relevantes no domínio.
- **Application Services:** coordenam os casos de uso.
- **Repository:** abstrai o acesso aos dados.
- **Infrastructure:** implementa persistência, comunicação com Kafka e interfaces HTTP.

### Eventos de domínio

Entre os eventos utilizados estão:

- `ContratoCriado`
- `ContratoConcluido`

Esses eventos representam acontecimentos do ciclo de vida do contrato e são utilizados para comunicar mudanças aos demais microsserviços.

---

# 🔄 Comunicação Assíncrona com Apache Kafka

O Apache Kafka é utilizado para distribuir eventos entre os microsserviços sem exigir chamadas HTTP diretas entre o produtor e os consumidores para cada processamento.

O tópico principal é:

```text
contrato.eventos
```

### Fluxo de publicação

```text
Contrato é criado ou concluído
             │
             ▼
      Evento de domínio
             │
             ▼
      Registro na Outbox
             │
             ▼
       OutboxPublisher
             │
             ▼
       Apache Kafka
             │
       ┌─────┼─────┐
       ▼     ▼     ▼
  Notificação Reputação Auditoria
```

O `OutboxPublisher` busca eventos pendentes, publica cada mensagem no Kafka e marca o evento como publicado após a confirmação do envio.

### Chave de particionamento

A publicação utiliza o identificador do contrato (`aggregateId`) como chave da mensagem.

Isso permite que eventos do mesmo contrato sejam encaminhados à mesma partição do tópico, quando a configuração do tópico e do produtor mantém a mesma estratégia de particionamento.

---

# 📤 Transactional Outbox

O padrão **Transactional Outbox** é utilizado para persistir os eventos que precisam ser enviados ao Kafka.

Em vez de depender exclusivamente de uma publicação imediata, o sistema registra o evento na tabela `outbox_events` e utiliza um publicador para enviar os registros pendentes.

### Funcionamento

1. O evento é registrado na Outbox.
2. O publicador consulta os eventos ainda não publicados.
3. O evento é enviado ao Kafka.
4. O envio é aguardado para verificar sua confirmação.
5. Após a confirmação, o registro recebe o horário de publicação em `publishedAt`.

A tabela utiliza informações como:

- `eventId`
- `aggregateId`
- `eventType`
- `payload`
- `occurredAt`
- `publishedAt`

O campo `publishedAt` permanece nulo enquanto o evento estiver pendente.

O padrão reduz o risco de perder eventos entre a persistência do estado do domínio e a publicação da mensagem. Ele não elimina sozinho todas as possibilidades de duplicação: por exemplo, uma falha após a publicação e antes da confirmação no banco pode exigir que o consumidor processe novamente a mesma mensagem.

---

# 📨 notificacao-service

O `notificacao-service` processa eventos recebidos do Kafka e mantém os registros de notificações associados ao marketplace.

### Responsabilidades

- Consumir eventos do tópico `contrato.eventos`.
- Processar eventos relevantes para notificações.
- Persistir os registros no banco `notificacao_db`.
- Identificar eventos já processados.
- Evitar a criação duplicada de notificações para o mesmo evento.
- Configurar tentativas de reprocessamento e publicação em Dead Letter Topic para falhas no consumidor.

O processamento utiliza o identificador único do evento para apoiar a idempotência.

### Tratamento de falhas

O consumidor possui configuração de reprocessamento com intervalo entre tentativas e encaminhamento de mensagens que continuam falhando para um tópico de mensagens não processadas, utilizando o sufixo `.DLT`.

A criação e a disponibilidade do tópico de destino precisam ser garantidas pela configuração do ambiente Kafka.

---

# ⭐ reputacao-service

O `reputacao-service` mantém informações agregadas sobre a atividade dos freelancers.

### Responsabilidades

- Consumir eventos de contratos concluídos.
- Identificar o freelancer associado ao contrato.
- Atualizar a quantidade de contratos concluídos.
- Acumular o valor dos contratos processados.
- Registrar os eventos processados para apoiar a idempotência.

### Dados agregados

Para cada freelancer, o serviço mantém informações como:

- Identificador do freelancer.
- Quantidade de contratos concluídos.
- Valor total dos contratos concluídos.

O registro dos eventos processados permite identificar mensagens que já foram tratadas e reduzir a duplicação de atualizações.

### Endpoint

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/reputacoes` | Consulta as reputações registradas |

---

# 🧾 auditoria-service

O `auditoria-service` registra eventos relevantes recebidos pelo sistema, oferecendo um histórico para análise e rastreabilidade.

### Responsabilidades

- Consumir eventos do Kafka.
- Registrar o identificador do evento.
- Armazenar o identificador do agregado relacionado.
- Registrar o tipo do evento e o payload recebido.
- Armazenar informações de correlação quando disponíveis.
- Identificar eventos já registrados para evitar duplicações.

### Informações de auditoria

Os registros podem conter:

- `eventId`
- `aggregateId`
- Tipo do evento
- `correlationId`, quando disponível
- Payload original
- Horário de recebimento

### Endpoint

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/auditoria` | Consulta os registros de auditoria |

---

# 🔁 Idempotência e Confiabilidade

Em sistemas distribuídos, uma mensagem pode ser entregue mais de uma vez. Por isso, os consumidores precisam identificar eventos já processados.

O projeto utiliza identificadores únicos de eventos e registros de processamento para reduzir os efeitos de mensagens duplicadas.

### Estratégias aplicadas

- Identificação única dos eventos por `eventId`.
- Verificação de eventos já registrados antes de executar o processamento.
- Restrições de unicidade no banco de dados para registros de eventos.
- Registro dos eventos processados no serviço de reputação.
- Tentativas de reprocessamento no consumidor configurado.
- Encaminhamento de mensagens com falha persistente para Dead Letter Topic.

A idempotência depende também da implementação transacional de cada consumidor e das restrições do banco de dados. Ela não significa que o Kafka entregue cada mensagem uma única vez.

---

# 🧭 eureka-server

O `eureka-server` fornece o registro e a descoberta dos microsserviços.

Os serviços se registram no Eureka para que o Gateway possa localizá-los pelo nome, reduzindo a dependência de endereços fixos.

**Porta:** `8761`

Interface web:

```text
http://localhost:8761
```

---

# 🚪 api-gateway

O `api-gateway` centraliza a entrada HTTP e encaminha as requisições aos microsserviços por meio das rotas configuradas e da descoberta de serviços.

**Porta:** `8080`

### Rotas principais

| Caminho | Serviço |
|---|---|
| `/api/contratos/**` | `contrato-service` |
| `/api/notificacoes/**` | `notificacao-service` |
| `/api/reputacoes/**` | `reputacao-service` |
| `/api/auditoria/**` | `auditoria-service` |

As rotas e os prefixos devem corresponder à configuração efetiva do Gateway.

---

# 🆔 Correlação e Logs

O projeto utiliza o cabeçalho HTTP:

```http
X-Correlation-Id: teste-contrato-001
```

Esse identificador permite associar registros de log relacionados a uma mesma requisição quando é encaminhado e registrado pelos componentes envolvidos.

Os logs incluem informações como:

- Data e horário.
- Nível de severidade.
- Nome do serviço.
- Thread.
- Logger.
- Mensagem do evento.

O payload de eventos também pode carregar informações de correlação, permitindo que os consumidores registrem o identificador quando disponível.

A propagação completa da correlação entre requisições HTTP e mensagens Kafka depende da inclusão desse dado no evento publicado e do tratamento realizado pelos consumidores.

---

# 📈 Observabilidade com Zipkin

O projeto inclui dependências de tracing e configuração de infraestrutura para o Zipkin, com o objetivo de apoiar o rastreamento distribuído entre componentes.

**Porta padrão do Zipkin:** `9411`

Interface local:

```text
http://localhost:9411
```

Para visualizar rastreamentos, é necessário que os serviços estejam configurados com o endpoint de envio de spans e que a instrumentação esteja ativa. A disponibilidade do container, por si só, não garante que os rastreamentos estejam sendo recebidos.

---

# 🐘 PostgreSQL

O PostgreSQL é utilizado como banco de dados relacional, com bancos separados por microsserviço.

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

Esses são os valores padrão do ambiente local descrito no projeto. Em outros ambientes, utilize variáveis de ambiente e credenciais apropriadas.

O script de inicialização dos bancos está em:

```text
infra/postgres/init-databases.sql
```

---

# 🐳 Infraestrutura com Docker Compose

Os serviços de infraestrutura estão definidos em:

```text
infra/docker-compose.yml
```

A infraestrutura inclui PostgreSQL, Kafka, Kafka UI e Zipkin.

### Iniciar os containers

Na raiz do repositório:

```bash
cd infra
docker compose up -d
```

### Verificar o estado dos containers

```bash
docker compose ps
```

### Consultar os logs

```bash
docker compose logs -f
```

Para consultar apenas os logs do Kafka:

```bash
docker compose logs -f kafka
```

### Encerrar os containers

```bash
docker compose down
```

### Remover também os volumes persistentes

```bash
docker compose down -v
```

**Atenção:** o último comando remove os volumes gerenciados pelo Compose e pode apagar os dados persistidos localmente.

---

# ▶️ Execução das Aplicações

Com a infraestrutura disponível, os microsserviços podem ser iniciados individualmente a partir da raiz do projeto.

### Eureka Server

```bash
mvn -pl eureka-server spring-boot:run
```

### API Gateway

```bash
mvn -pl api-gateway spring-boot:run
```

### Contrato Service

```bash
mvn -pl contrato-service spring-boot:run
```

### Notificação Service

```bash
mvn -pl notificacao-service spring-boot:run
```

### Reputação Service

```bash
mvn -pl reputacao-service spring-boot:run
```

### Auditoria Service

```bash
mvn -pl auditoria-service spring-boot:run
```

Cada aplicação precisa ter suas configurações de conexão com o PostgreSQL, Eureka e Kafka compatíveis com o ambiente em que está sendo executada.

---

# 🧪 Teste do Fluxo de Contratos

Com a infraestrutura e os serviços em execução, crie um contrato pelo Gateway:

```bash
curl -i -X POST http://localhost:8080/api/contratos \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: teste-contrato-001" \
  -d '{
    "clienteId": "11111111-1111-1111-1111-111111111111",
    "freelancerId": "22222222-2222-2222-2222-222222222222",
    "titulo": "Construção de API de pagamentos",
    "valor": 3500.00
  }'
```

Consulte os contratos:

```bash
curl http://localhost:8080/api/contratos
```

Depois de obter o identificador retornado pela API, registre a entrega:

```bash
curl -i -X PUT \
  http://localhost:8080/api/contratos/ID_DO_CONTRATO/entrega
```

Conclua o contrato:

```bash
curl -i -X PUT \
  http://localhost:8080/api/contratos/ID_DO_CONTRATO/concluir
```

Substitua `ID_DO_CONTRATO` pelo identificador real do contrato.

Após a conclusão, verifique os logs do `contrato-service` e dos consumidores. Com o Kafka e os serviços operacionais, o evento de conclusão pode ser processado pelos serviços interessados.

---

# 🛠️ Tecnologias

- **Java 21**
- **Spring Boot**
- **Spring Cloud**
- **Spring Cloud Gateway**
- **Netflix Eureka**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Apache Kafka 4**
- **Kafka UI**
- **Docker**
- **Docker Compose**
- **Maven**
- **Micrometer Tracing**
- **Zipkin**

---

# 📚 Conceitos Aplicados

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
- Logs e rastreamento distribuído.
- Conteinerização com Docker Compose.

---

# 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido no contexto da disciplina de **Desenvolvimento de Softwares Escaláveis**, com foco em microsserviços, Domain-Driven Design, comunicação assíncrona com Kafka, confiabilidade no processamento de eventos e observabilidade em sistemas distribuídos.

# Como clonar e rodar a aplicação localmente

Este guia explica como clonar o repositório e executar a aplicação localmente utilizando Docker Compose.

---

### Arquitetura da aplicação (Docker Compose)

Esta aplicação é totalmente orquestrada via Docker Compose.

Isso significa que **não é necessário instalar ou subir dependências manualmente**, pois o ambiente já inclui:

-  Banco de dados (PostgreSQL)
-  LocalStack (simulação de serviços AWS, como SQS)
-  Message Generator (serviço auxiliar de mensagens)
-  Transaction Authorizer (API principal)

Todos esses serviços são inicializados automaticamente através do arquivo `docker-compose.yml`.
Portanto, ao executar o comando de build, toda a infraestrutura necessária será criada automaticamente.

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- Git
- Docker

---

### Clonar o repositório

```bash
git clone https://github.com/Renato-RJ/itau-transaction-authorizer.git
```

### Acesse o diretório do projeto:

```bash
cd <diretório-onde-o-repo-foi-clonado>/itau-transaction-authorizer
```

## Build e execução da aplicação

Para construir as imagens e subir todos os serviços:

```bash
docker-compose up --build -d
```
Para visualizar os logs da aplicação principal:

```bash
docker-compose logs -f app
```

Para derrubar todos os containers:

```bash
docker-compose down
```

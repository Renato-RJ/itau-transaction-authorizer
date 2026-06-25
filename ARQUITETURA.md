# Decisões arquiteturais do sistema

## Arquitetura Hexagonal (Ports and Adapters)

Optei pela arquitetura hexagonal com o objetivo de isolar o domínio das dependências externas. Essa abordagem facilita 
a criação de testes, reduz acoplamento e permite a substituição de tecnologias sem impacto direto nas regras de negócio.

## Banco de dados – PostgreSQL

Foi escolhido o PostgreSQL por se tratar de uma aplicação financeira com manipulação de saldo, onde consistência e 
integridade transacional são críticas.

Por ser um banco relacional com suporte completo a transações ACID, ele garante maior segurança em operações sensíveis 
como débito e crédito, mesmo que isso implique maior latência em comparação a soluções NoSQL.

## Versionamento de schema – Flyway

Foi utilizado o Flyway para controle de versionamento do banco de dados via migrations.
Isso garante rastreabilidade das mudanças no schema, além de permitir consistência entre diferentes ambientes (dev,
homologação e produção).

## Camada de persistência – Stored Procedure e concorrência

Na camada de persistência, foi adotada uma stored procedure para manipulação do saldo(crédito e débito). Essa decisão 
tem como objetivo centralizar regras críticas de validação diretamente no banco, garantindo uma última camada de 
consistência antes da persistência.

Foi aplicado lock pessimista na conta durante a operação, reduzindo riscos de race conditions em atualizações
concorrentes de saldo.

Como as operações são sempre direcionadas a contas específicas, o risco de deadlock é considerado baixo, dado o padrão
de acesso bem definido.

## Resiliência e tolerância a falhas – Resilience4j

Foi adotado o uso do Resilience4j para implementar padrões de resiliência como Retry e Circuit Breaker nas integrações
externas, especialmente no acesso à fila SQS e em operações dependentes de serviços externos ou infraestrutura de banco
de dados.

# Débitos técnicos / melhorias futuras

## Cache (Cache-Aside Pattern)

Como evolução arquitetural, foi considerada a implementação do padrão cache-aside para validação de existência de contas,
reduzindo carga de leitura no banco de dados.

O cache poderia ser sincronizado via eventos provenientes de uma fila (ex: SQS), garantindo consistência eventual com o
banco.

## Réplicas de banco de dados (read replicas)

Como evolução arquitetural, foi considerada a implementação de réplicas de leitura (read replicas) com o objetivo de 
separar operações de leitura e escrita, reduzindo a carga no banco primário e melhorando a escalabilidade horizontal 
das consultas.

## Testes
A cobertura de testes no momento ainda não está no nível ideal para produção.


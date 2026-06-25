# Estratégia de Deploy com Jenkins (CI/CD em nuvem pública)

A estratégia de entrega contínua foi desenhada utilizando o Jenkins como orquestrador de pipeline, garantindo automação desde a validação do código até o deploy em ambiente produtivo em nuvem pública.

## Pipeline de CI (Continuous Integration)
   Etapas:
   
### 1 - Checkout do código

-   O pipeline inicia com a captura da versão mais recente do código fonte a partir do repositório Git.

### 1.2 - Execução de testes automatizados

- Antes de qualquer build, são executadas as validações da aplicação:

  - testes unitários (JUnit / Mockito)
  - testes de integração (Testcontainers, quando aplicável)
  - análise estática de código (SonarQube, se disponível)

 O objetivo é garantir que apenas código validado avance no pipeline.

### 1.3 - Build da aplicação

Após aprovação nos testes:

- geração do artefato (JAR/WAR)
- resolução de dependências via Maven ou Gradle
- versionamento do build (tag ou commit hash)
### 1.4 - Build de imagem (containerização)

A aplicação é empacotada em uma imagem Docker:

- imagem versionada (ex: app:1.0.23)
- push para registry (ECR, GCR ou Docker Hub privado)
### 2 - Estratégia de Deploy em Cloud

A entrega em produção pode seguir duas abordagens: Blue/Green ou Canary Deployment.

#### Blue/Green Deployment

Conceito

- Dois ambientes idênticos são mantidos:

    - Blue → versão atual em produção
    - Green → nova versão da aplicação


- Fluxo
  - Jenkins realiza deploy da nova versão no ambiente Green
  - Testes de validação pós-deploy são executados (smoke tests)
  - Após validação, o tráfego é redirecionado do Blue para o Green (via load balancer)
  - O ambiente Blue permanece ativo como fallback imediato


- Vantagens
  - rollback instantâneo (troca de tráfego)
  - zero downtime
  - validação segura antes de exposição total


- Desvantagens
  - custo dobrado de infraestrutura
  - maior consumo de recursos

#### Canary Deployment
Conceito

A nova versão é liberada gradualmente para uma porcentagem de usuários.

- Fluxo:

  - Deploy da nova versão em paralelo à versão atual
  - Início do tráfego com pequena porcentagem (ex: 5%)
  - Monitoramento de métricas:
    - latência
    - erros
    - consumo de recursos
    - logs de aplicação
  - Aumento progressivo do tráfego (10% → 50% → 100%)


- Vantagens
  - redução de risco em produção
  - validação com tráfego real
  - detecção precoce de problemas


- Desvantagens
  - maior complexidade operacional
  - necessidade de observabilidade robusta (logs/métricas/tracing)
### 3. Rollback Strategy

Em ambos os modelos:

- Blue/Green → rollback por reversão de tráfego
- Canary → rollback por redução de tráfego para versão anterior
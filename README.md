# itau-transaction-authorizer
Esta é uma aplicação de autorização de transações financeiras, responsável por validar e processar solicitações de transações antes que elas sejam efetivadas.

A API recebe requisições de autorização contendo os dados da transação e aplica regras de validação e domínio para determinar se a transação pode ser aprovada.

## Endpoint de Autorização

`POST /transactions/{transactionId}`

Responsável por autorizar uma transação financeira identificada por transactionId.
---
## Request

### Path parameter

 - `transactionId` (String): identificador único da transação

### Body

```json
{

    "accountId": "string",
    
    "accountOwnerId": "string",
    
    "type": "string",
    
    "amount": 100.50,
    
    "currency": "BRL"

}
```

## Response

A resposta contém os dados da transação processada, incluindo o status final da autorização e o saldo atualizado de 
acordo com a transação, ex:

```json
{
  "transaction": {
    "id": "36f7ef20-d1bb-409f-b42b-f97778417ab6",
    "type": "CREDIT",
    "amount": {
      "value": 150.00,
      "currency": "BRL"
    },
    "status": "SUCCEEDED",
    "timestamp": "2026-06-25TT12:20:34-03:00"
  },
  "account": {
    "id": "ccc4c853-157a-49bc-b33a-8eafabcb78ff",
    "balance": {
      "amount": 845.25,
      "currency": "BRL"
    }
  }
}
```

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 201 | Transação autorizada com sucesso |
| 200 | Transação rejeitada devido a regras de negócio (ex: saldo insuficiente) |
| 400 | Requisição inválida |
| 404 | Conta não encontrada |
| 409 | Conta já existente |
| 422 | Erro de domínio na transação |
| 500 | Erro interno do servidor |

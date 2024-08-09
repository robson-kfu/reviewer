# Reviewer Microservice

Seu objetivo é integrar dois sistemas: um sistema de versionamento de código (como o GitHub) 
e um serviço de IA (como o Ollama).

## Funcionalidades

1. **Recepção do ID de Pull Request**: O microserviço recebe um ID de PR para análise.
2. **Busca de Informações Adicionais**: Utiliza o ID do PR para buscar informações adicionais do repositório.
3. **Análise por IA**: Envia os dados para o serviço de IA para análise.
4. **Comentários Automáticos**: Recebe os comentários da IA e os posta nas linhas do PR.

## Tecnologias Utilizadas

- **Java 21**
- **Maven**
- **Spring Cloud**
- **GitHub API**
- **Ollama AI Service**

## Pré-requisitos

- Java 21
- Maven 3.8+

## Configuração do Projeto

1. Clone o repositório:
    ```bash
    git clone https://github.com/robson-kfu/reviewer.git
    cd reviewer
    ```
   
   2. Configure as propriedades do serviço do versionamento de código e do serviço de IA no `application.yaml`:
      1. [Configurando o GitHub](docs/configuracao-github.md)

## Uso

### Endpoint para Analisar Pull Request

- **Endpoint**: `/api/reviewer`
- **Método**: `POST`
- **Parâmetros**:
    - `prId`: ID do pull request a ser analisado

**Exemplo de Requisição**:
```bash
curl -X POST http://localhost:8080/api/analyze-pr -H "Content-Type: application/json" -d '{"prId": 123}'

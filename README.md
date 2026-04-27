# ZG Invest

Bem-vindo ao projeto ZG Invest. Este repositório é composto por um backend em Grails e um frontend em Angular.

## Estrutura do Projeto

- `/backend-ZG`: Aplicação backend desenvolvida em Grails e Groovy (API REST).
- `/frontend-ZG`: Aplicação frontend desenvolvida em Angular.
- `bolsa.bkp`: Script em SQL com o dump inicial da base de dados PostgreSQL.

## Pré-requisitos

Para rodar todo o ecossistema do projeto em um único comando, você precisará apenas ter instalado:

- **Docker** e **Docker Compose**

---

## Como rodar o Projeto Completo

A aplicação foi totalmente dockerizada para resolver incompatibilidades de versões (como as que ocorrem com versões recentes do Java).

No terminal, na raiz do projeto (`ZG-Invest`), rode o seguinte comando para construir e inicializar todo o projeto (Banco de dados, Backend e Frontend):

```bash
docker-compose up -d --build
```

**Como acessar:**

- **Frontend:** http://localhost:4200
- **Backend (API):** http://localhost:8080
- **Banco de Dados:** localhost:5432 (usuário: `postgres`, senha: `root`)

> **Nota:** Na primeira vez, o Docker baixará as imagens do Java, Node e Postgres, e executará o build de ambas as aplicações. Isso pode demorar alguns minutos. Após o build, os serviços subirão e o banco de dados já inicializará populado com os dados do script `bolsa.bkp`.

---

## Como parar a Aplicação

Para desligar todos os containers sem apagar os volumes de dados do banco:

```bash
docker-compose stop
```

Para desligar e remover todos os containers:

```bash
docker-compose down
```

## Aplicativo publicado no vercel

- https://zg-invest-2kmcvluln-rodrigobocks-projects.vercel.app/dashboard

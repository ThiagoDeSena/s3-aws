# 🚀 Spring Boot + AWS S3: Gestão de Arquivos Públicos e Privados (Presigned URLs)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![AWS S3](https://img.shields.io/badge/AWS%20S3-Cloud-orange.svg)](https://aws.amazon.com/s3/)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)

Projeto de demonstração para integração da API Spring Boot com o **Amazon S3**, abordando estratégias distintas de segurança e entrega de arquivos: **Acesso Público Direto** vs **Acesso Privado via Presigned URLs (URLs Assinadas)**.

---

## 📌 Contexto & Arquitetura

Em sistemas de produção, o gerenciamento de arquivos na nuvem exige separação clara de responsabilidades quanto ao controle de acesso:

1. **Arquivos Públicos (`/public-documents`):**
   * **Casos de uso:** Fotos de perfil, catálogos institucionais, imagens de produtos.
   * **Estratégia:** O S3 expõe os arquivos diretamente via HTTP. A aplicação gera a URL estática e a leitura é liberada por meio de uma **Bucket Policy** específica na AWS.

2. **Arquivos Privados (`/private-statements`):**
   * **Casos de uso:** Extratos bancários, relatórios financeiros, documentos com dados sensíveis (LGPD).
   * **Estratégia:** O bucket/pasta permanece **totalmente fechado** para a internet (`403 Access Denied`). O acesso é concedido de forma temporária gerando **Presigned URLs** assinadas via SDK com tempo de expiração customizado.

---

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot 3**
* **AWS SDK v2 para Java** (`S3Client` e `S3Presigner`)
* **Amazon S3** (Simple Storage Service)
* **Maven**

---

## 📐 Estrutura do Bucket & Segurança no S3

### 1. Bucket Policy (Permissão Pública Restrita)
Para permitir que apenas a pasta `public-documents/*` seja acessível diretamente sem token:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadForPublicFolderOnly",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::NOME_DO_SEU_BUCKET/public-documents/*"
    }
  ]
}

```

---

## 🔌 Endpoints da API

Abaixo estão os endpoints expostos pela controller `/files`:

| Método | Endpoint | Descrição | Parâmetros / Body |
| --- | --- | --- | --- |
| `POST` | `/files/public/upload` | Upload para a pasta pública do S3 | `file` (Multipart file) |
| `POST` | `/files/private/upload` | Upload para a pasta privada do S3 | `file` (Multipart file) |
| `GET` | `/files/private/view` | Gera a **Presigned URL** com tempo de expiração | `s3Key` (Query Param) |
| `GET` | `/files/download/{filename}` | Baixa o arquivo em stream/byte array via API | `filename` (Path Variable) |

---

## 🧪 Como Testar no Postman

### 1. Upload Público

* **Endpoint:** `POST http://localhost:8080/files/public/upload`
* **Body:** `form-data` | Key: `file` (Selecione o tipo **File**)
* **Resposta:** Retorna a URL estática direta do arquivo no S3. O link pode ser aberto em qualquer navegador anônimo.

### 2. Upload Privado

* **Endpoint:** `POST http://localhost:8080/files/private/upload`
* **Body:** `form-data` | Key: `file` (Selecione o tipo **File**)
* **Resposta:** Retorna a `s3Key` gerada (ex: `private-statements/documento.pdf`). A tentativa de acesso direto à URL do S3 retornará erro `403 Access Denied`.

### 3. Visualizar Arquivo Privado (URL Assinada)

* **Endpoint:** `GET http://localhost:8080/files/private/view?s3Key=private-statements/documento.pdf`
* **Resposta:** Retorna a **Presigned URL** gerada com credenciais temporárias. A URL expira em 5 minutos.

---

## ⚙️ Configuração Local

1. Cloar o repositório:
```bash
git clone https://github.com/ThiagoDeSena/s3-aws.git

```


2. Crie o arquivo `src/main/resources/application.properties` (com base no `application.properties.example`):
```properties
spring.application.name=demo-s3

# Credenciais da AWS
cloud.aws.credentials.access-key=SUA_ACCESS_KEY
cloud.aws.credentials.secret-key=SEU_SECRET_KEY
cloud.aws.region.static=us-east-1
aws.bucket.name=NOME_DO_SEU_BUCKET

```


3. Execute a aplicação:
```bash
mvn spring-boot:run

```



---

## 🔒 Segurança de Credenciais

> **Aviso:** O arquivo `application.properties` contendo as chaves privadas **NÃO** está versionado neste repositório por questões de segurança (listado no `.gitignore`). Utilize o modelo `application.properties.example` para preencher com suas credenciais.

```

```

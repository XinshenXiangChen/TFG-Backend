# TFGBackend

Spring Boot backend that depends on `DatalogLLM` and exposes generation/file endpoints for `TFGFrontEnd`.

## Prerequisite

Install `DatalogLLM` into your local Maven repository first:

```bash
cd ../DatalogLLM
mvn install -DskipTests
```

## Run backend

```bash
cd ../TFGBackend
mvn spring-boot:run
```

## API

- `POST /api/generate` with JSON body `{ "plantUml": "..." }`
- `GET /api/files`
- `GET /api/files/content?path=<relativePath>`
- `GET /api/files/archive`

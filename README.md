# bff-ux-accounts

Proyecto Java 21 con Gradle Groovy, Spring Boot 3.x, Spring Cloud OpenFeign, OpenAPI Generator, pruebas con JUnit 5 y JaCoCo.

## Requisitos

- Java 21
- Gradle Wrapper incluido en el proyecto
- Git para instalar el hook de pre-commit

## Como ejecutar

```sh
./gradlew bootRun
```

La aplicacion inicia en `http://localhost:8080`.

## Como correr pruebas

```sh
./gradlew test
```

Para ejecutar el build completo:

```sh
./gradlew clean build
```

## Cobertura JaCoCo

Generar reporte:

```sh
./gradlew jacocoTestReport
```

Ver el reporte HTML en:

```text
build/reports/jacoco/test/html/index.html
```

Validar cobertura minima del 80%:

```sh
./gradlew jacocoTestCoverageVerification
```

## Regenerar OpenAPI

Las especificaciones viven en:

```text
src/main/resources/openapi.yaml
src/main/resources/openapi-client.yaml
src/main/resources/open-api-token-validation.yaml
```

Regenerar codigo y compilar:

```sh
./gradlew clean compileJava
```

El codigo generado queda en `build/generated/src/main/java` y se agrega al `sourceSets.main`. No se mezcla con el codigo manual.

## Instalar pre-commit

```sh
./gradlew installGitHooks
```

El hook ejecuta:

```sh
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

## Arquitectura

La aplicacion conserva la estructura base del BFF UX de usuarios, preparada para implementar cuentas:

- `domain`: modelos y excepciones de dominio sin dependencias de Spring.
- `controller`: controladores REST que implementaran las interfaces generadas.
- `services`: casos de uso y logica de negocio de cuentas.
- `repository`: puertos e implementaciones externas.
- `handler`: controladores REST, DTOs y manejo de errores.
- `configuration`: configuracion Spring.
- `utils`: logging y utilidades compartidas.

Este esqueleto no incluye logica de negocio Java. Las rutas `controller`, `services` y `repository` quedan reservadas para la implementacion de cuentas.

## Docker

Construir imagen:

```sh
docker build -t bff-ux-accounts .
```

Ejecutar contenedor:

```sh
docker run --rm -p 8080:8080 bff-ux-accounts
```

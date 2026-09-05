# Spring PetClinic — with custom enhancements [![Build Status](https://github.com/am1lovsky/pet-clinic/actions/workflows/maven-build.yml/badge.svg)](https://github.com/am1lovsky/pet-clinic/actions/workflows/maven-build.yml)[![Build Status](https://github.com/am1lovsky/pet-clinic/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/am1lovsky/pet-clinic/actions/workflows/gradle-build.yml)

## About this repository

This is a fork of the official [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) sample application — a reference Spring Boot project maintained by the Spring team. I use it as a playground to practice Spring Boot/Spring Data JPA and to build small, self-contained features on top of a realistic, well-tested codebase.

Everything under `Custom functionality` below is my own work, added on top of the upstream project. Everything else (the base domain model, views, and infrastructure) comes from upstream Spring PetClinic.

## Custom functionality

### Visit booking validation
New rules enforced when an owner books a visit for a pet (`VisitService`):
- a visit can't be booked for today or a past date;
- a pet can't have two visits booked on the same day.

Both are surfaced as form validation errors on the existing "book a visit" page.

### Pet priority scoring
`GET /owners/{ownerId}/pets/{petId}/priority` returns whether a pet should be treated as a priority patient, and why. A pet is flagged as priority if it:
- has had 3+ visits in the last 12 months (frequent visitor),
- is older than 8 years (senior pet), or
- has a visit whose description mentions "emergency".

### Need-visit indicator
`GET /owners/{ownerId}/pets/{petId}/is-need-visit` returns `true` if a pet has never been seen or its last visit was more than a year ago — a quick signal for staff that a pet is overdue for a checkup.

### Owner pet count
`GET /owners/{ownerId}/pets/count` returns how many pets an owner has, backed by a single `COUNT` query instead of loading the owner and all of their pets into memory.

### Vet workload tracking
Visits are now linked to the vet who handled them. `GET /vets/{vetId}/workload` returns a vet's visit count over the last 7 days and flags them as overloaded once that count exceeds 5.

---

## Understanding the Spring Petclinic application with a few diagrams

See the presentation here:  
[Spring Petclinic Sample Application (legacy slides)](https://speakerdeck.com/michaelisvy/spring-petclinic-sample-application?slide=20)

> **Note:** These slides refer to a legacy, pre–Spring Boot version of Petclinic and may not reflect the current Spring Boot–based implementation.  
> For up-to-date information, please refer to this repository and its documentation.


## Run Petclinic locally

Spring Petclinic is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/) or [Gradle](https://spring.io/guides/gs/gradle/).
Java 17 or later is required for the build, and the application can run with Java 17 or newer.

You first need to clone the project locally:

```bash
git clone https://github.com/am1lovsky/pet-clinic.git
cd pet-clinic
```
If you are using Maven, you can start the application on the command-line as follows:

```bash
./mvnw spring-boot:run
```
With Gradle, the command is as follows:

```bash
./gradlew bootRun
```

You can then access the Petclinic at <http://localhost:8080/>.

<img width="1042" alt="petclinic-screenshot" src="https://cloud.githubusercontent.com/assets/838318/19727082/2aee6d6c-9b8e-11e6-81fe-e889a5ddfded.png">

You can, of course, run Petclinic in your favorite IDE.
See below for more details.

## Building a Container

There is no `Dockerfile` in this project. You can build a container image (if you have a docker daemon) using the Spring Boot build plugin:

## Running the Container Image

```bash
./mvnw spring-boot:build-image
docker images | grep petclinic
docker run -p 8080:8080 docker.io/library/spring-petclinic:latest
```

## Database configuration

In its default configuration, Petclinic uses an in-memory database (H2) which
gets populated at startup with data. The h2 console is exposed at `http://localhost:8080/h2-console`,
and it is possible to inspect the content of the database using the `jdbc:h2:mem:<uuid>` URL. The UUID is printed at startup to the console.

A similar setup is provided for MySQL and PostgreSQL if a persistent database configuration is needed. Note that whenever the database type changes, the app needs to run with a different profile: `spring.profiles.active=mysql` for MySQL or `spring.profiles.active=postgres` for PostgreSQL. See the [Spring Boot documentation](https://docs.spring.io/spring-boot/how-to/properties-and-configuration.html#howto.properties-and-configuration.set-active-spring-profiles) for more detail on how to set the active profile.

You can start MySQL or PostgreSQL locally with whatever installer works for your OS or use docker:

```bash
docker run -e MYSQL_USER=petclinic -e MYSQL_PASSWORD=petclinic -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=petclinic -p 3306:3306 mysql:9.7
```

or

```bash
docker run -e POSTGRES_USER=petclinic -e POSTGRES_PASSWORD=petclinic -e POSTGRES_DB=petclinic -p 5432:5432 postgres:18.4
```

Further documentation is provided for [MySQL](src/main/resources/db/mysql/petclinic_db_setup_mysql.txt)
and [PostgreSQL](src/main/resources/db/postgres/petclinic_db_setup_postgres.txt).

Instead of vanilla `docker` you can also use the provided `docker-compose.yml` file to start the database containers. Each one has a service named after the Spring profile:

```bash
docker compose up mysql
```

or

```bash
docker compose up postgres
```

## Test Applications

At development time we recommend you use the test applications set up as `main()` methods in `PetClinicIntegrationTests` (using the default H2 database and also adding Spring Boot Devtools), `MySqlTestApplication` and `PostgresIntegrationTests`. These are set up so that you can run the apps in your IDE to get fast feedback and also run the same classes as integration tests against the respective database. The MySql integration tests use Testcontainers to start the database in a Docker container, and the Postgres tests use Docker Compose to do the same thing.

## Compiling the CSS

There is a `petclinic.css` in `src/main/resources/static/resources/css`. It was generated from the `petclinic.scss` source, combined with the [Bootstrap](https://getbootstrap.com/) library. If you make changes to the `scss`, or upgrade Bootstrap, you will need to re-compile the CSS resources using the Maven profile "css", i.e. `./mvnw package -P css`. There is no build profile for Gradle to compile the CSS.

## Working with Petclinic in your IDE

### Prerequisites

The following items should be installed in your system:

- Java 17 or newer (full JDK, not a JRE)
- [Git command line tool](https://help.github.com/articles/set-up-git)
- Your preferred IDE
  - Eclipse with the m2e plugin. Note: when m2e is available, there is a m2 icon in `Help -> About` dialog. If m2e is
  not there, follow the installation process [here](https://www.eclipse.org/m2e/)
  - [Spring Tools Suite](https://spring.io/tools) (STS)
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/)
  - [VS Code](https://code.visualstudio.com)

### Steps

1. On the command line run:

    ```bash
    git clone https://github.com/am1lovsky/pet-clinic.git
    ```

1. Inside Eclipse or STS:

    Open the project via `File -> Import -> Maven -> Existing Maven project`, then select the root directory of the cloned repo.

    Then either build on the command line `./mvnw generate-resources` or use the Eclipse launcher (right-click on project and `Run As -> Maven install`) to generate the CSS. Run the application's main method by right-clicking on it and choosing `Run As -> Java Application`.

1. Inside IntelliJ IDEA:

    In the main menu, choose `File -> Open` and select the Petclinic [pom.xml](pom.xml). Click on the `Open` button.

    - CSS files are generated from the Maven build. You can build them on the command line `./mvnw generate-resources` or right-click on the `spring-petclinic` project then `Maven -> Generates sources and Update Folders`.

    - A run configuration named `PetClinicApplication` should have been created for you if you're using a recent Ultimate version. Otherwise, run the application by right-clicking on the `PetClinicApplication` main class and choosing `Run 'PetClinicApplication'`.

1. Navigate to the Petclinic

    Visit [http://localhost:8080](http://localhost:8080) in your browser.

## Upstream project

This fork is based on [spring-projects/spring-petclinic](https://github.com/spring-projects/spring-petclinic), the canonical Spring Boot + Thymeleaf implementation maintained by the Spring team. For the original project's issue tracker, related forks, and Spring ecosystem context, see the upstream repository.

## License

The Spring PetClinic sample application, and the enhancements in this fork, are released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

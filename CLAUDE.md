# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

fit-man is a Spring Boot app that manages `.fit` activity files (Garmin FIT protocol). It fixes GPS signal losses in
activities recorded by mobile devices: it uploads a `.fit` file, disables invalid track points on a map, and
recalculates activity statistics (total distance, moving time, average speed) from the corrected data.

## Commands

Local environment (Postgres via Podman/Docker compose) must be running before `bootRun` or the `test` profile's
integration paths need it:

```shell
cd local-env
podman compose up -d   # or docker-compose up -d
cd ..
```

```shell
./gradlew bootRun                 # run the app on localhost:8080
./gradlew build                   # full build: compile, test, jacoco coverage verification, ktlint
./gradlew test                    # run tests only
./gradlew test --tests "fit.man.app.service.AnalysisServiceTests"          # single test class
./gradlew test --tests "fit.man.app.service.AnalysisServiceTests.methodName" # single test method
./gradlew ktlintCheck             # static analysis (main source set also runs automatically before bootRun)
./gradlew ktlintFormat            # auto-fix formatting issues
./gradlew jacocoTestReport        # coverage report (build/reports/jacoco)
```

Paths once running, relative to `localhost:8080`:
- `/swagger-ui/index.html` — upload a `.fit` file and exercise other endpoints
- `/map` — view a track
- `/management` — browse uploaded activities (paginated) and upload new ones from the browser

API contract lives in `api/openapi-fit-man.yaml` and is the source of truth for controller interfaces (see
Architecture below) — edit the spec, not generated code.

## Architecture

### OpenAPI-first controllers

`api/openapi-fit-man.yaml` is compiled by the `openapi-generator` Gradle plugin (`kotlin-spring` generator) into
interfaces/models under `build/generated/openapi/src/main/kotlin` (package `fit.man.app.api` /
`fit.man.app.api.model`), added to the main source set via `sourceSets.main.kotlin.srcDir(...)`. `compileKotlin`
depends on `openApiGenerate`, so generated sources are always fresh on build. Controllers in
`src/main/kotlin/fit/man/app/controller` implement the generated `*Api` interfaces; DTOs are the generated
`*Request`/`*Response` model classes, converted to/from JPA entities via Kotlin extension functions in
`mapper/ActivityMapping.kt` (no MapStruct/Lombok). **To change API shapes, edit the YAML spec first**, then adjust
the controller/mapper implementation.

`UiController` (plain `@Controller`, not OpenAPI-generated) serves two Thymeleaf pages, `/map` and `/management`,
each backed by vanilla JS calling the REST endpoints above.

### Data model & pipeline

Core entities (`repository/entity`): `Activity` (1) → (`Record`, `Event`) many, and `Activity` (1) → (1) `Analysis`.
An `Activity` is parsed from a `.fit` file by `ActivityService.readFitFile` using Garmin's `com.garmin.fit.Decode` /
`MesgBroadcaster` listener API — each FIT message type (`SessionMesg`, `RecordMesg`, `EventMesg`, etc.) has its own
listener that populates the entity graph. New `.fit` fields to capture should be added as a new/extended listener
here.

Activities move through two background stages, both driven by `@Scheduled` jobs in `scheduling/AppScheduler.kt`
(interval/delay/batch size configurable under `fit-man.activity-scheduler.*`, see `config/AppProperties.kt` and
`application.yaml`):

1. **Markup** (`MarkupService.runMarkup`) — walks each activity's `Record`s in track order and disables (`MARK_DISABLED`)
   points with null position/time or with an implied speed above `maxSpeed` (GPS glitches), leaving good points as
   `MARK_DEFAULT`. Sets `activity.marked = true` when done. **Requires the first track point to be valid** — the
   algorithm anchors on it.
2. **Analysis** (`AnalysisService.runAnalysis`) — for activities that are `marked = true` and have no `Analysis` yet,
   recomputes total distance (great-circle distance between consecutive non-disabled points, via
   `ActivityUtils.calcDistance`/GeographicLib) and moving time (elapsed time between valid points minus idle
   intervals bounded by FIT `EventType.START`/`STOP`/`STOP_ALL` events), producing an `Analysis` row with
   `success=false` if the calculation throws or times out (`fit-man.activity-scheduler.timeout`).

Both `MarkupService` and `AnalysisService` only ever operate on records already marked/unmarked in sequence order —
when modifying their algorithms, preserve the two-pointer (`i`/`j`) scan pattern rather than introducing random
access, since correctness depends on strictly increasing timestamp order (`@OrderBy` on `Activity.records`/`events`).

### Constants & conventions

`util/ActivityUtils` centralizes FIT-specific magic numbers (semicircle-to-degree conversion, m/s-to-km/h factor,
mark values) — reuse these constants rather than redefining them. JPA entities (`repository/entity`) use plain
mutable `var` properties typed nullable (`var startTime: OffsetDateTime? = null`) rather than `lateinit` — Hibernate
can leave entities (and lazy proxies, e.g. `Record`/`Event`/`Analysis` → `Activity` via `@ManyToOne(fetch = LAZY)`)
in a partially-hydrated state longer than expected, and a `lateinit` getter would throw
`UninitializedPropertyAccessException` instead of a predictable `null` in that window.

### Testing

Tests mirror `src/main/kotlin` package structure under `src/test/kotlin`. `fixtures/ActivityFixtures.kt` is the
shared builder for `Activity`/`Record`/`Event`/`Analysis` test data — extend it rather than hand-rolling entity
graphs in new tests. Mocking uses `mockito-kotlin`'s `any()`/`whenever()` (not raw `org.mockito.Mockito`) to avoid
Kotlin null-safety crashes on primitive/non-null parameters. Jacoco enforces 80% line coverage (`build.gradle.kts`
`jacocoTestCoverageVerification`) as part of `./gradlew build`; generated API code, mappers, and `*Util` classes are
excluded from that check.

# fit-man

### [.fit](https://developer.garmin.com/fit/protocol/) files management tool

Version 1.0.0

### API

Openapi description in [openapi-fit-man.yaml](api/openapi-fit-man.yaml).

### Runtime

Requirements: Java 25, Podman Desktop

Local environment:

```shell
cd local-env
podman compose up -d
cd ..
```

Application:

```shell
./gradlew bootRun
```

Paths relative to `localhost:8080`:

- `/swagger-ui/index.html` - upload .fit file
- `/map` - view track

Scheduled jobs:

- runTrackAnalysis()

Track examples are [here](src/test/resources/files)

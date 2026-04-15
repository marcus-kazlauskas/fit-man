# fit-man

### [.fit](https://developer.garmin.com/fit/protocol/) files management tool

Version 1.0.0

### API

Openapi description in [openapi-fit-man.yaml](api/openapi-fit-man.yaml).

### Runtime

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

### TODO

- SQL to fix corrupted track by setting record.mark to 0
- Tests
- Profiles: local, test
- Github Actions workflow
- Analysis service to throw out corrupted data by speed calculation between points
- AI to analysis???
- Authorization???
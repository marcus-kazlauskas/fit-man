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

### Manual fix of the corrupted track:

If you see that the end of the track has irrelevant points, you can find `record.activity_id` of the activity shown
and estimate `record.id` in DB. Execute the similar query:

```
update record
set mark = 0
where activity_id = {record.activity_id} and id >= {record.id};
```

### TODO

- GitHub Actions workflow
- AI to analysis???
- Authorization???
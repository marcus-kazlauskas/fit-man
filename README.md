# fit-man

### [.fit](https://developer.garmin.com/fit/protocol/) files management tool

Version 1.0.0

This app is intended to fix GPS signal losses in activities recorded by mobile devices in .fit format. You can upload 
such an activity (for example, [this file](src/test/resources/files/3669406B-081F-4E9D-B36E-C15FFB139DA9.fit)) 
via `/swagger-ui/index.html` page and open `/map`. You might see something like this:

![Screenshot 2026-07-21 at 16.37.09.png](src/test/resources/files/Screenshot%202026-07-21%20at%2016.37.09.png)

After a minute, the view will look much better:

![Screenshot 2026-07-21 at 16.42.07.png](src/test/resources/files/Screenshot%202026-07-21%20at%2016.42.07.png)

Besides disabling incorrect track points on the map, the algorithm calculates new activity statistics based on the data. 
As a result, you get a new total distance, moving time and average speed.

Note: The algorithm requires the very first point of the track to be valid!

### Runtime

Requirements: 

- ***Java 21*** or higher
- ***Podman Desktop*** (or Docker Desktop)

Local environment:

```shell
cd local-env
podman compose up -d # or use docker-compose
cd ..
```

Application:

```shell
./gradlew bootRun
```

Paths relative to `localhost:8080`:

- `/swagger-ui/index.html` - upload .fit file and other Swagger methods
- `/map` - view track

Openapi description in [openapi-fit-man.yaml](api/openapi-fit-man.yaml).

Scheduled jobs:

- runActivityMarkup()
- runActivityAnalysis()

Track examples are [here](src/test/resources/files)

# Watchlist

Watchlist backend and API

## Compile and startup

Go to project root directory and compile through gradle:

```bash
gradle build
```

`.jar` file will be generated into `build/libs` 



### Run jar from command line

```bash
java -jar <generated-file.jar> \
  --watchlist.tmdb-api-key=<your_api_key> \
  --watchlist.yt-api-key=<your_api_key>
```


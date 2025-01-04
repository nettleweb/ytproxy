# YTProxy
A simple YouTube proxy API server based on [NewPipe Extractor](https://github.com/TeamNewPipe/NewPipeExtractor). It is currently used by [NettleWeb Videos](https://nettleweb.com/videos) (A lightweight, privacy-friendly, ad-less, unblocked YouTube client).

## Build JAR file
```
./gradlew jar
```

## Start the server
```
java -Xms256m -Xmx1024m -jar ./build/libs/ytproxy-0.1.0.jar -host 127.0.0.1 -port 8080
```

## Using the API

### Getting search results
#### Request
```
GET http://127.0.0.1:8080/search?q=${query}&f=${filter}&p=${page}&s=${sort}
```
`query`: The search query string. (required)

`filter`: The search filter options. Example: `videos`

`page`: The next page token.

`sort`: The sort filter. Example: `relevance`

#### Response
A JSON-encoded string containing a list of matching videos and their information.

### Getting video streams
#### Request
```
GET http://127.0.0.1:8080/stream?u=${url}
```
`url`: The direct URL pointing to the video. (required)
#### Response
A JSON-encoded string containing the video's stream information.

### Getting trending videos
### Request
```
GET http://127.0.0.1:8080/trending
```
### Response
A JSON-encoded string containing a list of videos and their information.

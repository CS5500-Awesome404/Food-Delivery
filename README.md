# Delivery

This is the basic Delivery project that all students forked in CS5500 F20.

## Requirements

A recent Gradle (>= 6.1.1 but < 7.0.0) and JDK 8.

## Building

`./gradlew build`

## Testing

`./gradlew test`

## Running

`./gradlew run`

The server will start on port 5000 by default.

## Deploying to Heroku

Configure as normal and use `git push heroku master`.

`./gradlew build deployHeroku` works to deploy without pushes...sometimes.

## MongoDB

The mongodb cluster is configured under a personal account in https://mlab.com/ and can be accessed with the following url:
mongodb+srv://cs5500:cs5500@cluster0.gz5ef.mongodb.net/test?retryWrites=true&w=majority

The default database name is test.

## Spotless?

Spotless automatically formats code. If it detects errors, run `./gradlew spotlessApply`
to automatically fix them. `./gradlew spotlessCheck` can be used to directly invoke
Spotless.

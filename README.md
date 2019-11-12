# Functional TODO App
Production-grade todo app REST API (with a database and everything) in functional scala

This is an effort to create high quality application using FP approach with cats.
As this is intended as both a learning and teaching project, I used more custom code instead of import FP libraries 
(minus Cats, obviously).

## Running the app

This project uses SBT to handle compiling, building, and running

- Compiling -- `sbt compile`
- Testing `sbt test` (runs both unit and acceptance tests)
- Running -- `sbt run` (this won't work unless you're running a local instance of PostGreSQL)

## Features so far

- functional modular system
- layered architecture (dao - service - controller - route)
- pure data access layer based on JDBC
- pure JDBC transaction management
- input validation
- quality error handling (error classification, REST error messages)
- pure logging
- structured audit logging
- data access layer integration tests using embedded postgres
- acceptance tests using embedded postgres
- packaging and dockerization. Try it now: `docker run --rm dylanm/functionaltodoapp --help`

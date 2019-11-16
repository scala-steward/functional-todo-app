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
- configuration from multiple sources (including `--help` command line parameter)
- pure data access layer based on JDBC
- pure JDBC transaction management
- separate thread pool for JDBC code
- quality JSON handling and validation (with readable messages, including reason, line and column)
- input validation (using Validated)
- quality error handling (error classification, REST error messages)
- pure logging
- data access layer tests using embedded postgres
- acceptance tests using embedded postgres
- packaging and dockerization. Try it now: `docker run --rm dylanm/functionaltodoapp --help`

## Architecture

### Layers

Typical multi layer architecture consisting of:
- data access layer (DAO)
- service layer (busines logic, intended to be called from within application)
- controller layer (includes transaction boundary, intended to be exposed to external clients via REST API)
- route layer (converts incoming HTTP request to controller call)
- server layer (HTTP server)

### Effects

Effect is an abstraction of some computation. 
Every effect can be created from computation definition, usually function (lifted) 
and at some time later can be run, extracting computation result (evaluated)

Application uses three types of effects:
- Generic effect F[_]. 
  - This is generic F used for asynchonous code with lazy evaluation, e.g. cats IO or monix Task
- Abstract database effect DbEffect[_]/concrete database effect SqlEffect[F[_], ?]
  - Wraps function `java.sql.Connection => T`. Usual synchronous database code takes this form. Instance of `java.sql.Connection` is needed to evaluate this effect. 
- application initialization effect I[_]
  - Wraps component initialization code. It is lazy so components will only be created on demand and caches its result to produce singletons

### Modules

Modules system used can be seen as extension of well-known cake pattern in that they allow for the ability to override any instance or to get any instance from assembled application.
In addition, though, modules support composition, precise explicit dependency management and lazy evaluation.  
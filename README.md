# Functional TODO App

[![Build Status](https://api.travis-ci.com/dmarticus/functional-todo-app.svg?branch=master)](https://travis-ci.com/github/dmarticus/functional-todo-app)

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
- performance: 11k req/sec on my i9 Laptop for `GET /api/v1/items`
- configuration from multiple sources (including `--help` command line parameter)
- pure data access layer based on JDBC
- pure JDBC transaction management
- separate thread pool for JDBC code
- quality JSON handling and validation (with readable messages, including reason, line and column)
- input validation (using Validated)
- quality error handling (error classification, REST error messages)
- pure logging
- structured audit logging
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
- Generic effect `F[_]`. 
  - This is generic F used for asynchonous code with lazy evaluation, e.g. cats IO or monix Task
- Abstract database effect `DbEffect[_]`/concrete database effect `SqlEffect[F[_], ?]`
  - Wraps function `java.sql.Connection => T`. Usual synchronous database code takes this form. Instance of `java.sql.Connection` is needed to evaluate this effect. 
- application initialization effect `I[_]`
  - Wraps component initialization code. It is lazy so components will only be created on demand and caches its result to produce singletons

### Modules

Modules system used can be seen as extension of well-known cake pattern in that they allow for the ability to override any instance or to get any instance from assembled application.
In addition, though, modules support composition, precise explicit dependency management and lazy evaluation.  

## TODO
- Immutable in-memory dao using `StateT`
- request context (including requestId for logging) using `Kleisli[F, Context, ?]` instead of F
- delayed logging - delay logging evaluation until the end of any request processing to decide log level based on response (e.g. enable debug logging for failed requests only)
- find better way to manage `Application` effects (3 seems to be a bit high)
- use `Resource` for `I[_]`? Not sure I should care about properly shutting down -- a well-written application should behave well in case of forced termination.
- consider moving to http4s for JSON?
- investigate adding request tracing to get more insights into application statistics; look into the example [here](https://medium.com/@ayushm4489/functional-tracing-using-scala-dc98b1f2ec5) for inspiration and potentially
use a library like [natchez](https://github.com/tpolecat/natchez) for implementing the `Trace` and `Span` attributes
- Add request flow control: timeouts, parallel request count limit
- cancellation on timeout? Does it make sense on JDBC? Will it improve behavior of overloaded app?

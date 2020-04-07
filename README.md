GAE Search service proxy (Java 8) 
============================

This provides a way of accessing the GAE text search service for applications which do not have first class access to it (for example node on GAE). It exposes capability to create/update indexes and to run queries.

The intention is it runs within the same GCP project as the main application as a GAE service called `search-service`. For example, if your GCP project is called `my-project-dev` the service will run at `https://search-service-dot-my-project-dev.appspot.com`.

### Running locally

    mvn appengine:run

### Deploying

    CLOUDSDK_CORE_PROJECT={PROJECT_ID} mvn appengine:deploy

where {PROJECT_ID} is your GCP project id eg. `my-project-dev`


Usage
=====

The relevant endpoints are `/index` and `/query`. The `SearchService` in `@mondomob/gae-node-nestjs` (https://github.com/mondo-mob/gae-node-nestjs) provides a generic, convenient way to execute index and query operations.

Current limitations
===================

- The only operation supported at the moment is equals (and IN/OR)

- Delete operations are not yet supported

- Only string fields are supported for indexing and only single strings or array of strings can be used for querying at the moment. 
There is a lot of conversion logic in https://github.com/mondo-mob/spring-boot-gae we can probably take.


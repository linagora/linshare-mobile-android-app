# 10. Using Room to implement DownloadingRepository

Date: 2020-02-21

## Status

Accepted

## Context

Application delegate download task to `DownloadManager`. All tasks are enqueued and will be execute by system in background.
Then, application created a `DownloadingRepository` to manage delegated downloading tasks. This repository need to persist data.

Android provide Room which is an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite:
- Room is an Object Relational Mapping library
- SQL validation at compile time
- Room maps database object to Kotlin object without boilerplate code
- Room support coroutines

## Decision

We will implement `DownloadRepository` by Room

## Consequences

- We can manage all downloading tasks in persistent database with less boilerplate code

## References
[Room](https://developer.android.com/training/data-storage/room?hl=en) 
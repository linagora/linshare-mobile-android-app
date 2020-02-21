# 9. DownloadingRepository to manage downloading tasks

Date: 2020-02-21

## Status

Accepted

## Context

Application delegate download task to `DownloadManager` system service and get an `unique id` for this task.
Application need to store this Id to do further stuff with this task:
 - Query status
 - Get the completed state
 - Get error details
 - Cancel a download task

## Decision

Creating a `DownloadingRepository` to manage downloading tasks

## Consequences

Application can reference in delegated download task and take action in particular state

Notification can't be customized. This is considered acceptable as some other refernce application (drive) also have that behaviour.

We have online/offline + retries being automatically managed for us

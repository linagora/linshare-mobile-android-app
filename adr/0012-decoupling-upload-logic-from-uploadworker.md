# 12. Decoupling Upload logic from UploadWorker

Date: 2020-04-24

## Status

Accepted

## Context

LinShare App not only upload to `MySpace`, but also to `WorkSpace`.
Currently, `UploadWorker` is tightly with the logic of Uploading to MySpace, so we can not reuse it with other new uploading logic

## Decision

#### Current Implementation
```
UploadWorker -> UploadInteractor
```
#### New Implementation
```
                                          |-------> UploadToMySpaceCmd
                                          |
UploadWorker -> UploadController -> UploadCommand
                                          |
                                          |-------> ***Cmd
```

We will add `UploadController` to `UploadWorker`, it will execute the `Command` as we want to execute upload logic.

## Consequences

- It made us easier in development new uploading.

- `UploadWorker` back to the origin role as a **background container** for uploading

- `UploadCompletedNotificationWorker` will be added as a `completed notification worker`.
It combine with `UploadWorker` to become a chain of work.
Whenever `UploadWorker` done, `UploadCompletedNotificationWorker` will notify.

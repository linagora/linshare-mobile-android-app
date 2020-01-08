# 7. Background task with worker

Date: 2019-12-17

## Status

Accepted

## Context

When to design an application for cloud file management, uploading or downloading always doing in the background, handle the network startup or any interruption is the requisite for mobile application.
Over the experiences and supported nattily by androidx we have a lib to help us do all those things.
WorkManager is a library used to en-queue deferrable work that is guaranteed to execute sometime after its Constraints are met. WorkManager allows observation of work status and the ability to create complex chains of work.

To summarize, WorkManager offers the following benefits:
- Handles compatibility with different OS versions
- Follows system health best practices
- Supports asynchronous one-off and periodic tasks
- Supports chained tasks with input/output
- Lets you set constraints on when the task runs
- Guarantees task execution, even if the app or device restarts

## Decision

We designed to chose the lib to handle uploads and downloads with LinShare in the background

## Consequences

Basically, Work Manager came with Android Jetpack handles background operations(i.e Services). That is suitable to develop for the Linshare android application in the future.

The result of this library practice is shown good result, could be use for most our case to implement task in background and observation work well.

## References

[WorkManager](https://developer.android.com/reference/kotlin/androidx/work/WorkManager.html).
[More pros](https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712).
[Alternatives](https://medium.com/@julian_falcionelli/background-processing-in-android-575fd4ecf769)


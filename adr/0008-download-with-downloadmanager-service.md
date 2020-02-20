# 8. Download with DownloadManager service

Date: 2020-02-20

## Status

Accepted

## Context

We have some ways to perform downloading stable in the background, but system exposed a service called `DownloadManager`. 
Client may request that a URI be downloaded to a particular destination file. The download manager will conduct the 
download in the background, taking care of HTTP interactions and retrying downloads after failures or across connectivity changes and system reboot.
 
Apps that request downloads through this API can register a broadcast receiver to handle when the download is progress, failure, completed.

## Decision

Instead of implementing a `Worker` like `Upload`, we will delegate downloading task to `DownloadManager` system service. 

## Consequences

As using `DownloadManager` we don't need to maintain an `Worker` to handle some stuffs (execute a long-running HTTP download, error handing, processing notification)

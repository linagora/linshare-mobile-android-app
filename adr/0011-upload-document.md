# 11. Upload document

Date: 2020-03-05

## Status

Accepted

## Context

At this time, we used `android.net.Uri` to extract information and open an `InputStream` to upload.

But, an `Uri` has `UriPermission` protect the data which is represented. 
This permission will be revoked by shared application when the received Activity no-longer run.
It is not critical to Android 7, but with Android 9 we always get `Permission Denied` when deliver Uri to Worker to execute.

## Decision

- We extract all requirement information of the Document at the time we receive the Intent.

- Instead of using Uri directly, we create a temporary file to store the file which Uri represent

- We deliver temporary file path to Worker

## Consequences

`+` We will not get `Permission Denied` in `UploadWorker` anymore 

`-` We will can not upload whenever device can not enough storage to create temp file

## References

[URI permissions](https://developer.android.com/guide/topics/permissions/overview#uri)
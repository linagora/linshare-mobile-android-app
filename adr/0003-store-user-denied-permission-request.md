# 3. Store User Denied Permission Request

Date: 2020-01-03

## Status

Accepted

## Context

#### Request permission
Application need `READ_EXTERNAL_STORAGE` to be allowed to perform file upload.

Every time upload is performed, application must check that permission

```kotlin
    when (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        PackageManager.PERMISSION_GRANTED -> { performUpload() }
        else -> { requestReadStoragePermission() }
    }
```
If the app has the permission, the method will returns `PERMISSION_GRANTED`.
Otherwise, the app has to explicit ask the user for permission.

#### Explain why the app needs permission

> One approach you might use is to provide an explanation only if the user 
has already denied that permission request. Android provides a utility method, 
`shouldShowRequestPermissionRationale()`, that returns `true` if the user has previously denied the request, 
and returns `false` if a user has denied a permission and selected the Don't ask again option 
in the permission request dialog, or if a device policy prohibits the permission.
 
Following code check if the app has permission to read storage:
```kotlin
// Here, thisActivity is the current activity
if (ContextCompat.checkSelfPermission(thisActivity,
        Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

    // Permission is not granted
    // Should we show an explanation?
    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE)) {
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
    } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(thisActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)

        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }
} else {
    // Permission has already been granted
}
```

#### Problem

>`shouldShowRequestPermissionRationale()`, that returns `true` if the user has previously denied the request

This method return `false` in the first time app call it. 

So that, System Permission request can not show and Explanation dialog always show.

## Decision

Store a flags to indicate User has denied permission or not and change the code above

## Consequences

It make the Permission Request is showed correctly

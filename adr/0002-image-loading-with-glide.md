# 7. Image Loading with Glide

Date: 2019-12-17

## Status

Accepted

## Context

In the android linshare application, we implement the list file from the space of user. Proposed user interface design includes the thumbnail of file preview when user wants to show
It is necessary to have an library to process first part is image thumbnail and preview.
Over the best practice of image processing there are 2 libraries is very commons by android developer community: Glide and Picasso
To compare between them, the commonly usage is the same, but Glide have much more strengthen rather than Picasso.
It process the image source and have method to generate the thumbnail natively, it consume less the memory than Picasso and the library is have smaller packer with much more APIs to help process image, witch could be useful later when we implement more functionality in the application

## Decision

We decided to use Glide instead of Picasso.

## Consequences

Base on our implementation, the application is working well and for the developer it should be also save time to develop the feature base on Glide lib

## References

[Comparison](https://medium.com/@multidots/glide-vs-picasso-930eed42b81d).
[Source code](https://github.com/bumptech/glide).

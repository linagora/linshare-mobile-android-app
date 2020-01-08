# 4. MVVM for android development

Date: 2019-12-17

## Status

Accepted

## Context

When we start to develop the android application, we have to chose the right architecture 
If you don’t choose the right architecture for your Android project, you will have a hard time maintaining it as your codebase grows and your team expands.
Here it comes with mostly behavioral design patents, MVC, MVP and MVVM, for linshare application and target for code quality, where MVVM is suitable for our code base could be testable, reduce some middle code to biding between layers

### Advantages of MVVM Architecture

- Code is even more easily testable than with plain MVVM.
- Code is further decoupled (the biggest advantage.)
- The package structure is even easier to navigate.
- The project is even easier to maintain.
- Team can add new features even more quickly.

### The Layers of MVVM with Clean Architecture

The code is divided into three separate layers:

- Presentation Layer
- Domain Layer
- Data Layer

## Decision

We agreed to implement the MVVM application design on android application.

## Consequences

The atenatives 
- MVC has a drawback presenter has NO ANDROID API and it can be easily tested. 
- MVVM easy to unit test base on the design and have Comparison in references.
- There are also some concern for the design patent MVVM. It takes time for newcomer, we have to explain how all the layers work together. It adds maybe extra classes, so it’s not ideal for low-complexity projects.

## References

[MVVM](https://developer.android.com/jetpack/docs/guide).
[More pros](https://www.toptal.com/android/android-apps-mvvm-with-clean-architecture).
[Comparison](https://medium.com/@mr.anmolsehgal/common-android-architectures-mvc-vs-mvp-vs-mvvm-afd8461e1fee).

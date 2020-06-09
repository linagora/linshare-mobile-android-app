package com.linagora.android.linshare.inject.annotation

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ChildFragmentScoped

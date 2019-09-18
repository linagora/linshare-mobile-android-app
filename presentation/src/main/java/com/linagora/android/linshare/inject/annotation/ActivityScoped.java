package com.linagora.android.linshare.inject.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.*;

import javax.inject.*;

import kotlin.annotation.*;

@MustBeDocumented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScoped {
}

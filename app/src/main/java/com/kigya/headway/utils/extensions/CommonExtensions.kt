package com.kigya.headway.utils.extensions

val Any.TAG: String
    get() = if (!javaClass.isAnonymousClass) {
        javaClass.simpleName
    } else {
        javaClass.name
    }

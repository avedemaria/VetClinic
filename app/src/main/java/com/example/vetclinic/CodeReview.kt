package com.example.vetclinic

@Target(AnnotationTarget.EXPRESSION, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class CodeReview(val comment: String)

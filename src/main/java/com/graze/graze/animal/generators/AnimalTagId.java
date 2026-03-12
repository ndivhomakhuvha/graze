package com.graze.graze.animal.generators;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(AnimalTagGenerator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface AnimalTagId {
}

/*
 * Copyright (C) 2018 The Harbby Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.harbby.gadtry.aop.v1;

import com.github.harbby.gadtry.aop.Binder;
import com.github.harbby.gadtry.aop.model.ClassInfo;
import com.github.harbby.gadtry.aop.model.MethodInfo;
import com.github.harbby.gadtry.aop.model.Pointcut;
import com.github.harbby.gadtry.classloader.ClassScanner;
import com.github.harbby.gadtry.collection.ImmutableSet;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.github.harbby.gadtry.base.Checks.checkState;

public class LocationBuilder
{
    private final Pointcut pointcut;
    private Set<Class<?>> inputClass = new HashSet<>();
    //class filter
    private String packageName;
    private Class<? extends Annotation>[] classAnnotations;
    private Class<?>[] subclasses;
    private Function<Class<?>, Boolean> whereClass = aClass -> true;
    //-- method filter
    private Class<? extends Annotation>[] methodAnnotations;
    private Class<?>[] returnTypes;
    private Function<MethodInfo, Boolean> whereMethod;

    public LocationBuilder(Pointcut pointcut)
    {
        this.pointcut = pointcut;
    }

    public LocationBuilder withPackage(String packageName)
    {
        this.packageName = packageName;
        return this;
    }

    @SafeVarargs
    public final LocationBuilder methodAnnotated(Class<? extends Annotation>... methodAnnotations)
    {
        this.methodAnnotations = methodAnnotations;
        return this;
    }

    @SafeVarargs
    public final LocationBuilder classAnnotated(Class<? extends Annotation>... classAnnotations)
    {
        this.classAnnotations = classAnnotations;
        return this;
    }

    public LocationBuilder classes(Class<?>... inputClass)
    {
        this.inputClass = ImmutableSet.of(inputClass);
        return this;
    }

    /**
     * or
     */
    public LocationBuilder subclassOf(Class<?>... subclasses)
    {
        this.subclasses = subclasses;
        return this;
    }

    public LocationBuilder returnType(Class<?>... returnTypes)
    {
        this.returnTypes = returnTypes;
        return this;
    }

    public LocationBuilder whereMethod(Function<MethodInfo, Boolean> whereMethod)
    {
        this.whereMethod = whereMethod;
        return this;
    }

    public LocationBuilder whereClass(Function<ClassInfo, Boolean> whereClass)
    {
        checkState(whereClass != null, "whereClass is null");
        this.whereClass = (aClass) -> whereClass.apply(ClassInfo.of(aClass));
        return this;
    }

    public Binder.PointBuilder build()
    {
        Set<Class<?>> scanClass = new HashSet<>();
        if (packageName != null) {
            ClassScanner scanner = ClassScanner.builder(packageName)
                    .annotated(classAnnotations)
                    .subclassOf(subclasses)
                    .filter(whereClass)
                    .scan();
            scanClass.addAll(scanner.getClasses());
        }
        scanClass.addAll(inputClass);

        Location location = new Location(
                methodAnnotations,
                returnTypes,
                whereMethod,
                scanClass
        );

        pointcut.setLocation(location);
        return new Binder.PointBuilder(pointcut);
    }
}

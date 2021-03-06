/*
 * Copyright 2012-2013 Project Funcito Contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.funcito.example.collectionsgeneric;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.funcito.FuncitoCollectGen.*;

public class MyClass {

    private String myString;
    private Integer other;

    public static final MyClass stubbedCallsTo = callsTo(MyClass.class);

    public static final Transformer<MyClass, String> getMyString = transformerFor(stubbedCallsTo.getMyString());
    public static final Transformer<MyClass, Integer> getOther = transformerFor(stubbedCallsTo.getOther());

    // alternative single line syntax
    public static final Transformer<MyClass, String> getMyStringF2 = transformerFor(callsTo(MyClass.class).getMyString());

    // These demonstrate not only predicates, but also method argument binding
    public static final Predicate<MyClass> isLengthGT1 = predicateFor(stubbedCallsTo.isLengthGreaterThan(1));
    public static final Predicate<MyClass> isLengthGT3 = predicateFor(stubbedCallsTo.isLengthGreaterThan(3));

    // demonstrating Closure creation around a method call that has a return value.
    public static final Closure<MyClass> incAndReturn = closureFor(stubbedCallsTo.incAndReturn());

    // demonstrating Closure creation around a void method call.

    // Note that for void version, the wrapped method call must be "prepared" before the Closure is created.  Also,
    // to be able to assign to a static field via an initializer, prepareVoid() requires a static block.  If assignment
    // were to a non-static field, a non-static block would be sufficient.  If assignment to a Closure variable is not
    // done in a field initializer (i.e., a local variable assignment or a field assignment in a method or execution
    // block), then no-extra "blocking" is needed for the prepareVoid() call.
    static {prepareVoid(stubbedCallsTo).inc();}
    public static final Closure<MyClass> inc = voidClosure();

    public MyClass(String myString, Integer other) {
        this.myString = myString;
        this.other = other;
    }

    public String getMyString() {
        return myString;
    }

    public void setMyString(String value) {
        this.myString = value;
    }

    public Integer getOther() {
        return other;
    }

    public void setOther(Integer other) {
        this.other = other;
    }

    public void inc() {
        ++other;
    }

    public Integer incAndReturn() {
        inc();
        return other;
    }

    public boolean isLengthGreaterThan(int lower) {
        return getMyString().length() > lower;
    }

    @Override
    public String toString() {
        return getMyString();
    }

    public static void main(String[] args) {
        MyClass m1 = new MyClass("A", 1);
        MyClass m2 = new MyClass("B", 2);
        MyClass m3 = new MyClass("C", 3);

        List<MyClass> list = Arrays.asList(m1,m2,m3);
        demoListTransforms(list);
        demoListFilters(list);
        demoClosures(list);

        m1.setMyString("This is my new value");
        m1.setOther(777);
        demoListTransforms(list);
        demoListFilters(list);
        demoClosures(list);
    }

    protected static void demoListTransforms(List<MyClass> list) {
        Collection<String> strList1 = CollectionUtils.collect(list, getMyString);
        Collection<String> strList2 = CollectionUtils.collect(list, getMyStringF2);
        Collection<Integer> intList = CollectionUtils.collect(list, getOther);

        printValues("xform1", strList1);
        printValues("xform2", strList2);
        printValues("otherXform", intList);
    }

    protected static void demoListFilters(List<MyClass> list) {
        Collection<MyClass> filtered1 = CollectionUtils.select(list, isLengthGT1);
        Collection<MyClass> filtered2 = CollectionUtils.select(list, isLengthGT3);

        printValues("select length > 1", filtered1);
        printValues("select length > 3", filtered2);
    }

    protected static void demoClosures(List<MyClass> list) {
        CollectionUtils.forAllDo(list, incAndReturn);
        printValues("List has incremented each", CollectionUtils.collect(list, getOther));

        CollectionUtils.forAllDo(list, inc);
        printValues("List has incremented each again", CollectionUtils.collect(list, getOther));
    }

    protected static void printValues(String desc, Collection<?> list) {
        System.out.println(desc + ": " + list);
    }
}

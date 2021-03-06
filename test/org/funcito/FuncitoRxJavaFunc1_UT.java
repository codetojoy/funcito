package org.funcito;

/**
 * Copyright 2013 Project Funcito Contributors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.funcito.internal.WrapperType;
import org.junit.After;
import org.junit.Test;
import rx.functions.Func1;

import static org.funcito.FuncitoRxJava.*;
import static org.funcito.mode.Modes.safeNav;
import static org.junit.Assert.*;

public class FuncitoRxJavaFunc1_UT {

    private StringThing CALLS_TO_STRING_THING = callsTo(StringThing.class);

    private static class StringThing {
        protected String myString;

        public StringThing(String myString) { this.myString = myString; }
        public int size() { return myString.length(); }
        public String toString() { return myString; }
    }

    @After
    public void tearDown() {
        try {
            // cleanup aftermath of failed tests
            delegate().extractInvokableState(WrapperType.RXJAVA_FUNC1);
        } catch (Throwable t) {}
    }

    @Test
    public void testFunc1For_AssignToFunc1WithMatchingTypes() {
        Func1<StringThing, Integer> superTypeRet = func1For(CALLS_TO_STRING_THING.size());
        assertEquals(3, superTypeRet.call(new StringThing("ABC")).intValue());
    }

    @Test
    public void testFunc1For_AssignToFunc1WithSourceSuperType() {
        Func1<Object, Integer> superTypeRet = func1For(CALLS_TO_STRING_THING.size());
        assertEquals(3, superTypeRet.call(new StringThing("ABC")).intValue());
    }

    @Test
    public void testFunc1For_AssignToFuncWithTargetSuperType() {
        Func1<StringThing, ? extends Number> superType = func1For(CALLS_TO_STRING_THING.size());
        StringThing thing = new StringThing("123456");
        Number n = superType.call(thing);
        assertEquals(6, n);
    }

    @Test
    public void testFunc1For_AllowUpcastToExtensionGenericType() {
        class Generic<T> {
            public Integer getVal() { return 123; }
        }
        Func1<Generic<?>, Integer> stringFunc = func1For(callsTo(Generic.class).getVal());
        Generic<Integer> integerGeneric = new Generic<Integer>();

        assertEquals(123, stringFunc.call(integerGeneric).intValue());
    }

    @Test
    public void testFunc1For_ExpressionsWithOperatorsAreUnsupported() {
        Func1<StringThing,String> pluralFunc = func1For(CALLS_TO_STRING_THING.toString() + "s");
        StringThing dog = new StringThing("dog");

        // NOTE: this test is a test that proves and documents a limitation of Funcito
        assertFalse("dogs".equals( pluralFunc.call(dog)));
        assertEquals("dog", pluralFunc.call(dog));
    }

    @Test
    public void testFunc1For_TypedAndUntypedModes() {
        class Child {}
        class Parent {
            public Child getChild() { return null; }
        }
        Parent parent = new Parent();
        Child altChild = new Child();
        assertNull(parent.getChild());

        // Note that in parameterized version, null must be cast for compile to work.
        Func1<Parent, Child> func = func1For(callsTo(Parent.class).getChild(), safeNav(altChild));
        Func1<Parent, Child> func2 = func1For(callsTo(Parent.class).getChild(), safeNav());

        assertSame(altChild, func.call(parent));
        assertNull(func2.call(parent));
    }
}


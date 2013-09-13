package org.funcito;

import org.funcito.internal.FuncitoDelegate;
import org.funcito.internal.WrapperType;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import play.libs.F.Callback;

import java.util.ArrayList;

import static org.funcito.FuncitoPlay2.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public class FuncitoPlay2Callback_UT {

    public @Rule ExpectedException thrown = ExpectedException.none();
    private Grows CALLS_TO_GROWS = callsTo(Grows.class);

    @After
    public void tearDown() {
        try {
            new FuncitoDelegate().extractInvokableState(WrapperType.JEDI_VOID_COMMAND);
        } catch (Throwable t) {}
    }

    public class Grows {
        int i = 0;
        public String incAndReturn() {
            return Integer.toString(++i);
        }
        public void inc() {
            i++;
        }
        public void dec() {
            i--;
        }
    }

    @Test
    public void testCallbackFor_AssignToCallbackWithSourceSuperType() throws Throwable {
        Grows grows = new Grows();

        Callback<Object> superTypeRet = callbackFor(CALLS_TO_GROWS.incAndReturn()); // Generic type is Object instead of Grows
        assertEquals(0, grows.i);

        superTypeRet.invoke(grows);
        assertEquals(1, grows.i);
    }

    class Generic<N extends Number> {
        Double number;
        public Generic(N n) { number = n.doubleValue(); }
        public Double incAndGet() {
            return ++number;
        }
        public void voidInc() {
            ++number;
        }
    }

    @Test
    public void testCallbackFor_ValidateDetectsMismatchedGenericTypes() {
        Callback<Generic<Float>> floatGenericCallback = callbackFor(callsTo(Generic.class).incAndGet());
        Generic<Integer> integerGeneric = new Generic<Integer>(0);

//        The below can't actually be compiled, which proves the test passes: compile time mismatch detection
//        floatGenericCallback.f(integerGeneric);
    }

    @Test
    public void testCallbackFor_AllowUpcastToExtensionGenericType() throws Throwable {
        Callback<Generic<? extends Object>> incCallback = callbackFor(callsTo(Generic.class).incAndGet());
        Generic<Integer> integerGeneric = new Generic<Integer>(0);
        assertEquals(0, integerGeneric.number, 0.01);

        incCallback.invoke(integerGeneric);

        assertEquals(1.0, integerGeneric.number, 0.01);
    }

    @Test
    public void testCallbackFor_SingleArgBinding() throws Throwable {
        class IncList extends ArrayList<Integer> {
            public int incIndex(int i) {
                int oldVal = this.get(i);
                int newVal = oldVal + 1;
                this.set(i, newVal);
                return newVal;
            }
        }
        IncList callsToIncList = callsTo(IncList.class);
        Callback<IncList> incElem0Func = callbackFor(callsToIncList.incIndex(0));
        Callback<IncList> incElem2Func = callbackFor(callsToIncList.incIndex(2));
        IncList list = new IncList();
        list.add(0); list.add(100); list.add(1000);

        incElem0Func.invoke(list);
        incElem2Func.invoke(list);

        assertEquals(1, list.get(0).intValue());
        assertEquals(100, list.get(1).intValue()); // unchanged
        assertEquals(1001, list.get(2).intValue());
    }

    @Test
    public void testVoidCallback_withPrepare() throws Throwable {
        Grows grows = new Grows();

        prepareVoid(CALLS_TO_GROWS).inc();
        Callback<Grows> normalCall = voidCallback();

        assertEquals(0, grows.i);
        normalCall.invoke(grows);
        assertEquals(1, grows.i);
    }

    @Test
    public void testVoidCallback_withoutPrepare() throws Throwable {
        Grows grows = new Grows();

        // non-preferred.  Better to use prepare() to help explain
        CALLS_TO_GROWS.inc();
        Callback<Grows> normalCall = voidCallback();

        assertEquals(0, grows.i);
        normalCall.invoke(grows);
        assertEquals(1, grows.i);
    }

    @Test
    public void testVoidCallback_prepareWithNoMethodCall() {
        prepareVoid(CALLS_TO_GROWS); // did not append any ".methodCall()" after close parenthesis

        thrown.expect(FuncitoException.class);
        thrown.expectMessage("No call to a");
        Callback<Grows> badCall = voidCallback();
    }

    @Test
    public void testVoidCallback_AssignToCallbackWithSourceSuperTypeOk() throws Throwable {
        Grows grows = new Grows();

        prepareVoid(CALLS_TO_GROWS).inc();
        Callback<Object> superTypeRet = voidCallback(); // Generic type is Object instead of Grows
        assertEquals(0, grows.i);

        superTypeRet.invoke(grows);
        assertEquals(1, grows.i);
    }

    @Test
    public void testVoidCallback_unsafeAssignment() throws Throwable {
        prepareVoid(CALLS_TO_GROWS).inc();
        Callback<Integer> unsafe = voidCallback();  // unsafe assignment compiles

        thrown.expect(FuncitoException.class);
        thrown.expectMessage("Method inc() does not exist");
        unsafe.invoke(3); // invocation target type does not match prepared target type
    }

    @Test
    public void testVoidCallback_badOrderOfPrepares() {
        // First call below requires a subsequent call to voidCallback() before another prepareVoid()
        prepareVoid(CALLS_TO_GROWS).inc();

        thrown.expect(FuncitoException.class);
        thrown.expectMessage("or back-to-back \"prepareVoid()\" calls");
        // bad to call prepareVoid() twice without intermediate voidCallback()
        prepareVoid(CALLS_TO_GROWS).dec();
        // see clean-up in method tearDown()
    }

    @Test
    public void testVoidCallback_interleavedPreparesDifferentSourcesAlsoNotOk() {
        prepareVoid(CALLS_TO_GROWS).inc();

        thrown.expect(FuncitoException.class);
        thrown.expectMessage("or back-to-back \"prepareVoid()\" calls");
        prepareVoid(callsTo(Generic.class)).voidInc();
    }

    @Test
    public void testVoidCallback_preparedForNonVoidMethod() throws Throwable {
        Grows grows = new Grows();
        assertEquals(0, grows.i);

        prepareVoid(CALLS_TO_GROWS).incAndReturn();
        Callback<Grows> e = voidCallback();

        e.invoke(grows);

        assertEquals(1, grows.i);
    }
}


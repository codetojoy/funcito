/*
 * Copyright 2012-2013 Project Funcito Contributors
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
package org.funcito.rxjava;

import org.funcito.internal.FuncitoDelegate;
import org.funcito.internal.InvokableState;
import org.funcito.mode.TypedMode;
import org.funcito.mode.NoOp;
import org.funcito.mode.Mode;
import rx.functions.Action1;
import rx.functions.Func1;

import static org.funcito.internal.WrapperType.*;

public class RxJavaDelegate extends FuncitoDelegate {

    /**
     * Delegated version of <code>FuncitoRxJava.func1For(V,TypedMode)</code>
     * @see org.funcito.FuncitoRxJava#func1For(Object)
     */
    public <T,V> Func1<T,V> func1For(V ignoredRetVal) {
        final InvokableState state = extractInvokableState(RXJAVA_FUNC1);
        return new RxJavaFunc1<T, V>(state, NoOp.NO_OP);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.func1For(V,TypedMode)</code>
     * @see org.funcito.FuncitoRxJava#func1For(Object, TypedMode)
     */
    public <T,V> Func1<T,V> func1For(V ignoredRetVal, TypedMode<V> mode) {
        final InvokableState state = extractInvokableState(RXJAVA_FUNC1);
        return new RxJavaFunc1<T, V>(state, mode);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.func1For(V,Mode)</code>
     * @see org.funcito.FuncitoRxJava#func1For(Object, Mode)
     */
    public <T,V> Func1<T,V> func1For(V ignoredRetVal, Mode mode) {
        final InvokableState state = extractInvokableState(RXJAVA_FUNC1);
        return new RxJavaFunc1<T, V>(state, mode);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.action1For(Object)</code>
     * @see org.funcito.FuncitoRxJava#action1For(Object)
     */
    public <T> Action1<T> action1For(Object proxiedMethodCall) {
        InvokableState state = extractInvokableState(RXJAVA_ACTION1);
        return new RxJavaAction1<T>(state, NoOp.NO_OP);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.action1For(Object,TypedMode)</code>
     * @see org.funcito.FuncitoRxJava#action1For(Object,TypedMode)
     */
    public <T> Action1<T> action1For(Object proxiedMethodCall, TypedMode<Void> mode) {
        InvokableState state = extractInvokableState(RXJAVA_ACTION1);
        return new RxJavaAction1<T>(state, mode);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.action1For(Object,Mode)</code>
     * @see org.funcito.FuncitoRxJava#action1For(Object,Mode)
     */
    public <T> Action1<T> action1For(Object proxiedMethodCall, Mode mode) {
        InvokableState state = extractInvokableState(RXJAVA_ACTION1);
        return new RxJavaAction1<T>(state, mode);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.prepareVoid(T)</code>
     * @see org.funcito.FuncitoRxJava#prepareVoid(Object)
     */
    public <T> T prepareVoid(T t) { return t; }

    /**
     * Delegated version of <code>FuncitoRxJava.voidAction1()</code>
     * @see org.funcito.FuncitoRxJava#voidAction1()
     */
    public <T> Action1<T> voidAction1() {
        InvokableState state = extractInvokableState(RXJAVA_VOID_ACTION1);
        return new RxJavaAction1<T>(state, NoOp.NO_OP);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.voidAction1(TypedMode)</code>
     * @see org.funcito.FuncitoRxJava#voidAction1(TypedMode)
     */
    public <T> Action1<T> voidAction1(TypedMode<Void> mode) {
        InvokableState state = extractInvokableState(RXJAVA_VOID_ACTION1);
        return new RxJavaAction1<T>(state, mode);
    }

    /**
     * Delegated version of <code>FuncitoRxJava.voidAction1(Mode)</code>
     * @see org.funcito.FuncitoRxJava#voidAction1(Mode)
     */
    public <T> Action1<T> voidAction1(Mode mode) {
        InvokableState state = extractInvokableState(RXJAVA_VOID_ACTION1);
        return new RxJavaAction1<T>(state, mode);
    }
}

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
package org.funcito;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.funcito.collectionsgeneric.CollectGenDelegate;
import org.funcito.mode.Mode;
import org.funcito.mode.TypedMode;
import org.funcito.mode.Modes;

/**
 * This class is the static entry point of the Funcito API for Collections-Generic.
 */
public class FuncitoCollectGen {

    private static final CollectGenDelegate collectGenDelegate = new CollectGenDelegate();

    private FuncitoCollectGen() {}

    /**
     * Generates a proxy object for use with other <code>FuncitoCollectGen</code> static methods.  This proxy should not
     * be used for any other purposes.  It is a convenience pass-thru to
     * {@link org.funcito.internal.FuncitoDelegate#callsTo(Class)}.  Example usages are:
     * <p>
     * <pre>
     * <code>
     *     // inlined:
     *     Transformer&lt;MyClass,RetType1&gt; xform = transformerFor( callsTo(MyClass.class).methodWithRetType1() );
     *
     *     // or extracted for repeat usage
     *     final MyClass CALLS_TO = callsTo(MyClass.class);
     *     Transformer&lt;MyClass,RetType1&gt; xform2 = transformerFor( CALLS_TO.method1WithRetType1() );
     *     Transformer&lt;MyClass,RetType2&gt; xform3 = transformerFor( CALLS_TO.method2WithRetType2() );
     * </code>
     * </pre>
     * <p>
     * Stubs are cached, so this method can be called multiple times for the same class without penalty.  But a single proxy
     * can also be reused for creating multiple Collections-Generic <code>Transformer</code> or <code>Predicate</code> objects.
     * @param clazz is the class to be proxied
     * @return a proxy which can be used by other <code>FuncitoCollectGen</code> static methods
     */
    public static <T> T callsTo(Class<T> clazz) {
        return collectGenDelegate.callsTo(clazz);
    }

    /**
     * Generates a Collections-Generic <code>Transformer</code> object that wraps a method call or method chain.  Resulting
     * <code>Transformer</code> is as thread-safe as the method itself.  Example usage is:
     * <p>
     * <code>
     *     Transformer&lt;MyClass,RetType1&gt; xform = transformerFor( callsTo(MyClass.class).methodWithRetType1() );
     * </code>
     * <p>
     * You can wrap methods with parameters, so long as you statically provide values for each
     * parameter.  Provided parameter values are statically bound to the Transformer and may not be changed:
     * <p>
     * <code>
     *     Transformer&lt;MyClass,RetType1&gt; xform = transformerFor( callsTo(MyClass.class).methodWithArgs("abc", 123L) );<br>
     *     // all invocations of xform will use "abc" and 123L as the arguments to methodWithArgs
     * </code>
     * <p>
     * It is also possible to wrap method call chains, with some restrictions:
     * <p>
     * <code>
     *     MyClass callsTo = callsTo(MyClass.class);<br>
     *     Transformer&lt;MyClass,RetType2&gt; xform = transformerFor( callsTo.methodWithRetType1().methodWithRetType2() );
     * </code>
     * <p>
     * Restrictions for chaining are that intermediate return types (all except for the final return type in the
     * chain) must be proxyable by the current Proxy provider, just like the initial target type.  In the above
     * example this means MyClass and RetType1 must be proxyable, but RetType2 need not be proxyable.
     * Intermediate return types also cannot be a Java Generic type because of type erasure.
     * <p>
     * @param proxiedMethodCall is the return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @return a Collections-Generic <code>Transformer</code> object that wraps the method call or chain.
     */
    public static <T,V>Transformer<T,V> transformerFor(V proxiedMethodCall) {
        return collectGenDelegate.transformerFor(proxiedMethodCall);
    }

    /**
     * <code>TypedMode</code> version of <code>FuncitoCollectGen.transformerFor(V)</code>
     * @see #transformerFor(Object)
     * @see Modes
     * @see TypedMode
     * @param proxiedMethodCall is the return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>TypedMode</code> that modifies the mode of execution of the resulting <code>Transformer</code>
     * @return a collections-generic <code>Transformer</code> object that wraps the method call or chain.
     */
    public static <T,V> Transformer<T,V> transformerFor(V proxiedMethodCall, TypedMode<V> mode) {
        return collectGenDelegate.transformerFor(proxiedMethodCall, (TypedMode<V>) mode);
    }

    /**
     * Untyped <code>Mode</code> version of <code>FuncitoCollectGen.transformerFor(V)</code>
     * @see #transformerFor(Object)
     * @see Modes
     * @see Mode
     * @param proxiedMethodCall is the return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>Mode</code> that modifies the mode of execution of the resulting <code>Transformer</code>
     * @return a collections-generic <code>Transformer</code> object that wraps the method call or chain.
     */
    public static <T,V> Transformer<T,V> transformerFor(V proxiedMethodCall, Mode mode) {
        return collectGenDelegate.transformerFor(proxiedMethodCall, mode);
    }

    /**
     * Generates a Collections-Generic <code>Predicate</code> object that wraps a <code>Boolean</code>- or <code>boolean</code>-return method call
     * or method chain.  Resulting <code>Predicate</code> is as thread-safe as the method itself.
     * Auto-boxing means you may always safely wrap a method that has a primitive boolean return type. Example usage
     * is:
     * <p>
     * <code>
     *     Predicate&lt;MyClass&gt; pred = predicateFor( callsTo(MyClass.class).methodWithBoolRetType() );
     * </code>
     * <p>
     * Users of this <code>Predicate</code> should be aware of a risk with Collections-Generic Predicates (not specific to Funcito) if the
     * return type of the method being wrapped is a Boolean wrapper rather than a primitive boolean.  Such calls are allowed,
     * but there is an inherent null-pointer risk because Collections-Generic <code>Predicate.evaluate(T)</code> returns a boolean primitive.
     * Use overloaded form of this method {@link #predicateFor(Boolean, org.funcito.mode.Mode)} with the {@link org.funcito.mode.TailDefault}
     * TypedMode for a mitigation of this risk.
     * <p>
     * This method also supports wrapping methods with arguments, and method chaining, as documented in {@link #transformerFor(Object)}.
     * @param proxiedMethodCall is the Boolean return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @return a Collections-Generic  <code>Predicate</code> object that wraps the method call or method chain.
     * @see #predicateFor(Boolean, boolean)
     */
    public static <T>Predicate<T> predicateFor(Boolean proxiedMethodCall) {
        return collectGenDelegate.predicateFor(proxiedMethodCall);
    }

    /**
     * Generates a Collections-Generic <code>Predicate</code> object that wraps a <code>Boolean</code>- or boolean-return
     * method call or method chain.  Resulting <code>Predicate</code> is as thread-safe as the method itself.
     * This form of the method is only required when there is a risk of the wrapped method returning a null-value, in which you case
     * you provide the default value to substitute for null return values from the wrapped method.
     * <p>
     * This method also supports wrapping methods with arguments, and method chaining, as documented in {@link #transformerFor(Object)}.
     * @param proxiedMethodCall is the Boolean return value from a method call to a <code>FuncitoCollectGen</code> callsTo object
     * @param defaultForNull is the default value that <code>Predicate</code> will return if the wrapped method returns a null-value.
     * @return a Collections-Generic  <code>Predicate</code> object that wraps the method call or method chain.
     * @see #predicateFor(Boolean)
     */
    // TODO: should this be deprecated?
    public static <T> Predicate<T> predicateFor(Boolean proxiedMethodCall, boolean defaultForNull) {
        return collectGenDelegate.predicateFor(proxiedMethodCall, (TypedMode<Boolean>)Modes.tailDefault(defaultForNull));
    }

    /**
     * <code>TypedMode</code> version of <code>FuncitoCollectGen.predicateFor(Boolean)</code>
     * @see #predicateFor(Boolean)
     * @see Modes
     * @see TypedMode
     * @param proxiedMethodCall is the Boolean return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>TypedMode</code> that modifies the mode of execution of the resulting <code>Predicate</code>
     * @return a collections-generic <code>Predicate</code> object that wraps the method call or chain.
     */
    public static <T> Predicate<T> predicateFor(Boolean proxiedMethodCall, TypedMode<Boolean> mode) {
        return collectGenDelegate.predicateFor(proxiedMethodCall, (TypedMode<Boolean>)mode);
    }

    /**
     * Untyped<code>Mode</code> version of <code>FuncitoCollectGen.predicateFor(Boolean)</code>
     * @see #predicateFor(Boolean)
     * @see Modes
     * @see Mode
     * @param proxiedMethodCall is the Boolean return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>Mode</code> that modifies the mode of execution of the resulting <code>Predicate</code>
     * @return a collections-generic <code>Predicate</code> object that wraps the method call or chain.
     */
    public static <T> Predicate<T> predicateFor(Boolean proxiedMethodCall, Mode mode) {
        return collectGenDelegate.predicateFor(proxiedMethodCall, mode);
    }

    /**
     * Generates a <strong>Collections-Generic</strong> <code>Closure</code> object that wraps a method call or method chain.  Resulting
     * <code>Closure</code> is as thread-safe as the method/chain itself.  This Closure generator can only be used for
     * method calls/chains with a non-void return type, even though the return type is ignored.  For a Closure
     * generator that works with void returning methods/chains, see {@link #voidClosure()}.  Example usage is:
     * <p>
     * <code>
     *     Closure&lt;MyClass&gt; cmd = closureFor( callsTo(MyClass.class).methodWithNonVoidReturnType() );
     * </code>
     * <p>
     * You can wrap methods with parameters, so long as you statically provide values for each
     * parameter.  Provided parameter values are statically bound to the <code>Closure</code> and may not be changed:
     * <p>
     * <code>
     *     Closure&lt;MyClass&gt; cmd = closureFor( callsTo(MyClass.class).methodWithArgs("abc", 123L) );<br/>
     *     // all invocations of closure will use "abc" and 123L as the arguments to methodWithArgs
     * </code>
     * <p>
     * It is also possible to wrap method call chains, with some restrictions:
     * <p>
     * <code>
     *     MyClass callMyClass = callsTo(MyClass.class);<br/>
     *     Closure&lt;MyClass&gt; cmd = closureFor( callsMyClass.methodWithRetType1().methodWithRetType2() );
     * </code>
     * <p>
     * Restrictions for chaining are that intermediate return types (all except for the final return type in the
     * chain) must be proxyable by the current Proxy provider, just like the initial target type.  In the above
     * example this means MyClass and RetType1 must be proxyable, but RetType2 need not be proxyable.
     * Intermediate return types also cannot be a Java Generic type because of type erasure.
     * <p>
     * @param proxiedMethodCall is the return value from an inlined method call to a <code>FuncitoFJ</code> proxy object
     * @param <T> is the input type of the Closure
     * @return a Collections-Generic <code>Closure</code> object that wraps a method call or chain.
     * @see #voidClosure()
     */
    public static <T> Closure<T> closureFor(Object proxiedMethodCall) {
        return     collectGenDelegate.closureFor(proxiedMethodCall);
    }

    /**
     * <code>TypedMode</code> version of <code>FuncitoCollectGen.closureFor(Object)</code>
     * @see #closureFor(Object)
     * @see Modes
     * @see TypedMode
     * @param proxiedMethodCall is the return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>TypedMode</code> that modifies the mode of execution of the resulting <code>Closure</code>
     * @return a collections-generic <code>Closure</code> object that wraps the method call or chain.
     */
    public static <T> Closure<T> closureFor(Object proxiedMethodCall, TypedMode<Void> mode) {
        return     collectGenDelegate.closureFor(proxiedMethodCall, mode);
    }

    /**
     * Untyped <code>Mode</code> version of <code>FuncitoCollectGen.closureFor(Object)</code>
     * @see #closureFor(Object)
     * @see Modes
     * @see Mode
     * @param proxiedMethodCall is the return value from a method call to a <code>FuncitoCollectGen</code> proxy object
     * @param mode is the <code>Mode</code> that modifies the mode of execution of the resulting <code>Closure</code>
     * @return a collections-generic <code>Closure</code> object that wraps the method call or chain.
     */
    public static <T> Closure<T> closureFor(Object proxiedMethodCall, Mode mode) {
        return     collectGenDelegate.closureFor(proxiedMethodCall, mode);
    }

    /**
     * Prepares a method-call or method-chain call that terminates with a void return type, for generation of a
     * <strong>Collections-Generic</strong> <code>Closure</code> object.  Use of this method must be followed with
     * execution of one of the void-Closure methods: {@link #voidClosure()}, {@link #voidClosure(TypedMode)},
     * {@link #voidClosure(Mode)}. Resulting <code>Closure</code> is as thread-safe as the method/chain itself.
     * Example usage is:
     * <p>
     * <code>
     *     prepareVoid(callsTo(MyClass.class)).methodWithVoidReturnType();
     * </code>
     * <p>
     * You can wrap methods with parameters, so long as you statically provide values for each
     * parameter.  Provided parameter values are statically bound to the <code>Closure</code> and may not be changed:
     * <p>
     * <code>
     *     prepareVoid(callsTo(MyClass.class)).voidMethodWithArgs("abc", 123L);<br/>
     *     // all invocations of closure will use "abc" and 123L as the arguments to voidMethodWithArgs
     * </code>
     * <p>
     * It is also possible to wrap method call chains, with some restrictions:
     * <p>
     * <code>
     *     MyClass callMyClass = callsTo(MyClass.class);<br>
     *     prepareVoid(callsMyClass).methodWithRetType1().voidMethod();<br/>
     * </code>
     * <p>
     * Restrictions for chaining are that intermediate return types (final return type does not matter because it is
     * void) must be proxyable by the current Proxy provider, just like the initial target type.  In the above
     * example this means MyClass and RetType1 must be proxyable.  Intermediate return types also cannot be a Java
     * Generic type because of type erasure.
     * <p>
     * @return the same Funcito proxy object that is passed in.  Provided for fluent API so that desired method chain
     * call may be directly appended.
     * @param <T> is the input type of the Closure being prepared
     * @see #voidClosure()
     */
    public static <T> T prepareVoid(T t) {
        return     collectGenDelegate.prepareVoid(t);
    }

    /**
     * Generates a <strong>Collections-Generic</strong> <code>Closure</code> object that wraps a method call or method chain.  Resulting
     * <code>Closure</code> is as thread-safe as the method/chain itself.  This Closure generator is only  appropriate for
     * method calls/chains with a void return type, and it requires previous usage of {@link #prepareVoid(Object)}.
     * Example usage is:
     * <p>
     * <code>
     *     prepareVoid(callsTo(MyClass.class)).methodWithVoidReturnType();<br/>
     *     Closure&lt;MyClass&gt; cmd = voidClosure();
     * </code>
     * <p>
     * @return a Collections-Generic <code>Closure</code> object that wraps a previously prepared method call or chain.
     * @param <T> is the input type of the Closure
     * @see #prepareVoid(Object)
     */
    public static <T> Closure<T> voidClosure() {
        return     collectGenDelegate.voidClosure();
    }

    /**
     * <code>TypedMode</code> version of <code>FuncitoCollectGen.voidClosure()</code>
     * @see #voidClosure()
     * @see Modes
     * @see TypedMode
     * @param mode is the <code>TypedMode</code> that modifies the mode of execution of the resulting <code>Closure</code>
     * @return a collections-generic <code>Closure</code> object that wraps the method call or chain.
     */
    public static <T> Closure<T> voidClosure(TypedMode<Void> mode) {
        return     collectGenDelegate.voidClosure(mode);
    }

    /**
     * Untyped <code>Mode</code> version of <code>FuncitoCollectGen.voidClosure()</code>
     * @see #voidClosure()
     * @see Modes
     * @see Mode
     * @param mode is the <code>Mode</code> that modifies the mode of execution of the resulting <code>Closure</code>
     * @return a collections-generic <code>Closure</code> object that wraps the method call or chain.
     */
    public static <T> Closure<T> voidClosure(Mode mode) {
        return     collectGenDelegate.voidClosure(mode);
    }

    static CollectGenDelegate delegate() {
        return collectGenDelegate;
    }
}

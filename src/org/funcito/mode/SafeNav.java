/*
 * Copyright 2013 Project Funcito Contributors
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
package org.funcito.mode;

import org.funcito.internal.InvokableState;
import org.funcito.functorbase.FunctorBase;
import org.funcito.functorbase.SafeNavFunctor;

/**
 * This <code>Mode</code> provides a <code>SafeNavFunctor</code> with an implicit null default value
 * @see SafeNavFunctor
 */
public class SafeNav implements Mode {

    public static final SafeNav SAFE_NAV = new SafeNav();

    @Override
    public FunctorBase<?,?> makeBase(InvokableState invokableState) {
        return new SafeNavFunctor<Object,Object>(invokableState, null);
    }
}

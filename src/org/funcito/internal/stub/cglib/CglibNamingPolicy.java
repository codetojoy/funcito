/*
 * Copyright 2011 Project Funcito Contributors
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
package org.funcito.internal.stub.cglib;

import net.sf.cglib.core.DefaultNamingPolicy;

class CglibNamingPolicy extends DefaultNamingPolicy {

    public static final CglibNamingPolicy INSTANCE = new CglibNamingPolicy();

    @Override
    protected String getTag() {
        return "ByFuncitoWithCGLIB";
    }
}
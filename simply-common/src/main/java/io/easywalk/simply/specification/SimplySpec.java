/*
 * Copyright (C) 2021 Dongju Paek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Base Specification of Simply
 *
 * @author Dongju Paek(bravopotato@gmail.com)
 * @since 0.1
 */
package io.easywalk.simply.specification;

import java.util.List;
import java.util.Map;

public interface SimplySpec<T, ID> {
    T create(T createForm) throws Throwable;

    T replaceById(ID id, T replace) throws Throwable;

    T updateById(ID id, Map<String, Object> fields) throws Throwable;

    T get(ID id) throws Throwable;

    List<T> getAll() throws Throwable;

    void deleteById(ID id) throws Throwable;

    void delete(T entity) throws Throwable;
}

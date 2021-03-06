/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ratpackframework.routing

import org.ratpackframework.templating.TemplateRenderer
import org.ratpackframework.responder.internal.ClosureBackedResponderFactory
import org.ratpackframework.routing.internal.PathRouter
import groovy.transform.CompileStatic

@CompileStatic
/**
 * This is the API in the ratpack.groovy file.
 */
class RouterBuilder {

  private final List<Router> routers = []

  private final TemplateRenderer templateRenderer

  RouterBuilder(List<Router> routers, TemplateRenderer templateRenderer) {
    this.routers = routers
    this.templateRenderer = templateRenderer
  }

  void register(String method, String path, Closure handler) {
    routers << new PathRouter(path, method, new ClosureBackedResponderFactory(templateRenderer, handler))
  }

  void get(String path, Closure handler) {
    register("get", path, handler)
  }

  void post(String path, Closure handler) {
    register("post", path, handler)
  }

  void put(String path, Closure handler) {
    register("put", path, handler)
  }

  void delete(String path, Closure handler) {
    register("delete", path, handler)
  }

}

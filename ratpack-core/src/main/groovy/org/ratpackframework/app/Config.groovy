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

package org.ratpackframework.app

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString
@CompileStatic
class Config {

  int port

  String publicDir
  String templatesDir
  String routes

  Config() {
    publicDir = "public"
    templatesDir = "templates"
    routes = "ratpack.groovy"
    port = 5050
  }

  void port(int port) {
    setPort(port)
  }

  void publicDir(String publicDir) {
    setPublicDir(publicDir)
  }

  void templatesDir(String templatesDir) {
    setTemplatesDir(templatesDir)
  }

  void routes(String routes) {
    setRoutes(routes)
  }

}

/*
 * Copyright 2013 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.gradle.flyway.task;

import com.googlecode.flyway.core.Flyway
import org.gradle.api.DefaultTask

/**
 * A base class for all flyway tasks.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
abstract class AbstractFlywayTask extends DefaultTask {

  AbstractFlywayTask() {
    group = 'Flyway'
  }

  /** Creates a new, configured flyway instance */
  protected Flyway create() {
    Flyway flyway = new Flyway()
    def props = new Properties()

    project.flyway.properties
      .findAll { name, value ->
        (value instanceof String) || (value instanceof Boolean)
      }.each { name, value ->
        props.setProperty("flyway.${name}", value)
      }

    def schemas = project.flyway.schemas.join(',')
    if (!schemas.isEmpty()) {
      props.setProperty('flyway.schemas', schemas)
    }

    def locations = project.flyway.locations.join(',')
    if (!locations.isEmpty()) {
      props.setProperty('flyway.locations', locations)
    }

    project.flyway.placeholders.each { name, value ->
      props.setProperty("flyway.placeholders.${name}", value)
    }

    logger.info 'Flyway configuration:'
    props.each { name, value ->
      logger.info " - ${name}: ${value}"
    }

    flyway.configure(props)
    flyway
  }
}

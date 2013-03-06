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
import org.gradle.api.tasks.TaskAction

/**
 * A base class for all flyway tasks.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
abstract class AbstractFlywayTask extends DefaultTask {

  AbstractFlywayTask() {
    group = 'Flyway'
  }

  @TaskAction
  def runTask() {
    run(create())
  }

  /** Executes the task's custom behavior. */
  def abstract run(flyway)

  /** Creates a new, configured flyway instance */
  protected Flyway create() {
    Flyway flyway = new Flyway()
    def props = new Properties()

    addSchemasTo(props)
    addLocationsTo(props)
    addBasicTypesTo(props)
    addPlaceholdersTo(props)

    logger.info 'Flyway configuration:'
    props.each { name, value ->
      logger.info " - ${name}: ${value}"
    }

    flyway.configure(props)
    flyway
  }

  private def addBasicTypesTo(props) {
    project.flyway.properties
      .findAll { name, value ->
        (value instanceof String) || (value instanceof Boolean)
      }.each { name, value ->
        props.setProperty("flyway.${name}", "${value}")
      }
  }

  private def addSchemasTo(props) {
    def schemas = project.flyway.schemas.join(',')
    if (!schemas.isEmpty()) {
      props.setProperty('flyway.schemas', schemas)
    }
  }

  private def addLocationsTo(props) {
    def locations = project.flyway.locations
    if (project.plugins.hasPlugin('java')) {
      locations += project.sourceSets.main.resources.srcDirs
        .collect { file -> "filesystem:${file.path}" }
    }
    if (!locations.isEmpty()) {
      props.setProperty('flyway.locations', locations.join(','))
    }
  }

  private def addPlaceholdersTo(props) {
    project.flyway.placeholders.each { name, value ->
      props.setProperty("flyway.placeholders.${name}", value)
    }
  }
}

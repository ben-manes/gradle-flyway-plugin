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
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * A base class for all flyway tasks.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
abstract class AbstractFlywayTask extends DefaultTask {

  AbstractFlywayTask() {
    group = 'Flyway'
    project.afterEvaluate {
      def flywayDependsOn = project.flyway.dependsOn
      if (isJavaProject()) {
        flywayDependsOn += project.tasks.processResources
      }
      this.dependsOn(flywayDependsOn)
    }
  }

  @TaskAction
  def runTask() {
    run(create())
  }

  /** Executes the task's custom behavior. */
  def abstract run(flyway)

  /** Creates a new, configured flyway instance */
  protected def create() {
    if (isJavaProject()) {
      addClassesDirToClassLoader()
    }

    def props = getFlywayProperties()
    logger.info 'Flyway configuration:'
    props.each { name, value ->
      logger.info " - ${name}: ${value}"
    }

    def flyway = new Flyway()
    flyway.configure(props)
    flyway
  }

  private def getFlywayProperties() {
    def props = new Properties()
    addSchemasTo(props)
    addLocationsTo(props)
    addBasicTypesTo(props)
    addPlaceholdersTo(props)
    props
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
    if (locations.isEmpty()) {
      locations += defaultLocations()
    }
    if (!locations.isEmpty()) {
      props.setProperty('flyway.locations', locations.join(','))
    }
  }

  private def defaultLocations() {
    def defaults = []
    if (isJavaProject()) {
      def resources = project.sourceSets.main.output.resourcesDir.path
      defaults += [ "filesystem:${resources}" ]
    }
    if (hasClasses()) {
      defaults += [ "classpath:db/migration" ]
    }
    defaults
  }

  private def addPlaceholdersTo(props) {
    project.flyway.placeholders.each { name, value ->
      props.setProperty("flyway.placeholders.${name}", value)
    }
  }

  protected boolean isJavaProject() {
    project.plugins.hasPlugin('java')
  }

  private def addClassesDirToClassLoader() {
    def classesUrl = project.sourceSets.main.output.classesDir.toURI().toURL()
    def classLoader = Thread.currentThread().getContextClassLoader()
    if (hasClasses() && !classLoader.getURLs().contains(classesUrl)) {
      classLoader.addURL(classesUrl)
      logger.info "Added ${classesUrl} to classloader"
    }
  }

  private def hasClasses() {
    def classesDir = project.sourceSets.main.output.classesDir
    def classesUrl = classesDir.toURI().toURL()
    (classesDir.list()?.length ?: 0) > 0
  }
}

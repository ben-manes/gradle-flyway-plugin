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
import com.googlecode.flyway.core.api.MigrationVersion
import com.googlecode.flyway.core.util.jdbc.DriverDataSource
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
    project.afterEvaluate {
      def dependsOnTasks = project.flyway.dependsOnTasks
      if (isJavaProject()) {
        dependsOnTasks += project.tasks.processResources
      }
      this.dependsOn(dependsOnTasks)
    }
  }

  @TaskAction
  def runTask() {
    run(create())
  }

  /** Executes the task's custom behavior. */
  def abstract run(Flyway flyway)

  /** Creates a new, configured flyway instance */
  protected def create() {
    if (isJavaProject()) {
      addClassesDirToClassLoader()
    }

    logger.info 'Flyway configuration:'
    def flyway = new Flyway()
    addDataSourceTo(flyway)
    addMetadataTable(flyway)
    addSchemasTo(flyway)
    addInitVersionTo(flyway)
    addLocationsTo(flyway)
    addSqlMigrationSettingsTo(flyway)
    addTargetVersionTo(flyway)
    addValidationSettingsTo(flyway)
    flyway
  }

  private def addDataSourceTo(Flyway flyway) {
    def dataSource = new DriverDataSource(project.flyway.driver,
      project.flyway.url, project.flyway.user, project.flyway.password)
    flyway.setDataSource(dataSource)

    logger.info " - driver: ${flyway.dataSource.driver.class.name}"
    logger.info " - url: ${flyway.dataSource.url}"
    logger.info " - user: ${flyway.dataSource.user}"
    logger.info " - password: ${flyway.dataSource.password}"
  }

  private def addMetadataTable(Flyway flyway) {
    if (project.flyway.table != null) {
      flyway.setTable(project.flyway.table)
    }
    logger.info " - table: ${flyway.table}"
  }

  private def addInitVersionTo(Flyway flyway) {
    if (project.flyway.initVersion != null) {
      flyway.setInitVersion(project.flyway.initVersion)
    }
    if (project.flyway.initDescription != null) {
      flyway.setInitDescription(project.flyway.initDescription)
    }
    logger.info " - initVersion: ${flyway.initVersion}"
    logger.info " - initDescription: ${flyway.initDescription}"
  }

  private def addSchemasTo(Flyway flyway) {
    if (!project.flyway.schemas.isEmpty()) {
      flyway.setSchemas(project.flyway.schemas.join(','))
    }
    logger.info " - schemas: ${flyway.schemas}"
  }

  private def addLocationsTo(Flyway flyway) {
    def locations = project.flyway.locations
    if (locations.isEmpty()) {
      locations += defaultLocations()
    }
    flyway.setLocations(locations as String[])
    logger.info ' - locations: ' + (locations.isEmpty() ? 'db/migration' : locations)
  }

  private def defaultLocations() {
    def defaults = []
    if (isJavaProject()) {
      def resources = project.sourceSets.main.output.resourcesDir.path
      defaults += "filesystem:${resources}"
    }
    if (hasClasses()) {
      defaults += "classpath:db/migration"
    }
    defaults
  }

  private def addSqlMigrationSettingsTo(Flyway flyway) {
    if (project.flyway.sqlMigrationPrefix != null) {
      flyway.setSqlMigrationPrefix(project.flyway.sqlMigrationPrefix)
    }
    if (project.flyway.sqlMigrationSuffix != null) {
      flyway.setSqlMigrationSuffix(project.flyway.sqlMigrationSuffix)
    }
    if (project.flyway.encoding != null) {
      flyway.setEncoding(project.flyway.encoding)
    }
    if (!project.flyway.placeholders.isEmpty()) {
      flyway.setPlaceholders(project.flyway.placeholders)
    }
    if (!project.flyway.placeholderPrefix != null) {
      flyway.setPlaceholderPrefix(project.flyway.placeholderPrefix)
    }
    if (!project.flyway.placeholderSuffix != null) {
      flyway.setPlaceholderSuffix(project.flyway.placeholderSuffix)
    }
    logger.info " - sql migration prefix: ${flyway.sqlMigrationPrefix}"
    logger.info " - sql migration prefix: ${flyway.sqlMigrationSuffix}"
    logger.info " - encoding: ${flyway.encoding}"
    logger.info " - placeholders: ${flyway.placeholders}"
    logger.info " - placeholder prefix: ${flyway.placeholderPrefix}"
    logger.info " - placeholder suffix: ${flyway.placeholderSuffix}"
  }

  private def addTargetVersionTo(Flyway flyway) {
    if (project.flyway.target != null) {
      flyway.setTarget(new MigrationVersion(project.flyway.target))
    }
    logger.info " - target: ${flyway.target}"
  }

  private def addValidationSettingsTo(Flyway flyway) {
    if (project.flyway.outOfOrder) {
      flyway.setOutOfOrder(project.flyway.outOfOrder)
    }
    if (project.flyway.validateOnMigrate) {
      flyway.setValidateOnMigrate(project.flyway.validateOnMigrate)
    }
    if (project.flyway.cleanOnValidationError) {
      flyway.setCleanOnValidationError(project.flyway.cleanOnValidationError)
    }
    if (project.flyway.initOnMigrate) {
      flyway.setInitOnMigrate(project.flyway.initOnMigrate)
    }
    logger.info " - out of order: ${flyway.outOfOrder}"
    logger.info " - validate on migrate: ${flyway.validateOnMigrate}"
    logger.info " - clean on validation error: ${flyway.cleanOnValidationError}"
    logger.info " - init on migrate: ${flyway.initOnMigrate}"
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

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
 * 
 */
package com.github.benmanes.gradle.flyway.task;

import com.googlecode.flyway.core.Flyway
import com.googlecode.flyway.core.api.MigrationVersion
import com.googlecode.flyway.core.util.jdbc.DriverDataSource
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.github.benmanes.gradle.flyway.FlywayExtension

/**
 * A base class for all flyway tasks.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
abstract class AbstractFlywayTask extends DefaultTask {

  AbstractFlywayTask() {
    group = 'Flyway'
    project.afterEvaluate {
      def dependsOnTasks = project.flyway.defaults.dependsOnTasks
      project.flyway.databases.all() {
        dependsOnTasks += delegate.dependsOnTasks
      }
      if (isJavaProject()) {
        dependsOnTasks += project.tasks.processResources
      }
      this.dependsOn(dependsOnTasks)
    }
  }

  @TaskAction
  def runTask() {
    project.flyway.databases.all() {
      logger.info "Executing ${this.getName()} for ${delegate.name}"
      run(delegate.name, create(delegate))      
    }
  }

  /** Executes the task's custom behavior. */
  def abstract run(String name, Flyway flyway)

  /** Creates a new, configured flyway instance */
  protected def create(FlywayExtension flywayExt) {  
    if (isJavaProject()) {
      addClassesDirToClassLoader()
    }
    logger.info 'Flyway configuration:'
    def flyway = new Flyway()
    addDataSourceTo(flyway, flywayExt)
    addMetadataTable(flyway, flywayExt)
    addSchemasTo(flyway, flywayExt)
    addInitVersionTo(flyway, flywayExt)
    addLocationsTo(flyway, flywayExt)
    addSqlMigrationSettingsTo(flyway, flywayExt)
    addTargetVersionTo(flyway, flywayExt)
    addValidationSettingsTo(flyway, flywayExt)
    flyway
  }
  
  private def addDataSourceTo(Flyway flyway, FlywayExtension flywayExt) {
    def dataSource = new DriverDataSource(flywayExt.driver ?: project.flyway.defaults.driver, 
    flywayExt.url ?: project.flyway.defaults.url, 
    flywayExt.user ?: project.flyway.defaults.user, 
    flywayExt.password ?: project.flyway.defaults.password)
    flyway.setDataSource(dataSource)

    logger.info " - driver: ${flyway.dataSource.driver.class.name}"
    logger.info " - url: ${flyway.dataSource.url}"
    logger.info " - user: ${flyway.dataSource.user}"
    logger.info " - password: ${flyway.dataSource.password.replaceAll('.', '*')}"
  }

  private def addMetadataTable(Flyway flyway, FlywayExtension flywayExt) {
    if (flywayExt.table != null || project.flyway.defaults.table != null) {
      flyway.setTable(flywayExt.table ?: project.flyway.defaults.table)
    }
    logger.info " - table: ${flyway.table}"
  }

  private def addInitVersionTo(Flyway flyway, FlywayExtension flywayExt) {
    if (flywayExt.initVersion != null || project.flyway.defaults.initVersion != null) {
      flyway.setInitVersion(flywayExt.initVersion ?: project.flyway.defaults.initVersion)
    }
    if (flywayExt.initDescription != null || project.flyway.defaults.initDescription != null) {
      flyway.setInitDescription(flywayExt.initDescription ?: project.flyway.defaults.initDescription)
    }
    logger.info " - initVersion: ${flyway.initVersion}"
    logger.info " - initDescription: ${flyway.initDescription}"
  }

  private def addSchemasTo(Flyway flyway, FlywayExtension flywayExt) {
    List schemas
    if (flywayExt.schemaDefaultFirst ?: project.flyway.defaults.schemaDefaultFirst) {
      schemas = project.flyway.defaults.schemas + flywayExt.schemas
    }
    else {
      schemas = flywayExt.schemas + project.flyway.defaults.schemas    
    }
      if (!schemas.isEmpty()) {
        flyway.setSchemas(schemas as String[])
      }
      logger.info " - schemas: ${flyway.schemas}"
  }

  private def addLocationsTo(Flyway flyway, FlywayExtension flywayExt) {
    def locations = flywayExt.locations + project.flyway.defaults.locations
    if (!locations.isEmpty()) {
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

  private def addSqlMigrationSettingsTo(Flyway flyway, FlywayExtension flywayExt) {
    if (flywayExt.sqlMigrationPrefix != null || project.flyway.defaults.sqlMigrationPrefix != null) {
      flyway.setSqlMigrationPrefix(flywayExt.sqlMigrationPrefix ?: project.flyway.defaults.sqlMigrationPrefix)
    }
    if (flywayExt.sqlMigrationSuffix != null || project.flyway.defaults.sqlMigrationSuffix != null) {
      flyway.setSqlMigrationSuffix(flywayExt.sqlMigrationSuffix ?: project.flyway.defaults.sqlMigrationSuffix)
    }
    if (flywayExt.encoding != null || project.flyway.defaults.encoding != null) {
      flyway.setEncoding(flywayExt.encoding ?: project.flyway.defaults.encoding)
    }
    if (!(flywayExt.placeholders.isEmpty() && project.flyway.defaults.placeholders.isEmpty())) {
      flyway.setPlaceholders(flywayExt.placeholders + project.flyway.defaults.placeholders)
    }
    if (flywayExt.placeholderPrefix != null || project.flyway.defaults.placeholderPrefix != null) {
      flyway.setPlaceholderPrefix(flywayExt.placeholderPrefix ?: project.flyway.defaults.placeholderPrefix)
    }
    if (flywayExt.placeholderSuffix != null || project.flyway.defaults.placeholderSuffix != null) {
      flyway.setPlaceholderSuffix(flywayExt.placeholderSuffix ?: project.flyway.defaults.placeholderSuffix)
    }
    logger.info " - sql migration prefix: ${flyway.sqlMigrationPrefix}"
    logger.info " - sql migration prefix: ${flyway.sqlMigrationSuffix}"
    logger.info " - encoding: ${flyway.encoding}"
    logger.info " - placeholders: ${flyway.placeholders}"
    logger.info " - placeholder prefix: ${flyway.placeholderPrefix}"
    logger.info " - placeholder suffix: ${flyway.placeholderSuffix}"
  }

  private def addTargetVersionTo(Flyway flyway, FlywayExtension flywayExt) {
    if (flywayExt.target != null || project.flyway.defaults.target != null) {
      flyway.setTarget(new MigrationVersion(flywayExt.target ?: project.flyway.defaults.target))
    }
    logger.info " - target: ${flyway.target}"
  }

  private def addValidationSettingsTo(Flyway flyway, FlywayExtension flywayExt) {
    if (flywayExt.outOfOrder != null ||  project.flyway.defaults.outOfOrder != null) {
      flyway.setOutOfOrder(flywayExt.outOfOrder ?: project.flyway.defaults.outOfOrder)
    }
    if (flywayExt.validateOnMigrate != null ||  project.flyway.defaults.validateOnMigrate != null) {
      flyway.setValidateOnMigrate(flywayExt.validateOnMigrate ?: project.flyway.defaults.validateOnMigrate)
    }
    if (flywayExt.cleanOnValidationError != null ||  project.flyway.defaults.cleanOnValidationError != null) {
      flyway.setCleanOnValidationError(flywayExt.cleanOnValidationError ?: project.flyway.defaults.cleanOnValidationError)
    }
    if (flywayExt.initOnMigrate != null ||  project.flyway.defaults.initOnMigrate != null) {
      flyway.setInitOnMigrate(flywayExt.initOnMigrate ?: project.flyway.defaults.initOnMigrate)
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

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
package com.github.benmanes.gradle.flyway;

import com.github.benmanes.gradle.flyway.task.FlywayCleanTask
import com.github.benmanes.gradle.flyway.task.FlywayInfoTask
import com.github.benmanes.gradle.flyway.task.FlywayInitTask
import com.github.benmanes.gradle.flyway.task.FlywayMigrateTask
import com.github.benmanes.gradle.flyway.task.FlywayRepairTask
import com.github.benmanes.gradle.flyway.task.FlywayValidateTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Registers the plugin's tasks.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 */
public class FlywayPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.extensions.create('flyway', FlywayContainer)
    project.tasks.create('flywayClean', FlywayCleanTask)
    project.tasks.create('flywayInit', FlywayInitTask)
    project.tasks.create('flywayMigrate', FlywayMigrateTask)
    project.tasks.create('flywayValidate', FlywayValidateTask)
    project.tasks.create('flywayInfo', FlywayInfoTask)
    project.tasks.create('flywayRepair', FlywayRepairTask)
  }
}


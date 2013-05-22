/*
 * Copyright 2013 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.github.benmanes.gradle.flyway

import java.util.List;

import org.gradle.api.NamedDomainObjectContainer

/**
 * The container that hold the defaults and configuration properties for Flyway.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 * @see http://flywaydb.org/documentation/commandline
 */

public class FlywayContainer {
  
  /** The defaults values that will be used if the individual database values are null */
  FlywayExtension defaults
  
  /** The configuration for each database that is part of a release */
  NamedDomainObjectContainer<FlywayExtension> databases

  /** The dependencies that all flyway tasks depend on. */
  List<Object> dependsOnTasks

  /** @see http://www.gradle.org/docs/current/javadoc/org/gradle/api/Task.html#dependencies */
  def dependsOnTasks(Object... paths) {
    dependsOnTasks += paths
  }

  def getDependsOnTasks() {
    dependsOnTasks
  }
}

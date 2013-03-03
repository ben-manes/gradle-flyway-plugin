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
package com.github.benmanes.gradle.flyway.task

import com.googlecode.flyway.core.Flyway
import org.gradle.api.tasks.TaskAction

/**
 * @author Ben Manes (ben.manes@gmail.com)
 */
class FlywayInfoTask extends AbstractFlywayTask {

  FlywayInfoTask() {
    description = 'Creates and initializes the metadata table in the schema.'
  }

  @TaskAction
  def info() {
    def flyway = create()
    def all = flyway.info().all()

    println '----- Flyway Info -----'
    if (all.length == 0) {
      println 'No migrations found'
    } else {
      all.eachWithIndex { info, idx ->
        if (idx != 0) {
          println ''
        }
        println(
          """|Version: ${info.version}
             |Description: ${info.description}
             |Script: ${info.script}
             |State: ${info.state}""".stripMargin())
        if (info.state.isApplied()) {
          println "Installed On: ${info.installedOn}"
        }
      }
    }
  }
}

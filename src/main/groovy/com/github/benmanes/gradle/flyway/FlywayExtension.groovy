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

/**
 * The flyway's configuration properties.
 *
 * @author Ben Manes (ben.manes@gmail.com)
 * @see http://flywaydb.org/documentation/commandline
 */
public class FlywayExtension {

  /** The fully qualified classname of the jdbc driver to use to connect to the database */
  String driver

  /** The jdbc url to use to connect to the database */
  String url

  /** The user to use to connect to the database */
  String user

  /** The password to use to connect to the database */
  String password

  /** The name of Flyway's metadata table */
  String table

  /** The case-sensitive list of schemas managed by Flyway */
  public List<String> schemas = []

  /** The initial version to put in the database */
  String initVersion

  /** The description of the initial version */
  String initDescription

  /**
   * Locations to scan recursively for migrations. The location type is determined by its prefix.
   *
   * <tt>Unprefixed locations or locations starting with classpath:</tt>
   * point to a package on the classpath and may contain both sql and java-based migrations.
   *
   * <tt>Locations starting with filesystem:</tt>
   * point to a directory on the filesystem and may only contain sql migrations.
   */
  List<String> locations = []

  /** The file name prefix for Sql migrations */
  String sqlMigrationPrefix

  /** The file name suffix for Sql migrations */
  String sqlMigrationSuffix

  /** The encoding of Sql migrations */
  String encoding

  /** Placeholders to replace in Sql migrations */
  Map<String, String> placeholders = [:]

  /** The prefix of every placeholder */
  String placeholderPrefix

  /** The suffix of every placeholder */
  String placeholderSuffix

  /**
   * The target version up to which Flyway should run migrations. Migrations with a higher version
   * number will not be applied.
   */
  String target

  /** Allows migrations to be run "out of order" */
  Boolean outOfOrder

  /** Whether to automatically call validate or not when running migrate */
  Boolean validateOnMigrate

  /** Whether to automatically call clean or not when a validation error occurs */
  Boolean cleanOnValidationError

  /**
   * Whether to automatically call init when migrate is executed against a non-empty schema
   * with no metadata table.
   */
  Boolean initOnMigrate
  
 
  private FlywayMasterExtension parent
  
  public getdriver() {return driver ?: parent?.driver}  
  public geturl() {return url ?: parent?.url}  
  public getuser() {return user ?: parent?.user}  
  public getpassword() {return password ?: parent?.password}  
  public gettable() {return table ?: parent?.table}  
  public getschemas() {
	List returnSchemas
	if (parent == null) {
		returnSchemas = schemas
	}
	else {
		returnSchemas = parent.schemaGenericFirst ? parent?.schemas + schemas : schemas + parent?.schemas
	}
	return returnSchemas}  
  public getinitVersion() {return initVersion ?: parent?.initVersion}  
  public getinitDescription() {return initDescription ?: parent?.initDescription}  
  public getlocations() {return parent == null ? locations : locations + parent?.locations}  
  public getsqlMigrationPrefix() {return sqlMigrationPrefix ?: parent?.sqlMigrationPrefix}  
  public getsqlMigrationSuffix() {return sqlMigrationSuffix ?: parent?.sqlMigrationSuffix}  
  public getplaceholders() {
	  return parent == null ? placeholders : parent?.placeholders + placeholders}  
  public getplaceholderPrefix() {return placeholderPrefix ?: parent?.placeholderPrefix}  
  public getplaceholderSuffix() {return placeholderSuffix ?: parent?.placeholderSuffix}  
  public gettarget() {return target ?: parent?.target}
  public getoutOfOrder() {return outOfOrder ?: parent?.outOfOrder}
  public getvalidateOnMigrate() {return validateOnMigrate ?: parent?.validateOnMigrate}
  public getcleanOnValidationError() {return cleanOnValidationError ?: parent?.cleanOnValidationError}
  public getinitOnMigrate() {return initOnMigrate ?: parent?.initOnMigrate}

  public FlywayExtension(parent) {
    this.parent = parent
    schemas = []
    locations = []
    placeholders = [:]
  }
  public FlywayExtension() {
    schemas = []
    locations = []
    placeholders = [:]
  }
}

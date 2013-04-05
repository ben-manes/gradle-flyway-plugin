# Gradle Flyway Plugin
[Flyway](http://flywaydb.org) database migration tasks for Gradle.

## Usage

This plugin is hosted on the Maven Central Repository. All actions are logged at the `info` level.

See Flyway's [command-line arguments](http://flywaydb.org/documentation/commandline) for the
configuration reference. The project's `sourceSets.main.resources` is added to the `locations`
automatically if the `java` plugin is detected. 

```groovy
apply plugin: 'flyway'

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.h2database:h2:1.3.170'
    classpath 'com.github.ben-manes:gradle-flyway-plugin:0.3'
  }
}

flyway {
  url = "jdbc:h2:${buildDir}/db/flyway"
}
```

## Tasks

### `flywayClean`
Drops all objects in the configured schemas.

### `flywayInit`
Creates and initializes the metadata table in the schema.

### `flywayMigrate`
Migrates the schema to the latest version.

### `flywayValidate`
Validates the applied migrations against the ones available on the classpath.

### `flywayInfo`
Prints the details and status information about all the migrations.

### `flywayRepair`
Repairs the Flyway metadata table after a failed migration.

## Sample

```groovy
flyway {
  url = "jdbc:h2:${buildDir}/db/flyway"    
  driver = 'org.h2.Driver'
  user = 'SA'
  password = 'mySecretPwd'
  table = 'schema_history'
  schemas = [ 'schema1', 'schema2', 'schema3' ]
  initialVersion = '1.0'
  initialDescription = 'Base Migration'
  locations = [
    'classpath:com.mycompany.project.migration',
    'filesystem:/sql-migrations',
    'database/migrations'
  ]
  sqlMigrationPrefix = 'Migration-'
  sqlMigrationSuffix = '-OK.sql'
  encoding = 'ISO-8859-1'
  placeholders = [ 
    'aplaceholder': 'value',
    'otherplaceholder': 'value123'
  ]
  placeholderPrefix = '#['
  placeholderSuffix = ']'
  target = '5.1'
  outOfOrder = false
  validateOnMigrate = true
  cleanOnValidationError = false
  initOnMigrate = false
}
```

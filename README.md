# Gradle Flyway Plugin
[Flyway](http://flywaydb.org) database migration tasks for Gradle.

## Usage

This plugin is hosted on the Maven Central Repository. All actions are logged at the `info` level.

See Flyway's [command-line arguments](http://flywaydb.org/documentation/commandline) for the
configuration reference. If the `locations` configuration is not set and the `java` plugin is
detected then setting is defaulted to the `sourceSets.main.output.resourcesDir`. If compiled
classes are detected then the `sourceSets.main.output.classesDir` are added to the classpath and
`classpath:db/migration` added to the default locations.

The flyway tasks may include Java migrations. This requires that the classes are compiled in a
dependent task. The `dependsOnTasks` configuration acts as a `dependsOn` relationship that applies
to all flyway tasks. This relationship is not required to allow for the inverse relationship, such
as for code generating from the schema. See the 
[gradle-jooq-plugin](https://github.com/ben-manes/gradle-jooq-plugin) for one such example.


```groovy
apply plugin: 'flyway'

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.h2database:h2:1.3.170'
    classpath 'com.github.ben-manes:gradle-flyway-plugin:0.4'
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
  dependsOnTasks(compileJava)

  url = "jdbc:h2:${buildDir}/db/flyway"    
  driver = 'org.h2.Driver'
  user = 'SA'
  password = 'mySecretPwd'
  table = 'schema_history'
  schemas = [ 'schema1', 'schema2', 'schema3' ]
  initVersion = '1.0'
  initDescription = 'Base Migration'
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

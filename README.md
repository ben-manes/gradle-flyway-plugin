# Gradle Flyway Plugin
[Flyway](http://flywaydb.org) database migration tasks for Gradle.

## Usage

This plugin is hosted on the Maven Central Repository. All actions are logged at the `info` level.

See Flyway's [command-line arguments](http://flywaydb.org/documentation/commandline) for the
configuration reference. The `locations` must be present on the classpath, which should be
specified in the `buildscript`.

```groovy
apply plugin: 'flyway'

buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.h2database:h2:1.3.170'
    classpath 'com.github.ben-manes:gradle-flyway-plugin:0.1'
    classpath files('src/main/resources', 'src/test/resources')
  }
}

flyway {
  url = "jdbc:h2:${buildDir}/db/flyway"    
  driver = 'org.h2.Driver'
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
Creates and initializes the metadata table in the schema.

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
    'com.mycompany.project.migration',
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

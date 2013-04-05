/* Copyright 2013 Addepar. All Rights Reserved. */
package db.migration;

import java.sql.Connection;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

/**
 * An example of a Java data migration that trims the first name field.
 *
 * @author Ben Manes (ben@addepar.com)
 */
public final class V2__Trim_first_name implements JdbcMigration {

  @Override
  public void migrate(Connection connection) throws Exception {
    String sql = "UPDATE user SET first_name = TRIM(first_name)";
    connection.createStatement().executeUpdate(sql);
  }
}

/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.dataset.postgis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

/**
 * Class for connectors to a PostGIS database.
 * @author GTouya
 *
 */
public class PostGISConnector {
  private static Logger logger = Logger
      .getLogger(PostGISLoader.class.getName());

  private String url;

  private String user;

  private String password;

  public PostGISConnector(String url, String user, String password) {
    super();
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public Connection connectToPostGISDB() {

    Connection conn = null;
    try {
      /*
       * Chargement du pilote JDBC et établissement d'une connection.
       */
      // Class.forName("org.postgresql.Driver");

      conn = DriverManager.getConnection(this.url, this.user, this.password);
      ((PGConnection) conn).addDataType("geometry", PGgeometry.class);
      conn.setReadOnly(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }

  /**
   * Close the connection given as parameter.
   * @param conn the postGIS connection to close.
   * @return
   */
  public boolean closeConnection(Connection conn) {
    try {
      conn.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Test if the JDBC connection is valid.
   * @return true if the connection is possible with the current arguments.
   */
  public boolean testConnection() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(this.url, this.user, this.password);
      ((PGConnection) conn).addDataType("geometry", PGgeometry.class);
      conn.setReadOnly(true);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    try {
      conn.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

  }

  /**
   * Get all the tables from the connected PostGIS database.
   * @return
   */
  public Object[][] getTables() {
    Connection conn = this.connectToPostGISDB();
    Statement s;
    ResultSet r = null;
    List<Object[]> list = new ArrayList<Object[]>();

    try {
      s = conn.createStatement();
      r = s.executeQuery("select * from geometry_columns");

      logger.debug("Request result " + r);
      while (r.next()) {
        Object[] obj = { r.getObject("f_table_name") };
        list.add(obj);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return (Object[][]) list.toArray();

  }
}

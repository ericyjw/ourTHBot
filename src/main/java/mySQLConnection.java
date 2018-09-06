/*

import java.sql.*;

public class mySQLConnection {

    private Connection con;
    private Statement st;
    private ResultSet rs;

    public mySQLConnection() {


        String url = "jdbc:mysql://178.128.85.69:3306/TH";
        String username = "root";
        String password = "He//owerld123";

        System.out.println("Connecting database...");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            throw new IllegalStateException("Cannot connect the database!", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

*/
/*
        try{

            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://178.128.85.69:3306/Temasek", "root", "He//owerld123");
            st = con.createStatement();

            System.out.println("Connected!");

        } catch (Exception e) {

            System.out.println("Error: " + e);

        }
        */

/*
    }

    public void getData() {
        try {
            String query = "select * from Temasekians";
            rs = st.executeQuery(query);

            System.out.println("Records from Database");
            while(rs.next()) {

                String name = rs.getString("name");
                String matric = rs.getString("matric");

                System.out.println("Name: " + name + " Matric: " + matric);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }



}








*/









/*
import java.sql.*;   // Use 'Connection', 'Statement' and 'ResultSet' classes in java.sql package

// JDK 1.7 and above
public class mySQLConnection {   // Save as "JdbcSelectTest.java"
    public static void main(String[] args) {
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://178.128.85.69:3306/Temasek", "root", "He//owerld123");
                // MySQL: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3: Execute a SQL SELECT query, the query result
            //  is returned in a 'ResultSet' object.
            String strSelect = "select chatID, name, matric, blk from books";
            System.out.println("The SQL query is: " + strSelect); // Echo For debugging
            System.out.println();

            ResultSet rset = stmt.executeQuery(strSelect);

            // Step 4: Process the ResultSet by scrolling the cursor forward via next().
            //  For each row, retrieve the contents of the cells with getXxx(columnName).
            System.out.println("The records selected are:");
            int rowCount = 0;
            while(rset.next()) {   // Move the cursor to the next row, return false if no more row
                String title = rset.getString("title");
                double price = rset.getDouble("price");
                int    qty   = rset.getInt("qty");
                System.out.println(title + ", " + price + ", " + qty);
                ++rowCount;
            }
            System.out.println("Total number of records = " + rowCount);

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        // Step 5: Close the resources - Done automatically by try-with-resources
    }
}

*/
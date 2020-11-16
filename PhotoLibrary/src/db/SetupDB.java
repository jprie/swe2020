package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import exception.ServiceException;

public class SetupDB {

	public static void main(String[] args) {
		
		try (Connection con = DriverManager.getConnection("jdbc:derby:./NewPhotoDB; create=true; user=test; password=test;");
				Statement stmt = con.createStatement()) {
			
//			stmt.execute("DROP TABLE Photo");
//			stmt.execute("DROP TABLE Photographer");
			stmt.execute("CREATE TABLE Photographer (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
					+ "first_name VARCHAR(30),"
					+ "last_name VARCHAR(30))");
			
			System.out.println("Table Photographer created");

			stmt.execute("CREATE TABLE Photo (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,"
					+ "name VARCHAR(50),"
					+ "url VARCHAR(100),"
					+ "photographer_id INTEGER REFERENCES Photographer(id),"
					+ "date DATE,"
					+ "location VARCHAR(100))");
			System.out.println("Table Photo created");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

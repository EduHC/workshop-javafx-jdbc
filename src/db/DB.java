package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {	
	private static Connection connector = null;
	
	
	public static Connection getConnection() {
		if(connector == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				
				connector = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		
		return connector;
	}
	
	
	public static void closeConnection() {
		if(connector != null) {
			
			try {
				connector.close();
			
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	
	private static Properties loadProperties(){
		try(FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs);
			
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement state) {
		if(state != null) {
			try {
				state.close();
			} catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet result) {
		if(result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	
	
	
}

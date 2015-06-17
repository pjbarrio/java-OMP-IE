package utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class databaseWriter {

	public static final String DATABASE_USER = "user";
	public static final String DATABASE_PASSWORD = "password";
	public static final String MYSQL_AUTO_RECONNECT = "autoReconnect";
	public static final String MYSQL_MAX_RECONNECTS = "maxReconnects";

	private Connection conn;
	private String computername;
	
	private String insertSentence = "INSERT INTO `SentenceRanking`.`Sentence` (`collection`,`idSentence`,`text`) VALUES (?,?,?);";

	public databaseWriter() {

		conn = null;

	}

	public synchronized void closeConnection() {
		try {
			
			getConnection().close();
			System.out.println("Disconnected from database");
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	private synchronized Connection getConnection() {
		if (conn == null){
			openConnection();
		}

		return conn;
	}


	public synchronized  void openConnection() {

		conn = null;
		String url = "jdbc:mysql://db-files.cs.columbia.edu:3306/";
		String dbName = "SentenceRanking";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "pjbarrio"; 
		String password = "test456";
		try {

			Class.forName(driver).newInstance();

			java.util.Properties connProperties = new java.util.Properties();

			connProperties.put(DATABASE_USER, userName);

			connProperties.put(DATABASE_PASSWORD, password);

			connProperties.put(MYSQL_AUTO_RECONNECT, "true");

			connProperties.put(MYSQL_MAX_RECONNECTS, "500");

			conn = DriverManager.getConnection(url+dbName,connProperties);

			System.out.println("Connected to the database");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	


	public String getComputerName() {

		if (computername == null){
		
			try{
				computername = InetAddress.getLocalHost().getHostName();
				System.out.println(computername);
			}catch (Exception e){
				System.out.println("Exception caught ="+e.getMessage());
			}
		}
		return computername;
	}




	public void insertSentence(int collection, int sentence, String sentenceText) {
		
		try {

			PreparedStatement PStmtexistsSplitForFile = getConnection().prepareStatement(insertSentence );

			PStmtexistsSplitForFile.setInt(1, collection);
			PStmtexistsSplitForFile.setInt(2, sentence);
			PStmtexistsSplitForFile.setString(3, sentenceText);
			
			
			PStmtexistsSplitForFile.execute();

			PStmtexistsSplitForFile.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public String getInformationExtractionSystemName(
			int idRelationExtractionSystem) {

		String ret = "";

		try {

			Statement StmtgetInformationExtractionSystemName = getConnection().createStatement();

			ResultSet RSgetInformationExtractionSystemName = StmtgetInformationExtractionSystemName.executeQuery
					("select name from RelationExtractionSystem where idRelationExtractionSystem = " + idRelationExtractionSystem);

			while (RSgetInformationExtractionSystemName.next()) {

				ret = RSgetInformationExtractionSystemName.getString(1);

			}

			RSgetInformationExtractionSystemName.close();
			StmtgetInformationExtractionSystemName.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ret;

	}
	
}

package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ADatabase implements Database {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL = "jdbc:mysql://localhost:3306/classroom";
	static String USERNAME = "root";
	static String PASSWORD = "carpediem8199";
	
	public ADatabase() {
		
	}
	
	public ADatabase(String dbURL, String username, String password) {
		DB_URL = dbURL;
		USERNAME = username;
		PASSWORD = password;
	}
	
	//Don't use this.
	public void createDatabase() {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		try {
			System.out.println("Creating database...");
			stmt = conn.createStatement();
			String sql = "create database if not exists CLASSROOM;"
					+ "use CLASSROOM;"
					+ "drop table if exists STUDENT;"
					+ "create table STUDENT ("
					+ "studentID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
					+ "firstName TEXT,"
					+ "lastName TEXT,"
					+ "currentStatus TEXT"
					+ ");"
					+ "drop table if exists DOCUMENT;"
					+ "create table DOCUMENT ("
					+ "docID INTEGER PRIMARY KEY,"
					+ "title TEXT,"
					+ "studentID INTEGER,"
					+ "FOREIGN KEY (studentID) references STUDENT (studentID)"
					+ ");"
					+ "drop table if exists CLASS;"
					+ "create table CLASS ("
					+ "classID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
					+ "name TEXT"
					+ ");"
					+ "drop table if exists CLASSHASSTUDENT;"
					+ "create table CLASSHASSTUDENT ("
					+ "recordID INTEGER PRIMARY KEY,"
					+ "studentID INTEGER,"
					+ "classID INTEGER, "
					+ "FOREIGN KEY (studentID) references STUDENT (studentID),"
					+ "FOREIGN KEY (classID) references CLASS (classID)"
					+ ");"
					+ "drop table if exists TEACHER;"
					+ "create table TEACHER ("
					+ "teacherID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
					+ "firstName TEXT,"
					+ "lastName TEXT"
					+ ");"
					+ "drop table if exists CLASSHASTEACHER;"
					+ "create table CLASSHASTEACHER ("
					+ "recordID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
					+ "teacherID INTEGER, "
					+ "classID INTEGER, "
					+ "FOREIGN KEY (teacherID) references TEACHER (teacherID),"
					+ "FOREIGN KEY (classID) references CLASS (classID)"
					+ ");"
					+ "drop table if exists ASSIGNMENT;"
					+ "create table ASSIGNMENT ("
					+ "assignmentID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
					+ "name TEXT,"
					+ "maxScore INTEGER,"
					+ "pageLength INTEGER,"
					+ "classID INTEGER,"
					+ "FOREIGN KEY (classID) references CLASS (classID)"
					+ ");"
					+ "drop table if exists STUDENTHASASSIGNMENT;"
					+ "create table STUDENTHASASSIGNMENT ("
					+ "recordID INTEGER PRIMARY KEY,"
					+ "studentID INTEGER, "
					+ "assignmentID INTEGER,"
					+ "documentID INTEGER UNIQUE"
					+ "FOREIGN KEY (studentID) references STUDENT (studentID), "
					+ "FOREIGN KEY (assignmentID) references ASSIGNMENT (assignmentID)"
					+ ");";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully...");
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, null);
		}
	}

	public Connection connectToDatabase() {
		Connection conn = null;
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			// Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} 
		return conn;
	}

	public void closeAndCleanup(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException se1) {
			se1.printStackTrace();
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se2) {
			se2.printStackTrace();
		} 
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se3) {
			se3.printStackTrace();
		}
	}
	
	public Integer addStudent(String firstName, String lastName) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		Integer studentID = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			String sql = "INSERT INTO Student (firstName, lastName) VALUES ('" + firstName + "', '" + lastName + "')";
			stmt.executeUpdate(sql);
			sql = "SELECT LAST_INSERT_ID() as id";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				studentID = rs.getInt("id");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return studentID;
	}
	
	public Integer createClass(String name, Integer[] teachers) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		Integer classID = null;
		try {
			stmt = conn.createStatement();
			//first, create the class
			String sql = "INSERT INTO Class (name) VALUES ('" + name + "')";
			stmt.executeUpdate(sql);
			sql = "SELECT LAST_INSERT_ID() as id";
			rs = stmt.executeQuery(sql);
			if (rs.next()) { 
				classID = rs.getInt("id");
			}
			//if successful, add the teacher(s) to it
			if (classID != null) {
				for (int i = 0; i < teachers.length; i++) {
					sql = "INSERT INTO ClassHasTeacher (teacherID, classID) VALUES ('" + teachers[i] + "', '" + classID + "')";
					stmt.executeUpdate(sql);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return classID;
	}
	
	public Integer createAssignment(String name, int maxScore, int pageLength, String classID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		Integer assignmentID = null;
		try {
			stmt = conn.createStatement();
			String sql = "INSERT INTO Assignment (name, maxScore, pageLength, classID) VALUES ('" + name + "', '" + maxScore + "', '" + pageLength + "', '" + classID + "')";
			stmt.executeUpdate(sql);
			sql = "SELECT LAST_INSERT_ID() as id";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				assignmentID = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return assignmentID;
	}
	
	public String getTeacherFirstName(Integer teacherID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String teacherFirstName = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT T.firstName"
					+ "FROM Teacher T"
					+ "WHERE T.teacherID = '" + teacherID + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				teacherFirstName = rs.getString("firstName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return teacherFirstName;
	}
	
	public String getTeacherLastName(Integer teacherID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String teacherLastName = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT T.lastName"
					+ "FROM Teacher T"
					+ "WHERE T.teacherID = '" + teacherID + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				teacherLastName = rs.getString("lastName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return teacherLastName;
	}
	
	public String getStudentFirstName(Integer studentID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String studentFirstName = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT S.firstName"
					+ "FROM Student S"
					+ "WHERE S.studentID = '" + studentID +"'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				studentFirstName = rs.getString("firstName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return studentFirstName;
	}
	
	public String getStudentLastName(Integer studentID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String studentLastName = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT S.lastName"
					+ "FROM Student S"
					+ "WHERE S.studentID = '" + studentID + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				studentLastName = rs.getString("lastName");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return studentLastName;
	}
	
	public String getClassName(Integer classID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String className = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT C.name"
					+ "FROM Class C"
					+ "WHERE C.classID = '" + classID + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				className = rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return className;
	}
	
	public List<List<String>> getClassTeacherNames(Integer classID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		List<List<String>> teacherNames = new ArrayList<List<String>>();
		List<String> teacherFirstNames = new ArrayList<String>();
		List<String> teacherLastNames = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			String sql = "SELECT T.firstName, T.lastName"
					+ "FROM ClassHasTeacher CT, Teacher T"
					+ "WHERE CT.classID = '" + classID + "' AND CT.teacherID = T.teacherID";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				teacherFirstNames.add(rs.getString("firstName"));
				teacherLastNames.add(rs.getString("lastName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		teacherNames.add(teacherFirstNames);
		teacherNames.add(teacherLastNames);
		return teacherNames;
	}
	
	public List<List<String>> getStudentNamesInClass(Integer classID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		List<List<String>> studentNames = new ArrayList<List<String>>();
		List<String> studentFirstNames = new ArrayList<String>();
		List<String> studentLastNames = new ArrayList<String>();
		try {
			stmt = conn.createStatement();
			String sql = "SELECT S.firstName, S.lastName"
					+ "FROM ClassHasStudent CS, Student S"
					+ "WHERE CS.classID = '" + classID + "' AND CS.studentID = S.studentID";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				studentFirstNames.add(rs.getString("firstName"));
				studentLastNames.add(rs.getString("lastName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		studentNames.add(studentFirstNames);
		studentNames.add(studentLastNames);
		return studentNames;
	}
	
	public String getStudentStatus(Integer studentID) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		ResultSet rs = null;
		String status = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT S.status"
					+ "FROM Studnet S"
					+ "WHERE S.studentID = '" + studentID + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				status = rs.getString("status");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, rs);
		}
		return status;
	}
	
	public void setStudentStatus(Integer studentID, String status) {
		Connection conn = connectToDatabase();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String sql = "UPDATE Student"
					+ "SET status = '" + status + "'"
					+ "WHERE studentID = '" + studentID + "'";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAndCleanup(conn, stmt, null);
		}
	}
}

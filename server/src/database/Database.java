package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public interface Database {

	public void createDatabase();

	public Connection connectToDatabase();

	public void closeAndCleanup(Connection conn, Statement stmt, ResultSet rs);

	public Integer addStudent(String firstName, String lastName);

	public Integer createClass(String name, Integer[] teachers);

	public Integer createAssignment(String name, int maxScore, int pageLength, String classID);

	public String getTeacherFirstName(Integer teacherID);

	public String getTeacherLastName(Integer teacherID);

	public String getStudentFirstName(Integer studentID);

	public String getStudentLastName(Integer studentID);

	public String getClassName(Integer classID);

	public List<List<String>> getClassTeacherNames(Integer classID);

	public List<List<String>> getStudentNamesInClass(Integer classID);

	public String getStudentStatus(Integer studentID);

}

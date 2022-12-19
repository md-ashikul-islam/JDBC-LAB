import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Hello world!");
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/jdbc-lab";
            String user = "ashik";
            String pass = "6969";

            Connection conn = DriverManager.getConnection(url,user,pass);

            String studenttable = "CREATE TABLE IF NOT EXISTS `jdbc-lab`.`student`(  \n" +
                    "  `idstudent` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `stname` VARCHAR(45) NULL,\n" +
                    "  `stid` INT(11) NOT NULL,\n" +
                    "  `role` VARCHAR(45) NOT NULL,\n" +
                    "  PRIMARY KEY (`idstudent`));";
//            System.out.println(studenttable);
            Statement statement = conn.createStatement();
            statement.executeUpdate(studenttable);

            if(!conn.isClosed()){
                Scanner sc = new Scanner(System.in);
                System.out.println("1. For Student\n2: For Teacher");
                int check = sc.nextInt();
                if(check == 1){
                    System.out.println("1. LogIN\n2. SignUP");
                    Statement statement1;
                    PreparedStatement preparedStatement;
                    int c = sc.nextInt();
                    if(c==1){
                        Object student[] = getinfostudent(sc);
                        loginstudent(conn, student);

                    }
                    else if (c==2) {

                        Object student[] = getinfostudent(sc);
                        signupstudent(conn,student);

                    }
                }
                else if (check == 2) {
                    Object teacher[] = getinfostudent(sc);
                    teacher[2] = "teacher";
//                    for(Object x:teacher) System.out.println(x);
                    loginteacher(conn, teacher);
                    int tc = sc.nextInt();
                    if(tc==1){
                                                  //add section
                      Object section[] =  takesectiondetails(sc);
//                      for(Object x:section){
//                          System.out.println(x);
//                      }
                      insertsection(conn,section);
                    } else if (tc==2) {
                        //edit section
                        System.out.println("All Sections");
                        System.err.println("ID    COURSE");
                        ResultSet allsection = getallsection(conn);

                        while (allsection.next()){
                            System.out.print(allsection.getInt("id")+"    ");
                            System.out.print(allsection.getString("course")+"    ");
                            System.out.println();
                        }
                        int up = sc.nextInt();
                        getsectiondetails(conn, up);
                        System.out.println();
                        System.out.println("Click 9 to update");
                        int update = sc.nextInt();
                        if(update==9){
                            editsection(conn,up, sc);
                        }else{
                            System.out.println("no");
                        }
                    } else if (tc == 3) {
                        System.out.println("All Sections");
                        System.err.println("ID    COURSE");
                        ResultSet allsection = getallsection(conn);

                        while (allsection.next()){
                            System.out.print(allsection.getInt("id")+"    ");
                            System.out.print(allsection.getString("course")+"    ");
                            System.out.println();
                        }
                        int up = sc.nextInt();
                        deletesection(conn, up);
                    }
                }
            }
            else {
                System.out.println("false");
            }
        }catch (Exception e){
          e.printStackTrace();
        }

    }

    private static void deletesection(Connection conn, int up) throws SQLException {
        PreparedStatement s;
        String sql = "DELETE FROM `jdbc-lab`.`section` WHERE `ìd`= ?;";
        s = conn.prepareStatement(sql);
        s.setInt(1, up);
        System.out.println(s);
        s.executeUpdate(sql);
    }

    private static void editsection(Connection conn, int up, Scanner sc) throws SQLException, IOException {
        Object sec[] = takesectiondetails(sc);
        sec[0] = up;
        updatesection(conn, sec);
    }

    private static void updatesection(Connection conn, Object[] section) throws SQLException, IOException {
        String sql = "UPDATE `jdbc-lab`.`section` SET `course` = ?, `date` = ?, `img` = ? WHERE (`id` = ?);";
        PreparedStatement p;
        p = conn.prepareStatement(sql);
        p.setString(1, (String)section[1]);
        p.setString(2, (String)section[2]);
        p.setBinaryStream(3, (FileInputStream)section[3], ((FileInputStream)section[3]).available());
        p.setInt(4, (int)section[0]);
        p.executeUpdate();
    }

    private static void getsectiondetails(Connection conn, int up) throws SQLException {
        PreparedStatement p;
        String sql = "SELECT * FROM jdbc-lab.section WHERE section.id = ?;";
        p = conn.prepareStatement(sql);
        p.setInt(1,up);
        ResultSet r = p.executeQuery();
        while (r.next()){
            System.out.print(r.getInt("id")+"    ");
            System.out.print(r.getString("course")+"    ");
            System.out.print(r.getString("date")+"    ");
            System.out.print(r.getBlob("img")+"    ");

        }
    }

    private static ResultSet getallsection(Connection connection) throws SQLException {
        Statement s;
        String sql = "SELECT * FROM students;";
        s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);
        return rs;
    }

    private static void insertsection(Connection conn, Object[] section) throws SQLException, IOException {
        String sql = "INSERT INTO `students`.`section` (`id`, `course`, `date`, `img`) VALUES (?,?,?,?);";
        PreparedStatement p;
        p = conn.prepareStatement(sql);
        p.setInt(1, (int)section[0]);
        p.setString(2, (String)section[1]);
        p.setString(3, (String)section[2]);
        p.setBinaryStream(4, (FileInputStream)section[3], ((FileInputStream)section[3]).available());
        p.executeUpdate();
    }

    private static Object[] takesectiondetails(Scanner sc) throws FileNotFoundException {
        Object s[] = new Object[4];
        System.out.println("Section Code Number");
        s[0] = sc.nextInt();
        System.out.println("Course Name");
        s[1] = sc.next();
        System.out.println("Time and date");
        s[2] = sc.next();
        System.out.println("Img");
        JFileChooser jf = new JFileChooser();
        jf.showOpenDialog(null);
        File file = jf.getSelectedFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        s[3] = fileInputStream;
        return s;
    }

    private static void loginteacher(Connection conn, Object[] teacher) throws SQLException{
        Statement s;
        String sql = "SELECT * FROM `jdbc-lab`.`teacher`;";
        s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        ArrayList<String> namelist = new ArrayList<>();
        while (rs.next()){
            namelist.add(rs.getString("tname"));
        }
        if(namelist.contains(teacher[0])){
            ResultSet resultSet = getpassword(conn, teacher);
            while (resultSet.next()){
                if(resultSet.getInt("tid") == (int)teacher[1]){
                    System.out.println("1. Add section\n2. Edit Section\n3. Delete Section");

                }
                else {
                    System.err.println("Incorrect Pass");
                }
            }
        }
        else{
            System.err.println("Name not found");
        }
    }

    private static void loginstudent(Connection conn, Object[] student) throws SQLException {
        Statement s;
        String sql = "SELECT * FROM `jdbc-lab`.`student`;";
        s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        ArrayList<String> namelist = new ArrayList<>();
        while (rs.next()){
            namelist.add(rs.getString("stname"));
        }
        if(namelist.contains(student[0])){
            ResultSet resultSet = getpassword(conn, student);
            while (resultSet.next()){
                if(resultSet.getInt("stid") == (int)student[1]){
                    System.out.println("choose section");
                }
                else {
                    System.err.println("Incorrect Pass");
                }
            }
        }
        else{
            System.err.println("Name not found");
        }
    }

    private static ResultSet getpassword(Connection conn, Object[] user) throws SQLException {
        ResultSet r = null;
        if( ((String)user[2]).equals("student")) {
            PreparedStatement p;
            String sql = "SELECT `stid` from `jdbc-lab`.`student` where `stname` = ?;";
            p = conn.prepareStatement(sql);
            p.setString(1, (String) user[0]);
            r = p.executeQuery();

            return r;
        } else if ( ((String)user[2]).equals("teacher")) {
            PreparedStatement p;
            String sql = "SELECT `tid` from `jdbc-lab`.`teacher` where `tname` = ?;";
            p = conn.prepareStatement(sql);
            p.setString(1, (String) user[0]);
            r = p.executeQuery();

            return r;

        }
        return r;
    }

    private static void signupstudent(Connection con, Object[] student) throws SQLException {
        PreparedStatement p ;
        String sql = "INSERT INTO `jdbc-lab`.`student` ( `stname`, `stid`, `role`) VALUES (?,?,?);";
        p = con.prepareStatement(sql);
        p.setString(1, (String)student[0]);
        p.setInt(2, (int)student[1]);
        p.setString(3, "student");
        p.executeUpdate();
    }

    public static Object[] getinfostudent(Scanner sc){
        Object info[] = new Object[3];
        System.out.println("Your Name");
        info[0] = sc.next();
        System.out.println("Your ID");
        info[1] = sc.nextInt();
        info[2] = "student";
        return info;
    }

    public static void print(Object array[]){
        for(Object x:array){
            System.out.println(x);
        }
    }
}
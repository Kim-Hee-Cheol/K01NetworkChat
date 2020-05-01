package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MultiServer {
   
   static PreparedStatement psmt;
   static Connection con;
   static Statement stmt;
   static ResultSet rs;
   
   //생성자
   public static void dbcon() {

      try {
         Class.forName("oracle.jdbc.OracleDriver");
         con = DriverManager.getConnection
               ("jdbc:oracle:thin://@localhost:1521:orcl", 
                     "kosmo","1234"
               );
         System.out.println("오라클 DB 연결성공");
      }
      catch (ClassNotFoundException e) {
         System.out.println("오라클 드라이버 로딩 실패");
         e.printStackTrace();
      }
      catch (SQLException e) {
         System.out.println("DB 연결 실패");
         e.printStackTrace();
      }
      catch (Exception e) {
         System.out.println("알 수 없는 예외 발생");
      }
   }
   
   public static void close() {
      try {
         if(psmt != null) psmt.close();
         if(stmt != null) stmt.close();
         if(con != null) con.close();
         if(rs != null) rs.close();
         
      }
      catch(SQLException e) {
         System.out.println("자원 반납 시 오류가 발생하였습니다.");
      }
   }


   public static void main(String[] args) {
	   
	    ServerSocket serverSocket = null;
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String s = "";
		String name = "";

		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");

			socket = serverSocket.accept();

			out = new PrintWriter(socket.getOutputStream(), true);

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
			if(in != null) {
				name = in.readLine();
				System.out.println(name +" 접속");
				out.println("> "+ name +"님이 접속했습니다.");
			}

			while(in != null) {
				s = in.readLine();
				if(s==null) {
					break;
				}
				
				 try {
					 dbcon();
				     String query = "INSERT INTO chatting_tb VALUES "
				                    + "(chatting_seq.NEXTVAL, ?, ?, sysdate)";
				         psmt = con.prepareStatement(query);
				         //클라이언트의 이름을 읽어와서 저장
				         psmt.setString(1, name);
				         psmt.setString(2, s);
				         
				         psmt.executeUpdate();
				 }
				 catch(Exception e) {
					 System.out.println("DB저장 실패");
					 e.printStackTrace();
				 }
				 System.out.println(name +" ==> "+ s);
				 out.println(">  "+ name +" ==> "+ s);
			}

			System.out.println("Bye...!!!");
		}
		catch (Exception e) {
			System.out.println("자원반납완료");
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외2:"+ e);
			}
		}
	}
}
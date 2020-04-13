package chat4;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiClient {

	public static void main(String[] args) {
		
		System.out.print("이름을 입력하세요:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		
		PrintWriter out = null;
		//서버의 메세지를 읽어오는 기능이 Receiver로 옮겨짐.
		//BufferedReader in = null;
		
		try {
			/*
			c:\> java 패키지명.MultiClient 접속할 IP주소
				=>위와같이 하면 해당IP주소로 접속할 수 있다.
				만약 IP주소가 없다면 localhost(127.0.0.1)로 접속된다.
			 */
			String ServerIP = "localhost";
			//클라이언트 실행시 매개변수가 있는경우 아이피로 설정함
			if(args.length > 0) {
				ServerIP = args[0];
			}
			//IP주소와 포트를 기반으로 소켓객체를 생성하여 서버에 접속함
			Socket socket = new Socket(ServerIP,9999);
			//서버와 연결되면 콘솔에 메세지 출력
			System.out.println("서버와 연결되었습니다...");
			
			//서버에서 보내는 메세지를 읽어올 Receiver쓰레드 시작
			Thread receiver = new Receiver(socket);
			//setDaemon(true)가 없으므로 독립쓰레드로 생성됨.
			receiver.start();
			
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println(s_name);
			
			while(out!=null) {
				try {
					String s2 = scanner.nextLine();
					if(s2.equals("q")|| s2.equals("Q")) {
						//입력값이 Q(q)이면 while 루프 탈출, 프로그램 종료
						break;
					}
					else {
						//만약Q(q)가 아니면 서버로 입력내용 전송
						out.println(s2);
					}
				}
				catch(Exception e) {
					System.out.println("예외:"+ e);
				}
			}
			
			//스트림과 소켓을 종료한다.
			out.close();
			socket.close();
		}
		catch(Exception e) {
			System.out.println("예외발생[MultiClient]" + e);
		}
	}

}

	
	
	
	
	
	
	
	
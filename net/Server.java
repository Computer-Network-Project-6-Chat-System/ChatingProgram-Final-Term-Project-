package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;


public class Server {

	/*
	 * clients : Client의 name과 printWriter를 HashMasp 형태로 key와 value로 Mapping하여 저장할 변수
	 */	
	private static Map<String, PrintWriter> clients = new HashMap<String, PrintWriter>();


	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(500);
		try (ServerSocket listener = new ServerSocket(59001)) {
			while (true) {
				pool.execute(new Handler(listener.accept()));
			}
		}
	}

	/**
	 * The client handler task.
	 */
	private static class Handler implements Runnable {
		/*
		 * name : 사용자의 이름을 저장할 변수
		 * socket : socket통신을 할 Server socket을 저장
		 * in : 값을 입력 받아올 Scanner
		 * out : 값을 내보 낼 PrintWriter
		 */
		private String name;
		private Socket socket;
		private Scanner in;
		private PrintWriter out;

		/**
		 * Constructs a handler thread, squirreling away the socket. All the interesting
		 * work is done in the run method. Remember the constructor is called from the
		 * server's main method, so this has to be as short as possible.
		 */
		public Handler(Socket socket) {
			//연결될 경우 받은 socket을 socket변수에다 저장
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name until a
		 * unique one has been submitted, then acknowledges the name and registers the
		 * output stream for the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			DBConnection db = new DBConnection();
			try {
				/*
				 * in은 socket에서 값을 받아올 변수, out은 socket에 값을 내보낼 변수
				 */
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				
				
				// Keep requesting a name until we get a unique one.
//				while (true) {
//					out.println("SUBMITNAME");
//					name = in.nextLine();
//					if (name == null) {
//						return;
//					}
//					synchronized (clients) {
//						if (name.length() > 0 && !clients.containsKey(name)) {
//							for (PrintWriter writer : clients.values()) {
//								writer.println("MESSAGE " + name + " has joined");
//							}
//							clients.put(name, out);
//							break;
//						}
//					}
//				}
				//TODO 임시로 사용자의 이름을 넣어둠 간단한 예제를 위한 코드
				String id = "123";
				name = "여찬영";
				String info = "졸려";
				ArrayList<Friend> friend = new ArrayList<Friend>();
				
				   //id, 닉네임, 이름, 생일, 이메일, 한줄소개(상태메세지), 전화번호, 온라인, 최근접속날짜 순서로 저장 
	            friend.add(new Friend("111", "김민수닉네임","김민수", "2020-12-12","aaa@gachon.ac.kr","자퇴합니다.", "010-2839-2892", "(오프라인)", "10/26"));
	            friend.add(new Friend("222", "이채원닉네임","이채원", "2020-12-12","aaa@gachon.ac.kr","자바 코딩 왤케 어려워 ㅠㅠ", "010-5985-9287", "(온라인)", "11/26"));
	            friend.add(new Friend("333", "박재현닉네임","박재현", "2020-12-12","aaa@gachon.ac.kr","과제 너무 하기 싫다 진짜 하...", "010-7547-2874", "(오프라인)", "11/24"));
	            friend.add(new Friend("444", "손흥민닉네임","손흥민", "2020-12-12","aaa@gachon.ac.kr","이번 시즌은 내가 득점왕", "010-3456-9298", "(오프라인)", "11/1"));
	            friend.add(new Friend("555", "황희찬닉네임","황희찬", "2020-12-12","aaa@gachon.ac.kr","오늘은 한골 더 ㄱㄱ", "010-4782-5895", "(온라인)", "11/26"));
				
				//사용자가 로그인 완료됐다는 메세지
				out.println("NAMEACCEPTED " + id + " " + name + " " + info);
				for(int i = 0; i< friend.size(); i++) {
					out.println("FRIENDLIST"+ ",/" + friend.get(i).id+ ",/" + friend.get(i).nickname + ",/" + friend.get(i).name + ",/" +friend.get(i).birth 
							+ ",/" + friend.get(i).email + ",/" + friend.get(i).info + ",/" + friend.get(i).getNumber() + ",/" + friend.get(i).getOnline() + ",/" + friend.get(i).getLast_date());
				}
				out.println("SET");
				
				
				while (true) {
					String input = in.nextLine();
					String[] lines;
					//사용자 정보 변경
					if(input.startsWith("Change name")) {
						//db.updateName("현재유저아이디",input.substring(12));
						name = input.substring(12);
						out.println("Change name:"+name);
						//TODO 사용자 이름 변경 DB 구현 // "현재유저아이디" 수정요망
					}
					else if(input.startsWith("Change info")) {
						//db.updateStatusMessage("현재유저아이디", input.substring(12));
						info = input.substring(12);
						out.println("Change info:"+info);
						//TODO 사용자 한줄 소개 변경 DB 구현.// "현재유저아이디" 수정요망
					}
					//검색기능
					else if(input.startsWith("search")) {
						db.searchID(input.substring(7));
//						System.out.println(input.substring(7));
						//TODO 검색 값에 따른 DB구현 //Console 창에만 출력됨. 
					}
					//1:1대화 기능
					else if(input.startsWith("chat_start_0")) {
						//TODO 1:1 대화 응답 구현
						System.out.println(input.substring(13));
						String chat = input.substring(13);
//						if (db.getConnectionStatus(input.substring(13)).equals("online")){
//							// TODO 아이디가 input.substring(13)인 상대방이 온라인이라면 연결 요청
//						}
						out.println("chat_start:"+chat);
					}
					else if(input.startsWith("Message")) {
						//TODO 1:1 대화 응답 구현
						lines = input.split(",/");
						//메세지 보내기 구현
						if(lines[3].equalsIgnoreCase("/quit")) {
							out.println("WindowQuit");
						}else {
							out.println("Message,/"+lines[1]+",/"+lines[2]+",/"+ lines[3]);
						}
					}
					else if(input.startsWith("send_ID_PW")) {
						lines = input.split(" ");
						System.out.println("lines");
						if (db.logIn(lines[0], lines[1])==true) {
							out.println("SUBMITLOGIN");
						}
					}
//					
//					if(input.startsWith("<")) {
//						try{
//							int idx = input.indexOf("/");
//							String whisper = input.substring(1, idx);
//							input = input.substring(idx+2);
//							if(clients.containsKey(whisper)) {
//								PrintWriter writer = clients.get(whisper);
//								writer.println("MESSAGE " + name + ": " + input);
//							}else {
//								PrintWriter writer = clients.get(name);
//								writer.println("MESSAGE " + "ERROR : whisper type: <name/> content");
//							}
//						}catch(StringIndexOutOfBoundsException e) {
//							for (PrintWriter writer : clients.values()) {
//								writer.println("MESSAGE " + name + ": " + input);
//							}
//						}
//					}else {
//						for (PrintWriter writer : clients.values()) {
//							writer.println("MESSAGE " + name + ": " + input);
//						}
//					}				
				}
	
			}catch (Exception e) {
				//Error Message 처리
				System.out.println(e);
			} finally {
				if (out != null && name!= null) {
					System.out.println(name + " is leaving");
					clients.remove(name);
					for (PrintWriter writer : clients.values()) {
						writer.println("MESSAGE " + name + " has left");
					}
				}
				try { socket.close(); } catch (IOException e) {}
			}
		}
	}

}

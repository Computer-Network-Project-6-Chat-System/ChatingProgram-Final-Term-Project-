package Chats;

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
		String id; //추가함
		private String name;
		String info; //추가함
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
				out.println("SUBMIT");
				while (true) {
					String input = in.nextLine();
					
					if (input == null) {
						return;
					} else if (input.startsWith("Sign_Up")) {
						String[] lines = input.split(",/");
						for(int i = 0; i< 9; i++) {
							System.out.println(lines[i]); // TODO 로그 남겨야 하는지 여쭤보기
						}
						if (db.checkID(lines[1])==false) {
							db.createAccount(lines[1], lines[2], lines[3], lines[4], lines[5], lines[6], lines[7], lines[8]);
							out.println("CREATE_SUCCESS");
						} else {
							out.println("DUPLICATED_ID");
						}
    					
					} else if (input.startsWith("send_ID_PW")) {
						String[] lines = input.split(" ");
						System.out.println(input); // TODO 암호화되지 않은 password정보 노출됨. 수정필요할듯함
						try {
							this.id = lines[1]; //추가
							this.info = db.getStatusMessage(id);//추가
							this.name = db.getName(id);//추가
						
						if (db.logIn(lines[1], lines[2])==true) {
							out.println("SUBMITLOGIN 1");
							break;
						} else { out.println("SUBMITLOGIN 0"); }
						} catch (java.lang.ArrayIndexOutOfBoundsException e) {
							out.println("SUBMITLOGIN 0");
						}
					} 
				}				

				out.println("NAMEACCEPTED " + id + ",/" + name + ",/" + info);
				String[] friends = db.getFriendList(id).split(" ");
				for(int i = 0; i< db.getNumberOfFriends(id); i++) {
					String frId = friends[i];
					out.println("FRIENDLIST" + ",/" + frId + ",/" + db.getNickName(frId) + ",/" + db.getName(frId) + ",/"
							+ db.getBirthDay(frId) + ",/" + db.getEmail(frId) + ",/" + db.getStatusMessage(frId) + ",/"
							+ db.getPhoneNumber(frId) + ",/" + db.getConnectionStatus(frId)	+ ",/" + db.getLatestAccess(frId));
				} 
				//여기까지 수정
				out.println("SET");	
				
				while (true) {
					String input = in.nextLine();
					//사용자 정보 변경
					if(input.startsWith("Change name")) {
						name = input.substring(12);
						db.updateName(id,name);
						out.println("Change name:"+name);
					}
					else if(input.startsWith("Change info")) {
						info = input.substring(12);
						db.updateStatusMessage(id, info);
						out.println("Change info:"+info);
					}
					else if(input.startsWith("Insert_Friend")) {
						System.out.println(input);
						String frId = input.split(",/")[2];
						db.addFriend(id,frId);
						out.println("FRIEND_ADD"+ ",/" + frId + ",/" + db.getNickName(frId) +",/" + db.getName(frId) + 
									",/" +db.getBirthDay(frId) +",/"+db.getEmail(frId)+ ",/" + db.getStatusMessage(frId) +
									",/" + db.getPhoneNumber(frId) + ",/" + db.getConnectionStatus(frId) + ",/" + db.getLatestAccess(frId));
					} 
					
					//검색기능
					else if(input.startsWith("Search")) {
						String keyword = input.substring(8);
						System.out.println(keyword);
//						String[] lines = db.searchItems(keyword).split(",/");
						out.println("search_value"+":/" + "101" + ",/" + "박지성닉네임" +",/" + "박지성" + ",/" +"2020-12-12" +",/"+
								"aaa@gachon.ac.kr"+ ",/" + "요요" + ",/" + "010-2948-5787" + ",/" + "오프라인" + ",/" + "12/13"
								+":/" + "101" + ",/" + "박지성닉네임" +",/" + "박지성" + ",/" +"2020-12-12" +",/"+
								"aaa@gachon.ac.kr"+ ",/" + "요요" + ",/" + "010-2948-5787" + ",/" + "오프라인" + ",/" + "12/13");
						 
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
					else if(input.startsWith("chat_start_Group")) {
						//TODO 1:1 대화 응답 구현
						String[] chats = input.split(",/");
						String chat = "";
						for(int i = 1; i < chats.length; i++) {
							chat = chat + ",/" + chats[i];
						}
						//연결 확인
//						if (db.getConnectionStatus(input.substring(13)).equals("online")){
//							// TODO 아이디가 input.substring(13)인 상대방이 온라인이라면 연결 요청
//						}
						out.println("group_chat_start"+chat);
					}
					else if(input.startsWith("Message")) {
						//TODO 1:1 대화 응답 구현
						String[] lines = input.split(",/");
						//메세지 보내기 구현
						if(lines[3].equalsIgnoreCase("/quit")) {
							out.println("WindowQuit");
						} else {
							out.println("Message,/"+lines[1]+",/"+lines[2]+",/"+ lines[3]);
						}
					}
					else if(input.startsWith("Group_Chat")) {
						//TODO 1:1 대화 응답 구현
						System.out.println(input);
						String[] lines = input.split(",/");
						String line = "";
						for(int i = 1; i< lines.length;i++) {
							line = line + ",/" + lines[i];
						}
						System.out.println(line);
//						메세지 보내기 구현
						if(lines[3].equalsIgnoreCase("/quit")) {
							out.println("GroupWindowQuit");
						} else {
							out.println("Group_Message"+line);
						}
					} else if (input.startsWith("LOGOUT_CLICKED")) {
						//로그아웃 (최근접속날짜 업데이트)
						db.logOut(id);
					} else if (input.startsWith("CANCEL_ACCOUNT_REQUEST")) {
						//회원 탈퇴 구현
						String[] lines = input.split(",/");
						if (db.cancelAccount(lines[1], lines[2],lines[3])==true) {
							out.println("ACCOUNT_CANCELED");							
						}						
					}					
				}
			} catch (java.util.NoSuchElementException e) {
				db.logOut(id);
			} catch (Exception e) {
				//Error Message 처리
				e.printStackTrace();
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

package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;


public class Server {
   /*
    * clients : Client의 name과 printWriter를 HashMasp 형태로 key와 value로 Mapping하여 저장할 변수
    */   
   private static Map<String, PrintWriter> clients = new HashMap<String, PrintWriter>();
   private static String GroupChat="";

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
                  try {
                     for(int i = 0; i< 9; i++) {
                        System.out.println(lines[i]); // TODO 로그 남겨야 하는지 여쭤보기
                     }
                     if (db.checkID(lines[1])==false) {
                        db.createAccount(lines[1], lines[2], lines[3], lines[4], lines[5], lines[6], lines[7], lines[8]);
                        out.println("CREATE_SUCCESS");
                     } else {out.println("DUPLICATED_ID");}
                                          
                  } catch (ArrayIndexOutOfBoundsException e) {out.println("EXCEPTION_OCCURED");}
                   
               } else if (input.startsWith("send_ID_PW")) {
                  String[] lines = input.split(" ");
                  System.out.println(input); // TODO 암호화되지 않은 password정보 노출됨. 수정필요할듯함
                  try {
                     this.id = lines[1];
                     this.info = db.getStatusMessage(id);
                     this.name = db.getName(id);
                  
                  if (db.logIn(lines[1], lines[2])==true) {
                     out.println("SUBMITLOGIN 1");
                     break;
                  } else { out.println("SUBMITLOGIN 0"); }
                  } catch (ArrayIndexOutOfBoundsException e) {
                     out.println("SUBMITLOGIN 0");
                  }
               } else if (input.startsWith("FIND_ID_REQUEST")) {
                  System.out.println(input);
                  String[] lines = input.split(",/");
                  try {
                     String foundId =db.findID(lines[1], lines[2], lines[3]); 
                     if(foundId==null) {throw new Exception();}
                     out.println("FIND_ID_SUCCESS "+foundId);                     
                  } catch (ArrayIndexOutOfBoundsException e) {
                     out.println("EXCEPTION_OCCURED");
                  } catch (Exception e) {
                     out.println("EXCEPTION_OCCURED");
                  }
               } else if (input.startsWith("FIND_PW_REQUEST")) {
                  System.out.println(input);
                  String[] lines = input.split(",/");
                  try {
                     String foundPw = db.findID(lines[1], lines[2], lines[3]);
                     if(foundPw==null) {throw new ArrayIndexOutOfBoundsException();}
                     out.println("FIND_PW_SUCCESS "+foundPw);                     
                  } catch (ArrayIndexOutOfBoundsException e) {
                     out.println("EXCEPTION_OCCURED");
                  }
               }
            }            
            clients.put(id, out);
            out.println("NAMEACCEPTED " + id + ",/" + name + ",/" + info);
            String[] friends = db.getFriendList(id).split(" ");
            for(int i = 0; i< db.getNumberOfFriends(id); i++) {
               String frId = friends[i];
               out.println("FRIENDLIST" + ",/" + frId + ",/" + db.getNickName(frId) + ",/" + db.getName(frId) + ",/"
                     + db.getBirthDay(frId) + ",/" + db.getEmail(frId) + ",/" + db.getStatusMessage(frId) + ",/"
                     + db.getPhoneNumber(frId) + ",/" + db.getConnectionStatus(frId)   + ",/" + db.getLatestAccess(frId));
            } 
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
                  try {                     
                     String frId = input.split(",/")[2];
                     if (!id.equals(frId)&&db.checkID(frId)==true) {
                        db.addFriend(id,frId);
                        out.println("FRIEND_ADD"+ ",/" + frId + ",/" + db.getNickName(frId) +",/" + db.getName(frId) + 
                              ",/" +db.getBirthDay(frId) +",/"+db.getEmail(frId)+ ",/" + db.getStatusMessage(frId) +
                              ",/" + db.getPhoneNumber(frId) + ",/" + db.getConnectionStatus(frId) + ",/" + db.getLatestAccess(frId));                        
                     } else { out.println("EXCEPTION_OCCURED"); }
                  } catch (ArrayIndexOutOfBoundsException e) {
                     out.println("EXCEPTION_OCCURED");
                  }
               } 
               
               //검색기능
               else if(input.startsWith("Search")) { 
                  String keyword = input.substring(8);
                  System.out.println(keyword);
                  String send="search_value";
                  if(db.searchItems(id,keyword).equals("")) {
                     send += (":/"+" "+",/"+" "+",/"+" "+",/"+" "+",/"+" "+",/"+" "+",/"+" "+",/"+" "+",/"+" "+",/");               
                  } else {
                     String[] lines = db.searchItems(id,keyword).split(",/");
                     lines = new HashSet<String>
                     (Arrays.asList(lines)).toArray(new String[0]); //중복제거
                     
                     for (String frid:lines) {
                        send += ":/" +frid+ ",/" + db.getNickName(frid)+ ",/" + db.getName(frid)+ ",/" 
                              + db.getBirthDay(frid) + ",/" + db.getEmail(frid)+ ",/" + db.getStatusMessage(frid)+ ",/" +db.getPhoneNumber(frid)
                              + ",/" + db.getConnectionStatus(frid) + ",/" +db.getLatestAccess(frid);
                     }
                  }
                  System.out.println(send);
                  out.println(send);
                   
               }
               //1:1대화 기능 
               else if(input.startsWith("chat_start_0")) {
                  //1:1 대화 응답
                  String[] lines = input.split(",/");
                  out.println("chat_start:"+lines[2]); //lines[2]: 친구 아이디
                  if(clients.containsKey(lines[2])) {
                     clients.get(lines[2]).println("chat_question " + lines[1]);
                  }else {
                     out.println("chat_fail");
                  }
               }else if(input.startsWith("chat_fail")) {
                  clients.get(input.substring(10)).println("chat_failed");
               }else if(input.startsWith("chat_success")) {
                  System.out.println(input);
                  String[] lines = input.split(",/");
                  clients.get(lines[1]).println("chat_start:"+lines[2]);
                  clients.get(lines[2]).println("chats_start:"+lines[1]);
                  clients.get(lines[1]).println("chats_start:"+lines[2]);
               }else if(input.startsWith("chat_start_Group")) {
                  //TODO 그룹대화 응답 구현
                  String[] chats = input.split(",/");
                  for(int i = 1; i < chats.length; i++) {
                     if(clients.containsKey(chats[i])){
                        clients.get(chats[i]).println("group_chat_question " + id);
                     }
                  }
                  if(GroupChat.equals("")) {
                     GroupChat = GroupChat + id;                     
                  }
               }else if(input.startsWith("group_chat_success")) {
                  GroupChat = GroupChat +",/"+ input.substring(19);
                  System.out.println(GroupChat);
                  String[] line = GroupChat.split(",/");
                  for(int i = 0; i < line.length ; i++) {
                     clients.get(line[i]).println("group_chat_start"+GroupChat);
                  }
               }else if(input.startsWith("Message")) {
                  //TODO 1:1 대화 응답 구현
                  String[] lines = input.split(",/");
                  //메세지 보내기 구현
                  if(lines[3].equalsIgnoreCase("/quit")) {
                     clients.get(lines[1]).println("WindowQuit");
                     clients.get(lines[2]).println("WindowQuit");
                  } else {
                     clients.get(lines[1]).println("Message,/"+lines[1]+",/"+lines[2]+",/"+ lines[3]);
                     clients.get(lines[2]).println("Message,/"+lines[1]+",/"+lines[2]+",/"+ lines[3]);
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
//                  메세지 보내기 구현
                  if(lines[2].equalsIgnoreCase("/quit")) {
                     out.println("GroupWindowQuit");
                  } else {
                     String[] client = GroupChat.split(",/");
                     for(int i = 0; i < client.length ; i++) {
                        clients.get(client[i]).println("Group_Message"+line);
                     }
                  }
               } else if (input.startsWith("LOGOUT_CLICKED")) {
                  //로그아웃 (최근접속날짜 업데이트)
                  db.logOut(id);
               } else if (input.startsWith("CANCEL_ACCOUNT_REQUEST")) {
                  //회원 탈퇴 구현
                  System.out.println(input);
                  String[] lines = input.split(",/");
                  if (db.cancelAccount(lines[1], lines[2],lines[3])==true) {
                     out.println("ACCOUNT_CANCELED");                     
                  }                  
               } else if (input.startsWith("delete friend")) {
                  //친구 삭제
                  String frId = input.substring(14);
                  db.deleteFriend(id, frId);
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
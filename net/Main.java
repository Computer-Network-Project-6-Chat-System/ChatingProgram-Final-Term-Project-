package Chat;

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.*;

import javax.swing.*;

public class Main {
	Scanner in;
    PrintWriter out;
    String id, name, info;
    ArrayList<Friend> friend = new ArrayList<Friend>();

    JFrame frame = new JFrame("Chatter");
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel chatPanel = new JPanel();
    JButton button1 = new JButton("<html>이름<br>변경</html>");
    JButton button2 = new JButton("<html>소개<br>변경</html>");      
    JButton button3 = new JButton("친구");
    JButton button4 = new JButton("채팅");
    JButton button5 = new JButton("<html>친구<br>등록</html>");
    NewWindow window;
   
    boolean frag = false;
   
    public Main() {
       
    }
    //친구 목록을 나타내는 창
    private void EditFriend(JFrame frame, JPanel friendPanel, int i, PrintWriter out ) {
    	JButton button = new JButton(Integer.toString(i));
    	button.setPreferredSize(new Dimension(250,30));
    	button.setBorderPainted(false);
    	button.setContentAreaFilled(false);
      
    	button.setText(friend.get(i).name+"   "+friend.get(i).info);
    	button.setHorizontalAlignment(SwingConstants.LEFT);
    	button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	String []list = {"1:1대화", "친구삭제 ", "취소"};
            int select = JOptionPane.showOptionDialog(frame, friend.get(i).getName()+" "+ friend.get(i).getNumber() + " "
                  + friend.get(i).getLast_date() +" " + friend.get(i).getOnline(), friend.get(i).getName(), 
                  JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, list, list[0]);
            if(select == 0) {
               out.println("chat_start_0 "+ friend.get(i).id);
            }
            if(select == 1) {
               out.println("delete feind " + friend.get(i).id);
            }
         }
         
      });
      
      friendPanel.add(button);
    }    

    //전반적인 메인 구성
    private void EditMainPage(JFrame frame, JPanel leftPanel, JPanel rightPanel, JPanel chatPanel, PrintWriter out) {
       leftPanel.setBackground(new Color(000, 102, 153));
      leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
      leftPanel.setPreferredSize(new Dimension(80, 0));
      
      button1.setPreferredSize(new Dimension(75,60));
      button2.setPreferredSize(new Dimension(75,60));
      button3.setPreferredSize(new Dimension(75,60));
      button4.setPreferredSize(new Dimension(75,60));
      button5.setPreferredSize(new Dimension(75,60));
      button5.setBackground(new Color(204,204,204));
      button1.setBackground(new Color(204,204,204));
      button2.setBackground(new Color(204,204,204));
      button3.setBackground(new Color(204,204,204));
      button4.setBackground(new Color(204,204,204));
      
      button1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String BtnValue = JOptionPane.showInputDialog(
                     "Change name:",
                     name
                 );
            if(BtnValue != null) {
               out.println("Change name "+ BtnValue);
            }
         }
         
      });
      button2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String BtnValue = JOptionPane.showInputDialog(
                     "Change Introduction:",
                     info
                 );
            if(BtnValue != null) {
               out.println("Change info "+ BtnValue);
            }
         }
      });
      button5.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String BtnValue = JOptionPane.showInputDialog(
                     "친구 추가"
                 );
            if(BtnValue != null) {
               out.println("Insert Friend "+ BtnValue);
            }
         }
      });
      button3.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.remove(chatPanel);
            chatPanel.removeAll();
            EditMain(frame, rightPanel, out, false);
            frame.getContentPane().add(rightPanel);
            frame.revalidate();
            frame.repaint();
         }
      });
      button4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.remove(rightPanel);
            rightPanel.removeAll();
            EditChat(frame, chatPanel, out);
            frame.getContentPane().add(chatPanel);
            frame.revalidate();
            frame.repaint();
         }
      });
      
      leftPanel.add(button1);
      leftPanel.add(button2);
      leftPanel.add(button5);
      leftPanel.add(button3);
      leftPanel.add(button4);
      EditMain(frame, rightPanel, out, false);
      //EditChat(frame, chatPanel, out);
      
      frame.getContentPane().add(leftPanel, BorderLayout.WEST);
      frame.getContentPane().add(rightPanel);
      frame.setVisible(true);
   }
    
    //친구 검색, 친구 목록, 검색창을 나타냄
    private void EditMain(JFrame frame, JPanel rightPanel,  PrintWriter out, boolean searchIs) {
      rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
      rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
      rightPanel.setPreferredSize(new Dimension(285, 100));
      JTextField search = new JTextField(18);
      search.setEditable(true);
      rightPanel.add(new JLabel("친구검색: "));
      rightPanel.add(search);   
        
      JPanel friendPanel = new JPanel();
      JLabel friendList = new JLabel("-친구목록-");
      JPanel searchPanel = new JPanel();
      
      friendList.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      friendPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
      friendPanel.setPreferredSize(new Dimension(285, 1000));
      friendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      friendPanel.add(friendList);
      
      searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
      searchPanel.setBackground(Color.BLUE);
      searchPanel.setPreferredSize(new Dimension(285, 1000));
      searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

         for(int i = 0;i < friend.size();i++) {
            EditFriend(frame, friendPanel, i, out);
         }

       rightPanel.add(friendPanel);
       
         
      //TODO 검색기능 구현
        search.addActionListener(new ActionListener() {
           @Override
            public void actionPerformed(ActionEvent e) {
            search.setText("");
            }
        });
   }
    private void EditChat(JFrame frame, JPanel chatPanel,  PrintWriter out) {
       chatPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
       chatPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
       chatPanel.setPreferredSize(new Dimension(285, 100));
       
       JButton button = new JButton();
      button.setPreferredSize(new Dimension(250,30));
      button.setContentAreaFilled(false);
      
      button.setText("그룹채팅 만들기");
      
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            ArrayList<String> chat_list = new ArrayList<String>();
            JPanel al = new JPanel();
            for (int i = 0; i< friend.size();i ++){
                JCheckBox box = new JCheckBox(friend.get(i).getName());
                box.addActionListener(new ActionListener() {

                  @Override
                  public void actionPerformed(ActionEvent arg0) {
                     // TODO Auto-generated method stub
                     if(chat_list.contains(box.getText())) {
                        chat_list.remove(box.getText());
                     }else {
                        chat_list.add(box.getText());
                     }
                  }
                   
                });
                al.add(box);
            }
            int d = JOptionPane.showConfirmDialog(null, al, "Information", JOptionPane.YES_NO_OPTION);
            if(d == 0) {
               for(String i : chat_list)
                  System.out.println(i);
            }
         }
      });
      
      chatPanel.add(button);
    }
    
    
    private void run() throws IOException {
       String localhost = "localhost";
      int socketnum = 59001;
      
      try {
           Socket socket = new Socket(localhost, socketnum);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            
            LoginWindows lgw = new LoginWindows();
            out.println("send_ID_PW "+" "+lgw.ID+" "+lgw.PW);
            
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String [] lines;
                //로그인 구현
                if (line.startsWith("SUBMITLOGIN")) {
                	frame.setVisible(true);
                } else if (line.startsWith("NAMEACCEPTED")) { //현재 사용자의 정보를 받아옴
                   lines =  line.substring(13).split(" ");
                   this.id = lines[0];
                   this.name = lines[1];
                   this.info =  lines[2];
                    this.frame.setTitle("Chatter - " + this.name);
                } else if (line.startsWith("FRIENDLIST")) {
                   lines = line.split(",/");
                   friend.add(new Friend(lines[1], lines[2], lines[3], lines[4], lines[5], lines[6], lines[7],lines[8], lines[9]));
                } else if (line.startsWith("SET")) {
                   EditMainPage(frame, leftPanel, rightPanel, chatPanel, out);
                } else if (line.startsWith("MESSAGE")) {
                
                    //messageArea.append(line.substring(8) + "\n");
                } else if (line.startsWith("Change name")) {
                   this.name = line.substring(12);
                   this.frame.setTitle("Chatter - " + this.name);
                } else if (line.startsWith("Change info")) {
                   this.info = line.substring(12);
                } else if (line.startsWith("chat_start")) {
                   System.out.println(line.substring(11));
                   if(window == null) {
                       window = new NewWindow(this.id, line.substring(11));
                   }
                } else if (line.startsWith("Message")) {
                   String[] inputs = line.split(",/");
                   window.changeMessage(inputs);
                } else if (line.startsWith("WindowQuit")) {
                   window.quit();
                   window = null;
                }
                socket.close();
            }
            socket.close();
        }catch(java.net.ConnectException e){
           //서버와 연결이 안됐을 경우 연결이 안됐다는 메세지를 출력 
           System.out.println("Can't Connect Server");
        }catch(Exception e) {
           //Error Message출력
           System.out.println(e.getMessage());
        }finally {
           //GUI창을 종료함
            frame.setVisible(false);
            frame.dispose();
            System.out.println("Chat Finish");
        }
    }
    
    class NewWindow extends JFrame {
        JTextField textField = new JTextField(50);
        JTextArea messageArea = new JTextArea(16, 50);
        NewWindow(String sndId, String rcvId) {
           
            setTitle(rcvId);
            
            textField.setEditable(true);
            messageArea.setEditable(false);
            getContentPane().add(textField, BorderLayout.SOUTH);
            getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
            pack();
            setSize(300, 400);
            
            setVisible(true);
            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    out.println("Message,/"+sndId+",/"+rcvId+",/"+textField.getText());
                    textField.setText("");
                }
            });
        }
        private void changeMessage(String[] message){
           messageArea.append(message[1]+": "+message[3]+"\n");
        }
        private void quit() {
           setVisible(false);
        }
    }
    
   public static void main(String[] args) throws Exception {
      // TODO Auto-generated method stub
	   Main client = new Main();
	   LoginWindows lgn = new LoginWindows();
	   lgn.login();
	   client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   client.frame.setSize(400, 600);
//      	client.frame.setVisible(true);
       System.out.println("Chat Start...");
       client.run();
      
      
   }
}
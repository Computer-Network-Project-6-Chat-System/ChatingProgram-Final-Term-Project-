package Chats;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InfoConfig { //serverinfo.txt파일로부터 Port number와 IP주소를 얻어내는 역할을 하는 class.
    String Port;
    String Ip;
    public void getInfo() {
        try{
            File f = new File("serverinfo.txt");
            Scanner sc = new Scanner(f);
            
            Ip = sc.nextLine().split(":")[1]; //Scanner로 읽은 첫 번째 행을 ":"을 기준으로 나눈 후, 두 번째 요소를 택하면 IP주소를 얻을 수 있다.
            Port = sc.nextLine().split(":")[1]; //Client의 Socket클래스가 받는 Port number인자는 정수형이므로 형변환을 시켜준다.
            if(sc!=null) sc.close(); 

        } catch (FileNotFoundException e){ //FileNotFoundException 발생 시 default 값을 할당한다
            Ip = "localhost";
            Port = "3306";
        }
    }
}

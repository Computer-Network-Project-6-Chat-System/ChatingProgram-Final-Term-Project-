package Chats;

public class Friend {
   String id;
   String name;
   String nickname;   
   String birth;
   String email;
   String info;
   String number;
   String online;
   String last_date;
   
   public Friend(String id, String nickname,String name,String birth,String email, String info, String number, String online,String last_date) {
      this.id = id;
      this.nickname = nickname;
      this.name = name;
      this.birth = birth;
      this.email = email;
      this.info = info;
      this.number = number;
      this.online = online;
      this.last_date = last_date;
   }
   
   DBConnection db = new DBConnection();

   public String getId() {
//     id = db.getId(id);
	   return id;
  }
   public String getNickname() {
//      nickname = db.getNickName(id);
      return nickname;
   }
   public void setNickname(String nickname) { 
//      nickname = db.updateNickName(id, nickname);
      this.nickname = nickname;
   }
   public String getName() { 
//      name = db.getName(id);
      return name;
   }
   public void setName(String name) { 
//      name = db.updateName(id, name);
      this.name = name;
   }
   public String getBirth() { //전화번호 
//      number = db.getBirthDay(id);
      return number;
   }
   public void setBirth(String birth) { 
//      birth = db.updateBirthDay(id, birth);
      this.birth = birth;
   }
   public String getEmail() { //전화번호 
//      email = db.getEmail(id);
      return email;
   }
   public void setEmail(String email) { 
//      birth = db.updateEmail(id, email);
      this.email = email;
   }
   public String getNumber() { //전화번호 
//      number = db.getPhoneNumber(id);
      return number;
   }
   public void setNumber(String number) {
//      number = db.updatePhoneNumber(id, number);
      this.number = number;
   }
   public String getInfo() { //한줄소개 
//      info= db.getStatusMessage(id);
      return info;
   }
   public void setInfo(String info) {
//      info = db.updateStatusMessage(id, info); 
      this.info = info;
   }
   public String getOnline() {
//      online = db.getConnectionStatus(id);
      return online;
   }
   public void setOnline(String online) {
//      online = db.updateConnectionStatus(id); 
      this.online = online;
   }
   public String getLast_date() {
      return last_date;
   }
   public void setLast_date(String last_date) {
      this.last_date = last_date;
   }
   

}
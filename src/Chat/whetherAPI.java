package Chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
public class whetherAPI {
	
	/**
	 * 수정구 복정동에서의 날씨
	 */
	public String getColorCode(){
		 
		String ans="";
		
		try {
			String serviceKey = "nmwGSJ4MGuujj9LZoDn%2FNy8lczMP%2BK%2BR%2BmdNIlrsd3ZSatkP%2Fc6SM6hwUWpq8z%2BTr1cemXnmAuOku6QRLxLwrg%3D%3D";
			DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd");  
			DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HHmm");  
			LocalDateTime now = LocalDateTime.now();

			String dataType = "JSON";
			String xcoordinate = "62"; //"복정동"의 x좌표
			String ycoordinate = "124"; //"복정동"의 y좌표
			String base_date = dtf1.format(now); //발표일자
			String base_time = dtf2.format(now); //발표시각
		
			StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"); //URL
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + serviceKey); //Service Key
			urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //한 페이지 결과 수
			urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
			urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); //요청자료형식: JSON
			urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF8")); // 2020년 11월 20일 발표
			urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time, "UTF8"));  // 17시 발표
			urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(xcoordinate, "UTF-8")); // 예보지점의 X 좌표값
			urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ycoordinate, "UTF8")); // 예보지점의 Y 좌표값
			
			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
			conn.setRequestMethod("GET"); //HTTP요청 발생
			conn.setRequestProperty("Content-type", "application/json");
			BufferedReader rd;
	    
			if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder(); 
			String line;
	    
			while ((line = rd.readLine()) != null) { //InputStream이 null이 아니라면 StringBuilder에 line을 추가
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			
			//parsing
			JSONParser parser = new JSONParser();
		
        	JSONObject weather_obj = (JSONObject)parser.parse(sb.toString());
        	
        	JSONObject response = (JSONObject)weather_obj.get("response");
        	JSONObject body = (JSONObject)response.get("body");
        	JSONObject items = (JSONObject)body.get("items");
        	JSONArray item = (JSONArray)items.get("item");
        	
        	for(int i=0;i<item.size();i++) {
        		JSONObject info=(JSONObject)item.get(i);
        		String category = (String)info.get("category");
        		String value = (String)info.get("fcstValue");
        		
        		if(category.equals("PTY")) {
        			if (value.equals("0")){ //비 x
        				ans =  "204,204,204";
        			} else if (value.equals("1")){ //비
        				ans = "126,134,138";        				
        			} else if (value.equals("2")){ //비+눈
        				ans = "221,221,221";
        			} else if (value.equals("3")){ //눈
        				ans = "255,255,255";
        			} else if (value.equals("4")){ //소나기
        				ans = "51,153,255";
        			} else if (value.equals("5")){ //빗방울
        				ans = "0,102,255";
        			} else if (value.equals("6")){ //빗방울+눈날림
        				ans = "0,204,255";
        			} else if (value.equals("7")){ //눈날림
        				ans = "0,255,255";
        			}
        		}
        	}
        } catch (Exception e) {
        	ans =  "204,204,204"; //default color
        }
        return ans;
	}
}



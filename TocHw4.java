import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;
import org.json.*;

class Road{
	String Name;
	ArrayList<Integer> Month;
	int DistMonth;
	int HighestPrice;
	int LowestPrice;
	
	public Road(String name,int month,int price){
		Name = name;
		DistMonth = 1;
		HighestPrice = price;
		LowestPrice = price;
		Month = new ArrayList<Integer>();
		Month.add(month);
	}

	public void add(int month,int price){
		if(!Month.contains(month)){
			Month.add(month);
			DistMonth += 1;
		}
		if(price > HighestPrice)
			HighestPrice = price;
		else if(price < LowestPrice)
			LowestPrice = price;
	} 
}

class TocHw4{
	public static void main(String args[]){
		// declaring variables
		String content;
		ArrayList<JSONObject> target = new ArrayList<JSONObject>();
		if(args.length == 0)return;

		// sending GET request
		try{
			content = sendGet(args[0]);
		}catch(Exception e){
			return;
		}

		// convert request result to JSON object
		JSONArray array = new JSONArray(content);
		Pattern regex = Pattern.compile("(.+路)|(.+大道)|(.+街)|(.+巷)");
		Matcher matcher;
		
		ArrayList<Road> roads = new ArrayList<Road>();
		ArrayList<String> roadList = new ArrayList<String>();
	
		JSONObject obj;
		for(int i=0;i<array.length();i++){
			obj = array.getJSONObject(i);
			matcher = regex.matcher(obj.getString("土地區段位置或建物區門牌"));
			if(matcher.find()){
				if(!roadList.contains(matcher.group())){
					roadList.add(matcher.group());
					roads.add(new Road(matcher.group(),obj.getInt("交易年月"),obj.getInt("總價元")));
				}
				else{
					Road r = roads.get(roadList.indexOf(matcher.group()));
					r.add(obj.getInt("交易年月"),obj.getInt("總價元"));
				}
			}
		}
		
		ArrayList<Road> result = new ArrayList<Road>();
		int max = 1;
		for(int i=0;i<roads.size();i++){
			if(roads.get(i).DistMonth > max)
				max = roads.get(i).DistMonth;
		}
		for(int i=0;i<roads.size();i++){
			if(roads.get(i).DistMonth == max)
				result.add(roads.get(i));
		}
		for(int i=0;i<result.size();i++){
			Road r = result.get(i);
			System.out.println(r.Name+", 最高成交價:"+r.HighestPrice+", 最低成交價:"+r.LowestPrice);
		}
		
		
	}
	private static String sendGet(String url) throws Exception{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
 
		int responseCode = con.getResponseCode();
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(),"UTF8"));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine+'\n');
		in.close();
 
		return response.toString();
	}
}

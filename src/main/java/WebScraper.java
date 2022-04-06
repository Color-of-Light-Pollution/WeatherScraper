import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author acelaena
 *
 * Web Scraper to grab weather data from Weather.com.
 */
public class WebScraper {
	/**
	 *  Static function to visit a specified Weather.com webpage to gather data.
	 */
	public static void getWeatherData() {
		URL url = null;
		try {
			url = new URL("https://weather.com/weather/today/l/69bedc6a5b6e977993fb3e5344e3c06d8bc36a1fb6754c3ddfb5310a3c6d6c87");
		} catch (MalformedURLException e1) {
			System.out.println("Bad URL. Cannot get data. Exiting.");
			return;
		}
		
		/* time this record was taken, accurate to the date and hour
		 * format: yyyy - MM - dd | HH:00 */
		String currentTime =  DateTimeFormatter.ofPattern("yyyy - MM - dd | HH:00").format(LocalDateTime.now());
		
		Document html;
		String innerHtml = "";
		List <String> weatherHtml = null;

		try {
			html = Jsoup.parse(url, 15000);
			innerHtml = html.select("section[data-testid=\"TodaysDetailsModule\"]").first().text();
			weatherHtml = html.select("div[data-cq-observe=\"true\"]").eachText();
		} catch (IOException e) {
			System.out.println("exception in getting html. Exiting.");
			e.printStackTrace();
			return;
		}
		
		//regex patterns to match with
		String [] patterns= new String [] { "Sun Rise \\d:\\d\\d [ap]m",
											"Sunset \\d:\\d\\d [ap]m",
											"Humidity \\d*%",
											"Dew Point \\d*°",
											"Visibility \\d* mi",
											"Wind Direction \\d* mph",
											"UV Index \\d* of 10",
											};
		
		String [] subPatterns = new String [] {"\\d:\\d\\d [ap]m", "\\d+"};
		
		Matcher matcher;
		ArrayList <String> matchedStrings = new ArrayList <String>();
		ArrayList <String> cleanedMatchedStrings = new ArrayList <String>();

		//temp & weather
		matcher = Pattern.compile("\\d*° .* Day").matcher(weatherHtml.get(0));
		if  (matcher.find()) {
			String chunk = matcher.group();
			cleanedMatchedStrings.add(chunk.substring(0,2));
			cleanedMatchedStrings.add(chunk.substring(4, chunk.length()-4));
		}
		//AQI -- 16
		matcher = Pattern.compile("Air Quality Index \\d*").matcher(weatherHtml.get(1));
		if  (matcher.find()) {
			cleanedMatchedStrings.add(matcher.group().substring(18));
		}
		//everything else
		String str, matched = "";
		for (int i = 0; i < patterns.length; i++) {
			str = patterns [i];
			matcher = Pattern.compile(str).matcher(innerHtml);
			
			if  (matcher.find()) {
				matched = matcher.group();
				matchedStrings.add(matched);
			}  else {
				System.out.println("Did not find matching pattern for "+ str);
			}
			
			if (i < 2) {
				matcher = Pattern.compile(subPatterns[0]).matcher(matched);
			} else {
				matcher = Pattern.compile(subPatterns[1]).matcher(matched);
			}
			
			if  (matcher.find()) {
				cleanedMatchedStrings.add(matcher.group());
			}  else {
				System.out.println("Did not find matching pattern for "+ str);
			}
		}
		
		

		/*
		 * Writing (appending) the scraped data to CSV.
		 * data:
		 * Date & time , temperature (F), weather condition, aqi, sunrise time, sunset time, humidity %,
		 * dew point (f), visibility range, wind speed, uv index
		 * 
		 * I expect only the weather condition + aqi points to be relevant.
		 */
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File ("WebData.csv"), true))) {
			writer.write(currentTime);
			
			for (String clean : cleanedMatchedStrings) {
				writer.write(",");
				writer.write(clean);
			}
			
			writer.write("\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(currentTime + ", " + matchedStrings);
		
		
	}
	
	

}

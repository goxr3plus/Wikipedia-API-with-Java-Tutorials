package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WikipediaTalker {
	
	public static void main(String[] args) {
		//Create TextToSpeech
		TextToSpeech tts = new TextToSpeech();
		
		//Print all the available voices
		tts.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		
		// Setting the Current Voice
		tts.setVoice("cmu-rms-hsmm");
		
		//tts.speak("NBC", 2.0f, false, true);
		
		String encoding = "UTF-8";
		
		try {
			String searchText = "what is a fish ";
			
			searchText += " Wikipedia";
			Document google = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();
			
			String wikipediaURL = google.getElementsByTag("cite").get(0).text();
			System.out.println(wikipediaURL);
			
			String wikipediaApiJSON = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&explaintext=&titles="
					+ URLEncoder.encode(wikipediaURL.replaceAll("https://en.wikipedia.org/wiki/", ""), encoding);
			
			System.out.println(wikipediaApiJSON);
			
			//"extract":"
			HttpURLConnection httpcon = (HttpURLConnection) new URL(wikipediaApiJSON).openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
			
			//Read line by line
			String responseSB = in.lines().collect(Collectors.joining());
			in.close();
			
			String result = responseSB.split("\"extract\":\"")[1];
			System.out.println(result);
			
			//Speak
			tts.speak(result.substring(0, 100), 1.5f, false, true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

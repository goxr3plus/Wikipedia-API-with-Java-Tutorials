package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WikipediaTalker {
	
	//Create the Scanner Object that we need
	private static final Scanner scanner = new Scanner(System.in);
	private static final String encoding = "UTF-8";
	
	//Create TextToSpeech
	private static final TextToSpeech tts = new TextToSpeech();
	
	public static void main(String[] args) {
		
		//Print all the available voices
		tts.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		
		// Setting the Current Voice
		tts.setVoice("cmu-rms-hsmm");
		
		boolean exit = false;
		
		//Run until exit =true
		while (!exit) {
			
			try {
				//String searchText = "what is a fish ";
				
				//Wait for user response
				System.out.println("\n\nType something that you want me to search on the internet...");
				String nextLine = scanner.nextLine();
				if (nextLine == null)
					continue;
				tts.stopSpeaking();
				String searchText = nextLine + " wikipedia";
				System.out.println("Searching on the web....");
				
				//Search the google for Wikipedia Links
				Document google = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();
				
				//Get the first link about Wikipedia
				String wikipediaURL = google.getElementsByTag("cite").get(0).text();
				
				//Use Wikipedia API to get JSON File
				String wikipediaApiJSON = "https://www.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
						+ URLEncoder.encode(wikipediaURL.substring(wikipediaURL.lastIndexOf("/") + 1, wikipediaURL.length()), encoding);
				
				//Let's see what it found
				System.out.println(wikipediaURL);
				System.out.println(wikipediaApiJSON);
				
				//"extract":" the summary of the article
				HttpURLConnection httpcon = (HttpURLConnection) new URL(wikipediaApiJSON).openConnection();
				httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
				BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
				
				//Read line by line
				String responseSB = in.lines().collect(Collectors.joining());
				in.close();
				
				//Print the result for us to see
				//System.out.println(responseSB);
				String result = responseSB.split("extract\":\"")[1];
				//System.out.println(result);
				
				//Tell only the 150 first characters of the result
				String textToTell = result.length() > 250 ? result.substring(0, 250) : result;
				System.out.println(textToTell + "...");
				
				//Speak 
				tts.speak(textToTell, 1.5f, false, false);
				
			} catch (Exception ex) {
				ex.printStackTrace();
				//Speak 
				tts.speak("Please try searching something other", 1.5f, false, false);
			}
			
		}
		
	}
	
}

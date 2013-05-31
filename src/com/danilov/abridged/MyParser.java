package com.danilov.abridged;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class MyParser {
	
	public MyParser(){
		
	}
	
	private ArrayList<Book> books = new ArrayList<Book>();
	private ArrayList<String> pagedText = new ArrayList<String>();
	private String text = new String();
	
	public void parseSearch(String request){
		try {
			Document doc = Jsoup.connect(request).get();
			Elements results = doc.getElementsByClass("string_result_td");
			for(Element e : results){
				Elements links = e.getElementsByAttribute("href");
				Element tmpLink = links.get(0);
				String link = tmpLink.attr("abs:href");
				String bookName = tmpLink.ownText();
				Elements names = e.getElementsByAttribute("style");
				String authorName = names.get(0).ownText();
				Elements tmpDescription = e.getElementsByClass("description");
				String description = tmpDescription.get(0).ownText();
				books.add(new Book(authorName, bookName, link, description));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void parseText(String request){
		request = getCorrectLink(request);
		text = new String();
		try {
			Document doc = Jsoup.connect(request).get();
			Elements results = doc.getElementsByTag("pre");
			for(Element e : results){
				text = e.ownText();
				pageText(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void pageText(String text){
		if(text.length() > 7000){
			boolean flag = true;
			String leftText = text;
			while(flag){
				String tmp = leftText.substring(0, 7000);
				int index = tmp.lastIndexOf("\r\n");
				if(index == -1){
					index = tmp.length();
				}
				String newText = tmp.substring(0, index);
				pagedText.add(newText);
				leftText = leftText.substring(index, leftText.length());
				if(leftText.length() < 7000){
					flag = false;
					pagedText.add(leftText);
				}
			}
		}else{
			pagedText.add(text);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String getCorrectLink(String url){
		String link = "";
		String tmp = url.substring(7, url.length());
		int index = tmp.indexOf("/");
		String base = url.substring(0, 8 + index);
		link += base;
		while(index != -1){
			tmp = tmp.substring(index + 1, tmp.length());
			int tmpIndex = tmp.indexOf("/");
			index = tmpIndex;
			if(tmpIndex == -1){
				tmpIndex = tmp.length();
			}
			String notEncoded = tmp.substring(0, tmpIndex);
			String encoded = URLEncoder.encode(notEncoded);
			link += encoded + "/";
		}
		link = link.substring(0, link.length()-1);
		return link;
	}
	
	public String getTestText(){
		return text;
	}
	
	
	public ArrayList<Book> getBooks(){
		return books;
	}
	
	public ArrayList<String> getText(){
		return pagedText;
	}
	
}

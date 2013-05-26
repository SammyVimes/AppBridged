package com.danilov.abridged;

import java.io.Serializable;

public class Book implements Serializable{

	private static final long serialVersionUID = 7526472295622776147L;
	private String author;
	private String bookName;
	private String link;
	private String description;
	
	public Book(String author, String bookName, String link, String description){
		this.author = author;
		this.bookName = bookName;
		this.link = link;
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public String getBookName() {
		return bookName;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		String tmp = description;
		if(description.length() > 50){
			tmp = description.substring(0, 49); 
		}
		tmp = tmp + "...";
		return tmp;
	}
	
	
	
	
}

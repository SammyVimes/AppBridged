package com.danilov.abridged;

import java.util.ArrayList;

import android.content.Context;
import android.content.IntentFilter.AuthorityEntry;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends BaseAdapter  {
	
	private int size;
	private LayoutInflater inflater;
	private ArrayList<Book> books = new ArrayList<Book>();
	
	public MyArrayAdapter(Context context, ArrayList<Book> books){
		this.books = books;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		size = books.size();
	}
	

	@Override
	public int getCount() {
		return size;
	}

	@Override
	public Book getItem(int position) {
		return books.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Автоматически созданная заглушка метода
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewBag vb;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item, null);
			vb = new ViewBag();
			vb.authorAndBookName = (TextView)convertView.findViewById(R.id.textAuthorAndBook);
			vb.description = (TextView)convertView.findViewById(R.id.textDescription);
			convertView.setTag(vb);
		}else{
			vb = (ViewBag)convertView.getTag();
		}
		Book book = books.get(position);
		String authorAndBookName = book.getAuthor() + " - " + book.getBookName();
		String description = book.getDescription();
		vb.authorAndBookName.setText(authorAndBookName);
		vb.description.setText(description);
		return convertView;
	}



	private class ViewBag{
		TextView authorAndBookName;
		TextView description;
	}
}

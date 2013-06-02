package com.danilov.abridged;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class AbridgedActivity extends Activity {

	public static final String LINK = "link";
	
	private static final String TEXT_ARRAY = "TEXT_ARRAY";
	private static final String CURRENT_PAGE = "CURRENT_PAGE";
	private TextView tv;
	private ProgressBar progressBar;
	private ArrayList<String> text = new ArrayList<String>();
	private Button buttonPrevBot;
	private Button buttonNextBot;
	private int curPage;
	private String testText = new String();
	private int curLastSymbolIndex = 0;
	private int curFirstSymbolIndex = 0;
	private boolean hasPrevPage = false;
	private boolean hasNextPage = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abridged);
		tv = (TextView)findViewById(R.id.text);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		buttonPrevBot = (Button)findViewById(R.id.buttonPrevBot);
		buttonNextBot = (Button)findViewById(R.id.buttonNextBot);
		MyOnClickListener listener = new MyOnClickListener();
		buttonPrevBot.setOnClickListener(listener);
		buttonNextBot.setOnClickListener(listener);
		if(savedInstanceState == null){
			String link = getIntent().getStringExtra(LINK);
			new ResultTask().execute(link);
		}else{
			text = (ArrayList<String>) savedInstanceState.getSerializable(TEXT_ARRAY);
			curPage = savedInstanceState.getInt(CURRENT_PAGE);
		}
	}
	
class ResultTask extends AsyncTask<String, Void, String>{
    	
    	String request = "";
    	
    	@Override
        protected void onPreExecute() {
    		tv.setVisibility(View.INVISIBLE);
    		progressBar.setVisibility(View.VISIBLE);
        }

		@Override
		protected String doInBackground(String... params) {
			MyParser p = new MyParser();
			p.parseText(params[0]);
			text = p.getText();
			testText = p.getTestText();
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			nextPage();
			handleButtonState();
			tv.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
	    }
    }

	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			boolean changed = false;
			switch(v.getId()){
			case R.id.buttonPrevBot:
				prevPage();
				break;
			case R.id.buttonNextBot:
				nextPage();
				break;
			}
			handleButtonState();
		}
		
	}


	private void handleButtonState(){
		if(!hasPrevPage){
			buttonPrevBot.setVisibility(View.INVISIBLE);
		}else{
			buttonPrevBot.setVisibility(View.VISIBLE);
		}
		if(!hasNextPage){
			buttonNextBot.setVisibility(View.INVISIBLE);
		}else{
			buttonNextBot.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void nextPage(){
		String curText = testText.substring(curLastSymbolIndex);
		int totalLine = tv.getHeight() / tv.getLineHeight();
		String textToBeShown = new String();
		for(int i = 0; i < totalLine; i++){
			int number = tv.getPaint().breakText(curText, 0, curText.length(), true,
                    tv.getWidth(), null);
			String tmp = curText.substring(0, number);
			textToBeShown += tmp;
			curText = curText.substring(number);
			i += quantityOfEnters(tmp);
		}
		int lastSpaceIndex = textToBeShown.lastIndexOf(' ');
		if(lastSpaceIndex != -1){
			textToBeShown = textToBeShown.substring(0, lastSpaceIndex);
		}
		tv.setText(textToBeShown);
		curFirstSymbolIndex = curLastSymbolIndex;
		curLastSymbolIndex = curFirstSymbolIndex + textToBeShown.length() + 1;
		if(curFirstSymbolIndex > 0){
			hasPrevPage = true;
		}else{
			hasPrevPage = false;
		}
		if(curLastSymbolIndex < testText.length()){
			hasNextPage = true;
		}else{
			hasNextPage = false;
		}
	}
	
	private void prevPage(){
		ArrayList<String> lines = new ArrayList<String>();
		String curText = testText.substring(0, curFirstSymbolIndex);
		int totalLine = tv.getHeight() / tv.getLineHeight();
		String textToBeShown = new String();
		String inversedString = getInversedString(curText);
		for(int i = 0; i < totalLine; i++){
			int number = tv.getPaint().breakText(inversedString, 0, inversedString.length(), true,
                    tv.getWidth(), null);
			String tmp = curText.substring(curText.length() - number);
			lines.add(tmp);
			curText = curText.substring(0, curText.length() - number);
			inversedString = inversedString.substring(number);
			i += quantityOfEnters(tmp);
		}
		textToBeShown = inversedListToString(lines);
		int firstSpaceIndex = textToBeShown.indexOf(' ');
		if(firstSpaceIndex != -1 && firstSpaceIndex < textToBeShown.length() - 1){
			textToBeShown = textToBeShown.substring(firstSpaceIndex + 1);
		}
		tv.setText(textToBeShown);
		curLastSymbolIndex = curFirstSymbolIndex;
		curFirstSymbolIndex = curFirstSymbolIndex - textToBeShown.length();
		if(curFirstSymbolIndex > 0){
			hasPrevPage = true;
		}else{
			hasPrevPage = false;
		}
		if(curLastSymbolIndex < testText.length()){
			hasNextPage = true;
		}else{
			hasNextPage = false;
		}
	}
	
	private static int quantityOfEnters(String line){
		int quantity = 0;
		int index = line.indexOf("\n");
		while(index != -1){
			quantity++;
			line = line.substring(index + 1);
			index = line.indexOf("\n");
		}
		return quantity;
	}
	
	private static String getInversedString(String normalString){
		String inversedString = new String();
		int count = 0;
		for(int i = normalString.length() - 1; i >= 0; i--){
			count++;
			inversedString += normalString.charAt(i);
			if(count > 2000){
				break;
			}
		}
		return inversedString;
	}
	
	private static String inversedListToString(ArrayList<String> list){
		String result = new String();
		for(int i = list.size() - 1; i >= 0; i--){
			result += list.get(i);
		}
		return result;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.abridged, menu);
		return true;
	}
	
	@Override
    public void onSaveInstanceState(Bundle bundle){
    	super.onSaveInstanceState(bundle);
    	if(text != null){
    		bundle.putSerializable(TEXT_ARRAY, text);
    		bundle.putInt(CURRENT_PAGE, curPage);
    	}
    	super.onSaveInstanceState(bundle);
    }
	

}

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
	private ScrollView scrollView;
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
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		if(savedInstanceState == null){
			String link = getIntent().getStringExtra(LINK);
			new ResultTask().execute(link);
		}else{
			text = (ArrayList<String>) savedInstanceState.getSerializable(TEXT_ARRAY);
			curPage = savedInstanceState.getInt(CURRENT_PAGE);
			loadCurPage();
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
	
	private void loadCurPage(){
		tv.setText(text.get(curPage));
		handleButtonState();
		scrollView.smoothScrollTo(0, 0);
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
		int index = 0;
		boolean flag = false;
		String tmp;
		int lastSymbol = 0;
		String newText = new String();
		while(!flag){
			while(curText.charAt(index) != ' '){
				index++;
				if(index >= curText.length() - 1){
					newText = curText;
					curFirstSymbolIndex = curLastSymbolIndex;
					curLastSymbolIndex = curText.length() - 1;
					break;
				}
			}
			index++;
			tmp = curText.substring(0, index);
			if(isTextTooLong(tmp)){
				lastSymbol = tmp.lastIndexOf(' ');
				newText = curText.substring(0, lastSymbol);
				curFirstSymbolIndex = curLastSymbolIndex;
				curLastSymbolIndex = lastSymbol;
				flag = true;
			}
		}
		tv.setText(newText);
		if(curLastSymbolIndex <= curText.length() - 1){
			hasNextPage = true;
		}else{
			hasNextPage = false;
		}
		if(curFirstSymbolIndex > 0){
			hasPrevPage = true;
		}else{
			hasPrevPage = false;
		}
	}
	
	private void prevPage(){
		String curText = testText.substring(0, curFirstSymbolIndex);
		int index = curFirstSymbolIndex - 1;
		boolean flag = false;
		String tmp;
		int firstSymbol = 0;
		String newText = new String();
		while(!flag){
			while(curText.charAt(index) != ' '){
				index--;
				if(index < 0){
					newText = curText;
					curFirstSymbolIndex = 0;
					curLastSymbolIndex = curFirstSymbolIndex;
					break;
				}
			}
			index--;
			tmp = curText.substring(index);
			if(isTextTooLong(tmp)){
				firstSymbol = tmp.indexOf(' ');
				newText = curText.substring(firstSymbol, curFirstSymbolIndex);
				curLastSymbolIndex = curFirstSymbolIndex;
				curFirstSymbolIndex = firstSymbol;
				flag = true;
			}
		}
		tv.setText(newText);
		if(curFirstSymbolIndex > 0){
			hasPrevPage = true;
		}else{
			hasPrevPage = false;
		}
	}
	
	
	private boolean isTextTooLong(String newText){
		boolean isTooLong = false;
		float textSize = tv.getTextSize();
		Display mDisplay= getWindowManager().getDefaultDisplay();
		int deviceWidth= mDisplay.getWidth();
		int textHeight = getHeight(getBaseContext()	, newText, textSize, deviceWidth);
		View v = (View) tv.getParent().getParent();
		isTooLong = textHeight > v.getMeasuredHeight();
		return isTooLong;
	}
	
	public static int getHeight(Context context, String text, float textSize, int deviceWidth) {
	    TextView textView = new TextView(context);
	    textView.setText(text);
	    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.AT_MOST);
	    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	    textView.measure(widthMeasureSpec, heightMeasureSpec);
	    return textView.getMeasuredHeight();
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

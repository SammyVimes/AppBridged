package com.danilov.abridged;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			curPage = 0;
			if(text.size() > 0){
				tv.setText(text.get(curPage));
				tv.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
				handleButtonState();
			}
	    }
    }

	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			boolean changed = false;
			switch(v.getId()){
			case R.id.buttonPrevBot:
				if(curPage > 0){
					curPage--;
					changed = true;
				}
				break;
			case R.id.buttonNextBot:
				if(curPage < text.size() - 1){
					curPage++;
					changed = true;
				}
				break;
			}
			if(changed){
				loadCurPage();
			}
		}
		
	}
	
	private void loadCurPage(){
		tv.setText(text.get(curPage));
		handleButtonState();
		scrollView.smoothScrollTo(0, 0);
	}

	private void handleButtonState(){
		if(curPage == 0){
			buttonPrevBot.setVisibility(View.INVISIBLE);
		}else{
			buttonPrevBot.setVisibility(View.VISIBLE);
		}
		if(curPage == text.size() - 1){
			buttonNextBot.setVisibility(View.INVISIBLE);
		}else{
			buttonNextBot.setVisibility(View.VISIBLE);
		}
		
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

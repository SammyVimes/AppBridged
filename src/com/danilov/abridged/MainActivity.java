package com.danilov.abridged;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	public static final String BASE_ADDRESS = "http://pokoroche.ru/search.php?q=";
	private static final String BOOKS_ARRAY = "BOOKS_ARRAY";
	
	private EditText et;
	private String encodedQuery;
	private MyArrayAdapter adapter;
	private ListView lv;
	private ProgressBar progressBar;
	private ArrayList<Book> books = new ArrayList<Book>();
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText)findViewById(R.id.editText1);
        Button b = (Button)findViewById(R.id.button1);
        lv = (ListView)findViewById(R.id.listView1);
        lv.setOnItemClickListener(new MyClickListener());
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					test();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        if(savedInstanceState != null){
        	books = (ArrayList<Book>)savedInstanceState.getSerializable(BOOKS_ARRAY);
        	adapter = new MyArrayAdapter(getBaseContext(), books);
  	      	lv.setAdapter(adapter);
  	      	lv.setVisibility(View.VISIBLE);
        }
    }
   
    private void test() throws ClientProtocolException, IOException{
    	ResultTask task = new ResultTask();
    	task.execute();
    }
    
    private class MyClickListener implements OnItemClickListener{
    	
		@Override
		public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
				long arg3) {
			startAbridgedActivity(adapter.getItem(position).getLink());
		}
    	
    }
    
    private void startAbridgedActivity(String string){
    	Intent intent = new Intent(this, AbridgedActivity.class);
    	intent.putExtra(AbridgedActivity.LINK, string);
    	startActivity(intent);
    }
    
    
    class ResultTask extends AsyncTask<Void, Void, String>{
    	
    	String request = "";
    	
    	@SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
          super.onPreExecute();
          String query = et.getText().toString();
          encodedQuery = URLEncoder.encode(query);
          request = new String(BASE_ADDRESS + encodedQuery);
          progressBar.setVisibility(View.VISIBLE);
        }

		@Override
		protected String doInBackground(Void... params) {
			MyParser p = new MyParser();
			p.parseSearch(request);
			books = p.getBooks();
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
	      adapter = new MyArrayAdapter(getBaseContext(), books);
	      lv.setAdapter(adapter);
	      lv.setVisibility(View.VISIBLE);
	      progressBar.setVisibility(View.INVISIBLE);
	    }
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onSaveInstanceState(Bundle bundle){
    	if(books != null && books.size() > 0){
    		bundle.putSerializable(BOOKS_ARRAY, books);
    	}
    	super.onSaveInstanceState(bundle);
    }
    
}

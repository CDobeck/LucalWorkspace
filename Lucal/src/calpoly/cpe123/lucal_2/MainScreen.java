package calpoly.cpe123.lucal_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainScreen extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	ImageButton playButton = (ImageButton)findViewById(R.id.playButton);
		
    playButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        	
        	Intent startGame = new Intent(MainScreen.this, LucalActivity_v2.class);
        	startActivity(startGame);
        	finish();
        }
   });
    
    ImageButton helpButton = (ImageButton)findViewById(R.id.helpButton);
    
    helpButton.setOnClickListener(new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		
    		Intent helpScreen = new Intent(MainScreen.this, HelpScreen.class);
    		startActivity(helpScreen);
    	}
    });
    
    ImageButton creditsButton = (ImageButton)findViewById(R.id.creditsButton);
    
    creditsButton.setOnClickListener(new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		Intent creditsScreen = new Intent(MainScreen.this, Credits.class);
    		startActivity(creditsScreen); 
    	}
    }); 
	}

}

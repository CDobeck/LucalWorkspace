package calpoly.cpe123.lucal_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class GameOver extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gameover); 
		
	ImageButton replayButton = (ImageButton)findViewById(R.id.replayButton);
	
	replayButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent gameOverScreen = new Intent(GameOver.this, MainScreen.class); 
			startActivity(gameOverScreen);
			finish();
		}
	
		
	});
		
	}}

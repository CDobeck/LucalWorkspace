package calpoly.cpe123.lucal_2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Credits extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credits); 
		
	ImageButton creditsButton = (ImageButton)findViewById(R.id.creditsButton);
	
	creditsButton.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
		finish(); 
	}
		
	});
		
	}

}

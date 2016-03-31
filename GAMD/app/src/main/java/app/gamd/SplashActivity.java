package app.gamd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    for (int i=1; i<3;i++){
                        sleep(1000);
                        progressBar.setProgress(i);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                    Toast toastMessage = Toast.makeText(getApplicationContext(), "ERROR_NO_CONTROLADO", Toast.LENGTH_SHORT);
                    toastMessage.show();
                }finally{
                    Intent intentMenu = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intentMenu);
                    finish();
                }
            }
        };
        timerThread.start();
    }
}

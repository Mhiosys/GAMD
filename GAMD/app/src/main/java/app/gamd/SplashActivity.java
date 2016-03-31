package app.gamd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.gamd.common.Constantes;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    for (int i=1; i<4;i++){
                        sleep(1000);
                        progressBar.setProgress(i);
                    }
                }catch(InterruptedException e){
                    Log.d(TAG, e.getMessage());
                    Toast toastMessage = Toast.makeText(getApplicationContext(), Constantes.ERROR_NO_CONTROLADO, Toast.LENGTH_SHORT);
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

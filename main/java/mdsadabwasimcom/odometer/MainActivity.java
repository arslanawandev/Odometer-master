package mdsadabwasimcom.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
    private  OdometerService odometer;
    private Boolean bound=false;
    private Boolean wasRunning=true;
    TextView distanceView;



    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
           OdometerService.OdometerBinder odometerBinder= (OdometerService.OdometerBinder) binder;
            odometer = odometerBinder.getOdometer();
            bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
   bound = false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         distanceView= (TextView) findViewById(R.id.distanceView);
        //intialize buttons and set onclicklistener.
        Button startBtn= (Button) findViewById(R.id.startbtn);
        startBtn.setOnClickListener(this);
        Button stopBtn= (Button) findViewById(R.id.stopbtn);
        stopBtn.setOnClickListener(this);
        Button resetBtn= (Button) findViewById(R.id.resetbtn);
        resetBtn.setOnClickListener(this);

        watchMileage();

    }


    private void watchMileage() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance =0.0;
                if(odometer!=null) {
                    distance = odometer.getKm();
                }
                String distanceStr = String.format("%1$,.2f km", distance);
                doIt(distanceStr);
                handler.postDelayed(this,1000);
            }
        });

    }
    public void doIt(String distanceValue){
        if (wasRunning) {
            distanceView.setText(distanceValue);
        }else{
            return ;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound){
            unbindService(connection);
            bound=false;
        }
    }

    @Override
    public void onClick(View v) {
      switch(v.getId()){
          case R.id.startbtn:
              wasRunning=true;
              break;
          case R.id.stopbtn:
              wasRunning=false;
              break;
          case R.id.resetbtn:
              Double aDouble=0.0;
              wasRunning=false;
              String format = String.format("%1$,.2f km",aDouble);
              doIt(format);
              break;

      }

    }


}

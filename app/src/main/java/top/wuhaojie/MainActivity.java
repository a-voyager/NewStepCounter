package top.wuhaojie;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MAIN";
    private TextView tv_info;
    private StepCounter stepCounter;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
        initSensor();

    }

    private void initSensor() {
        stepCounter = StepCounter.getInstance(MainActivity.this);
        boolean register = stepCounter.register();
        Log.e(TAG, "register = " + register);
    }

    private void initView() {
        tv_info = (TextView) findViewById(R.id.tv_info);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_start.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                initSensor();
                stepCounter.start();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        final int stepNum = stepCounter.getmStepNum();
                        final float orientation = stepCounter.getmOrientation();
                        final float pressure = stepCounter.getmPressure();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_info.setText("步数: " + stepNum + "\t角度: " + orientation + "\t气压: " + pressure);
                            }
                        });
                    }
                };
                timer = new Timer();
                timer.schedule(timerTask, 0, 500);
                break;
            case R.id.btn_reset:
                stepCounter.reset();
                break;
            case R.id.btn_stop:
                if (timer != null)
                    timer.cancel();
                stepCounter.unRegister();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        stepCounter.unRegister();
        super.onDestroy();
    }
}

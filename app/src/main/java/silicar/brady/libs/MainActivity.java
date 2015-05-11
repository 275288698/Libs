package silicar.brady.libs;

import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import silicar.brady.libmp3lame.MP3Recorder;

import static android.os.SystemClock.sleep;


public class MainActivity extends ActionBarActivity {

    private LinearLayout linearLayout;
    private SparseArray<View> ViewSparse;
    private Button btnStart,btnStop;
    private TextView text1;
    private Thread thread;
    private Handler handler;
    //声明录音文件
    private MP3Recorder mRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        init();
        setOnClink();
    }

    private void findView()
    {
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);
        btnStart = new Button(this);
        btnStart.setText("Start");
        linearLayout.addView(btnStart);
        btnStop = new Button(this);
        btnStop.setText("Stop");
        linearLayout.addView(btnStop);
        text1 = (TextView)findViewById(R.id.text1);
    }

    private void init()
    {
        //创建录音文件
        mRecorder = new MP3Recorder(new File(Environment.getExternalStorageDirectory(),"test.mp3"));
        handler = new Handler();
        thread = new Thread(new Runnable() {
            @Override
            public void run()
            {while (true)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        text1.setText("" + mRecorder.getVolume());
                    }
                });
                sleep(100);
            }
            }
        });
        thread.start();
    }

    private void setOnClink()
    {
        //开始录音
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //停止录音
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder.stop();
            }
        });
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
}

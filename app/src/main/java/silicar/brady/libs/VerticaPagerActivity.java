package silicar.brady.libs;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import silicar.brady.view.VerticalPager;


public class VerticaPagerActivity extends ActionBarActivity {

    private VerticalPager verticalPager;
    private Button btnPrevious,btnNext;
    private RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertica_pager);
        findview();
        setOnClick();
    }

    private void findview() {
        LayoutInflater inflater = LayoutInflater.from(this);
        layout = (RelativeLayout) inflater.inflate(R.layout.layout, null);
        verticalPager = (VerticalPager)findViewById(R.id.view);
        verticalPager.addView(layout);
        btnPrevious = (Button)findViewById(R.id.previous);
        btnNext = (Button)findViewById(R.id.next);
    }

    private void setOnClick() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalPager.scrollPrevious();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalPager.scrollNext();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vertica_pager, menu);
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

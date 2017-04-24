package it.generaladapter.demo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Button mLinearVerticalBtn;
    private Button mLinearHorizontalBtn;
    private Button mGridViewBtn;
    private Button mStaggerdGridViewBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLinearVerticalBtn = (Button) findViewById(R.id.linear_vertical_btn);
        mLinearHorizontalBtn = (Button) findViewById(R.id.linear_horizontal_btn);
        mGridViewBtn = (Button) findViewById(R.id.gridview_btn);
        mStaggerdGridViewBtn = (Button) findViewById(R.id.staggered_gridview_btn);
        mLinearVerticalBtn.setOnClickListener(this);
        mLinearHorizontalBtn.setOnClickListener(this);
        mGridViewBtn.setOnClickListener(this);
        mStaggerdGridViewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_vertical_btn:
                startActivity(new Intent(MainActivity.this, LinearVerticalActivity.class));
                break;
            case R.id.linear_horizontal_btn:
                startActivity(new Intent(MainActivity.this, LinearHorizontalActivity.class));
                break;
            case R.id.gridview_btn:
                startActivity(new Intent(MainActivity.this, GridViewActivity.class));
                break;
            case R.id.staggered_gridview_btn:
                startActivity(new Intent(MainActivity.this, StaggeredGridViewActivity.class));
                break;
            default:
                break;
        }
    }
}

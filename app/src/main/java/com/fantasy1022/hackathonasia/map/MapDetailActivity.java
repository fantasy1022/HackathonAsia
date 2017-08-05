package com.fantasy1022.hackathonasia.map;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.fantasy1022.hackathonasia.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapDetailActivity extends AppCompatActivity {

    private static final String TAG = MapDetailActivity.class.getSimpleName();
    public static final String KEY_DETAIL_URL = "KEY_DETAIL_URL";
    public static final String KEY_INFO_TXT = "KEY_INFO_TXT";
    private String info;
    private int thumbNum;
    @BindView(R.id.detailImg)
    ImageView detailImg;
    @BindView(R.id.infoTxt)
    TextView infoTxt;
    @BindView(R.id.thunmbImg)
    ImageView thunmbImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra(KEY_DETAIL_URL);
        Picasso.with(this)
                .load(url)
                .fit()
                .centerCrop()
                .into(detailImg);
        info = getIntent().getStringExtra(KEY_INFO_TXT);
        //thumbNum = Integer.parseInt((info.substring(index + 5, indexSolve).replaceAll("\"","")));

        infoTxt.setText(info);
    }

    public static void newIntent(Activity activity, String url, String info) {
        Intent intent = new Intent(activity, MapDetailActivity.class);
        intent.putExtra(KEY_DETAIL_URL, url);
        intent.putExtra(KEY_INFO_TXT, info);
        activity.startActivity(intent);
    }

}


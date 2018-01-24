package com.innovation.neha.tracklocation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.neha.tracklocation.R;

public class DialogActivity extends AppCompatActivity {

    ImageView dialog_image;
    TextView dialog_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

       // dialog_image=(ImageView)findViewById(R.id.img_dialog);
        dialog_text=(TextView)findViewById(R.id.tv_dialog);

    }
}

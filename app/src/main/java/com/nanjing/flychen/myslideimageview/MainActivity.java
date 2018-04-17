package com.nanjing.flychen.myslideimageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Bitmap> list;
    private SlideImageView slide_imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slide_imageview = ((SlideImageView) findViewById(R.id.slide_imageview));
        list=new ArrayList<>();
        managelvAskitem();
    }
    private void managelvAskitem() {
        Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_home_banner_2);
        Bitmap bitmap2 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_home_banner_3);
        Bitmap bitmap3 = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_home_banner_4);
        list.add(bitmap1);
        list.add(bitmap2);
        list.add(bitmap3);
        slide_imageview.setBitmaps(list);
    }
}

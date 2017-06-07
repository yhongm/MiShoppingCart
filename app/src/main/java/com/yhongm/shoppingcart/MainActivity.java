package com.yhongm.shoppingcart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ShoppingCartViewGroup.ShoppingCartListener {
    ShoppingCartViewGroup scvg;
    private ViewGroup anim_mask_layout;//动画层private ViewGroup anim_mask_layout;//动画层
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scvg= (ShoppingCartViewGroup) findViewById(R.id.vg);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.mi5);
        scvg.setProductBitmap(bitmap);
        scvg.setShoppingCartListener(this);
    }

    @Override
    public void add(int num) {
    }

    @Override
    public void love(boolean isLove) {
        Toast.makeText(this, isLove?"点击了喜欢":"取消了喜欢", Toast.LENGTH_SHORT).show();
    }
}

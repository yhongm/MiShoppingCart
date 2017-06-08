package com.yhongm.mishoppingcart_demo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yhongm.shoppingcart.ShoppingCartView;

public class MainActivity extends AppCompatActivity implements ShoppingCartView.ShoppingCartListener {
    ShoppingCartView scvg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yhongm.shoppingcart.R.layout.activity_main);
        scvg= (ShoppingCartView) findViewById(com.yhongm.shoppingcart.R.id.vg);
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.mi6);
        Bitmap bitmap = drawable.getBitmap();
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

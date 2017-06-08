# 仿小米商城购物车
## 通过自定义view,自定义viewGroup仿小米商城购物车动画效果
## 演示效果》
<img src="/preview/demo.gif">

## 使用方法
## 1.布局文件添加以下属性
### <com.yhongm.shoppingcart.ShoppingCartView
###        android:id="@+id/vg"
###        android:layout_alignParentBottom="true"
###        android:layout_width="match_parent"
###        android:layout_height="300dp" />

## 2.java方法：
### scvg.setProductBitmap(产品图片bitmap); 设置产品图片
### scvg.setShoppingCartListener(this); 设置点击监听 
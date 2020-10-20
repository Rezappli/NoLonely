package com.example.nalone;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class CustomToast{

    private Toast toast;
    private boolean isShow = false;

    public CustomToast(Context context, String text, boolean centerToast, boolean centerText) {
        this.toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        if(centerText) {
            LinearLayout layout = (LinearLayout) toast.getView();
            if (layout.getChildCount() > 0) {
                TextView tv = (TextView) layout.getChildAt(0);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            }
        }

        if(centerToast) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setText(text);
    }

    public void show(){
        isShow = true;
        toast.show();
    }

    public boolean isShow(){
        return isShow;
    }

    public void setShow(boolean show){
        isShow = show;
    }
}

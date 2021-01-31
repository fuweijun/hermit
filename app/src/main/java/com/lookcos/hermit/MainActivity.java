package com.lookcos.hermit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static ClipboardManager cm;
    private static Context context;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerManager sm = new ServerManager(this);
        sm.startServer();
        context = this;
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

    }

    /*
    * 检查权限
    * */
    @Override
    protected void onResume() {
        super.onResume();
        if (!AccessbilityCore.isStart()) {
            Toast toast = Toast.makeText(this,"请注意检查无障碍权限", Toast.LENGTH_SHORT);
            toast.show();
            try {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static String getCliBoardText(){
        ClipData data = cm.getPrimaryClip();
        String content = null;
        if(data != null){
            ClipData.Item item = data.getItemAt(0);
            content = item.getText().toString();
        }
        return content;
    }
    public static int getScreenHeight(){
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(){
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static void setCliBoardText(String text){
        ClipData data = ClipData.newPlainText("text", text);
        cm.setPrimaryClip(data);
        return ;
    }
}
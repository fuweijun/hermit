package com.lookcos.hermit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static ClipboardManager cm;
    public static Context context;
    private static int dwidth;
    private static int dheight;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerManager sm = new ServerManager(this);
        sm.startServer();

        context = this;
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 申请 存储权限与无障碍权限
        boolean isGrantExternalRW = Utils.isGrantExternalRW(this, 1);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        dwidth = dm.widthPixels;
        dheight = dm.heightPixels;
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

    public static void setCliBoardText(String text){
        ClipData data = ClipData.newPlainText("text", text);
        cm.setPrimaryClip(data);
        return ;
    }
    public static int getScreenHeight(){
        return dheight;
    }

    public static int getScreenWidth(){
        return dwidth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200) {
            AccessbilityCore.result = resultCode;
            AccessbilityCore.intentData = data;
        }
    }
}
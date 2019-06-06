package com.cn.banpai;

import android.app.smdt.SmdtManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    String keyContent = "";//仿真键盘输出内容,最终读取的卡号
    private TextView tvInfo;
    private TextView tvVersion;
    private Button mBtnUpdate;
    private Button btnDefine;
    private EditText mOffTime;
    private EditText mOnTime;
    private EditText mCertain;
    SmdtManager smdtManager = SmdtManager.create(MainActivity.this);//初始化依赖库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.text);
        tvVersion = findViewById(R.id.version);
        mBtnUpdate = findViewById(R.id.btn_update);
        btnDefine = findViewById(R.id.btn_define);
        mOffTime = findViewById(R.id.offTime);
        mOnTime = findViewById(R.id.onTime);
        mCertain = findViewById(R.id.certain);

        smdtManager.smdtSetStatusBar(MainActivity.this,false);//去掉状态栏
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //path为apk存放路径
                smdtManager.smdtSilentInstall("/mnt/sdcard/test.apk",getApplicationContext());//静默安装
            }
        });

        btnDefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置定时开关机，没有特别精确，开关机时间会根据设置的时间延后2-5分钟
                String offTime = mOffTime.getText().toString();
                String onTime = mOnTime.getText().toString();
                String enable = mCertain.getText().toString().trim();
                if(!(offTime.equals("") && onTime.equals("") && enable.equals(""))){
                    smdtManager.smdtSetTimingSwitchMachine(offTime, onTime, enable);
                }else{
                    Toast.makeText(getApplicationContext(), "please input shutdown time", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        packageName(MainActivity.this);

    }
//
//    public String packageName(Context context) { PackageManager manager = context.getPackageManager();
//        String name = null;
//        try {
//            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//            name = info.versionName;
//            tvVersion.setText("V:"+name);
//        } catch (PackageManager.NameNotFoundException e) { e.printStackTrace();
//        }
//        return name;
//    }

    /*
     * 以下为识别卡号并打印的过程，输出的是keyContent
     **/

    boolean fGet = true;//是否要取的标志。由于输出的值会重复发送一次，为防止重复接收，设置一个标志来判断是否要接收

    //将键值转成对应的ASCII码
    public char GetCharByKeyCode(int keyCode) {
        char outChar = 0;


        if(keyCode > 6 && keyCode < 17)//数字
            outChar = (char)((keyCode -7) + 0x30);

        if(keyCode > 28 && keyCode < 55)//字母
            outChar = (char)((keyCode - 29) + 0x41);

        return outChar;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {//在这里监控键盘输出的键值

        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if(keyContent.length()> 0)
                if(keyContent.equals("1694470533")){
                    tvInfo.setText("登陆成功，欢迎您！");
                } else {
                    tvInfo.setText("此卡无效，请换一张！");
                }

            keyContent = "";
        }
        else {
            int keyCode = event.getKeyCode();
            if(true == fGet) {
                keyContent += GetCharByKeyCode(keyCode);
                fGet = false;
            }else fGet = true;
        }
        return super.dispatchKeyEvent(event);
    }
}



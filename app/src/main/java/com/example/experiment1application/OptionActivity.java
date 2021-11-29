package com.example.experiment1application;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OptionActivity extends AppCompatActivity {

    //导入菜单布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.binary_men,menu);
        return true;
    }

    // 创建菜单项点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_help:
                Toast.makeText(this,"点击了帮助",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_exit:
                android.os.Process.killProcess(android.os.Process.myPid());//获取pid
                System.exit(0);
                break;
            case R.id.action_calculator:
                item.setIntent(new Intent(OptionActivity.this,MainActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //导入进制布局

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        Button buttonBtoH=(Button)findViewById(R.id.two_16);
        Button buttonHtoB=(Button)findViewById(R.id.sixteen_two);
        Button buttonTtoB=(Button)findViewById(R.id.ten_two);
        Button buttonTtoH=(Button)findViewById(R.id.ten_16);
        EditText num=findViewById(R.id.num);
        EditText result=findViewById(R.id.result);

        buttonBtoH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=num.getText().toString();
                result.setText(BtoH(str));
            }
        });
        buttonHtoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=num.getText().toString();
                result.setText(HtoB(str));
            }
        });
        buttonTtoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=num.getText().toString();

                result.setText(TtoB(str));
            }
        });
        buttonTtoH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=num.getText().toString();

                result.setText(TtoH(str));
            }
        });


    }

    /*十六进制转二进制*/
    public String HtoB(String s){
        String b=Integer.toBinaryString(Integer.valueOf(toD(s,16)));
        return b;
    }

    /*二进制转为十六进制*/
    public String BtoH(String s){
        //将二进制转为十进制再从十进制转为十六进制
        String b=Integer.toHexString(Integer.valueOf(toD(s,2)));
        return b;
    }
    /*十进制转为二进制*/
    public String TtoB(String s){
        String b=Integer.toBinaryString(Integer.parseInt(s));
        return b;
    }

    /*十进制转为十六进制*/
    public String TtoH(String s){
        String b=Integer.toHexString(Integer.parseInt(s));
        return b;
    }

    /*任意进制数转为十进制数*/
    public String toD(String s,int a){
        int r=0;
        for(int i=0;i<s.length();i++){
            r=(int)(r+formatting(s.substring(i,i+1))*Math.pow(a,s.length()-i-1));
        }
        return String.valueOf(r);
    }
    /*二进制转为十进制*/
    public String formattingT(String s){
        String b=String.valueOf(Integer.parseInt(s,2));
        return b;
    }


    /*十六进制中字母转为对应十进制数字*/
    public int formatting(String s){
        int i=0;
        for(int j=0;j<10;j++) {
            if (s.equals(String.valueOf(j))) {
                i = j;
            }
        }
        if(s.equals("a")){
            i=10;
        }
        if(s.equals("b")){
            i=11;
        }
        if(s.equals("c")){
            i=12;
        }
        if(s.equals("d")){
            i=13;
        }
        if(s.equals("e")){
            i=14;
        }
        if(s.equals("f")){
            i=15;
        }
        return i;
    }

    /*十进制数字转为十六进制字母*/
    public String formattingH(int a){
        String i=String.valueOf(a);
        switch (a){
            case 10:
                i="a";
                break;
            case 11:
                i="b";
                break;
            case 12:
                i="c";
                break;
            case 13:
                i="d";
                break;
            case 14:
                i="e";
                break;
            case 15:
                i="f";
                break;
        }
        return i;
    }
}

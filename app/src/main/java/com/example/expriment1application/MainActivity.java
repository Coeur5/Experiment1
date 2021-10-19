package com.example.expriment1application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String operator="";//运算符号
    private String firstnum="";//第一个数
    private String secondnum="";//下一个数
    private String result="";//结果
    private String showText="";
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_port);
        Button button_CE=findViewById(R.id.btn_CE);
        Button button_C=findViewById(R.id.btn_C);
        Button button_0=findViewById(R.id.btn_0);
        Button button_1=findViewById(R.id.btn_1);
        Button button_2=findViewById(R.id.btn_2);
        Button button_3=findViewById(R.id.btn_3);
        Button button_4=findViewById(R.id.btn_4);
        Button button_5=findViewById(R.id.btn_5);
        Button button_6=findViewById(R.id.btn_6);
        Button button_7=findViewById(R.id.btn_7);
        Button button_8=findViewById(R.id.btn_8);
        Button button_9=findViewById(R.id.btn_9);
        Button button_equal=findViewById(R.id.btn_equal);
        Button button_squareroot=findViewById(R.id.btn_squareroot);
        Button button_add=findViewById(R.id.btn_add);
        Button button_divide=findViewById(R.id.btn_divide);
        Button button_multiple=findViewById(R.id.btn_multiple);
        Button button_point=findViewById(R.id.btn_point);
        Button button_reduce=findViewById(R.id.btn_reduce);

        textView=findViewById(R.id.tv_shownumber);

        button_CE.setOnClickListener(this);
        button_C.setOnClickListener(this);
        button_0.setOnClickListener(this);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        button_7.setOnClickListener(this);
        button_8.setOnClickListener(this);
        button_9.setOnClickListener(this);
        button_equal.setOnClickListener(this);
        button_add.setOnClickListener(this);
        button_multiple.setOnClickListener(this);
        button_reduce.setOnClickListener(this);
        button_point.setOnClickListener(this);
        button_squareroot.setOnClickListener(this);
        button_divide.setOnClickListener(this);

        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
    }

    private boolean calculate(){
        BigDecimal num1=new BigDecimal(this.firstnum);
        BigDecimal num2=new BigDecimal(this.secondnum);
        if(operator.equals("+")){
            result=num1.add(num2).toString();
        }else if(operator.equals("-")){
            result=num1.subtract(num2).toString();
        }else if(operator.equals("×")){
            result=num1.multiply(num2).toString();
        }else if(operator.equals("÷")){
            if(secondnum.equals("0")){
                Toast.makeText(this,"被除数不能为0！",Toast.LENGTH_LONG).show();
                return false;
            }else{
                result=num1.divide(num2).toString();
            }
        }
        firstnum=result;
        secondnum="";
        return true;
    }
    private void clear(String text){
        showText=text;
        textView.setText(showText);
        operator="";
        firstnum="";
        secondnum="";
        result="";
    }

    @Override
    public void onClick(View view) {
        int resID=view.getId();
        String intputText;
        if(resID==R.id.btn_squareroot){
            intputText="√";
        }else{
            intputText=((TextView)view).getText().toString();
        }

        if(resID==R.id.btn_CE){
            clear("");
        }else if(resID==R.id.btn_C){
            if(operator.equals("")){
                if(firstnum.length()==1){
                    firstnum="0";
                }else if(firstnum.length()>1){
                    firstnum=firstnum.substring(0,firstnum.length()-1);
                }else {
                    Toast.makeText(this,"没有可以删除的数字了！",Toast.LENGTH_LONG).show();
                }
                showText=firstnum;
                textView.setText(showText);
            } else if(!operator.equals("")) {
                if (secondnum.length() == 1) {
                    secondnum = "";
                } else if (secondnum.length() > 1) {
                    secondnum = secondnum.substring(0, secondnum.length() - 1);
                } else if (secondnum.length() <= 0) {
                    Toast.makeText(this, "没有可以消除的数字了！", Toast.LENGTH_LONG).show();
                }
                showText = showText.substring(0, showText.length() - 1);
                textView.setText(showText);
            }
        }else if(resID==R.id.btn_equal){
            if(operator.length()==0||operator.equals("=")){
                Toast.makeText(this,"请输入运算符！",Toast.LENGTH_LONG).show();
            }else if(secondnum.length()<=0){
                Toast.makeText(this,"请输入数字！",Toast.LENGTH_LONG).show();
            }
            if(calculate()){
                firstnum=result;
                operator=intputText;
                showText=showText+"="+result;
                textView.setText(showText);
            }
        }else if(resID==R.id.btn_add||resID==R.id.btn_reduce||resID==R.id.btn_multiple||resID==R.id.btn_divide){
            if(firstnum.length()<=0){
                Toast.makeText(this,"请输入数字！",Toast.LENGTH_LONG).show();
            }
            if(operator.length()==0||operator.equals("=")||operator.equals("√")){
                operator=intputText;
                showText=showText+operator;
                textView.setText(showText);
            }else{
                showText=showText+operator;
                textView.setText(showText);
            }
        }else if(resID==R.id.btn_squareroot) {
            if (firstnum.length() <= 0) {
                Toast.makeText(this, "请输入数字！", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(firstnum) < 0) {
                Toast.makeText(this, "开根号的数值不能小于零！", Toast.LENGTH_LONG).show();
            }
            result = String.valueOf(Math.sqrt(Double.parseDouble(firstnum)));
            firstnum = result;
            secondnum = "";
            operator = intputText;
            showText = showText + "√=" + result;
            textView.setText(showText);
        }else{
            if(operator.equals("=")){
                operator="";
                firstnum="";
                showText="";
            }
            if(resID==R.id.btn_point){
                intputText=".";
            }
            if(operator.equals("")){
                firstnum=firstnum+intputText;
            }else{
                secondnum=secondnum+intputText;
            }
            showText=showText+intputText;
            textView.setText(showText);
        }
    }
}
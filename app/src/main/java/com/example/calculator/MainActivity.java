package com.example.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.icu.math.BigDecimal;
import android.icu.number.NumberFormatter;
import android.icu.util.IslamicCalendar;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    /*定义变量*/
    //舍人精度
    public static final int[] DEF_DIV_SCALE={17};
    //初始化显示数据
    private final String[] init=new String[1];
    //显示框
    private EditText input;//用于显示输出结果
    //划动条
    private SeekBar seekbar;
    //普通控件及变量
    private Button[] btn=new Button[10];//0到9十个数字
    private TextView mem,_drg,tip;
    private Button divide,mul,sub,add,equal,sin,cos,tan,log,ln,sqrt,
                   square,factorial,bksp,left,right,dot,db,bd,drg,mc,c;
    public String str_old;
    public String str_new;
    public boolean vbegin=true;//控制输入，true为重新输入，false为接着输入
    public boolean drg_flag=true;//true为角度，false为弧度
    public double pi=4*Math.atan(1);//pai值
    public boolean tip_lock=true;//true为正确，false为错误，输入锁定
    public boolean equals_flag=true;//是否在按下等号后输入，true为之前，false为之后

    //定义ContextMenu中每个菜单选项的ID
    final int Menu_1= Menu.FIRST;
    final int Menu_2=Menu.FIRST+1;
    private ClipboardManager mclipboard=null;

    public boolean onCreateOptionsMenu(Menu menu){
        //导入菜单布局
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //创建菜单项点击事件
        switch(item.getItemId()){
            case R.id.menu_setting:
                Toast.makeText(this,"点击了设置",Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_out:
                android.os.Process.killProcess(android.os.Process.myPid());//获取pid
                System.exit(0);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //切换布局后调用，初始化显示
        if(savedInstanceState!=null){
            init[0]=savedInstanceState.getString("exp");
            equals_flag=false;
        }
        //屏幕方向监听
        int orientation=getResources().getConfiguration().orientation;

        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            //横屏内容
            setContentView(R.layout.activity_land);
            InitWigdet();
            AllWidgetListener();
        }
        else if(orientation==Configuration.ORIENTATION_PORTRAIT){
            //竖屏内容
            setContentView(R.layout.activity_land);
            input=(EditText) findViewById(R.id.textView);
            input.setText(init[0]);
            seekbar=(SeekBar) findViewById(R.id.seekBar);
            seekbar.setProgress(DEF_DIV_SCALE[0]);
            seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
            Display defualtDiaplay=getWindowManager().getDefaultDisplay();
            Point point=new Point();
            defualtDiaplay.getSize(point);
            Integer x=point.x;
            Integer y=point.y;

            input.setKeyListener(new KeyListener() {
                @Override
                public int getInputType() {
                    return 0;
                }

                @Override
                public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                    return false;
                }

                @Override
                public void clearMetaKeyState(View view, Editable editable, int i) {

                }
            });
            View.OnClickListener onCleanListener=new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input.setText("");
                }
            };
            View.OnClickListener onCleanLastListener=new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s=input.getText().toString();
                    int len=s.length();
                    if(len>0){
                        s=s.substring(0,len-1);
                        input.setText(s);
                    }
                }
            };
            View.OnClickListener onEXPListener=new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Button temp=(Button)view;
                    String s=temp.getText().toString();
                    String show=input.getText().toString();
                    int len=show.length();

                    if(show.length()==0){
                        if(s.equals("+")||s.equals("×")||s.equals("÷"))return;
                    }
                    if((show.endsWith("+"))||(show.endsWith("-"))||(show.endsWith("×"))||(show.endsWith("÷"))||(show.endsWith("."))){
                        if(s.equals("+")||s.equals("×")||s.equals("÷")||s.equals("-")||(s.equals("."))){
                            show=show.substring(0,len-1)+s;
                            input.setText(show);
                        }else{
                            input.append(s);
                        }
                    }else{
                        input.append(s);
                    }
                }
            };
            View.OnClickListener onEXECListener =new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s=input.getText().toString();
                    if(s.length()==0)return;
                    s=s.replace("×","*");
                    s=s.replace("÷","/");
                    s=s.replace("%","");
                    if((s.endsWith("+"))||(s.endsWith("-"))||(s.endsWith("*"))||(s.endsWith("/"))||(s.endsWith(".")))return;
                    try{
                        String re=Calculator.conversion(s);
                        re=round(re,DEF_DIV_SCALE[0]);
                        if(re.indexOf(".")>0){
                            re=re.replaceAll("0+?$","");//去掉多余的0
                            re=re.replaceAll("[.]$","");//如最后一位是小数点，则去掉
                        }
                        input.setText(re);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        input.setText("");
                    }
                }
            };
            View.OnClickListener onPERCENTListener =new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s=input.getText().toString();
                    if(s.length()==0)return;
                    s=s.replace("×","*");
                    s=s.replace("÷","/");
                    s=s.replace("%","");
                    if((s.endsWith("+"))||(s.endsWith("-"))||(s.endsWith("*"))||(s.endsWith("/"))||(s.endsWith(".")))return;
                    s=s+"/100";

                    try{
                        String re=Calculator.conversion(s);
                        re=round(re,DEF_DIV_SCALE[0]);
                        if(re.indexOf(".")>0){
                            re=re.replaceAll("0+?$","");//去掉多余的0
                            re=re.replaceAll("[.]$","");//若最后一位是小数点则去掉
                        }
                        input.setText(re+"%");
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        input.setText("");
                    }
                }
            };
            input.getLayoutParams().height=2*y/7;
            Button button0=(Button) findViewById(R.id.button_0);
            button0.getLayoutParams().width=x/4;
            button0.getLayoutParams().height=y/8;

            Button button1=(Button) findViewById(R.id.button_1);
            button1.getLayoutParams().width=x/4;
            button1.getLayoutParams().height=y/8;

            Button button2=(Button) findViewById(R.id.button_2);
            button2.getLayoutParams().width=x/4;
            button2.getLayoutParams().height=y/8;

            Button button3=(Button) findViewById(R.id.button_3);
            button3.getLayoutParams().width=x/4;
            button3.getLayoutParams().height=y/8;

            Button button4=(Button) findViewById(R.id.button_4);
            button4.getLayoutParams().width=x/4;
            button4.getLayoutParams().height=y/8;

            Button button5=(Button) findViewById(R.id.button_5);
            button5.getLayoutParams().width=x/4;
            button5.getLayoutParams().height=y/8;

            Button button6=(Button) findViewById(R.id.button_6);
            button6.getLayoutParams().width=x/4;
            button6.getLayoutParams().height=y/8;

            Button button7=(Button) findViewById(R.id.button_7);
            button7.getLayoutParams().width=x/4;
            button7.getLayoutParams().height=y/8;

            Button button8=(Button) findViewById(R.id.button_8);
            button8.getLayoutParams().width=x/4;
            button8.getLayoutParams().height=y/8;

            Button button9=(Button) findViewById(R.id.button_9);
            button9.getLayoutParams().width=x/4;
            button9.getLayoutParams().height=y/8;

            Button buttonC=(Button) findViewById(R.id.button_C);
            buttonC.getLayoutParams().width=x/4;
            buttonC.getLayoutParams().height=y/8;

            Button buttonreduce=(Button) findViewById(R.id.button_reduce);
            buttonreduce.getLayoutParams().width=x/4;
            buttonreduce.getLayoutParams().height=y/8;

            Button buttonadd=(Button) findViewById(R.id.button_add);
            buttonadd.getLayoutParams().width=x/4;
            buttonadd.getLayoutParams().height=y/8;

            Button buttonbksp=(Button) findViewById(R.id.button_bksp);
            buttonbksp.getLayoutParams().width=x/4;
            buttonbksp.getLayoutParams().height=y/8;

            Button buttondiv=(Button) findViewById(R.id.button_divide);
            buttondiv.getLayoutParams().width=x/4;
            buttondiv.getLayoutParams().height=y/8;

            Button buttonequal=(Button) findViewById(R.id.button_equal);
            buttonequal.getLayoutParams().width=x/4;
            buttonequal.getLayoutParams().height=y/8;

            Button buttonmul=(Button) findViewById(R.id.button_multiply);
            buttonmul.getLayoutParams().width=x/4;
            buttonmul.getLayoutParams().height=y/8;

            Button buttonper=(Button) findViewById(R.id.button_percent);
            buttonper.getLayoutParams().width=x/4;
            buttonper.getLayoutParams().height=y/8;

            Button buttonpoint=(Button) findViewById(R.id.button_point);
            buttonpoint.getLayoutParams().width=x/4;
            buttonpoint.getLayoutParams().height=y/8;



            buttonC.setOnClickListener(onCleanListener);
            buttonbksp.setOnClickListener(onCleanLastListener);
            buttonequal.setOnClickListener(onEXECListener);
            buttonper.setOnClickListener(onPERCENTListener);

            button0.setOnClickListener(onEXPListener);
            button1.setOnClickListener(onEXPListener);
            button2.setOnClickListener(onEXPListener);
            button3.setOnClickListener(onEXPListener);
            button4.setOnClickListener(onEXPListener);
            button5.setOnClickListener(onEXPListener);
            button6.setOnClickListener(onEXPListener);
            button7.setOnClickListener(onEXPListener);
            button8.setOnClickListener(onEXPListener);
            button9.setOnClickListener(onEXPListener);
            buttonadd.setOnClickListener(onEXPListener);
            buttondiv.setOnClickListener(onEXPListener);
            buttonmul.setOnClickListener(onEXPListener);
            buttonpoint.setOnClickListener(onEXPListener);
            buttonreduce.setOnClickListener(onEXPListener);
        }
    }

    /*初始化所有组件*/
    private void InitWigdet(){
        //获取屏幕参数
        Display defaultDisplay=getWindowManager().getDefaultDisplay();
        Point point =new Point();
        defaultDisplay.getSize(point);
        Integer x=point.x;
        Integer y=point.y-60;

        //获取界面元素
        input=(EditText) findViewById(R.id.input);
        input.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() {
                return 0;
            }

            @Override
            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable editable, int i) {

            }
        });
        input.setText(init[0]);
        seekbar=(SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        input.getLayoutParams().height=y/9;
        mem=(TextView) findViewById(R.id.id_mem);
        tip=(TextView) findViewById(R.id.tip);

        _drg=(TextView) findViewById(R.id.id_drg);
        _drg.getLayoutParams().width=x/6;
        _drg.getLayoutParams().height=y/10-8;

        btn[0]=(Button)findViewById(R.id.btn_0);
        btn[0].getLayoutParams().width=x/6;
        btn[0].getLayoutParams().height=y/10;

        btn[1]=(Button)findViewById(R.id.btn_1);
        btn[1].getLayoutParams().width=x/6;
        btn[1].getLayoutParams().height=y/10;

        btn[2]=(Button)findViewById(R.id.btn_2);
        btn[2].getLayoutParams().width=x/6;
        btn[2].getLayoutParams().height=y/10;

        btn[3]=(Button)findViewById(R.id.btn_3);
        btn[3].getLayoutParams().width=x/6;
        btn[3].getLayoutParams().height=y/10;

        btn[4]=(Button)findViewById(R.id.btn_4);
        btn[4].getLayoutParams().width=x/6;
        btn[4].getLayoutParams().height=y/10;

        btn[5]=(Button)findViewById(R.id.btn_5);
        btn[5].getLayoutParams().width=x/6;
        btn[5].getLayoutParams().height=y/10;

        btn[6]=(Button)findViewById(R.id.btn_6);
        btn[6].getLayoutParams().width=x/6;
        btn[6].getLayoutParams().height=y/10;

        btn[7]=(Button)findViewById(R.id.btn_7);
        btn[7].getLayoutParams().width=x/6;
        btn[7].getLayoutParams().height=y/10;

        btn[8]=(Button)findViewById(R.id.btn_8);
        btn[8].getLayoutParams().width=x/6;
        btn[8].getLayoutParams().height=y/10;

        btn[9]=(Button)findViewById(R.id.btn_9);
        btn[9].getLayoutParams().width=x/6;
        btn[9].getLayoutParams().height=y/10;

        divide=(Button)findViewById(R.id.btn_divide);
        divide.getLayoutParams().width=x/6;
        divide.getLayoutParams().height=y/10;

        mul=(Button)findViewById(R.id.btn_mul);
        mul.getLayoutParams().width=x/6;
        mul.getLayoutParams().height=y/10;

        sub=(Button)findViewById(R.id.btn_mul);
        sub.getLayoutParams().width=x/6;
        sub.getLayoutParams().height=y/10;

        add=(Button)findViewById(R.id.btn_add);
        add.getLayoutParams().width=x/6;
        add.getLayoutParams().height=y/10;

        equal=(Button)findViewById(R.id.btn_equal);
        equal.getLayoutParams().width=x/6;
        equal.getLayoutParams().height=y/10;

        sin=(Button)findViewById(R.id.id_sin);
        sin.getLayoutParams().width=x/6;
        sin.getLayoutParams().height=y/10;

        cos=(Button)findViewById(R.id.id_cos);
        cos.getLayoutParams().width=x/6;
        cos.getLayoutParams().height=y/10;

        tan=(Button)findViewById(R.id.id_tan);
        tan.getLayoutParams().width=x/6;
        tan.getLayoutParams().height=y/10;

        log=(Button)findViewById(R.id.btn_log);
        log.getLayoutParams().width=x/6;
        log.getLayoutParams().height=y/10;

        ln=(Button)findViewById(R.id.btn_ln);
        ln.getLayoutParams().width=x/6;
        ln.getLayoutParams().height=y/10;

        sqrt=(Button)findViewById(R.id.btn_sqrt);
        sqrt.getLayoutParams().width=x/6;
        sqrt.getLayoutParams().height=y/10;

        square=(Button)findViewById(R.id.btn_square);
        square.getLayoutParams().width=x/6;
        square.getLayoutParams().height=y/10;

        factorial=(Button)findViewById(R.id.id_factorial);
        factorial.getLayoutParams().width=x/6;
        factorial.getLayoutParams().height=y/10-8;

        bksp=(Button)findViewById(R.id.id_bksp);
        bksp.getLayoutParams().width=2*x/6;
        bksp.getLayoutParams().height=y/10-8;

        left=(Button)findViewById(R.id.btn_left);
        left.getLayoutParams().width=x/6;
        left.getLayoutParams().height=y/10;

        right=(Button)findViewById(R.id.btn_right);
        right.getLayoutParams().width=x/6;
        right.getLayoutParams().height=y/10;

        dot=(Button)findViewById(R.id.btn_point);
        dot.getLayoutParams().width=x/6;
        dot.getLayoutParams().height=y/10;

        LinearLayout linear=(LinearLayout) findViewById(R.id.linear);
        linear.getLayoutParams().width=x/6;
        linear.getLayoutParams().height=y/10;

        bd=(Button)findViewById(R.id.btn_bd);
        db=(Button)findViewById(R.id.btn_db);

        drg=(Button)findViewById(R.id.drg);
        drg.getLayoutParams().width=x/6;
        drg.getLayoutParams().height=y/10-8;

        mc=(Button)findViewById(R.id.id_mc);
        mc.getLayoutParams().width=2*x/6;
        mc.getLayoutParams().height=y/10-8;

        c=(Button)findViewById(R.id.id_c);
        c.getLayoutParams().width=2*x/6;
        c.getLayoutParams().height=y/10-8;
    }

    /*为所有按键绑定监听器*/
    private void AllWidgetListener(){
        //数字键
        for(int i=0;i<10;i++){
            btn[i].setOnClickListener(actionPerformed);
        }

        //运算符
        divide.setOnClickListener(actionPerformed);
        mul.setOnClickListener(actionPerformed);
        sub.setOnClickListener(actionPerformed);
        add.setOnClickListener(actionPerformed);
        equal.setOnClickListener(actionPerformed);
        sin.setOnClickListener(actionPerformed);
        cos.setOnClickListener(actionPerformed);
        tan.setOnClickListener(actionPerformed);
        log.setOnClickListener(actionPerformed);
        ln.setOnClickListener(actionPerformed);
        sqrt.setOnClickListener(actionPerformed);
        square.setOnClickListener(actionPerformed);
        factorial.setOnClickListener(actionPerformed);
        bksp.setOnClickListener(actionPerformed);
        left.setOnClickListener(actionPerformed);
        right.setOnClickListener(actionPerformed);
        dot.setOnClickListener(actionPerformed);
        db.setOnClickListener(actionPerformed);
        bd.setOnClickListener(actionPerformed);
        drg.setOnClickListener(actionPerformed);
        mc.setOnClickListener(actionPerformed);
        c.setOnClickListener(actionPerformed);
    }

    /*键盘命令捕捉*/
    String[] TipCommand=new String[500];
    int tip_i=0;//指针
    private View.OnClickListener actionPerformed=new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            //按键上命令获取
            String command = ((Button) view).getText().toString();
            //显示器上的字符串
            String str = input.getText().toString();
            //检测输入是否正确
            if (equals_flag == false && "0123456789.()sincostanlnlogn!+-×÷√∧".indexOf(command) != -1) {
                //监测显示器上字符串是否正确
                if (right(str)) {
                    if ("+-×÷√∧)".indexOf(command) != -1) {
                        for (int i = 0; i < str.length(); i++) {
                            TipCommand[tip_i] = String.valueOf(str.charAt(i));
                            tip_i++;
                        }
                        vbegin = false;
                    }
                } else {
                    input.setText("0");
                    vbegin = true;
                    tip_i = 0;
                    tip_lock = true;
                    tip.setText("welcome use the APP!");
                }
                equals_flag = true;
            }
            if (tip_i > 0) {
                TipChecker(TipCommand[tip_i - 1], command);
            } else if (tip_i == 0) {
                TipChecker("#", command);
            }
            if ("0123456789.()sincostanlnlogn!+-×÷√∧".indexOf(command) != -1 && tip_lock) {
                print(command);
            } else if (command.compareTo("DRG") == 0 && tip_lock) {
                if (drg_flag == true) {
                    drg_flag = false;
                    _drg.setText("RAD");
                } else {
                    drg_flag = true;
                    _drg.setText("DEG");
                }
                //如果输入时退格键，并且是在按等号之前
            } else if (command.compareTo("◀") == 0 && equals_flag) {
                //依次删除三个字符
                if (TTO(str) == 3) {
                    if (str.length() > 3) {
                        input.setText(str.substring(0, str.length() - 3));
                    } else if (str.length() == 3) {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome use the APP!");
                    }
                }
                //依次删除两个字符
                else if (TTO(str) == 2) {
                    if (str.length() > 2) {
                        input.setText(str.substring(0, str.length() - 2));
                    } else if (str.length() == 2) {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome ues the APP!");
                    }
                }
                //依次删除一个字符
                else if (TTO(str) == 1) {
                    //若之前删除的字符串合法则删除一个字符
                    if (right(str)) {
                        if (str.length() > 1) {
                            input.setText(str.substring(0, str.length() - 1));
                        } else if (str.length() == 1) {
                            input.setText("0");
                            vbegin = true;
                            tip_i = 0;
                            tip.setText("welcome ues the APP!");
                        }
                    }
                    //若之前输入的字符串不合法则删除全部字符串
                    else {
                        input.setText("0");
                        vbegin = true;
                        tip_i = 0;
                        tip.setText("welcome ues the APP!");
                    }
                }
                if (input.getText().toString().compareTo("-") == 0 || equals_flag == false) {
                    input.setText("0");
                    vbegin = true;
                    tip_i = 0;
                    tip.setText("welcome ues the APP!");
                }
                tip_lock = true;
                if (tip_i > 0) {
                    tip_i--;
                    //如果是在按等号之后输入退格键
                }
            } else if (command.compareTo("Bksp") == 0 && equals_flag == false) {
                //将显示其内容设置为0
                input.setText("0");
                vbegin = true;
                tip_i = 0;
                tip.setText("welcome ues the APP!");
                //如果输入的是清除键
            } else if (command.compareTo("C") == 0) {
                //将显示其内容设置为0
                input.setText("0");
                //重新输入标志置为true
                vbegin = true;
                //缓存命令位数清零
                tip_i = 0;
                //表明输入可以继续
                tip_lock = true;
                //表明输入等号之前
                equals_flag = true;
                tip.setText("welcone use the APP!");
                //如果输入的是”MC“，则将存储器内容清零
            } else if (command.compareTo("MC") == 0) {
                mem.setText("0");
                //如果按”bd“则转换进制
            } else if (command.compareTo("B") == 0) {
                String s = input.getText().toString();
                s = str.replace(".", "");
                //非法字符清屏
                if (".+-×÷√∧sincostanloglnn!()".indexOf(s) != -1) {
                    input.setText("0");
                } else {
                    try {
                        int a = Integer.valueOf(s);
                        String re = Integer.toBinaryString(a);
                        input.setText(re);
                    } catch (Exception e) {
                        input.setText("");
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                //如果按”bd“则转换进制
            } else if (command.compareTo("D") == 0) {
                String s = input.getText().toString();
                s = s.replace(".", "");
                input.setText("1234567890");
                //非法字符清空
                if ("23456789+-×÷√∧sincostanloglnn!().".indexOf(s) !=-1){
                    input.setText("0");
                }else{
                    try {
                        Integer re = Integer.parseInt(s, 2);
                        input.setText(re.toString());
                    } catch (Exception e) {
                        input.setText("");
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (command.compareTo("=") == 0 && tip_lock && right(str) && equals_flag) {
                tip_i = 0;
                //表明不可以继续输入
                tip_lock = false;
                //表明输入等号后
                equals_flag = false;
                //保存原来式子样式
                str_old = str;
                //替换式子中的运算符，便于计算
                str = str.replace("sin", "s");
                str = str.replace("cos", "c");
                str = str.replace("tan", "t");
                str = str.replace("log", "g");
                str = str.replace("ln", "l");
                str = str.replace("n!", "!");
                //重新输入标志置为true
                vbegin = true;
                //将-1x转换成-
                str_new = str.replaceAll("-", "-1×");
                //计算式子的结果
                new calc().process(str_new);

            }
            //表明可以继续输入
            tip_lock = true;
        }
    };

    /*小数点前后均可以省略，表示0数字第一位可以为零*/
    private void TipChecker(String tipcommand1,String tipcommand2){

        //Tipcode1表示错误类型，Tipcode2表示名词解释类型
        int Tipcode1=0,Tipcode2=0;
        //表示命令类型
        int tiptype1=0,tiptype2=0;
        //括号数
        int bracket=0;
        //+-×÷√∧不能作为第一位
        if(tipcommand1.compareTo("#")==0&&(tipcommand2.compareTo("÷")==0||tipcommand2.compareTo("×")==0||tipcommand2.compareTo("+")==0||tipcommand2.compareTo(")")==0||tipcommand2.compareTo("√")==0||tipcommand2.compareTo("∧")==0)){
            Tipcode1=-1;
        }
        //定义存储字符串最后一位的类型
        else if(tipcommand1.compareTo("#")!=0){
            if(tipcommand1.compareTo("(")==0){
                tiptype1=1;
            }else if(tipcommand1.compareTo(")")==0){
                tiptype1=2;
            }else if(tipcommand1.compareTo(".")==0){
                tiptype1=3;
            }else if("0123456789".indexOf(tipcommand1)!=-1){
                tiptype1=4;
            }else if("+-×÷".indexOf(tipcommand1)!=1){
                tiptype1=5;
            }else if("√∧".indexOf(tipcommand1)!=-1){
                tiptype1=6;
            }else if("sincostanlonlnn!".indexOf(tipcommand1)!=-1){
                tiptype1=7;
            }
            //定义欲输入的按键类型
            if(tipcommand2.compareTo("(")==0){
                tiptype2=1;
            }else if(tipcommand2.compareTo(")")==0){
                tiptype2=2;
            }else if(tipcommand2.compareTo(".")==0){
                tiptype2=3;
            }else if("0123456789".indexOf(tipcommand2)!=-1){
                tiptype2=4;
            }else if("+-×÷".indexOf(tipcommand2)!=-1){
                tiptype2=5;
            }else if("√∧".indexOf(tipcommand2)!=-1){
                tiptype2=6;
            }else if("sincostanlonlnn!".indexOf(tipcommand2)!=-1){
                tiptype2=7;
            }
            switch (tiptype1){
                case 1:
                    //左括号后面直接接右括号，”+×÷“，或者”√∧“
                    if(tiptype2==2||(tiptype2==5&&tipcommand2.compareTo("-")!=0)||tiptype2==6){
                        Tipcode1=1;
                    }
                    break;
                case 2:
                    //右括号后面接左括号，数字，”+-×÷√∧sincostanloglnn!“
                    if(tiptype2==1||tiptype2==3||tiptype2==4||tiptype2==7){
                        Tipcode1=2;
                    }
                    break;
                case 3:
                    //“.”后面接左括号或者"sincos..."
                    if(tiptype2==1||tiptype2==7){
                        Tipcode1=3;
                    }
                    //连续输入两个小数点
                    if(tiptype2==3){
                        Tipcode1=8;
                    }
                    break;
                case 4:
                    //数字后面直接接左括号或者“sincos”
                    if(tiptype2==1||tiptype2==7){
                        Tipcode1=4;
                    }
                    break;
                case 5:
                    //“+-×÷”后面直接接右括号“+-×÷√∧”以及“sincos..."
                    if(tiptype2==2||tiptype2==5||tiptype2==6){
                        Tipcode1=5;
                    }
                    break;
                case 6:
                    //√∧后面直接接右括号“+-×÷√∧”以及“sincos..."
                    if(tiptype2==2||tiptype2==5||tiptype2==6||tiptype2==7){
                        Tipcode1=6;
                    }
                    break;
                case 7:
                    //sincos...后面直接接右括号”+-×÷√∧”以及“sincos..."
                    if(tiptype2==2||tiptype2==5||tiptype2==6||tiptype2==7){
                        Tipcode1=7;
                    }
                    break;
            }
        }
        //检测小数点的重复性，Tipconde=1表明满足前面的规则
        if(Tipcode1==0&&tipcommand2.compareTo(".")==0){
            int tip_point=0;
            for(int i=0;i<tip_i;i++){
                //若之前出现一个小数点，则小数点计数加一
                if(TipCommand[i].compareTo(".")==0){
                    tip_point++;
                }
                //若出现以下几个运算符之一，小数点计数清零
                if(TipCommand[i].compareTo("sin")==0||
                        TipCommand[i].compareTo("cos")==0||
                        TipCommand[i].compareTo("tan")==0||
                        TipCommand[i].compareTo("log")==0||
                        TipCommand[i].compareTo("ln")==0||
                        TipCommand[i].compareTo("n!")==0||
                        TipCommand[i].compareTo("√")==0||
                        TipCommand[i].compareTo("∧")==0||
                        TipCommand[i].compareTo("÷")==0||
                        TipCommand[i].compareTo("×")==0||
                        TipCommand[i].compareTo("-")==0||
                        TipCommand[i].compareTo("+")==0||
                        TipCommand[i].compareTo("（")==0||
                        TipCommand[i].compareTo("）")==0){
                    tip_point=0;
                }
            }
            tip_point++;
            //若小数点计数大于一，表明小数点重复了
            if(tip_point>1){
                Tipcode1=8;
            }
        }
        //检测左右括号是否匹配
        if(Tipcode1==0&&tipcommand2.compareTo(")")==0){
            int tip_right_bracket=0;
            for(int i=0;i<tip_i;i++){
                //如果出现一个左括号，则计数加一
                if(TipCommand[i].compareTo("(")==0){
                    tip_right_bracket++;
                }
                //如果出现一个右括号，则计数减一
                if(TipCommand[i].compareTo(")")==0){
                    tip_right_bracket--;
                }
            }
            //如果右括号计数等于零，表明没有相应的有左括号与当前右括号匹配
            if(tip_right_bracket==0){
                Tipcode1=10;
            }
        }
        //检查输入等好的合法
        if(Tipcode1==0&&tipcommand2.compareTo("=")==0){
            //括号匹配数
            int tip_bracket=0;
            for(int i=0;i<tip_i;i++){
                if(TipCommand[i].compareTo("(")==0){
                    tip_bracket++;
                }
                if(TipCommand[i].compareTo(")")==0){
                    tip_bracket--;
                }
            }
            //若大于零，表明左括号还有未匹配的
            if(tip_bracket>0){
                Tipcode1=9;
                bracket=tip_bracket;
            }else if(tip_bracket==0){
                //若前一个字符是一下之一，表明等号不合法
                if("√∧sincostanloglnn!".indexOf(tipcommand1)!=-1){
                    Tipcode1=6;
                }
                //若前一个字符是一下之一，表明等号不合法
                if("+-×÷".indexOf(tipcommand1)!=-1){
                    Tipcode1=5;
                }
            }
        }
        //若命令是以下之一，则显示相应的帮助信息
        if(tipcommand2.compareTo("MC")==0){
            Tipcode2=1;
        }
        if(tipcommand2.compareTo("C")==0){
            Tipcode2=2;
        }
        if(tipcommand2.compareTo("DRG")==0){
            Tipcode2=3;
        }
        if(tipcommand2.compareTo("Bksp")==0){
            Tipcode2=4;
        }
        if(tipcommand2.compareTo("sin")==0){
            Tipcode2=5;
        }
        if(tipcommand2.compareTo("cos")==0){
            Tipcode2=6;
        }
        if(tipcommand2.compareTo("tan")==0){
            Tipcode2=7;
        }
        if(tipcommand2.compareTo("log")==0){
            Tipcode2=8;
        }
        if(tipcommand2.compareTo("ln")==0){
            Tipcode2=9;
        }
        if(tipcommand2.compareTo("n!")==0){
            Tipcode2=10;
        }
        if(tipcommand2.compareTo("√")==0){
            Tipcode2=11;
        }
        if(tipcommand2.compareTo("∧")==0){
            Tipcode2=12;
        }
        //显示错误信息和帮助
        TipShow(bracket,Tipcode1,Tipcode2,tipcommand1,tipcommand2);
    }

    /*反馈Tip信息*/
    private void TipShow(int bracket,int tipcode1,int tipcode2,String tipcommand1,String tipcommand2){
        String tipmessage="";
        if(tipcode1!=0){
            tip_lock=false;
            //表明输入有误
        }
        switch (tipcode1){
            case -1:
                tipmessage=tipcommand2+"不能作为第一个运算符\n";
                break;
            case 1:
                tipmessage=tipcommand1+"后应输入：数字/(/./-/函数\n";
                break;
            case 2:
                tipmessage=tipcommand1+"后应输入：)/运算符\n";
                break;
            case 3:
                tipmessage=tipcommand1+"后应输入：)/数字/运算符\n";
                break;
            case 4:
                tipmessage=tipcommand1+"后应输入：)/./数字/运算符\n";
                break;
            case 5:
                tipmessage=tipcommand1+"后应输入：(/./数字/函数\n";
                break;
            case 6:
                tipmessage=tipcommand1+"后应输入：(/./数字\n";
                break;
            case 7:
                tipmessage=tipcommand1+"后应输入：(/./数字\n";
                break;
            case 8:
                tipmessage="小数点重复\n";
                break;
            case 9:
                tipmessage="不能计算，缺少"+bracket+"个";
                break;
            case 10:
                tipmessage="不需要)";
                break;
        }
        switch (tipcode2){
            case 1:
                tipmessage=tipmessage+"[MC 用法：清楚记忆MEM]";
                break;
            case 2:
                tipmessage=tipmessage+"[C 用法：归零]";
                break;
            case 3:
                tipmessage=tipmessage+"[DRG 用法：选择DEG或RAD]";
                break;
            case 4:
                tipmessage=tipmessage+"[Bksp 用法：退格]";
                break;
            case 5:
                tipmessage=tipmessage+"sin 函数用法示例：\n" +"DEG:sin30=0.5   RAD:sin1=0.84\n"+"注：与其他函数一起使用时要加括号，如：\n"+"sin(cos45),而不是sincos45";
                break;
            case 6:
                tipmessage=tipmessage+"cos 函数用法示例：\n"+"DEG:sin30=0.5    RAD:sin1=0.84\n"+"注：与其他函数一起使用时要加括号，如：\n"+"cos(sin45),而不是cossin45";
                break;
            case 7:
                tipmessage=tipmessage+"tan 函数用法示例：\n"+"DEG:tan45=1      RAD:tan1=1.55\n"+"注：与其他函数一起使用时要加括号，如：\n"+"tan(cos45),而不是tancos45";
                break;
            case 8:
                tipmessage=tipmessage+"log 函数用法示例：\n"+"log10=log(5+5)=1\n"+"注：与其他函数一起使用时要加括号，如：\n"+"log(tan45),而不是logtan45";
                break;
            case 9:
                tipmessage=tipmessage+"ln 函数用法示例：\n"+"ln10=le(5+5)=2.3   lne=1\n"+"注：与其他函数一起使用时要加括号，如：\n"+"ln(tan45),而不是lntan45";
                break;
            case 10:
                tipmessage=tipmessage+"n! 函数用法示例：\n"+"n!3=n!(1+2)=3×2×1=6\n"+"注：与其他函数一起使用时要加括号，如\n"+"n!(log1000),而不是n!log1000";
                break;
            case 11:
                tipmessage=tipmessage+"√ 用法示例：开任意次根号\n"+"如：27开3次根为 27√3=3\n"+"注：与其他函数一起使用时要加括号，如：\n"+"（函数）√（函数）,(n!3)√(log100)=36";
                break;
            case 12:
                tipmessage=tipmessage+"∧ 用法示例：开任意次方根\n"+"如：2的3次方为 2∧3=8\n"+"注：与其他函数一起使用时要加括号，如：\n"+"（函数）∧（函数），（n!3)∧(log100)=36";
                break;
        }
        //将显示信息提示到tip
        tip.setText(tipmessage);
    }

    /*将信息显示到屏幕上*/
    private void print(String str){
        //清屏后输出
        if(vbegin){
            input.setText(str);
        }else{
            input.append(str);
        }
        vbegin=false;
    }

    /*
    * 返回3，表示str尾部为sin、cos、tan、log中的一个，应当一次删除三个，
    * 返回2，表示str尾部为ln、n!中的一个，应当一次删除两个，
    * 返回1，表示str尾部为除2、3之外的情况，只需要删除一个
    * */
    private int TTO(String str){
        if((str.charAt(str.length()-1)=='n'&&str.charAt(str.length()-2)=='i'&&str.charAt(str.length()-3)=='s')||(str.charAt(str.length()-1)=='s'&&str.charAt(str.length()-2)=='o'&&str.charAt(str.length()-3)=='c')||(str.charAt(str.length()-1)=='n'&&str.charAt(str.length()-2)=='a'&&str.charAt(str.length()-3)=='t')||(str.charAt(str.length()-1)=='g'&&str.charAt(str.length()-2)=='o'&&str.charAt(str.length()-3)=='l')){
            return 3;
        }else if((str.charAt(str.length()-1)=='n'&&str.charAt(str.length()-2)=='l')||(str.charAt(str.length()-1)=='!'&& str.charAt(str.length()-2)=='n')){
            return 2;
        }else{
            return 1;
        }
    }

    /*
    * 判断一个str是否是合法的，返回值为true或false
    * 只包含0123456789.（）sincostanloglnn!+-×÷√∧的是合法的str，返回true
    * 包含了除0123456789.（）sincostanloglnn!+-×÷√∧以外的字符的str为非法的，返回false*/
    private boolean right(String str){
        int i=0;
        for(i=0;i<str.length();i++){
            if(str.charAt(i)!='0'&&str.charAt(i)!='1'
                    && str.charAt(i)!='2'&&str.charAt(i)!='3'
                    && str.charAt(i)!='4'&&str.charAt(i)!='5'
                    && str.charAt(i)!='6'&&str.charAt(i)!='7'
                    && str.charAt(i)!='8'&&str.charAt(i)!='9'
                    && str.charAt(i)!='.'&&str.charAt(i)!='-'
                    && str.charAt(i)!='+'&&str.charAt(i)!='×'
                    && str.charAt(i)!='÷'&&str.charAt(i)!='√'
                    && str.charAt(i)!='∧'&&str.charAt(i)!='s'
                    && str.charAt(i)!='i'&&str.charAt(i)!='n'
                    && str.charAt(i)!='c'&&str.charAt(i)!='o'
                    && str.charAt(i)!='t'&&str.charAt(i)!='a'
                    && str.charAt(i)!='l'&&str.charAt(i)!='g'
                    && str.charAt(i)!='('&&str.charAt(i)!=')'
                    && str.charAt(i)!='!'){
                break;
            }
        }
        if(i==str.length()){
            return true;
        }else{
            return false;
        }
    }

    /*计算核心*/
    public class calc{
        public calc(){}
        final int MAXLEN=500;
        public void process(String str) {
            int weightPlus = 0;
            int topOp = 0;
            int topNum = 0;
            int flag = 1;
            int weightTemp = 0;
            int weight[];//保存运算符的优先级
            double number[];//保存数字
            char ch;
            char ch_gai;
            char operator[];//保存运算符
            String num;//记录数字
            weight=new int[MAXLEN];
            number = new double[MAXLEN];
            operator = new char[MAXLEN];
            String expression = str;
            StringTokenizer expToken = new StringTokenizer(expression, "+-×÷()sctgl!√∧");
            int i = 0;
            while (i < expression.length()) {
                ch = expression.charAt(i);
                //判断正负
                if (i == 0) {
                    if (ch == '-') {
                        flag = -1;
                    } else if (expression.charAt(i - 1) == '(' && ch == '-') {
                        flag = -1;
                    }
                    //取得数字，将正负号转移给数字
                    if (ch <= '9' && ch >= '0' || ch == '.' || ch == 'E') {
                        num = expToken.nextToken();
                        ch_gai = ch;
                        Log.e("guojs", ch + "--->" + i);
                        //取得数字
                        while (i < expression.length() && (ch_gai <= '9' && ch_gai >= '0' || ch_gai == '.' || ch_gai == 'E')) {
                            ch_gai = expression.charAt(i++);
                            Log.e("guojs", "i的值为：" + i);
                        }
                        //将指针退回之前的位置
                        if (i >= expression.length()) {
                            i -= 1;
                        } else {
                            i -= 2;
                        }
                        if (num.compareTo(".") == 0) {
                            number[topNum++] = 0;//将正负号交给数字
                        } else {
                            number[topNum++] = Double.parseDouble(num) * flag;
                            flag = 1;
                        }
                    }
                    //计算运算符的优先级
                    if (ch == '(') {
                        weightPlus += 4;
                    }
                    if (ch == ')') {
                        weightPlus -= 4;
                    }
                    if (ch == '-' && flag == 1 || ch == '+' || ch == 'x' || ch =='÷' || ch == 's' || ch == 'c' || ch == 't' || ch =='g' || ch == 'l' || ch == '!' || ch =='√'||ch=='∧'){
                        switch (ch){
                            //+-的优先级最低
                            case '+':
                            case '-':
                                weightTemp=1+weightPlus;
                                break;
                            //×÷的优先级稍高
                            case '×':
                            case '÷':
                                weightTemp=2+weightPlus;
                                break;
                            //sincos之类的优先级为3
                            case 's':
                            case 'c':
                            case 't':
                            case 'g':
                            case 'l':
                            case '!':
                                weightTemp=3+weightPlus;
                                break;
                            //其余优先级为4
                            default:
                                weightTemp=4+weightPlus;
                                break;
                        }
                        //如果当前优先级大于堆栈顶部元素，则直接入栈
                        if(topOp==0||weight[topOp-1]<weightTemp){
                            weight[topOp]=weightTemp;
                            operator[topOp]=ch;
                            topOp++;
                            //否则将堆栈中的运算符逐个取出，知道当前堆栈顶部运算符的优先级小于当前运算符
                        }else{
                            while (topOp>0&&weight[topOp-1]>=weightTemp){
                                switch (operator[topOp-1]){
                                    //取出数字数组的相应元素进行计算
                                    case '+':
                                        number[topNum-2]+=number[topNum-1];
                                        break;
                                    case '-':
                                        number[topNum-2]-=number[topNum-1];
                                        break;
                                    case '×':
                                        number[topNum-2]*=number[topNum-1];
                                        break;
                                    //判断除数为零的情况
                                    case '÷':
                                        if(number[topNum-1]==0){
                                            showError(1,str_old);
                                            return;
                                        }
                                        number[topNum-2]/=number[topNum-1];
                                        break;
                                    case '√':
                                        if(number[topNum-1]==0||(number[topNum-2]<0&&number[topNum-1]%2==0)){
                                            showError(2,str_old);
                                            return;
                                        }
                                        number[topNum-2]=Math.pow(number[topNum-2],1/number[topNum-1] );
                                        break;
                                    case '∧':
                                        number[topNum-2]=Math.pow(number[topNum-2],number[topNum-1]);
                                        break;
                                    case 's':
                                        if(drg_flag==true){
                                            number[topNum-1]=Math.sin((number[topNum-1]/180)*pi);
                                        }else{
                                            number[topNum-1]=Math.sin(number[topNum-1]);
                                        }
                                        topNum++;
                                        break;
                                    case 'c':
                                        if(drg_flag==true){
                                            number[topNum-1]=Math.cos((number[topNum-1]/180)*pi);
                                        }else{
                                            number[topNum-1] =Math.cos(number[topNum-1]);
                                        }
                                        topNum++;
                                        break;
                                    case 't':
                                        if(drg_flag==true){
                                            if((Math.abs(number[topNum-1])/90)%2==1){
                                                showError(2,str_old);
                                                return;
                                            }
                                            number[topNum-1]=Math.tan((number[topNum-1]/180)*pi);
                                        }else{
                                            if((Math.abs(number[topNum-1])/(pi/2))%2==1){
                                                showError(2,str_old);
                                                return;
                                            }
                                            number[topNum-1]=Math.tan(number[topNum-1]);
                                        }
                                        topNum++;
                                        break;
                                    case 'g':
                                        if(number[topNum-1]<=0){
                                            showError(2,str_old);
                                            return;
                                        }
                                        number[topNum-1]=Math.log(number[topNum-1]);
                                        topNum++;
                                        break;
                                    case '!':
                                        if(number[topNum-1]>170){
                                            showError(3,str_old);
                                            return;
                                        }else if(number[topNum-1]<0){
                                            showError(2,str_old);
                                            return;
                                        }
                                        number[topNum-1]=N(number[topNum-1]);
                                        topNum++;
                                        break;
                                }
                                //取堆栈下一个元素计算
                                topNum--;
                                topOp--;
                            }
                            //如果数字太大，提示错误信息
                            if(number[0]>7.3E306){
                                showError(3,str_old);
                                return;
                            }
                            //输出最终结果
                            input.setText(String.valueOf(FP(number[0])));
                            tip.setText("计算完毕，要继续请按归零键C");
                            mem.setText(str_old+"="+String.valueOf(FP(number[0])));
                        }

                    }
                }

            }
        }
        public double FP(double n){
            Double re=n;
            String s=round(re.toString(),DEF_DIV_SCALE[0]);
            return Double.parseDouble(s);
        }
        public double N(double n){
            int i=0;
            double sum=1;
            //依次将小于等于n的值相乘
            for(i=1;i<=n;i++){
                sum=sum*i;
            }
            return sum;
        }
        public void showError(int code,String str){
            String message="";
            switch (code){
                case 1:
                    message="零不能作为除数";
                    break;
                case 2:
                    message="函数格式错误";
                    break;
                case 3:
                    message="值过大，超出范围";
            }
            input.setText("\""+str+"\""+": "+message);
            tip.setText(message+"\n"+"计算完毕。要继续请按归零键C");
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        init[0]=input.getText().toString();
        outState.putString("exp",init[0]);
    }

    public static String round(String v,int scale){
        if(scale<0){
            try {
                throw new IllegalAccessException("The scale must be a positive intger or zero");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        BigDecimal b=new BigDecimal(v);
        BigDecimal one=new BigDecimal("1");

        BigDecimal re=b.divide(one,scale,BigDecimal.ROUND_HALF_UP);
         return re.toString();
    }

    /*音量键*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String s=input.getText().toString();
        if(s.length()<1){}else{
            switch (keyCode){
                //音量减小
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if(DEF_DIV_SCALE[0]>1){
                        DEF_DIV_SCALE[0]--;
                    }
                    seekbar.setProgress(DEF_DIV_SCALE[0]);
                    Toast.makeText(this.getApplicationContext(),"DOWN"+DEF_DIV_SCALE[0],Toast.LENGTH_LONG).show();
                    if(s.contains("."))input.setText(round(s,DEF_DIV_SCALE[0]));
                    //音量减小时应该执行的功能代码
                    return true;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if(DEF_DIV_SCALE[0]<40){
                            DEF_DIV_SCALE[0]++;
                        }
                        seekbar.setProgress(DEF_DIV_SCALE[0]);
                        Toast.makeText(this.getApplicationContext(),"UP"+DEF_DIV_SCALE[0],Toast.LENGTH_LONG).show();
                        if(s.contains("."))input.setText(round(s,DEF_DIV_SCALE[0]));
                        //音量增大时应该执行的功能代码
                        return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /*滑动条*/
    public SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String s=input.getText().toString();
            if(s.length()<1){
                return;
            }else{
                if(i>DEF_DIV_SCALE[0]){
                    DEF_DIV_SCALE[0]=i;
                    Toast.makeText(getApplicationContext(),"UP"+DEF_DIV_SCALE[0],Toast.LENGTH_LONG).show();
                    if(s.contains("."))input.setText(round(s,DEF_DIV_SCALE[0]));
                }
                else if(i<DEF_DIV_SCALE[0]){
                    DEF_DIV_SCALE[0]= i;
                    Toast.makeText(getApplicationContext(),"DOWN"+DEF_DIV_SCALE[0],Toast.LENGTH_LONG).show();
                    if(s.contains("."))input.setText(round(s,DEF_DIV_SCALE[0]));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /*进制计算*/
    public int BinaryToDecimal(String s){
        int binaryNumber=Integer.valueOf(s);
        int decimal=0;
        int p=0;
        while (true){
            if(binaryNumber==0){
                break;
            }else{
                int temp=binaryNumber%10;
                decimal+=temp*Math.pow(2,p);
                binaryNumber=binaryNumber/10;
                p++;
            }
        }
        return decimal;
    }



}
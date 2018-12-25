package com.example.android.mooddiary.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mooddiary.R;
import com.example.android.mooddiary.diary.db.DiaryDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class HelloChart extends AppCompatActivity {

    @Bind(R.id.home_iv_draw)
    ImageView mIvDraw;
    @Bind(R.id.home_iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.home_tv_title_normal)
    TextView mTvTitle;
    @Bind(R.id.home_tv_title_center)
    TextView mTvCenter;

    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 1;         //线条的数量
    private int numberOfPoints = 30;     //最多点的数量

    float[] randomNumbersTab = new float[numberOfPoints]; //一维数组，最多点的数量

    private boolean hasAxes = true;       //是否有轴，x和y轴
    private boolean hasAxesNames = true;   //是否有轴的名字
    private boolean hasLines = true;       //是否有线（点和点连接的线）
    private boolean hasPoints = true;       //是否有点（每个值的点）
    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
    private boolean isFilled = false;                //是否是填充
    private boolean hasLabels = false;               //每个点是否有名字
    private boolean isCubic = true;                 //是否是立方的，线条是直线还是弧线
    private boolean hasLabelForSelected = false;       //每个点是否可以选择（点击效果）
    private boolean pointsHaveDifferentColor;           //线条的颜色变换
    private boolean hasGradientToTransparent = false;      //是否有梯度的透明

    private ArrayList<MoodBean> values;
    private List<AxisValue> mAxisXValues;
    private List<AxisValue> mAxisYValues;
    private DiaryDatabaseHelper mHelper;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, HelloChart.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpandroidchart);
        ButterKnife.bind(this);
        mHelper = new DiaryDatabaseHelper(this, "Diary.db", null, 1);
        initToolbar();
        initView();
        initData();
        initEvent();
        initChart();
    }

    private void initView() {
        //实例化
        chart = (LineChartView) findViewById(R.id.linechart);
    }

    private void initChart(){
        mAxisXValues = new ArrayList<AxisValue>();   //x轴方向的坐标数据
        mAxisYValues = new ArrayList<AxisValue>();  //y轴方向的坐标数据

        //设置x轴坐标 ，显示的是时间5-1,5-2.。。。
        if(values.size() < numberOfPoints){
            for (int i = 0; i < values.size(); i++) {
                //获取年月日中的月日格式（xxxx 年 xx 月 xx 日 ）
                String data = values.get(i).getDate();
                String[] split = data.split(" 年 ");
                String[] split1 = split[1].split(" ");
                data =split1[0] + " - " + split1[2];
                mAxisXValues.add(new AxisValue(i).setLabel(data));
            }
        }else {
            for (int i = 0; i < numberOfPoints; i++) {
                //获取年月日中的月日格式（xxxx 年 xx 月 xx 日 ）
                String data = values.get(i + values.size() - numberOfPoints).getDate();
                String[] split = data.split(" 年 ");
                String[] split1 = split[1].split(" ");
                data =split1[0] + "-" + split1[2];
                mAxisXValues.add(new AxisValue(i).setLabel(data));
            }
        }

        //设置y轴坐标，显示的是数值0、1、2、3、4、5、6...50。。。
        for (int i = 0; i < 101; i++) {
            mAxisYValues.add(new AxisValue(i).setLabel(i+""));
        }

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                //axisX.setName("日期");//x轴坐标显示的标题
                //axisY.setName("心情");//y轴坐标显示的标题
            }

            //对x轴，数据和属性的设置
            axisX.setTextSize(8);//设置字体的大小
            axisX.setHasTiltedLabels(true);//x坐标轴字体是斜的显示还是直的，true表示斜的
            axisX.setTextColor(R.color.colorPrimary);//设置字体颜色
            axisX.setHasLines(true);//x轴的分割线
            axisX.setValues(mAxisXValues); //设置x轴各个坐标点名称

            //对Y轴 ，数据和属性的设置
            axisY.setTextSize(10);
            axisY.setHasTiltedLabels(false);//true表示斜的
            axisY.setTextColor(R.color.colorPrimary);//设置字体颜色
            axisY.setValues(mAxisYValues); //设置x轴各个坐标点名称


            data.setAxisXBottom(axisX);//x轴坐标线的文字，显示在x轴下方
            //data.setAxisXTop();      //显示在x轴上方
            data.setAxisYLeft(axisY);   //显示在y轴的左边，也可以设置在右边

        }
    }

    private void initData() {
        // Generate some random values.
        generateValues();   //设置线的值数据
        generateData();    //设置数据

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
        resetViewport();   //设置折线图的显示大小
    }

    private void initEvent() {
        chart.setOnValueTouchListener(new ValueTouchListener());

    }

    /**
     * 图像显示大小
     */
    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        if(values.size() < numberOfPoints)
            v.right = values.size()-1;
        else v.right = numberOfPoints-1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 设置四条线条的数据
     */
    private void generateValues() {
        values = new ArrayList<MoodBean>();//数据
        SQLiteDatabase db = mHelper.getWritableDatabase();
        //查询获得游标
        Cursor cursor = db.query ("Diary",null,null,null,null,null,null);
        //判断游标是否为空
        if(cursor.moveToFirst()){
            //遍历游标
            do{
                //获得日期
                String date = cursor.getString(cursor.getColumnIndex("date"));
                //获得心情
                int mood = cursor.getInt(cursor.getColumnIndex("mood"));
                values.add(new MoodBean(date, mood));
            }while (cursor.moveToNext());
        }
        cursor.close();

        if(values.size() < numberOfPoints){
            for (int i = 0; i < values.size(); ++i) {
                int val = values.get(i).getMood();
                randomNumbersTab[i] =  (float) val;
            }
        }else {
            for (int i = 0; i < numberOfPoints; ++i) {
                int val = values.get(i + values.size() - numberOfPoints).getMood();
                randomNumbersTab[i] =  (float) val;
            }
        }
    }


    /**
     * 配置数据
     */
    private void generateData() {
        //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();
        //把数据设置到线条上面去
        for (int i = 0; i < numberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            //line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

//        if (hasAxes) {
//            Axis axisX = new Axis();
//            Axis axisY = new Axis().setHasLines(true);
//            if (hasAxesNames) {
//                axisX.setTextColor(Color.BLACK);//设置x轴字体的颜色
//                axisY.setTextColor(Color.BLACK);//设置y轴字体的颜色
//                axisX.setName("Axis X");
//                axisY.setName("Axis Y");
//            }
//            data.setAxisXBottom(axisX);
//            data.setAxisYLeft(axisY);
//        } else {
//            data.setAxisXBottom(null);
//            data.setAxisYLeft(null);
//        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    /**
     * 触摸监听类
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            String data = values.get((int)value.getX()).getDate();
            String[] split = data.split(" 年 ");
            data = split[1] + "心情值为" + (int)value.getY();
            Toast.makeText(HelloChart.this, data , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {


        }

    }


    private void initToolbar() {
        mIvDraw.setImageResource(R.drawable.app_back);
        mIvDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvTitle.setText("心情统计");
        mTvCenter.setVisibility(View.GONE);
        mIvMenu.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

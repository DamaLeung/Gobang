package com.example.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MyPanel extends View{
    public MyPanel(Context context) {
        this(context, null);
    }
    public MyPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private int mPanelWidth;
    public float mLineHeight;
    private int MAX_LINE_NUM = 10;
    public Chess array[][]=new Chess[MAX_LINE_NUM][MAX_LINE_NUM];
    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    public boolean mod=true;
    public boolean order=true;
    private float pieceScaleRatio = 3 * 1.0f / 4;

    public ArrayList<Point> mWhiteArray = new ArrayList<>();
    public ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean isGameOver;
    private void init() {
        //初始化画笔
        mPaint.setColor(0x88000000);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置防抖动
        mPaint.setDither(true);
        //设置为空心(画线)
        mPaint.setStyle(Paint.Style.STROKE);


        //初始化棋子
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_black_piece);
        for (int i = 0; i < MAX_LINE_NUM; i++){
            for (int j = 0; j < MAX_LINE_NUM; j++) {
                array[i][j] = new Chess();
                array[i][j].occupy = 0;
                array[i][j].offense_tag = 0;
                array[i][j].defense_tag = 0;
                array[i][j].chong4 = 0;
                array[i][j].defense_point = 0;
                array[i][j].offense_point = 0;
                array[i][j].total_point = 0;
                array[i][j].availablle_tag = new int[4];
                array[i][j].defense_direction = new int[4];
                array[i][j].offense_direction = new int[4];
                array[i][j].huo2 = 0;
                array[i][j].huo3 = 0;
                array[i][j].huo4 = 0;
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //此处的逻辑判断是处理当我们自定义的View被嵌套在ScrollView中时,获得的测量模式
        // 会是UNSPECIFIED
        // 使得到的widthSize或者heightSize为0
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        //调用此方法使我们的测量结果生效
        setMeasuredDimension(width, width);
    }
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //此处的参数w就是在onMeasure()方法中设置的自定义View的大小
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE_NUM;

        //将棋子根据行高变化
        int pieceWidth = (int) (pieceScaleRatio * mLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE_NUM; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isGameOver) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){

            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValidPoint(x, y);
            if(point.x>=10||point.y>=10){
                return false;
            }
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)){
                return false;
            }
            if(mod==true){
                personPlay(1,point);
                comPlay();
            }else{
                if(order==true)
                    personPlay(1,point);
                else
                    personPlay(2,point);
            }





        }
        return true;
    }
    protected  void  personPlay(int occupy,Point point){
        int res=0;
        if(occupy==1){
            mWhiteArray.add(point);
        }else{
            mBlackArray.add(point);
        }
        array[point.y][point.x].occupy=occupy;
        res=check(point.y,point.x,occupy);
        invalidate();
        if(res>=5){
            Toast.makeText(getContext(), "玩家获胜", Toast.LENGTH_SHORT).show();
            isGameOver=true;
        }else{
            upDate(occupy);
            order=!order;
        }

    }
    protected  void comPlay(){
        isGameOver=true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Point comPoint=computer();
                Log.i("tips", show());
                mBlackArray.add(new Point(comPoint.y,comPoint.x));
                array[comPoint.x][comPoint.y].occupy=2;
                int res=check(comPoint.x,comPoint.y,2);
                invalidate();
                if(res>=5){
                    Toast.makeText(getContext(), "电脑获胜", Toast.LENGTH_SHORT).show();
                    isGameOver=true;
                }else{
                    upDate(2);
                    isGameOver=false;
                    order=!order;
                }
            }
        }, 800);
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);

    }
    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - pieceScaleRatio) / 2) * mLineHeight,
                    (whitePoint.y + (1 - pieceScaleRatio) / 2) * mLineHeight, null);
        }
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - pieceScaleRatio) / 2) * mLineHeight,
                    (blackPoint.y + (1 - pieceScaleRatio) / 2) * mLineHeight, null);
        }
    }
    public void restart(){
        mBlackArray.clear();
        mWhiteArray.clear();
        order=true;
        isGameOver = false;
        for (int i = 0; i < MAX_LINE_NUM; i++){
            for (int j = 0; j < MAX_LINE_NUM; j++) {
                array[i][j] = new Chess();
                array[i][j].occupy = 0;
                array[i][j].offense_tag = 0;
                array[i][j].defense_tag = 0;
                array[i][j].chong4 = 0;
                array[i][j].defense_point = 0;
                array[i][j].offense_point = 0;
                array[i][j].total_point = 0;
                array[i][j].availablle_tag = new int[4];
                array[i][j].defense_direction = new int[4];
                array[i][j].offense_direction = new int[4];
                array[i][j].huo2 = 0;
                array[i][j].huo3 = 0;
                array[i][j].huo4 = 0;
            }
        }
        //重绘
        invalidate();
    }
    public int check(int x, int y, int occupy) {
        int n = 0;
        int amount = 1;
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        for (int i = 1; i <= 4; i++) {
            if (i == 1) {
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x - j][y - j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x + j][y + j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                if (amount >= n) {
                    n = amount;
                }
                n1 = amount;
                amount = 1;
            }
            if (i == 2) {
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x - j][y].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x + j][y].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                if (amount >= n) {
                    n = amount;
                }
                n2 = amount;
                amount = 1;
            }
            if (i == 3) {
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x - j][y + j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }

                }
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x + j][y - j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                if (amount >= n) {
                    n = amount;
                }
                n3 = amount;
                amount = 1;
            }
            if (i == 4) {
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x][y - j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                for (int j = 1; j <= 4; j++) {
                    try {
                        if (array[x][y + j].occupy == occupy) {
                            amount++;
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
                if (amount >= n) {
                    n = amount;
                }
                n4 = amount;

                amount = 1;
            }
        }
        if (occupy == 1) {
            array[x][y].defense_direction[0] = n1;
            array[x][y].defense_direction[1] = n2;
            array[x][y].defense_direction[2] = n3;
            array[x][y].defense_direction[3] = n4;
        }
        if (occupy == 2) {
            array[x][y].offense_direction[0] = n1;
            array[x][y].offense_direction[1] = n2;
            array[x][y].offense_direction[2] = n3;
            array[x][y].offense_direction[3] = n4;
        }
        return n;
    }
    public void upDate(int occupy) {
        int n = 0;
        for (int x = 0; x < MAX_LINE_NUM; x++) {
            for (int y = 0; y < MAX_LINE_NUM; y++) {
                if (array[x][y].occupy == 0) {
                    n = check(x, y, occupy);
                } else {
                    n = 0;
                    array[x][y].defense_tag = 0;
                    array[x][y].offense_tag = 0;
                    array[x][y].total_point = 0;
                }
                if (occupy == 1) {
                    array[x][y].defense_tag = n;
                }
                if (occupy == 2) {
                    array[x][y].offense_tag = n;
                }
            }
        }

    }
    public int huo(int x, int y, int occupy, int target) {
        int n = 0;
        int tag = 0;
        int direction[] = new int[4];
        int avaliable[] = array[x][y].availablle_tag;
        if (occupy == 1) {
            tag = array[x][y].defense_tag;
            direction = array[x][y].defense_direction;
        }
        if (occupy == 2) {
            tag = array[x][y].offense_tag;
            direction = array[x][y].offense_direction;
        }
        if (tag < target) {
            n = 0;
        } else {
            for (int i = 0; i < 4; i++) {
                int way = 0;
                if (direction[i] == target) {
                    way = i + 1;
                } else {
                    continue;
                }
                if (way == 1 && avaliable[0] >= 4) {
                    for (int j = 1; j <= target; j++) {
                        try {

                            if (array[x - j][y - j].occupy == 0) {
                                if (array[x + ((target - j) + 1)][y + ((target - j) + 1)].occupy == 0) {
                                    n++;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
                if (way == 2 && avaliable[1] >= 4) {
                    for (int j = 1; j <= target; j++) {
                        try {
                            if (array[x - j][y].occupy == 0) {
                                if (array[x + ((target - j) + 1)][y].occupy == 0) {
                                    n++;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
                if (way == 3 && avaliable[2] >= 4) {
                    for (int j = 1; j <= target; j++) {
                        try {
                            if (array[x - j][y + j].occupy == 0) {
                                if (array[x + ((target - j) + 1)][y - ((target - j) + 1)].occupy == 0) {
                                    n++;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
                if (way == 4 && avaliable[3] >= 4) {
                    for (int j = 1; j <=target; j++) {
                        try {
                            if (array[x][y - j].occupy == 0) {
                                if (array[x][y + (target - j) + 1].occupy == 0) {
                                    n++;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }
        }
        return n;
    }
    public int chong(int x, int y, int occupy, int target) {
        int n = 0;
        int re = 0;
        int direction[] = new int[4];
        if (occupy == 1) {
            direction = array[x][y].defense_direction;
        }
        if (occupy == 2) {
            direction = array[x][y].offense_direction;
        }
        for (int i = 0; i < 4; i++) {
            if (direction[i] == target && array[x][y].availablle_tag[i] >= 4)
                n++;
        }
//		两个冲四等于一个活四
        if (target == 4) {
            re = n - array[x][y].huo4;
            int a = re / 2;
            array[x][y].huo4 = array[x][y].huo4 + a;
            re = re - a * 2;
        }

        if (target == 3) {
            re = n - array[x][y].huo3;
            int a = re / 2;
            array[x][y].huo3 = array[x][y].huo3 + a;
            re = re - a * 2;
        }
        if (target == 2) {
            re = n - array[x][y].huo2;
            int a = re / 2;
            array[x][y].huo2 = array[x][y].huo2 + a;
            re = re - a * 2;
        }
        return re;
    }
    public void avaliableCheck(int x, int y, int enemyOccupy) {
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        for (int i = 0; i < 4; i++) {
            int l = 1;
            int r = 1;
            if (i == 0) {
                while (true) {
                    try {
                        if (array[x - l][y - l].occupy == enemyOccupy) {
                            break;
                        } else {
                            n1++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    l++;
                }
                while (true) {
                    try {
                        if (array[x + r][y + r].occupy == enemyOccupy) {
                            break;
                        } else {
                            n1++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    r++;
                }
                array[x][y].availablle_tag[0] = n1;
            }
            if (i == 1) {
                while (true) {
                    try {
                        if (array[x - l][y].occupy == enemyOccupy) {
                            break;
                        } else {
                            n2++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    l++;
                }
                while (true) {
                    try {
                        if (array[x + r][y].occupy == enemyOccupy) {
                            break;
                        } else {
                            n2++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    r++;
                }
                array[x][y].availablle_tag[1] = n2;
            }
            if (i == 2) {
                while (true) {
                    try {
                        if (array[x - l][y + l].occupy == enemyOccupy) {
                            break;
                        } else {
                            n3++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    l++;
                }
                while (true) {
                    try {
                        if (array[x + r][y - r].occupy == enemyOccupy) {
                            break;
                        } else {
                            n3++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    r++;
                }
                array[x][y].availablle_tag[2] = n3;
            }
            if (i == 3) {
                while (true) {
                    try {
                        if (array[x][y - l].occupy == enemyOccupy) {
                            break;
                        } else {
                            n4++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    l++;
                }
                while (true) {
                    try {
                        if (array[x][y + r].occupy == enemyOccupy) {
                            break;
                        } else {
                            n4++;
                        }
                    } catch (Exception e) {
                        break;
                    }
                    r++;
                }
                array[x][y].availablle_tag[3] = n4;
            }
        }
    }
    public void tiaohuo3(int x, int y, int occupy) {
        int direction[] = new int[4];
        if (occupy == 1) {
            direction = array[x][y].defense_direction;
        }
        if (occupy == 2) {
            direction = array[x][y].offense_direction;
        }
        if (array[x][y].huo2 == 0) {
            return;
        } else {
            for (int i = 0; i < 4; i++) {
                if (direction[i] == 2) {
                    if (i == 0) {
                        try {
                            if (array[x - 1][y - 1].occupy == occupy && array[x + 1][y + 1].occupy == 0
                                    && array[x - 2][y - 2].occupy == 0) {
                                if (array[x - 3][y - 3].occupy == occupy && array[x - 4][y - 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 2][y + 2].occupy == occupy && array[x + 3][y + 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                            if (array[x - 1][y - 1].occupy == 0 && array[x + 1][y + 1].occupy == occupy
                                    && array[x + 2][y + 2].occupy == 0) {
                                if (array[x - 2][y - 2].occupy == occupy && array[x - 3][y - 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 3][y + 3].occupy == occupy && array[x + 4][y + 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                    if (i == 1) {
                        try {
                            if (array[x - 1][y].occupy == occupy && array[x + 1][y].occupy == 0
                                    && array[x - 2][y].occupy == 0) {
                                if (array[x - 3][y].occupy == occupy && array[x - 4][y].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 2][y].occupy == occupy && array[x + 3][y].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                            if (array[x - 1][y].occupy == 0 && array[x + 1][y].occupy == occupy
                                    && array[x + 2][y].occupy == 0) {
                                if (array[x - 2][y].occupy == occupy && array[x - 3][y].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 3][y].occupy == occupy && array[x + 4][y].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                    if (i == 2) {
                        try {
                            if (array[x - 1][y + 1].occupy == occupy && array[x + 1][y - 1].occupy == 0
                                    && array[x - 2][y + 2].occupy == 0) {
                                if (array[x - 3][y + 3].occupy == occupy && array[x - 4][y + 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 2][y - 2].occupy == occupy && array[x + 3][y - 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                            if (array[x - 1][y + 1].occupy == 0 && array[x + 1][y - 1].occupy == occupy
                                    && array[x + 2][y - 2].occupy == 0) {
                                if (array[x - 2][y + 2].occupy == occupy && array[x - 3][y + 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x + 3][y - 3].occupy == occupy && array[x + 4][y - 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                    if (i == 3) {
                        try {
                            if (array[x][y - 1].occupy == occupy && array[x][y + 1].occupy == 0
                                    && array[x][y - 2].occupy == 0) {
                                if (array[x][y - 3].occupy == occupy && array[x][y - 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x][y + 2].occupy == occupy && array[x][y + 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                            if (array[x][y - 1].occupy == 0 && array[x][y + 1].occupy == occupy
                                    && array[x][y + 2].occupy == 0) {
                                if (array[x][y - 2].occupy == occupy && array[x][y - 3].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                                if (array[x][y + 3].occupy == occupy && array[x][y + 4].occupy == 0) {
                                    direction[i] = 3;
                                    array[x][y].huo3++;
                                    array[x][y].huo2--;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
        if (occupy == 1) {
            array[x][y].defense_direction = direction;
        }
        if (occupy == 2) {
            array[x][y].offense_direction = direction;
        }
    }
    public Point computer() {
        int x = 0;
        int y = 0;
        int max = 0;
        for (int i = 0; i < MAX_LINE_NUM; i++) {
            for (int j = 0; j < MAX_LINE_NUM; j++) {
                if (array[i][j].defense_tag > 1 || array[i][j].offense_tag > 1) {
                    if (array[i][j].defense_tag >= 5)
                        array[i][j].fin = 100000;
                    avaliableCheck(i, j, 2);
                    array[i][j].huo4 = huo(i, j, 1, 4);
                    array[i][j].chong4 = chong(i, j, 1, 4);
                    array[i][j].huo3 = huo(i, j, 1, 3);
                    array[i][j].chong3 = chong(i, j, 1, 3);
                    array[i][j].huo2 = huo(i, j, 1, 2);
                    tiaohuo3(i, j, 1);
                    array[i][j].chong2 = chong(i, j, 1, 2);
//					对于某些特殊情况必须要进行防守的，不能通过单纯计算得到的结果，特殊处理
//					两个活三
                    if(array[i][j].huo3>=2){
                        int a=array[i][j].huo3/2;
                        array[i][j].huo4=array[i][j].huo4+a;
                        array[i][j].huo3=array[i][j].huo3-a*2;
                    }
//					一个冲四和一个活三
                    if(array[i][j].chong4>=1&&array[i][j].huo3>=1){
                        int m=array[i][j].chong4;
                        if(array[i][j].huo3<array[i][j].chong4){
                            m=array[i][j].huo3;
                        }
                        array[i][j].huo4=array[i][j].huo4+m;
                        array[i][j].huo3=array[i][j].huo3-m;
                        array[i][j].chong4=array[i][j].chong4-m;
                    }
                    array[i][j].defense_point = array[i][j].fin + array[i][j].huo2 * 100 + array[i][j].huo3 * 1000
                            + array[i][j].huo4 * 10000 + array[i][j].chong2 * 10 + array[i][j].chong3 * 100
                            + array[i][j].chong4 * 1000;
                    // 重复利用、减少空间浪费
                    array[i][j].fin = 0;
                    array[i][j].huo4 = 0;
                    array[i][j].chong4 = 0;
                    array[i][j].huo3 = 0;
                    array[i][j].chong3 = 0;
                    array[i][j].huo2 = 0;
                    array[i][j].chong2 = 0;
                    if (array[i][j].offense_tag >= 5)
                        // 如果计算机发现有直接下就有可以赢的点，则直接下这一点
                        // 这一点的分数也设为各种情况中的最大值
                        array[i][j].fin = 150000;
                    avaliableCheck(i, j, 1);
                    array[i][j].huo4 = huo(i, j, 2, 4);
                    array[i][j].chong4 = chong(i, j, 2, 4);
                    array[i][j].huo3 = huo(i, j, 2, 3);
                    array[i][j].chong3 = chong(i, j, 2, 3);
                    array[i][j].huo2 = huo(i, j, 2, 2);
                    tiaohuo3(i, j, 2);
                    array[i][j].chong2 = chong(i, j, 2, 2);
                    if(array[i][j].huo3>=2){
                        int a=array[i][j].huo3/2;
                        array[i][j].huo4=array[i][j].huo4+a;
                        array[i][j].huo3=array[i][j].huo3-a*2;
                    }
                    if(array[i][j].chong4>=1&&array[i][j].huo3>=1){
                        int m=array[i][j].chong4;
                        if(array[i][j].huo3<array[i][j].chong4){
                            m=array[i][j].huo3;
                        }
                        array[i][j].huo4=array[i][j].huo4+m;
                        array[i][j].huo3=array[i][j].huo3-m;
                        array[i][j].chong4=array[i][j].chong4-m;
                    }
                    array[i][j].offense_point = array[i][j].fin + array[i][j].huo2 * 100 + array[i][j].huo3 * 1000
                            + array[i][j].huo4 * 10000 + array[i][j].chong2 * 10 + array[i][j].chong3 * 100
                            + array[i][j].chong4 * 1000;
                    array[i][j].fin = 0;
                    array[i][j].huo4 = 0;
                    array[i][j].chong4 = 0;
                    array[i][j].huo3 = 0;
                    array[i][j].chong3 = 0;
                    array[i][j].huo2 = 0;
                    array[i][j].chong2 = 0;
                    // 根据多次试验得到，防守优先更易取胜
                    array[i][j].total_point = (int) ( array[i][j].offense_point + 1.2*array[i][j].defense_point);
                    if (array[i][j].total_point > max) {
                        max = array[i][j].total_point;
                        x = i;
                        y = j;
                    }
                }
            }
        }
        Point point =new Point(x,y);
        return point;
    }
    public String show(){
        StringBuilder text=new StringBuilder("下面显示每个点的总分数:\n");
        for(int i=0;i<MAX_LINE_NUM;i++){
            for(int j=0;j<MAX_LINE_NUM;j++){
                text.append(String.valueOf(array[i][j].total_point)+"  ");
            }
            text.append("\n");
        }
        return text.toString();
    }
    public void changeMod(boolean mod){
        this.mod=mod;
        if(mod==true&&order==false){
            comPlay();
        }
    }
}

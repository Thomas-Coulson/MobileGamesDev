package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;

public class GameView extends SurfaceView implements Runnable
{

    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Canvas canvas;
    private long fps;
    private long timeThisFrame;
    private Bitmap playerSpriteRight;
    private Bitmap playerSpriteLeft;
    private Bitmap bgSprite1;
    private Bitmap bgSprite2;

    private Bitmap coinSprite;
    private int coinX = 0;
    private int coinY = 0;
    private int currentCoins = 0;
    private int totalCoins = 0;
    private Coin coinIcon;
    private int powerUpCoinsNeeded = 10;

    private Player player;
    private int playerStartPosX = 180, playerStartPosY = 140;
    private int currentLevelIndex = 0;

    private Bitmap[] levelBackgrounds = new Bitmap[2];//stores all level images
    private int[] levelFileIds = new int[2];

    //max of 5 coins per level
    private int maxLevelCoins = 6; //includes coin used as the coinCount Sprite
    private Coin[] levelCoins = new Coin[maxLevelCoins];

    private int screenWidth = 720, screenHeight = 1612;
    private int gridX = 8;
    private int gridY = 18;
    private int gridSize = 90;//pixels
    private GridNode[][] gameGrid = new GridNode[gridY][gridX];// nodes stored [y][x] in array

    private long startTime = System.currentTimeMillis();
    private int levelStartTime = 30;//seconds
    private long elapsedLevelTime = 0;
    private int coinTimeAddition = 10;//seconds

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        surfaceHolder = getHolder();

        //save levelFileIds
        levelFileIds[0] = R.raw.level1;
        levelFileIds[1] = R.raw.level2;

        loadSprites();

        //loadLevel will throw an exception if file cannot be read
        try
        {
            loadLevel();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();;
        }

        player = new Player(playerSpriteRight, playerSpriteLeft);
        coinIcon = new Coin(510, 25, coinSprite, false);//coin sprite for UI
        coinIcon.SetVisible(true);
    }

    public void loadLevel() throws IOException
    {
        for(int i = 0; i < maxLevelCoins; i++)
        {
            levelCoins[i] = new Coin(0, 0, coinSprite, true);
        }

        //read level from file
        String string = "";
        StringBuilder stringbuilder = new StringBuilder();
        InputStream inputStream = this.getResources().openRawResource(levelFileIds[currentLevelIndex]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(true)
        {
            try
            {
                if((string = reader.readLine()) == null)
                {
                    break;
                }
            }
            catch(IOException exception)
            {
                exception.printStackTrace();
            }
            stringbuilder.append(string).append("");
        }
        inputStream.close();


        String levelString = string.valueOf(stringbuilder);

        int levelStringIndex = 0;

        //make gameGrid
        for(int y = 0; y < gridY; y++)
        {
            for(int x = 0; x < gridX; x++)
            {
                //Contruct level based on loaded file.
                NodeType nodeType = NodeType.none;
                if(levelString.charAt(levelStringIndex) == 'X')
                {
                    nodeType = NodeType.wall;
                }
                else if(levelString.charAt(levelStringIndex) == 'C')
                {
                    nodeType = NodeType.coin;
                    for(int i = 0; i < maxLevelCoins; i++)
                    {
                        if(!levelCoins[i].GetVisible())
                        {
                            levelCoins[i].SetNewPosition(x * gridSize, y * gridSize);
                            levelCoins[i].SetVisible(true);
                            break;
                        }

                    }
                }

                gameGrid[y][x] = new GridNode(nodeType, gridSize, x * gridSize, y * gridSize);
                levelStringIndex++;
            }
        }
    }

    public void loadSprites()
    {
        playerSpriteRight = BitmapFactory.decodeResource(getResources(), R.drawable.playerright);
        playerSpriteLeft = BitmapFactory.decodeResource(getResources(), R.drawable.playerleft);

        bgSprite1 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg1);
        bgSprite1 = Bitmap.createScaledBitmap(bgSprite1, screenWidth, screenHeight, false);
        levelBackgrounds[0] = bgSprite1;

        bgSprite2 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg2);
        bgSprite2 = Bitmap.createScaledBitmap(bgSprite2, screenWidth, screenHeight, false);
        levelBackgrounds[1] = bgSprite2;

        coinSprite = BitmapFactory.decodeResource(getResources(), R.drawable.coin);

    }

    @Override
    public void run()
    {
        while(playing)
        {
            if(elapsedLevelTime >= levelStartTime*1000)
            {
                Log.v("Timer", "Level Time is up");
            }
            //game loop
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update()
    {
        //update Game Timer
        elapsedLevelTime = (new Date()).getTime() - startTime;

        //update player
        player.update(fps, this);

        if(player.GetPositionY() > screenHeight)
        {
            loadNextLevel();
        }

        //update Collisions
        checkCollisions();

    }

    public void checkCollisions()
    {
        //check level collisions
        for(int y = 0; y < gridY; y++)
        {
            for(int x = 0; x < gridX; x++)
            {
                //check player against walls (must be in line to collide)
                if(gameGrid[y][x].GetNodeType() == NodeType.wall)
                {
                    switch(player.getMoveDirection())
                    {
                        case Right:
                            //check player and right wall
                            if(player.getHitBox().right >= gameGrid[y][x].GetHitBox().left
                                    && player.getHitBox().top == gameGrid[y][x].GetHitBox().top
                                    && player.getHitBox().bottom == gameGrid[y][x].GetHitBox().bottom
                                    && player.getHitBox().left < gameGrid[y][x].GetHitBox().right)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x-1].GetHitBox().left, gameGrid[y-1][x].GetHitBox().top + 50);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        case Left:
                            //check player and left wall
                            if(player.getHitBox().left <= gameGrid[y][x].GetHitBox().right
                                    && player.getHitBox().top == gameGrid[y][x].GetHitBox().top
                                    && player.getHitBox().bottom == gameGrid[y][x].GetHitBox().bottom
                                    && player.getHitBox().right > gameGrid[y][x].GetHitBox().left)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x].GetHitBox().right, gameGrid[y-1][x].GetHitBox().top + 50);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        case Up:
                            //check player and top wall
                            if(player.getHitBox().top <= gameGrid[y][x].GetHitBox().bottom
                                    && player.getHitBox().right == gameGrid[y][x].GetHitBox().right
                                    && player.getHitBox().left == gameGrid[y][x].GetHitBox().left
                                    && player.getHitBox().bottom > gameGrid[y][x].GetHitBox().top)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x].GetHitBox().left, gameGrid[y][x].GetHitBox().top + 50);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        case Down:
                            //check player and bottom wall
                            if(player.getHitBox().bottom >= gameGrid[y][x].GetHitBox().top
                                    && player.getHitBox().right == gameGrid[y][x].GetHitBox().right
                                    && player.getHitBox().left == gameGrid[y][x].GetHitBox().left
                                    && player.getHitBox().top < gameGrid[y][x].GetHitBox().bottom)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x].GetHitBox().left, gameGrid[y-2][x].GetHitBox().top + 50);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //check all coins against player
        for(int i = 0; i < maxLevelCoins; i++)
        {
            if(levelCoins[i].GetVisible())
            {
                switch(player.getMoveDirection())
                {
                    case Right:
                        //check player and right wall
                        if(player.getHitBox().right >= levelCoins[i].GetHitBox().left
                                && player.getHitBox().top == levelCoins[i].GetHitBox().top
                                && player.getHitBox().bottom == levelCoins[i].GetHitBox().bottom
                                && player.getHitBox().left < levelCoins[i].GetHitBox().right)
                        {
                            //player picks up coin
                            levelCoins[i].SetVisible(false);
                            currentCoins++;
                            totalCoins++;
                            addCoinTime();
                            break;
                        }
                        break;
                    case Left:
                        //check player and left wall
                        if(player.getHitBox().left <= levelCoins[i].GetHitBox().right
                                && player.getHitBox().top == levelCoins[i].GetHitBox().top
                                && player.getHitBox().bottom == levelCoins[i].GetHitBox().bottom
                                && player.getHitBox().right > levelCoins[i].GetHitBox().left)
                        {
                            //player picks up coin
                            levelCoins[i].SetVisible(false);
                            currentCoins++;
                            totalCoins++;
                            addCoinTime();
                            break;
                        }
                        break;
                    case Up:
                        //check player and top wall
                        if(player.getHitBox().top <= levelCoins[i].GetHitBox().bottom
                                && player.getHitBox().right == levelCoins[i].GetHitBox().right
                                && player.getHitBox().left == levelCoins[i].GetHitBox().left
                                && player.getHitBox().bottom > levelCoins[i].GetHitBox().top)
                        {
                            //player picks up coin
                            levelCoins[i].SetVisible(false);
                            currentCoins++;
                            totalCoins++;
                            addCoinTime();
                            break;
                        }
                        break;
                    case Down:
                        //check player and bottom wall
                        if(player.getHitBox().bottom >= levelCoins[i].GetHitBox().top
                                && player.getHitBox().right == levelCoins[i].GetHitBox().right
                                && player.getHitBox().left == levelCoins[i].GetHitBox().left
                                && player.getHitBox().top < levelCoins[i].GetHitBox().bottom)
                        {
                            //player picks up coin
                            levelCoins[i].SetVisible(false);
                            currentCoins++;
                            totalCoins++;
                            addCoinTime();
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }



    }

    public void movePlayerRight()
    {
        if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Right);
        }
    }

    public void movePlayerLeft()
    {
        if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Left);
        }
    }

    public void movePlayerUp()
    {
        if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Up);
        }
    }

    public void movePlayerDown()
    {
        if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Down);
        }
    }

    public void addCoinTime()
    {
        //check if I need to add coin time
        if(totalCoins % 5 == 0)
        {
            startTime = startTime + (coinTimeAddition * 1000);
        }
    }

    public void draw()
    {
        //game draw
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //(Clear bg)
            //draw level bg
            canvas.drawBitmap(levelBackgrounds[currentLevelIndex], 0, 0, null);

            //draw coins
            for(int i = 0; i < maxLevelCoins; i++)
            {
                levelCoins[i].draw(canvas);
            }

            //draw player
            player.draw(canvas);

            //draw coin UI
            drawCoinUI(canvas);
            //draw timer ui
            drawTimerUI(canvas);

            //draw shake ui
            if(currentCoins >= powerUpCoinsNeeded)
                drawShakeUI(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    public void drawTimerUI(Canvas canvas)
    {
        long timerValue = levelStartTime - (elapsedLevelTime / 1000);

        //draw UI
        Paint uiTRectPaint = new Paint();
        uiTRectPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, 225, 155, uiTRectPaint);

        uiTRectPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, 220, 150, uiTRectPaint);

        Paint uiTextPaint = new Paint();
        if(timerValue <= 10)//make timer red when low
            uiTextPaint.setColor(Color.RED);
        else
            uiTextPaint.setColor(Color.WHITE);
        uiTextPaint.setTextSize(60);
        canvas.drawText(String.valueOf(levelStartTime - (elapsedLevelTime / 1000)), 60, 90, uiTextPaint);
    }

    public void drawCoinUI(Canvas canvas)
    {
        //draw UI
        Paint uiTRectPaint = new Paint();
        uiTRectPaint.setColor(Color.WHITE);
        canvas.drawRect(495, 0, 720, 155, uiTRectPaint);

        uiTRectPaint.setColor(Color.BLACK);
        canvas.drawRect(500, 0, 720, 150, uiTRectPaint);

        coinIcon.draw(canvas);

        Paint uiTextPaint = new Paint();
        uiTextPaint.setColor(Color.WHITE);
        uiTextPaint.setTextSize(60);
        canvas.drawText(String.valueOf(currentCoins), 610, 90, uiTextPaint);
    }

    public void drawShakeUI(Canvas canvas)
    {
        Paint uiTextPaint = new Paint();
        uiTextPaint.setColor(Color.WHITE);
        uiTextPaint.setTextSize(60);
        canvas.drawText("-SHAKE-", 500, 220, uiTextPaint);
    }



    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("GameView", "Interrupted");
        }
    }

    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void loadNextLevel()
    {
        //Increase level index (cycle round when at end of list)
        if(currentLevelIndex >= levelFileIds.length - 1)
            currentLevelIndex = 0;
        else
            currentLevelIndex++;

        //load next level
        try
        {
            loadLevel();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();;
        }

        player.setPosition(playerStartPosX, playerStartPosY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN :
                player.onTouchScreen();
                break;
        }
        return true;
    }

}

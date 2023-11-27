package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.content.Intent;
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
    private Bitmap bgSprite3;
    private Bitmap bgSprite4;
    private Bitmap bgSprite5;
    private Bitmap bgSprite6;

    private Bitmap coinSprite;
    private int coinX = 0;
    private int coinY = 0;
    private int currentCoins = 0;
    private int totalCoins = 0;
    private Coin coinIcon;
    private int powerUpCoinsNeeded = 10;

    private Player player;
    private int playerStartPosX = 0, playerStartPosY = 0;
    private int currentLevelIndex = 0;
    private int playerBaseSpeed = 700;
    private int playerBoostedSpeed = 1200;

    private Bitmap[] levelBackgrounds = new Bitmap[6];//stores all level images
    private int[] levelFileIds = new int[6];

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

    private long startPowerupTime;
    private long elapsedPowerupTime = 0;
    private int powerupDuration = 5;//seconds
    private boolean poweredUp = false;

    private long startGameOverTime;
    private long elapsedGameOverTime = 0;
    private int gameOverTimeDuration = 2;//seconds

    private boolean gameOver = false;
    private boolean canRestart = false;
    private GameActivity gameActivity;

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        surfaceHolder = getHolder();

        //save levelFileIds
        levelFileIds[0] = R.raw.level1;
        levelFileIds[1] = R.raw.level2;
        levelFileIds[2] = R.raw.level3;
        levelFileIds[3] = R.raw.level4;
        levelFileIds[4] = R.raw.level5;
        levelFileIds[5] = R.raw.level6;

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
        player.setPosition(playerStartPosX, playerStartPosY);
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
                else if(levelString.charAt(levelStringIndex) == 'P')
                {
                    playerStartPosX = x * 90;
                    playerStartPosY = (y * 90) - 40;
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

        bgSprite3 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg3);
        bgSprite3 = Bitmap.createScaledBitmap(bgSprite3, screenWidth, screenHeight, false);
        levelBackgrounds[2] = bgSprite3;

        bgSprite4 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg4);
        bgSprite4 = Bitmap.createScaledBitmap(bgSprite4, screenWidth, screenHeight, false);
        levelBackgrounds[3] = bgSprite4;

        bgSprite5 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg5);
        bgSprite5 = Bitmap.createScaledBitmap(bgSprite5, screenWidth, screenHeight, false);
        levelBackgrounds[4] = bgSprite5;

        bgSprite6 = BitmapFactory.decodeResource(getResources(), R.drawable.levelbg6);
        bgSprite6 = Bitmap.createScaledBitmap(bgSprite6, screenWidth, screenHeight, false);
        levelBackgrounds[5] = bgSprite6;

        coinSprite = BitmapFactory.decodeResource(getResources(), R.drawable.coin);

    }

    @Override
    public void run()
    {
        while(playing)
        {
            long startFrameTime = System.currentTimeMillis();

            if(elapsedLevelTime >= levelStartTime*1000)
            {
                //Log.v("Timer", "Level Time is up");
                //Log.v("Timer", "gameOver = " + gameOver);
                if(gameOver == false)
                {
                    //Log.v("GOTimer", "game over timer started");
                    startGameOverTimer();
                }
                gameOver = true;

            }
            //Log.v("Timer", "CanRestart = " + canRestart);
            if(gameOver && !canRestart)
            {
                Log.v("Timer", "restart timer finished");
                if(elapsedGameOverTime >= gameOverTimeDuration*1000)
                {
                    Log.v("Timer", "restart timer finished");
                    canRestart = true;
                }
            }
            if(poweredUp)
            {
                if(elapsedPowerupTime >= powerupDuration*1000)
                {
                    stopSpeedBoost();
                }
            }

            //game loop
            if(!gameOver)
            {
                update();
            }
            else
            {
                //essentially pause game herre
                player.setMoveDirection(MoveDirection.Stopped);
            }

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

        //update powerup timer
        if(poweredUp)
        {
            elapsedPowerupTime = (new Date()).getTime() - startPowerupTime;
        }

        if(gameOver && !canRestart)
        {
            Log.v("Timer", "Updating elsapsed time");
            elapsedGameOverTime = (new Date()).getTime() - startGameOverTime;
        }

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
        if(gameOver)
        {
            //reset game
            resetGame();
        }
        else if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Up);
        }
    }

    public void movePlayerDown()
    {
        if(gameOver)
        {
            //go back to main activity
            gameActivity.finish();
        }
        else if(player.getMoveDirection() == MoveDirection.Stopped)
        {
            player.setMoveDirection(MoveDirection.Down);
        }
    }

    public void setGameActivity(GameActivity currentGameActivity){gameActivity = currentGameActivity;}

    public void onShakeDetected()
    {
        startSpeedBoost();
    }

    public void startSpeedBoost()
    {
        if(currentCoins >= 10 && !poweredUp)
        {
            currentCoins -= 10;
            startPowerupTime = System.currentTimeMillis();
            player.setPlayerSpeed(playerBoostedSpeed);
            poweredUp = true;
        }
    }

    public void stopSpeedBoost()
    {
        poweredUp = false;
        player.setPlayerSpeed(playerBaseSpeed);
        elapsedPowerupTime = 0;
    }

    public void startGameOverTimer()
    {
        elapsedGameOverTime = 0;
        startGameOverTime = System.currentTimeMillis();
    }

    public void addCoinTime()
    {
        //check if I need to add coin time
        if(totalCoins % 5 == 0)
        {
            startTime = startTime + (coinTimeAddition * 1000);
        }
    }

    public void resetGame()
    {
        currentLevelIndex = 0;
        //empty visited list when randomisising levels

        stopSpeedBoost();

        //loadLevel will throw an exception if file cannot be read
        try
        {
            loadLevel();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();;
        }

        player.setPosition(playerStartPosX, playerStartPosY);
        totalCoins = 0;
        currentCoins = 0;

        startTime = System.currentTimeMillis();
        elapsedLevelTime = 0;

        gameOver = false;
        canRestart = false;
    }

    public void draw()
    {
        //game draw
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //(Clear bg)

            canvas.drawColor(Color.BLACK);

            if(!gameOver)
            {
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
            }
            else
            {
                //game over screen
                drawGameOverrUI(canvas);
            }

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

    public void drawGameOverrUI(Canvas canvas)
    {
        Paint uiTextPaint = new Paint();
        uiTextPaint.setColor(Color.WHITE);
        uiTextPaint.setTextSize(60);
        canvas.drawText("GAME OVER", 50, 400, uiTextPaint);

        if(canRestart)
        {
            Paint uiSwipeUpTextPaint = new Paint();
            uiTextPaint.setColor(Color.WHITE);
            uiTextPaint.setTextSize(50);
            canvas.drawText("Swipe up to restart", 50, 700, uiTextPaint);

            Paint uiTextSwipeDownPaint = new Paint();
            uiTextPaint.setColor(Color.WHITE);
            uiTextPaint.setTextSize(50);
            canvas.drawText("Swipe down for Title Screen", 50, 1000, uiTextPaint);
        }

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

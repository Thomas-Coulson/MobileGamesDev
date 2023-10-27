package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;

public class GameView extends SurfaceView implements Runnable
{

    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Canvas canvas;
    private long fps;
    private long timeThisFrame;
    private Bitmap playerSprite;
    private Bitmap bgSprite;

    private Player player;

    private int gridX = 8;
    private int gridY = 16;
    private int gridSize = 135;//pixels
    private GridNode[][] gameGrid = new GridNode[gridY][gridX];// nodes stored [y][x] in array

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d("GameView", "Constructor");
        surfaceHolder = getHolder();

        //make gameGrid
        for(int y = 0; y < gridY; y++)
        {
            for(int x = 0; x < gridX; x++)
            {
                //temp, manually place wall;
                NodeType nodeType = NodeType.path;
                if((x == 1 && y == 7) || (x == 6  && y == 7))//left and right main
                {
                    nodeType = NodeType.wall;
                }
                if((x == 2  && y == 6) || (x == 5 && y == 6))//left and right top
                {
                    nodeType = NodeType.wall;
                }
                if((x == 2  && y ==8) || (x == 5 && y == 8))//left and right bottom
                {
                    nodeType = NodeType.wall;
                }
                gameGrid[y][x] = new GridNode(nodeType, gridSize, x * gridSize, y * gridSize);
            }
        }

        playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.testplayer);
        player = new Player(playerSprite);

        bgSprite = BitmapFactory.decodeResource(getResources(), R.drawable.testbg);
        bgSprite = Bitmap.createScaledBitmap(bgSprite, 1080, 2088, false);
    }

    @Override
    public void run()
    {
        while(playing)
        {
            //game loop
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame >= 1)
            {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update()
    {
        //update player
        player.update(fps, this);

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
                                player.setPosition(gameGrid[y][x-1].GetHitBox().left, gameGrid[y-1][x].GetHitBox().top + 95);//yPos = +95 from node above
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
                                player.setPosition(gameGrid[y][x].GetHitBox().right, gameGrid[y-1][x].GetHitBox().top + 95);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        case Up:
                            //check player and left wall
                            if(player.getHitBox().top <= gameGrid[y][x].GetHitBox().bottom
                                    && player.getHitBox().right == gameGrid[y][x].GetHitBox().right
                                    && player.getHitBox().left == gameGrid[y][x].GetHitBox().left
                                    && player.getHitBox().bottom > gameGrid[y][x].GetHitBox().top)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x].GetHitBox().left, gameGrid[y][x].GetHitBox().top + 95);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        case Down:
                            //check player and left wall
                            if(player.getHitBox().bottom >= gameGrid[y][x].GetHitBox().top
                                    && player.getHitBox().right == gameGrid[y][x].GetHitBox().right
                                    && player.getHitBox().left == gameGrid[y][x].GetHitBox().left
                                    && player.getHitBox().top < gameGrid[y][x].GetHitBox().bottom)
                            {
                                //player collides with wall
                                player.setPosition(gameGrid[y][x].GetHitBox().left, gameGrid[y-2][x].GetHitBox().top + 95);//yPos = +95 from node above
                                player.setMoveDirection(MoveDirection.Stopped);
                                break;//dont need to check other collions once we have stopped
                            }
                            break;
                        //copy above for other directions
                        default:
                            break;

                    }
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


    public void draw()
    {
        //game draw
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //(Clear bg)
            //draw testbg
            canvas.drawBitmap(bgSprite, 0, 0, null);

            //draw player
            player.draw(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
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

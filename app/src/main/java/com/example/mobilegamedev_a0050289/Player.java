package com.example.mobilegamedev_a0050289;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

enum MoveDirection
{
    Up,
    Right,
    Down,
    Left,
    Stopped;
}

public class Player
{
    private Bitmap playerSprite;

    private int frameScale = 2;
    private int frameW = 60 * frameScale, frameH = 78 * frameScale;
    private int frameCount = 4;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;

    private float xPos = 500, yPos = 500;
    private float speed = 1000; //px/s
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, frameH);

    private MoveDirection moveDirection = MoveDirection.Stopped;

    public Player(Bitmap sprite)
    {
        playerSprite = Bitmap.createScaledBitmap(sprite, frameW * frameCount, frameH, false);
    }

    public void update(float fps, GameView gameView)
    {

        //Log.v("GameViewX", "GameView Width = " + gameView.getWidth());
        //Log.v("GameViewY", "GameView Height = " + gameView.getHeight());

        //always move on update (for now)
        switch(moveDirection)
        {
            case Up:
                //move up
                yPos = yPos - speed / fps;
                if(yPos < 0)
                {
                    yPos = gameView.getHeight() - frameH;
                }
                break;
            case Right:
                //move right
                xPos = xPos + speed / fps;
                if(xPos > gameView.getWidth())
                {
                    xPos = 10;
                }
                break;
            case Down:
                //move Down
                yPos = yPos + speed / fps;
                if(yPos + frameH > gameView.getHeight())
                {
                    yPos = 10;
                }
                break;
            case Left:
                //move left
                xPos = xPos - speed / fps;
                if(xPos < 0)
                {
                    xPos = gameView.getWidth() - frameW;
                }
                break;
            default:
                break;
        }
    }

    protected void manageCurrentFrame()
    {
        long time = System.currentTimeMillis();
        if(time > lastFrameChangeTime + frameLengthInMS)
        {
            lastFrameChangeTime = time;
            currentFrame++;

            if(currentFrame >= frameCount)
            {
                currentFrame = 0;
            }
        }

        frameToDraw.left = currentFrame * frameW;
        frameToDraw.right = frameToDraw.left + frameW;
    }

    public void draw(Canvas canvas)
    {
        whereToDraw.set(xPos, yPos, xPos + frameW, yPos + frameH);
        manageCurrentFrame();
        canvas.drawBitmap(playerSprite, frameToDraw, whereToDraw, null);
    }

    public void onTouchScreen()
    {
        moveDirection =MoveDirection.Down;
    }


}

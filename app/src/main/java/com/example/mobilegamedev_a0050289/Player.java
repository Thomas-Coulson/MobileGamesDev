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

   // private double frameScale = 2.25;
    private int frameW = 135, frameH = 175;
    private int frameCount = 4;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;

    private float xPos = 270, yPos = 900;// (+90 from grid pos above) (should really need in actual game)
    private float speed = 700; //px/s
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, frameH);

    private MoveDirection moveDirection = MoveDirection.Stopped;

    public Player(Bitmap sprite)
    {
        playerSprite = Bitmap.createScaledBitmap(sprite, frameW * frameCount, frameH, false);
    }

    public void update(float fps, GameView gameView)
    {
        //always move on update (for now)
        if(xPos >= 675)//(test bg purposes)
        {
            xPos = 675;
            moveDirection = MoveDirection.Stopped;
        }

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
                    xPos = -frameW;
                }
                break;
            case Down:
                //move Down
                yPos = yPos + speed / fps;
                if(yPos > gameView.getHeight())
                {
                    yPos = -frameH;
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
        //animate sprite
        long time = System.currentTimeMillis();

        //Don't Animate if stopped
        if(moveDirection == MoveDirection.Stopped)
        {
            currentFrame = 0;
        }
        else
        {
            if(time > lastFrameChangeTime + frameLengthInMS)
            {
                lastFrameChangeTime = time;
                currentFrame++;

                if(currentFrame >= frameCount)
                {
                    currentFrame = 0;
                }
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
        if(moveDirection == MoveDirection.Stopped)
        {
            moveDirection =MoveDirection.Right;
        }
        else
        {
            moveDirection = MoveDirection.Stopped;
        }
    }


}

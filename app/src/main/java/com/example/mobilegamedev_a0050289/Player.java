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
    private Bitmap playerSpriteR;
    private Bitmap playerSpriteL;
    private Bitmap currentSprite;

    private int frameW = 90, frameH = 130;
    private int frameCount = 4;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;

    private float xPos = 180, yPos = 140;//(should use grid pos') (-40 above current node)
    private float speed = 700; //px/s
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);

    private int gridSize = 90;
    private RectF hitBox;

    private MoveDirection moveDirection = MoveDirection.Stopped;

    public Player(Bitmap spriteR, Bitmap spriteL)
    {
        playerSpriteR = Bitmap.createScaledBitmap(spriteR, frameW * frameCount, frameH, false);
        playerSpriteL = Bitmap.createScaledBitmap(spriteL, frameW * frameCount, frameH, false);
        currentSprite = playerSpriteR;
        //weird top pos as sprite extends over grid size
        hitBox = new RectF(xPos, (yPos + frameH) - gridSize, xPos + frameW, yPos + frameH);
    }

    public void update(float fps, GameView gameView)
    {
        //(only allow off edge cycling in horizontal directions)
        switch(moveDirection)
        {
            case Up:
                //move up
                yPos = yPos - speed / fps;
                break;
            case Right:
                //move right
                xPos = xPos + speed / fps;
                currentSprite = playerSpriteR;
                if(xPos > gameView.getWidth())
                {
                    xPos = -frameW;
                }
                break;
            case Down:
                //move Down
                yPos = yPos + speed / fps;
                break;
            case Left:
                //move left
                xPos = xPos - speed / fps;
                currentSprite = playerSpriteL;
                if(xPos < 0)
                {
                    xPos = gameView.getWidth() - frameW;
                }
                break;
            default:
                break;
        }

        //update player hitbox
        hitBox.set(xPos, (yPos + frameH) - gridSize, xPos + frameW, yPos + frameH);
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
        canvas.drawBitmap(currentSprite, frameToDraw, whereToDraw, null);
    }

    public void setMoveDirection(MoveDirection moveDir)
    {
        moveDirection = moveDir;
    }

    public void setPosition(int x, int y)
    {
        xPos = x;
        yPos = y;
    }

    public void setPlayerSpeed(int newSpeed){speed = newSpeed;}

    public float GetPositionX() { return xPos; }
    public float GetPositionY() { return yPos; }
    public MoveDirection getMoveDirection()
    {
        return moveDirection;
    }
    public RectF getHitBox()
    {
        return hitBox;
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

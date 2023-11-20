package com.example.mobilegamedev_a0050289;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Coin {

    //private boolean pickedUp = false;
    private boolean visible = false;
    private boolean animated = false;
    private int xPos = 0, yPos = 0;

    private Bitmap sprite;
    private int frameW = 90, frameH = 90;
    private int frameCount = 4;
    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);

    private RectF Hitbox = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);

    public Coin(int xpos, int ypos, Bitmap coinSprite, boolean isAnimated)
    {
        sprite = Bitmap.createScaledBitmap(coinSprite, frameW * frameCount, frameH, false);
        xPos = xpos;
        yPos = ypos;
        animated = isAnimated;
        Hitbox = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);
        whereToDraw = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);
    }

    protected void manageCurrentFrame()
    {
        //animate sprite
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

        if(visible)
        {
            whereToDraw = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);
            if(animated)
                manageCurrentFrame();
            canvas.drawBitmap(sprite, frameToDraw, whereToDraw, null);
        }

    }

    public RectF GetHitBox(){return Hitbox;}
    public boolean GetVisible(){return visible;}
    public void SetVisible(boolean Visible){visible = Visible;}

    public void SetNewPosition(int x, int y)
    {
        xPos = x;
        yPos = y;
        Hitbox = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);
    }

}

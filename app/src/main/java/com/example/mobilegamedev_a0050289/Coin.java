package com.example.mobilegamedev_a0050289;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Coin {

    private Bitmap sprite;
    private int frameW = 90, frameH = 90;
    //private boolean pickedUp = false;
    private boolean visible = false;
    private int xPos = 0, yPos = 0;
    private RectF Hitbox = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);

    public Coin(int xpos, int ypos, Bitmap coinSprite)
    {
        sprite = Bitmap.createScaledBitmap(coinSprite, frameW, frameH, false);
        xPos = xpos;
        yPos = ypos;
        Hitbox = new RectF(xPos, yPos, xPos + frameW, yPos + frameH);
    }

    public void draw(Canvas canvas)
    {
        if(visible)
        {
            canvas.drawBitmap(sprite, xPos, yPos, null);
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

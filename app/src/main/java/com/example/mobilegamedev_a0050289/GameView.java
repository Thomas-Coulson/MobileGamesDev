package com.example.mobilegamedev_a0050289;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private Player player;

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Log.d("GameView", "Constructor");
        surfaceHolder = getHolder();

        playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.run);
        player = new Player(playerSprite);
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
        //game update

        //update player
        player.update(fps, this);

    }

    public void draw()
    {
        //game draw
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //currently drawing white background
            canvas.drawColor(Color.WHITE);
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

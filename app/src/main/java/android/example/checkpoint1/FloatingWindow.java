package android.example.checkpoint1;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FloatingWindow extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    ImageView ivWidget;
    float height, width;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent == null || !intent.getBooleanExtra("Called", false)){
            stopSelf();
            return START_NOT_STICKY;
        }

        helper();

        return START_STICKY;
    }

    private void helper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
        );

        layoutParams.gravity = Gravity.TOP|Gravity.END;
        layoutParams.x = 0;
        layoutParams.y = 0;

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.close_white_foreground);
        imageClose.setVisibility(View.GONE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        if(Build.VERSION.SDK_INT < 30) {
            height = windowManager.getDefaultDisplay().getHeight();
            width = windowManager.getDefaultDisplay().getWidth();
        }else{
            height = windowManager.getCurrentWindowMetrics().getBounds().height();
            width = windowManager.getCurrentWindowMetrics().getBounds().width();
        }


        ivWidget = mFloatingView.findViewById(R.id.imageWidget);

        // drag movements for widget

        ivWidget.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            boolean longPressed = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageClose.setVisibility(View.VISIBLE);

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        longPressed = false;

                        new CountDownTimer(1000, 200) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                //Log.d("TICKING", "onTick: ");
                            }

                            @Override
                            public void onFinish() {
                                longPressed = true;
                                //Log.d("FINISHED", "FINISHED TICKING: ");
                            }
                        }.start();

                        return true;
                    case MotionEvent.ACTION_UP:
                        imageClose.setVisibility(View.GONE);
                        layoutParams.x = initialX + (int) (initialTouchX - event.getRawX());
                        layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if (layoutParams.y > (height*0.8) && (layoutParams.x >=width*0.3 && layoutParams.x <= width*0.7)) {
                            stopSelf();
                        }

                        //Log.d("TAG", initialTouchX +" " +event.getRawX() + " " + initialTouchY +" " +event.getRawY() + longPressed);

                        if(Math.abs(initialTouchX - event.getRawX()) <= 2 && Math.abs(initialTouchY - event.getRawY()) <=2 && !longPressed){
                            Log.d("ICON TOuched", "TOUCHED");
                            Toast.makeText(getApplicationContext(), BackgroundService.currentApp, Toast.LENGTH_SHORT).show();
                        }
                        longPressed = false;

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = initialX + (int) (initialTouchX - event.getRawX());
                        layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        windowManager.updateViewLayout(mFloatingView, layoutParams);
                        imageClose.setImageResource(R.drawable.close_white_foreground);
                        return true;
                }
                return false;
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            windowManager.removeView(mFloatingView);
        }
        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }
}
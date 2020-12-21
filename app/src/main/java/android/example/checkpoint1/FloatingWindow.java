package android.example.checkpoint1;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FloatingWindow extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    TextView tvWidget;
    float height, width;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        layoutParams.gravity = Gravity.TOP|Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 100;

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
            140,
            140,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        imageParams.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.close_white_foreground);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mFloatingView, layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        tvWidget = (TextView) mFloatingView.findViewById(R.id.text_widget);

        // drag movements for widget

        tvWidget.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
//            long startClickTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageClose.setVisibility(View.VISIBLE);

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        imageClose.setVisibility(View.GONE);
                        layoutParams.x = initialX + (int) (initialTouchX - event.getRawX());
                        layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return false;
            }
        });

        return START_STICKY;
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

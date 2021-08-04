package com.example.taimoor.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.taimoor.utilities.Constants;
import com.example.taimoor.R;
import com.example.taimoor.utilities.UserKeysSharedPreferences;


/**
 * Activity for loading splash screen.
 * This activity is used to display splash screen at the start of the app.
 */

public class SplashActivity extends Activity {

    protected boolean _active = true;
    protected int _splashTime = 2000;
    private ImageView mLogo;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        initView();


    }

    private void initView() {
        mLogo = (ImageView) findViewById(R.id.logo);
        setAnimation();
    }

    private void setAnimation() {
        animateLogo();
    }


    private void animateLogo() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 2.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(800);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 2.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(800);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(800);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                StartActivityFinishThread();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }


    void StartActivityFinishThread() {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }

                    }
                } catch (InterruptedException e) {

                } finally {

                    launchNextActivity();

                }
            }
        };
        splashTread.start();
    }


    public void launchNextActivity() {
        runOnUiThread(() -> {
            if (UserKeysSharedPreferences.getInstance().getBoolean(Constants.USER_LOGGED_IN) &&
                    !UserKeysSharedPreferences.getInstance().getString(Constants.USER_EMAIL).isEmpty()) {
                startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
           // overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            finish();

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }


    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }
}

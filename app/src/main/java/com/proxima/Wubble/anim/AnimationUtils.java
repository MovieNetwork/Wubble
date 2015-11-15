package com.proxima.Wubble.anim;

/**
 * Created by Emre on 15.3.2015.
 */

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class AnimationUtils {
    public static void scaleXY(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0);
        holder.itemView.setScaleY(0);

        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("scaleX", 1);
        PropertyValuesHolder propy = PropertyValuesHolder.ofFloat("scaleY", 1);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, propx, propy);


        animator.setDuration(800);
        animator.start();
    }

    public static void scaleX(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0);

        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("scaleX", 1);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, propx);


        animator.setDuration(800);
        animator.start();
    }

    public static void scaleY(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleY(0);

        PropertyValuesHolder propy = PropertyValuesHolder.ofFloat("scaleY", 1);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder.itemView, propy);

        animator.setDuration(800);
        animator.start();
    }

    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown) {

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown == true ? 300 : -300, 0);
        animatorTranslateY.setDuration(500);
        animatorTranslateY.start();


    }

    public static void animateToolbar(View containerToolbar) {


    }

    /*
    * Makes fade animation for Relativelayout
    * description is view, which we want to animate
    * isShow is flag for showing or hiding
    * */
    public static void PopupAnimate(RelativeLayout description, boolean isShowing) {
        if (isShowing == true) {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(description);
        } else {
            YoYo.with(Techniques.FadeOut).duration(500).playOn(description);
        }
    }

}

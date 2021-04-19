package com.example.r_edu_kt.HelperClasses.HomeAdapter;

import android.graphics.drawable.GradientDrawable;

public class CategoriesHelperClass {
    int image;
    String title;
    GradientDrawable gradient;

    public CategoriesHelperClass(GradientDrawable gradient,int image, String title) {
        this.image = image;
        this.title = title;
        this.gradient = gradient;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public GradientDrawable getGradient() {
        return gradient;
    }
}

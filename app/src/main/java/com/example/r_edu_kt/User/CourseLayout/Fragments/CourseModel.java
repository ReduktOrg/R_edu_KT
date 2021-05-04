package com.example.r_edu_kt.User.CourseLayout.Fragments;

public class CourseModel {
    private String mainTitle, subTitle;
    private int videoIcon, completedIcon;

    public CourseModel() {
    }

    public CourseModel(String mainTitle, String subTitle, int videoIcon, int completedIcon) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.videoIcon = videoIcon;
        this.completedIcon = completedIcon;
    }

    //getter

    public String getMainTitle() {
        return mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }


    public int getVideoIcon() {
        return videoIcon;
    }

    public int getCompletedIcon() {
        return completedIcon;
    }

    //setter

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setVideoIcon(int videoIcon) {
        this.videoIcon = videoIcon;
    }

    public void setCompletedIcon(int completedIcon) {
        this.completedIcon = completedIcon;
    }


}

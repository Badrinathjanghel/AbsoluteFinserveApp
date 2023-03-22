package com.absolute.bnj;

public class IntroSliderItem {
    String Title,Description, ScreenImg;

    public IntroSliderItem(String title, String description, String screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(String screenImg) {
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getScreenImg() {
        return ScreenImg;
    }
}

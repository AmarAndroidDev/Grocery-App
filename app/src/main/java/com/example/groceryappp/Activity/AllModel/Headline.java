package com.example.groceryappp.Activity.AllModel;

public class Headline {
    int img;
    String  title,category;

    public Headline(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public int getImg() {
        return img;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public Headline() {
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Headline(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public class Alldetails{
        int img;
        String  title;

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}


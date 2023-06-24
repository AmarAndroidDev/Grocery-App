package com.example.groceryappp.Activity.AllModel;

public class Headline {
    String img;
    String  title,category;

    public Headline(String title, String category,String img) {
        this.title = title;
        this.category = category;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public Headline() {
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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


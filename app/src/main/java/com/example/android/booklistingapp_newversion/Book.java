package com.example.android.booklistingapp_newversion;

/**
 * Created by Tudor Cristian on 07.07.2017.
 */

public class Book {

    public String mTitle;
    public String[] mAuthors;
    public String mDescription;
    public String mBookImage;
    public String mUrl;

    public Book(String bookTitle, String[] bookAuthors, String bookDescription, String bookImage, String bookUrl) {
        mTitle = bookTitle;
        mAuthors = bookAuthors;
        mDescription = bookDescription;
        mBookImage = bookImage;
        mUrl = bookUrl;

    }

    public String getBookTitle() {
        return mTitle;
    }

    public String[] getBookAuthors() {
        return mAuthors;
    }

    public String getBookDescription() {
        return mDescription;
    }

    public String getBookImage() {
        return mBookImage;
    }

    public String getBookUrl() {
        return mUrl;
    }
}

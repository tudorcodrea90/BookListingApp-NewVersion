package com.example.android.booklistingapp_newversion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.text.TextUtils.join;

/**
 * Created by Tudor Cristian on 07.07.2017.
 */

public class BookListAdapter extends ArrayAdapter<Book> {

    public BookListAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
        }

        final Book currentBook = getItem(position);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(currentBook.getBookImage());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    Bitmap bookImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Set title for the book
        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        titleView.setText(currentBook.getBookTitle());

        //Set authors for the book
        TextView authorView = (TextView) listItemView.findViewById(R.id.book_authors);
        String authors = join(", ", currentBook.getBookAuthors());
        authorView.setText(authors);

        //Set description for the book
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.book_description);
        descriptionView.setText(currentBook.getBookDescription());

        //Set book image
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.image);
        Glide.with(getContext()).load(currentBook.getBookImage()).into((ImageView) bookImage);

        return listItemView;
    }
}


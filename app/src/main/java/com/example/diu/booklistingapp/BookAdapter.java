package com.example.diu.booklistingapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> /*implements Filterable */{

   // ValueFilter valueFilter;
    //ArrayList<Book> mStringFilterList;
    //ArrayList<Book> books;
    public BookAdapter(Activity context, ArrayList<Book> books){
        super(context,0,books);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView=convertView;
        if(listItemView==null){
            listItemView=LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        // Find the Book at the given position in the list of earthquakes
        Book currentBook=getItem(position);
        TextView book_name = (TextView) listItemView.findViewById(R.id.book_name);
        book_name.setText(currentBook.getName());

        TextView book_author = (TextView) listItemView.findViewById(R.id.book_author);
        book_author.setText(currentBook.getAuthor());
        ImageView book_image = (ImageView) listItemView.findViewById(R.id.book_image);
        Picasso.with(getContext()).load(currentBook.getImageResource()).into(book_image);
        return listItemView;
    }


}

package com.zoco.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoco.common.DiskLruImageCache;
import com.zoco.common.ZocoNetwork;
import com.zoco.obj.BookInfo;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dookim on 2/14/15.
 */

public class BookListAdapter extends ArrayAdapter<BookInfo> {

    protected final Context context;
    private final ArrayList<BookInfo> values;
    DiskLruImageCache diskLruImageCache;
    //캐시!
    //만약에 캐시의 유일한 이름이 사랑질때 새롭게 생성되는지 여부에 대해서 파악할것?
    //아마 예상한대로 될것
    public BookListAdapter(Context context, ArrayList<BookInfo> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        diskLruImageCache = new DiskLruImageCache(context, DiskLruImageCache.CACHE_NAME, Integer.MAX_VALUE);

    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, parent, false);
        }


        //find image in disk;
        final CircleImageView bookImage = (CircleImageView) rowView.findViewById(R.id.book_image);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView author = (TextView) rowView.findViewById(R.id.author);
        TextView price = (TextView) rowView.findViewById(R.id.price);
        Button msgBtn = (Button)rowView.findViewById(R.id.msgBtn);

        BookInfo item = (BookInfo) getItem(position);

        String isbn_4_cache = item.isbn.replaceAll(" ", "_");
        Bitmap bitmap = diskLruImageCache.getBitmap(isbn_4_cache);

        if (bitmap == null) {
            try {
                new DownloadImageTask(bookImage, diskLruImageCache, isbn_4_cache).execute(ZocoNetwork.URL_4_QUERY_IMAGE + URLEncoder.encode(item.isbn, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            bookImage.setImageBitmap(bitmap);
        }
        title.setText(item.title);
        author.setText(item.author);
        price.setText(item.price + "");

        return rowView;
    }


    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        DiskLruImageCache diskLruImageCache;
        String isbn;

        public DownloadImageTask(ImageView bmImage, DiskLruImageCache diskLruImageCache, String isbn) {
            this.bmImage = bmImage;
            this.diskLruImageCache = diskLruImageCache;
            this.isbn = isbn;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            diskLruImageCache.put(isbn, result);
            bmImage.setImageBitmap(result);
        }
    }
}

/*
    class ImageTask extends AsyncTask<String, Void, String> {

        CircleImageView bookImage;

        ImageTask(CircleImageView bookImage) {
            this.bookImage = bookImage;
        }

        @Override
        protected String doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            byte[] bytes=Base64.decode(result, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bookImage.setImageBitmap(bitmap);
            super.onPostExecute(result);
        }
    }

*/
//image 다운로드 해야함
//string to byte로 해도되는지에 대한 의문?



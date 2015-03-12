package com.zoco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zoco.common.ReqTask;
import com.zoco.common.ZocoNetwork;
import com.zoco.obj.BookInfo;
import com.zoco.obj.Item;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookRegisterActivity extends ActionBarActivity {

    CircleImageView circleImageView;
    TextView priceTV;
    TextView authorTV;
    TextView titleTV;
    NumberPicker numberPicker;
    String imgStr;
    Item bookItem;
    CheckBox scribble;
    CheckBox hasAnswer;
    CheckBox checkAnswer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.book_register);

        Intent intent = getIntent();
        bookItem = (Item) intent.getSerializableExtra(MainActivity.BOOK_INFO);

        //url to image
        circleImageView = (CircleImageView)findViewById(R.id.book_image);
        priceTV = (TextView)findViewById(R.id.price);
        authorTV = (TextView)findViewById(R.id.author);
        titleTV = (TextView)findViewById(R.id.title);
        scribble = (CheckBox)findViewById(R.id.scribble);
        hasAnswer = (CheckBox)findViewById(R.id.hasAnswer);
        checkAnswer = (CheckBox)findViewById(R.id.checkAnswer);

        numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMinValue(0);

        String[] prices;

        if(bookItem.price%500 == 0) {
            prices = new String[bookItem.price/500+1];
            numberPicker.setMaxValue(bookItem.price/500);
        } else {
            prices = new String[(bookItem.price/500) + 2];
            numberPicker.setMaxValue((bookItem.price/500) + 1);
            prices[(bookItem.price/500)] = "0";
        }
        int i = 0;
        for(int price = bookItem.price; price >= 0; price -= 500) {
            prices[i++] = price + "";
        }

        numberPicker.setDisplayedValues(prices);

        new DownloadImageTask(circleImageView).execute(bookItem.image);

        priceTV.setText(bookItem.price + "");
        authorTV.setText(bookItem.author);
        titleTV.setText(bookItem.title);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.register) {
            int selectedPrice = Integer.parseInt(numberPicker.getDisplayedValues()[numberPicker.getValue()]);
            BookInfo bookInfo = new BookInfo("doo871128@gmail.com",bookItem.isbn,bookItem.author,bookItem.price,selectedPrice,scribble.isChecked(),checkAnswer.isChecked(),hasAnswer.isChecked(),imgStr,bookItem.title);
            String json = new Gson().toJson(bookInfo);
            Toast.makeText(getBaseContext(), json, Toast.LENGTH_LONG).show();
            new ReqTask(getBaseContext(),ZocoNetwork.Method.POST).execute(ZocoNetwork.URL_4_REGISTER_BOOK,json);
        }
        return super.onOptionsItemSelected(item);
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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

            bmImage.setImageBitmap(result);
            //서버에 업로드 하기위해 img str를 생성함.
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            imgStr = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        }
    }
}

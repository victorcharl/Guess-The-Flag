package com.victorcharl.guessthepicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    Bitmap image;

    @Override
    protected Bitmap doInBackground(String... urls) {
        URL url;
        HttpURLConnection urlConnection;
        InputStream is;

        try {
            url=new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            is=urlConnection.getInputStream();
            image= BitmapFactory.decodeStream(is);
            return image;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.kirschbrown.popflix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jrkirsch on 11/17/2015.
 */
public class FetchMoviePoster extends AsyncTask<String, Void, Void>{

    private final String LOG_TAG = FetchMoviePoster.class.getSimpleName();
    private final Context mContext;

    public FetchMoviePoster(Context context) {
        mContext = context;
    }

    protected Void doInBackground(String... data) {
        String movieId = data[0];
        String posterURL = data[1];

        try {
            String imagePath = downloadImage(mContext, posterURL, movieId);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String downloadImage(Context context, String urlImg, String movieId) throws Exception{

        URL url = new URL(urlImg);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setReadTimeout(10000);
        con.setConnectTimeout(10000);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = context.getFilesDir();
        // Create imageDir
        String fileName = movieId + ".jpg";
        File filePath = new File(directory,fileName);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filePath);
            Bitmap b = BitmapFactory.decodeStream(con.getInputStream());
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        //Log.d("FilePath", filePath.getAbsolutePath());
        return filePath.getAbsolutePath();
    }

}

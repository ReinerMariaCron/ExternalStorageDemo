package com.example.master.externalstoragedemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STROAGE = 456;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
        if(Build.VERSION.SDK_INT>=23){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_WRITE_EXTERNAL_STROAGE);
            } else {
                doIt();
            }
        } else {
            doIt();
        }
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[],int grantResults[]){
        if((requestCode==PERMISSION_REQUEST_WRITE_EXTERNAL_STROAGE) && (grantResults.length>0 &&
                grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            doIt();
        }
    }

    private void doIt() {
        tv.setText(String.format("Medium kann%s entfernt werden", Environment.isExternalStorageRemovable() ? "": " nicht"));
        String state =Environment.getExternalStorageState();
        boolean canRead,canWrite;
        switch (state){
            case Environment.MEDIA_MOUNTED:
                canRead = canWrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                canRead = true;
                canWrite = false;
                break;
            default:
                canRead=canWrite=false;
        }
        tv.append("\ncanRead:"+canRead+" \ncanWrite:"+canWrite+"\n");
        tv.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        tv.append("\nEmulated: "+Environment.isExternalStorageEmulated());
        File dirApps = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+getClass().getPackage().getName()+
                File.separator+"meinTest");
        if(!dirApps.mkdirs()){
            tv.append("\nVerzeichnissstruktur schon vorhanden");
        } else {
            tv.append("\nVerzeichnissstruktur erstellt");
        }
        tv.append("\nBasisverzeichnis: "+getExternalFilesDir(null));
        tv.append("\nApp Bilder: "+getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        tv.append("\npublic picture Verzeichnis: "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        File dirPublicPictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(dirPublicPictures,"meineGrafik.png");
        try(FileOutputStream fos = new FileOutputStream(file)){
            saveBitmap(fos);
        } catch(IOException e){
            Log.e(LOG_TAG,"outputstream: ",e);
        }
    }

    private void saveBitmap(FileOutputStream fos) {
        int w = 100;
        int h = 100;
        Bitmap bm = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bm);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        c.drawRect(0,0,w-1,h-1,paint);
        paint.setColor(Color.BLUE);
        c.drawLine(0,0,w-1,h-1,paint);
        c.drawLine(0,h-1,w-1,0,paint);
        paint.setColor(Color.BLACK);
        c.drawText("Hallo",w/2,h/2,paint);
        //speichern:
        bm.compress(Bitmap.CompressFormat.PNG,100,fos);
    }
}

package com.appdsn.ehttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.ResponseBody;

/**
 *
 * Created by baozhong 2016/02/01
 */
public abstract class BitmapCallback extends HttpCallback<Bitmap>
{
    @Override
    public Bitmap parseResponse(ResponseBody body) throws Exception
    {
        return BitmapFactory.decodeStream(body.byteStream());
    }

}

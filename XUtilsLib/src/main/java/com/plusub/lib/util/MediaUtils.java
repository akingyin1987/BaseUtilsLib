/*
 * FileName: MediaUtil.java
 * Copyright (C) 2014 Plusub Tech. Co. Ltd. All Rights Reserved <admin@plusub.com>
 * 
 * Licensed under the Plusub License, Version 1.0 (the "License");
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * author  : Administrator
 * date     : 2014-6-1 下午9:56:46
 * last modify author :
 * version : 1.0
 */
package com.plusub.lib.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MediaUtils {

    /**
     * 根据视频Uri地址取得指定的视频缩略图
     * @param cr
     * @param uri  本地视频Uri标示
     * @return 返回bitmap类型数据
     */
    public static Bitmap getVideoThumbnail(ContentResolver cr, Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Cursor cursor = cr.query(uri, new String[] { MediaStore.Video.Media._ID }, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)); // image id in image table.s

        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong,
                MediaStore.Images.Thumbnails.MICRO_KIND, options);

        return bitmap;
    }


	/** 
     * 得到amr的时长 
     * @param file 
     * @return 
     * @throws IOException 
     */  
    public static long getAmrDuration(File file) {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
        RandomAccessFile randomAccessFile = null;
        int pos = 6;//设置初始位置
        int frameCount = 0;//初始帧数
        int packedPos = -1;  
        try {
        	long length = file.length();//文件的长度
            randomAccessFile = new RandomAccessFile(file, "rw");  
            byte[] datas = new byte[1];//初始数据值
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            duration += frameCount * 20;//帧数*20
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (randomAccessFile != null) {
                try {
					randomAccessFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return duration;
    } 
}

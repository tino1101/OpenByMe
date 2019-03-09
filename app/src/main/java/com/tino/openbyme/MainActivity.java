package com.tino.openbyme;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView typeTextView;
    private TextView pathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeTextView = findViewById(R.id.type);
        pathTextView = findViewById(R.id.path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
//            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Uri uri = intent.getData();
            String filePath = getRealPathFromURI(this, uri);
            if (!TextUtils.isEmpty(filePath)) {
                typeTextView.setText("文件类型：" + type);
                pathTextView.setText("文件路径：" + filePath);
            }
        }
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        if (null == uri) return null;
        String filePath = null;
        if (uri.getScheme().equalsIgnoreCase("content")) {
            if (uri.getAuthority().equals("com.tencent.mm.external.fileprovider")) {
                filePath = Environment.getExternalStorageDirectory() + uri.getPath().replace("/external", "");
            } else {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);
                if (null != cursor) {
                    try {
                        cursor.moveToFirst();
                        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            }
        } else if (uri.getScheme().equalsIgnoreCase("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }
}

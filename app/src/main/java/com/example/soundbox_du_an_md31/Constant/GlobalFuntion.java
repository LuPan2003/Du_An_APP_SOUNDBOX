package com.example.soundbox_du_an_md31.Constant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.soundbox_du_an_md31.Service.MusicReceiver;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.Service.MyServiceDown;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class GlobalFuntion {

    //Khởi chạy hoạt động mới.
    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //Ẩn bàn phím mềm.
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

//    Mở ứng dụng hoặc trang web Facebook.
    public static void onClickOpenFacebook(Context context) {
        Intent intent;
        try {
            String urlFacebook = Constant.LINK_FACEBOOK;
            PackageManager packageManager = context.getPackageManager();
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlFacebook = "fb://facewebmodal/f?href=" + Constant.LINK_FACEBOOK;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.LINK_FACEBOOK));
        }
        context.startActivity(intent);
    }
    //Mở kênh YouTube cho ứng dụng.
    public static void onClickOpenYoutubeChannel(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.LINK_YOUTUBE)));
    }

    //Gọi số điện thoại được chỉ định trong lớp Constant.
    public static void callPhoneNumber(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Constant.PHONE_NUMBER));
                activity.startActivity(callIntent);

            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Constant.PHONE_NUMBER));
                activity.startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //Hiển thị thông báo toast.
    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    //Xóa dấu phụ âm khỏi một chuỗi.
    public static String getTextSearch(String input) {
        if (input == null) {
            return ""; // hoặc thực hiện xử lý khác tùy theo yêu cầu
        }
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    //Khởi động dịch vụ âm nhạc.
    public static void startMusicService(Context ctx, int action, int songPosition) {
        Intent musicService = new Intent(ctx, MusicService.class);
        musicService.putExtra(Constant.MUSIC_ACTION, action);
        musicService.putExtra(Constant.SONG_POSITION, songPosition);
        ctx.startService(musicService);
    }
    public static void startMusicServiceDown(Context ctx, int action, int songPosition) {
        Intent musicService = new Intent(ctx, MyServiceDown.class);
        musicService.putExtra(Constant.MUSIC_ACTION, action);
        musicService.putExtra(Constant.SONG_POSITION, songPosition);
        ctx.startService(musicService);
    }
    //Trả về một PendingIntent có thể được sử dụng để mở trình nhận âm nhạc.
    @SuppressLint("UnspecifiedImmutableFlag")
    public static PendingIntent openMusicReceiver(Context ctx, int action) {
        Intent intent = new Intent(ctx, MusicReceiver.class);
        intent.putExtra(Constant.MUSIC_ACTION, action);

        int pendingFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        return PendingIntent.getBroadcast(ctx.getApplicationContext(), action, intent, pendingFlag);
    }
}
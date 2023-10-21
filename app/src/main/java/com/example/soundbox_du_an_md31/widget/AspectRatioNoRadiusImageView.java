package com.example.soundbox_du_an_md31.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.soundbox_du_an_md31.R;


public class AspectRatioNoRadiusImageView extends AppCompatImageView {

    public static final int MEASUREMENT_WIDTH = 0;
    public static final int MEASUREMENT_HEIGHT = 1;

    private static final float DEFAULT_ASPECT_RATIO = 1f;
    private static final boolean DEFAULT_ASPECT_RATIO_ENABLED = false;
    private static final int DEFAULT_DOMINANT_MEASUREMENT = MEASUREMENT_WIDTH;

    private float aspectRatio;
    private boolean aspectRatioEnabled;
    private int dominantMeasurement;

    public AspectRatioNoRadiusImageView(Context context) {
        this(context, null);
    }

    public AspectRatioNoRadiusImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //hiển thị ảnh với tỷ lệ khung hình cố định
    public AspectRatioNoRadiusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadStateFromAttrs(attrs);
    }

    //tải các thuộc tính từ XML và lưu trữ chúng vào các biến thành viên của lớp.
    @SuppressLint("CustomViewStyleable")
    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.AspectRatioView);
            aspectRatio = a.getFloat(R.styleable.AspectRatioView_aspectRatio, DEFAULT_ASPECT_RATIO);
            aspectRatioEnabled = a.getBoolean(R.styleable.AspectRatioView_aspectRatioEnabled,
                    DEFAULT_ASPECT_RATIO_ENABLED);
            dominantMeasurement = a.getInt(R.styleable.AspectRatioView_dominantMeasurement,
                    DEFAULT_DOMINANT_MEASUREMENT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    //được gọi khi lớp cần đo kích thước của mình. Hàm này sẽ kiểm tra xem thuộc tính aspectRatioEnabled
    // có được kích hoạt hay không. Nếu có, hàm sẽ tính toán kích thước mới của lớp dựa trên thuộc tính
    // aspectRatio và dominantMeasurement.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!aspectRatioEnabled) return;

        int newWidth;
        int newHeight;
        switch (dominantMeasurement) {
            case MEASUREMENT_WIDTH:
                newWidth = getMeasuredWidth();
                newHeight = (int) (newWidth * aspectRatio);
                break;

            case MEASUREMENT_HEIGHT:
                newHeight = getMeasuredHeight();
                newWidth = (int) (newHeight * aspectRatio);
                break;

            default:
                throw new IllegalStateException("Unknown measurement with ID " + dominantMeasurement);
        }

        setMeasuredDimension(newWidth, newHeight);
    }
}

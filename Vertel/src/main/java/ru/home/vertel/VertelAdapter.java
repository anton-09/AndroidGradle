package ru.home.vertel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ru.home.vertel.wheel.AbstractWheelAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class VertelAdapter extends AbstractWheelAdapter
{
    // Image size
    private String mItems[];

    // Cached images
    private List<SoftReference<Bitmap>> mImages;

    final int IMAGE_WIDTH = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / MyApplication.getCropFactor();
    final int IMAGE_HEIGHT = MyApplication.getAppContext().getResources().getDisplayMetrics().widthPixels / MyApplication.getCropFactor();

    /**
     * Constructor
     * @param imagePack smile pack
     */
    public VertelAdapter(ImagePack imagePack)
    {
        mItems = new String[imagePack.mCheckedResURIs.size()];
        for (int i=0; i<imagePack.mCheckedResURIs.size(); i++)
        {
            mItems[i] = imagePack.mCheckedResURIs.get(i);
        }

        mImages = new ArrayList<SoftReference<Bitmap>>(mItems.length);
        for (String id : mItems) {
            mImages.add(new SoftReference<Bitmap>(loadImage(id)));
        }
    }

    /**
     * Loads image from resources
     * @param id Resource ID
     * @return Loaded image
     */
    private Bitmap loadImage(String id)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(id);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
        bitmap.recycle();
        return scaled;
    }

    @Override
    public int getItemsCount()
    {
        return mItems.length;
    }

    // Layout params for image view
    final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent)
    {
        ImageView img;
        if (cachedView != null) {
            img = (ImageView) cachedView;
        } else {
            img = new ImageView(MyApplication.getAppContext());
        }
        img.setLayoutParams(params);
        SoftReference<Bitmap> bitmapRef = mImages.get(index);
        Bitmap bitmap = bitmapRef.get();
        if (bitmap == null)
        {
            bitmap = loadImage(mItems[index]);
            mImages.set(index, new SoftReference<Bitmap>(bitmap));
        }
        img.setImageBitmap(bitmap);

        return img;
    }

}
package in.dbit.csiapp.Gallery.DisplayImageAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import in.dbit.csiapp.R;

import java.util.ArrayList;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images;
    LayoutInflater inflater;

    public FullSizeAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images  = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.full_item,null);

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)v.findViewById(R.id.img);

        Glide.with(context)
                .asBitmap()
                .load(images.get(position))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImage(ImageSource.bitmap(resource));
                    }
                });

//        Glide.with(context)
//                .load(images.get(position))
//                .asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                view.setImage(ImageSource.bitmap(resource));
//            }
//        });

        ViewPager vp = (ViewPager) container;
        vp.addView(v,0);

        return v;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager)container;
        View v = (View) object;
        viewPager.removeView(v);
    }
}

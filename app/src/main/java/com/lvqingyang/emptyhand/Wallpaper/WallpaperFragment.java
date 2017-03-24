package com.lvqingyang.emptyhand.Wallpaper;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.Picture.ColorArt;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.Address;
import com.lvqingyang.emptyhand.Tools.MyOkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lvqingyang.emptyhand.Picture.AppConstants.drawableToBitmap;

/**
 * Author：LvQingYang
 * Date：2017/2/3
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class WallpaperFragment extends BaseFragment {
    //View
    private RecyclerView mRecyclerView;
    //Data
    private List<Wallpaper> mWallpapers=new ArrayList<>();
    private PaperAdapter mAdapter;
    private static final String TAG = "WallpaperFragment";
    //Dialog
    private static final String DIALOG_GALLERY = "DIALOG_GALLERY";
    //Save
    private static final String KEY_WALLPAPER_LIST = "WALLPAPER_LIST";

    public static WallpaperFragment newInstance() {

        Bundle args = new Bundle();

        WallpaperFragment fragment = new WallpaperFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void initeView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
    }

    @Override
    protected void initeData() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String > subscriber) {
                try {
                    String response= MyOkHttp.getInstance().run(Address.wallpaperUrl);
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String  response) {
                        Log.d(TAG, "onNext: "+response);

                        //解析Json
                        try {
                            JSONObject object=new JSONObject(response);
                            JSONArray array=object.getJSONArray("images");
                            for(int i=0;i<array.length();i++){
                                JSONObject imgObject=array.getJSONObject(i);
                                Wallpaper paper=new Wallpaper();

                                String s=imgObject.getString("copyright");
                                paper.setTitle(s);
                                paper.setImgUrl(getString(R.string.url_head)+imgObject.getString("url"));
                                mWallpapers.add(paper);
                            }
                        } catch (JSONException e) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        setAdapter();
                        showContent();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        cameError();
                    }
                });
    }

    private void setAdapter(){
        mAdapter=new PaperAdapter(mWallpapers);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }


    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putParcelableArrayList(KEY_WALLPAPER_LIST, (ArrayList<? extends Parcelable>) mWallpapers);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mWallpapers=savedInstanceState.getParcelableArrayList(KEY_WALLPAPER_LIST);
        setAdapter();
    }


    private class PaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mImage;
        private Wallpaper mWallpaper;

        public PaperViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.img);

            itemView.setOnClickListener(this);
        }

        public void bindPaper(Wallpaper wallpaper,int pos){
            mWallpaper=wallpaper;
//            Log.d(TAG, "bindPaper: "+mWallpaper.getTitle()+"\n"+mWallpaper.getImgUrl());
            Glide.with(getActivity())
                    .load(mWallpaper.getImgUrl())
                    .placeholder(pos%2==0?R.color.img1:R.color.img2)
                    .into(mImage);
        }

        @Override
        public void onClick(View v) {
            int bgColor=-1,detailColor=-1;
            if (mImage.getDrawable() != null) {
                ColorArt colorArt = new ColorArt(drawableToBitmap(mImage.getDrawable()));
                bgColor=colorArt.getBackgroundColor();
                detailColor=colorArt.getDetailColor();
            }

            GalleryDialog dialog=GalleryDialog.newInstance(mWallpaper,bgColor,detailColor);
            dialog.show(getFragmentManager(), DIALOG_GALLERY);
        }
    }

    private class PaperAdapter extends RecyclerView.Adapter<PaperViewHolder>{
        private List<Wallpaper> mPapers;

        public PaperAdapter(List<Wallpaper> papers){
            mPapers=papers;
        }
        @Override
        public PaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getActivity()).inflate(R.layout.item_wallpaper,parent,false);
            return new PaperViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PaperViewHolder holder, int position) {
            holder.bindPaper(mPapers.get(position),position);
        }

        @Override
        public int getItemCount() {
            return mPapers.size();
        }
    }
}

package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lvqingyang.emptyhand.Book.BookInfo;
import com.lvqingyang.emptyhand.Book.Part;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.DepthPageTransformer;

import java.util.List;

public class ReadBookActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private static final String KEY_BOOK = "BOOK";
    private static final String KEY_POS = "POS";
    private BookInfo mBook;
    private List<Part> mParts;

    public static void start(Context context, BookInfo b,int pos) {
        Intent starter = new Intent(context, ReadBookActivity.class);
        starter.putExtra(KEY_BOOK,b);
        starter.putExtra(KEY_POS,pos);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        mBook=getIntent().getParcelableExtra(KEY_BOOK);
        mParts=mBook.getParts();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return ReadBookFragment.newInstance(mParts.get(position));
            }

            @Override
            public int getCount() {
                return mParts.size();
            }
        });
        mViewPager.setCurrentItem(getIntent().getIntExtra(KEY_POS,0));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

    }
}

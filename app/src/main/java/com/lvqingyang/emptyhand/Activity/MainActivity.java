package com.lvqingyang.emptyhand.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lvqingyang.emptyhand.Article.ArticleFragment;
import com.lvqingyang.emptyhand.Base.BaseActivity;
import com.lvqingyang.emptyhand.Base.BaseFragment;
import com.lvqingyang.emptyhand.Book.BookFragment;
import com.lvqingyang.emptyhand.Picture.PictureFragment;
import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;
import com.lvqingyang.emptyhand.Wallpaper.WallpaperFragment;
import com.lvqingyang.emptyhand.Word.WordFragment;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    //View
    private Toolbar mToolbar;
    private TextView mTitleText;
    private ViewPager mViewPager;
    //Data
    private List<BaseFragment> mFragments;
    private ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initeData();
        initeView();
    }

   private void initeView() {
       mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (TextView) findViewById(R.id.title);
        TypefaceUtils.setTypeface(mTitleText);
       mViewPager = (ViewPager) findViewById(R.id.view_pager);

       FragmentManager fragmentManager=getSupportFragmentManager();
       mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
           @Override
           public Fragment getItem(int position) {
               return mFragments.get(position);
           }

           @Override
           public int getCount() {
               return mFragments.size();
           }
       });

       //Menu
       MenuParams menuParams = new MenuParams();
       menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.menu_item_height));
       menuParams.setMenuObjects(getMenuObjects());
//       menuParams.setClosableOutside(true);
       menuParams.setFitsSystemWindow(true);
       menuParams.setClipToPadding(false);
       mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
       mMenuDialogFragment.setItemClickListener(new OnMenuItemClickListener() {
           @Override
           public void onMenuItemClick(View clickedView, int position) {
               switch (position) {
                   case 0:
                       mMenuDialogFragment.dismiss();
                       break;
                   case 1:
                       CollectActivity.start(MainActivity.this);
                       break;
                   case 2:
                       BookActivity.start(MainActivity.this);
                       break;
                   case 3:
                       AboutActivity.start(MainActivity.this);
                       break;
                   case 4:
                       finish();
                       break;
               }
           }
       });
       findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mMenuDialogFragment.show(getSupportFragmentManager(), "ContextMenu");
           }
       });
   }

    private void initeData() {
        mFragments=new ArrayList<>();
        mFragments.add(ArticleFragment.newInstance());
        mFragments.add(WordFragment.newInstance());
        mFragments.add(BookFragment.newInstance());
        mFragments.add(PictureFragment.newInstance());
        mFragments.add(WallpaperFragment.newInstance());
    }

    private List<MenuObject> getMenuObjects(){
        MenuObject close=new MenuObject();
        close.setResource(R.drawable.ic_action_close);


        MenuObject collect=new MenuObject(getString(R.string.my_collect));
        collect.setMenuTextAppearanceStyle(R.style.MenuTextViewStyle);
        collect.setResource(R.drawable.ic_action_collect);

        MenuObject mybook=new MenuObject(getString(R.string.my_book));
        mybook.setMenuTextAppearanceStyle(R.style.MenuTextViewStyle);
        mybook.setResource(R.drawable.ic_action_book);

        MenuObject about=new MenuObject(getString(R.string.about));
        about.setMenuTextAppearanceStyle(R.style.MenuTextViewStyle);
        about.setResource(R.drawable.ic_action_about);

        MenuObject quit=new MenuObject(getString(R.string.quit));
        quit.setMenuTextAppearanceStyle(R.style.MenuTextViewStyle);
        quit.setResource(R.drawable.ic_action_quit);

        List<MenuObject> objects=new ArrayList<>();
        objects.add(close);
        objects.add(collect);
        objects.add(mybook);
        objects.add(about);
        objects.add(quit);

//        for (int i = 0; i < objects.size()-1; i++) {
//            objects.get(i).setDividerColor();
//        }

        return objects;
    }
}

package com.lvqingyang.emptyhand.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;
import com.lvqingyang.emptyhand.View.SunBabyLoadingView;

/**
 * Author：LvQingYang
 * Date：2017/2/2
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public abstract class BaseFragment extends Fragment {

    private View mShowLayout;
    private SunBabyLoadingView mLoadingView;
    private TextView mRetry;
    private static final String TAG = "BaseFragment";
    private Bundle savedState;
    private boolean isLoaded;
    private static final String KEY_SAVED_STAYE = "SAVED_STAYE";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(getLayoutId(),container,false);
        initeView(view);

        mShowLayout = view.findViewById(R.id.show_layout);
        mLoadingView = (SunBabyLoadingView) view.findViewById(R.id.loading);
        mRetry = (TextView) view.findViewById(R.id.retry);
        TypefaceUtils.setTypeface(mRetry);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                initeData();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!restoreStateFromArguments()) {
            mShowLayout.setVisibility(View.GONE);
            mRetry.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);

            initeData();
        }else {
            showContent();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        // Save State Here
        saveStateToArguments();
    }

    private void saveStateToArguments() {
        if (!isLoaded) {
            return;
        }
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle(KEY_SAVED_STAYE, savedState);
        }
    }

    ////////////////////
    // Don't Touch !!
    ////////////////////

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        savedState = b.getBundle(KEY_SAVED_STAYE);
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    /////////////////////////////////
    // Restore Instance State Here
    /////////////////////////////////

    private void restoreState() {
        if (savedState != null) {
            // For Example
            //tv1.setText(savedState.getString("text"));
            onRestoreState(savedState);
        }
    }

    protected void onRestoreState(Bundle savedInstanceState) {

    }

    //////////////////////////////
    // Save Instance State Here
    //////////////////////////////

    private Bundle saveState() {
        Bundle state = new Bundle();
        // For Example
        //state.putString("text", tv1.getText().toString());
        onSaveState(state);
        return state;
    }

    protected void onSaveState(Bundle outState) {

    }

    protected abstract  int getLayoutId();

    protected abstract void initeView(View v);

    protected abstract void initeData();


    protected void setText(TextView textView,String text){
        textView.setText(text);
        TypefaceUtils.setTypeface(textView);
    }

    protected void showContent(){
        isLoaded=true;
        mShowLayout.setVisibility(View.VISIBLE);
        mRetry.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    protected void cameError(){
        mRetry.setVisibility(View.VISIBLE);
    }

}

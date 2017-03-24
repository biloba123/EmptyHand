package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView github;
    private TextView email;
    private TextView qq;
    private TextView feedback;

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.feedback = (TextView) findViewById(R.id.feedback);
        this.qq = (TextView) findViewById(R.id.qq);
        this.email = (TextView) findViewById(R.id.email);
        this.github = (TextView) findViewById(R.id.github);
        TypefaceUtils.setText((TextView) findViewById(R.id.title),getString(R.string.about));
        //Back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        feedback.setOnClickListener(this);
        qq.setOnClickListener(this);
        email.setOnClickListener(this);
        github.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback:
                FeedbackActivity.start(this, getString(R.string.feedback_add));
                break;
            case R.id.qq:
                joinQQGroup("Ts_RdlwaI9p3hXsP_81dS4_2SNBFC6zd");
                break;
            case R.id.email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.my_email));

                startActivity(Intent.createChooser(intent, "Send Email"));
                break;
            case R.id.github:
                Uri uri=Uri.parse(getString(R.string.my_github));
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
                break;
        }
    }

    /****************
     *
     * 发起添加群流程。群号：Penguin &amp; Android(585297184) 的 key 为： Ts_RdlwaI9p3hXsP_81dS4_2SNBFC6zd
     * 调用 joinQQGroup(Ts_RdlwaI9p3hXsP_81dS4_2SNBFC6zd) 即可发起手Q客户端申请加群 Penguin &amp; Android(585297184)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}

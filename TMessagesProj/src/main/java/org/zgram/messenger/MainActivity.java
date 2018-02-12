package org.zgram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import org.zgram.messenger.browser.Browser;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);


        Button membercn =(Button) findViewById(R.id.member_cn);
        Button member_offline =(Button) findViewById(R.id.member_offline);
        Button member_group =(Button) findViewById(R.id.member_group);
        Button tabligh_not =(Button) findViewById(R.id.tabligh_not);
        Button fallower_insta =(Button) findViewById(R.id.fallower_insta);
        Button support =(Button) findViewById(R.id.support);
        Button etemad =(Button) findViewById(R.id.etemad);

        final WebView wb =new WebView(ApplicationLoader.applicationContext);





        membercn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "http://netfixed.ir/add");
            }
        });
        member_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "http://netfixed.ir/offline");
            }
        });
        member_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "http://netfixed.ir/group");
            }
        });
        tabligh_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "https://notification.zone/dialog");
            }
        });
        fallower_insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "http://www.netfixed.ir/follower");
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=NeTFixed_Member"));
                startActivity(intent);
            }
        });
        etemad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.openUrl(MainActivity.this, "http://www.netfixed.ir/etemad");
            }
        });








    }












}

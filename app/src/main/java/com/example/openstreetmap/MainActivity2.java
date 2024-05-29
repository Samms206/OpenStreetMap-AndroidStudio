package com.example.openstreetmap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    WebView webView;
    Double latTujuan=0.0, longTujuan=0.0, latAsal=0.0, longAsal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = (WebView) findViewById(R.id.myWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);

        Intent intent = getIntent();
        latTujuan = intent.getDoubleExtra("LatTujuan", 0);
        longTujuan = intent.getDoubleExtra("LongTujuan", 0);
        latAsal = intent.getDoubleExtra("LatAsal", 0);
        longAsal = intent.getDoubleExtra("LongAsal", 0);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)){
                    return false;
                }
                if (appInstalledOrNot(url)){
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent1);
                }
                return true;
            }
        });

        webView.loadUrl("https://www.google.com/maps?"+"saddr="+latAsal+","+longAsal+"&daddr="+latTujuan+","+longTujuan);
        Toast.makeText(this, "Loading Direction, Please Wait...", Toast.LENGTH_LONG).show();
    }

    boolean appInstalledOrNot(String url){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (PackageManager.NameNotFoundException e){

        }
        return  false;
    }
}
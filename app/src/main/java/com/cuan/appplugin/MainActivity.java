package com.cuan.appplugin;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cuan.plugincore.plugin.Plugin;
import com.cuan.plugincore.pluginmanager.PluginManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Plugin plugin = PluginManager.getInstance().getPluginByPackageName("com.cuan.testplugin");
        ClassLoader loader = plugin.getPluginClassloader();
        try {
            loader.loadClass("com.cuan.testplugin.MainActivity");
            android.util.Log.i("shajia","--loader: "+loader);
            android.util.Log.i("shajia","--: find class!!!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            android.util.Log.i("shajia","--: not find class!!!");

        }


    }
}

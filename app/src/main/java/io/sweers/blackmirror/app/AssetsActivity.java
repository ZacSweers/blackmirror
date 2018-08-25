package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import packaged.Packaged;

public class AssetsActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assets);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Assets");
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    try {
      ((TextView) findViewById(R.id.text)).setText(new Packaged().sayHello());
    } catch (NoClassDefFoundError e) {
      System.out.println(e);
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }
}

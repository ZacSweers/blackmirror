package io.sweers.blackmirror.app;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {

  public void setUpHeader(String title) {
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle(title);
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

}

package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import io.sweers.blackmirror.sample.assets.AssetsHelper;
import io.sweers.blackmirror.sample.hello.Hello;

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
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    TextView textView = findViewById(R.id.text);
    try {
      Class<?> helloImpl = AssetsHelper.loadHelloImpl(this);
      Hello instance = (Hello) helloImpl.newInstance();
      textView.setText(instance.sayHello());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}

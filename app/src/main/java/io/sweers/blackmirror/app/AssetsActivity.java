package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.widget.TextView;
import io.sweers.blackmirror.sample.assets.AssetsHelper;
import io.sweers.blackmirror.sample.hello.Hello;

public class AssetsActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assets);
    setUpHeader("Assets");

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

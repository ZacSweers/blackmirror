package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.widget.TextView;
import io.sweers.blackmirror.sample.hello.Hello;
import io.sweers.blackmirror.samples.resources.ResourcesHelper;

public class ResourcesActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assets);
    setUpHeader("Resources");

    TextView textView = findViewById(R.id.text);
    try {
      Class<?> helloImpl = ResourcesHelper.loadHelloImpl(this);
      Hello instance = (Hello) helloImpl.newInstance();
      textView.setText(instance.sayHello());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

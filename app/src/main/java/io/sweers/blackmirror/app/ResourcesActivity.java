package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import io.sweers.blackmirror.samples.resources.ResourcesInterceptor;
import java.lang.reflect.Method;

public class ResourcesActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_assets);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Resources");
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Wasn't quite able to get this working yet, but the idea if that we can have this API
    // be provided and supply its API impl via BlackMirror later
    //try {
    //  ((TextView) findViewById(R.id.text)).setText(new Packaged().sayHello());
    //} catch (NoClassDefFoundError e) {
    //  System.out.println(e);
    //}

    TextView textView = findViewById(R.id.text);
    try {
      Class<?> packaged = ResourcesInterceptor.loadPackaged(this);
      Object packagedInstance = packaged.newInstance();
      Method sayHelloMethod = packaged.getDeclaredMethod("sayHello");
      String message = (String) sayHelloMethod.invoke(packagedInstance);
      textView.setText(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

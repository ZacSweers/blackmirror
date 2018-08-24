package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.sweers.blackmirror.spy.Spy;

public class SayHelloActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buttonwithtext);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Say Hello");
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Button button = findViewById(R.id.button);
    button.setText("Say hello");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          Spy spy = new Spy(SayHelloActivity.this);
          String hello = spy.sayHello();

          ((TextView) findViewById(R.id.text)).setText(hello);
        } catch (Exception e) {
          ((TextView) findViewById(R.id.text)).setText(e.getMessage());
        }
      }
    });
  }
}

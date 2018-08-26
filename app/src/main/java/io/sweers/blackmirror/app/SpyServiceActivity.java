package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.sweers.blackmirror.spy.Spies;
import java.lang.reflect.Method;

public class SpyServiceActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buttonwithtext);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Spy Service");
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Button button = findViewById(R.id.button);
    button.setText("Spy service");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          Class<?> serviceClass =
              Spies.service(SpyServiceActivity.this, "io.sweers.blackmirror.neighbor",
                  "io.sweers.blackmirror.neighbor.NeighborApi");

          StringBuilder builder = new StringBuilder();

          builder.append(serviceClass.getName()).append(" {");
          for (Method method : serviceClass.getDeclaredMethods()) {
            builder.append("\n")
                .append("\t")
                .append(method.getName())
                .append("()");

            // getGenericReturnType()
            // annotations
            // params + param annotations
          }
          builder.append("\n}");

          ((TextView) findViewById(R.id.text)).setText(builder.toString());
        } catch (Exception e) {
          ((TextView) findViewById(R.id.text)).setText(e.getMessage());
        }
      }
    });
  }
}

package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.sweers.blackmirror.spy.Spies;
import java.lang.reflect.Method;

public class SpyServiceActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buttonwithtext);
    setUpHeader("Spy Service");

    Button button = findViewById(R.id.button);
    button.setText("Spy service");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          Class<?> serviceClass =
              Spies.service(SpyServiceActivity.this, "io.sweers.blackmirror.neighbor",
                  "io.sweers.blackmirror.neighbor.NeighborApi");

          StringBuilder builder = new StringBuilder();

          builder.append("interface ").append(serviceClass.getSimpleName()).append(" {");
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

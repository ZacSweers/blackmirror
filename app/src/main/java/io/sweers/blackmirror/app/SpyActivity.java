package io.sweers.blackmirror.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.sweers.blackmirror.spy.Spies;
import java.util.Map;
import java.util.concurrent.Callable;

public class SpyActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buttonwithtext);
    setUpHeader("Spy");

    Button button = findViewById(R.id.button);
    button.setText("Spy");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          final ProgressDialog dialog = new ProgressDialog(SpyActivity.this);
          dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
          dialog.setTitle("Searching for build config fields...");

          dialog.show();
          Single.fromCallable(new Callable<String>() {
            @Override public String call() {
              return Spies.guessBuildConfig(SpyActivity.this, "io.sweers.blackmirror.neighbor");
            }
          })
              .map(new Function<String, Map<String, String>>() {
                @Override public Map<String, String> apply(String buildConfig) {
                  //dialog.setTitle("Parsing fields...");
                  Map<String, String> vals =
                      Spies.buildConfigFields(SpyActivity.this, "io.sweers.blackmirror.neighbor",
                          buildConfig);
                  return vals;
                }
              })
              .subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread())
              .doOnSubscribe(new Consumer<Disposable>() {
                @Override public void accept(Disposable disposable) {
                  dialog.show();
                }
              })
              .doOnDispose(new Action() {
                @Override public void run() {
                  dialog.cancel();
                }
              })
              .as(AutoDispose.<Map<String, String>>autoDisposable(
                  AndroidLifecycleScopeProvider.from(SpyActivity.this)))
              .subscribe(new Consumer<Map<String, String>>() {
                @Override public void accept(Map<String, String> buildConfigFields) {
                  dialog.cancel();
                  StringBuilder builder = new StringBuilder();
                  for (Map.Entry<String, String> entry : buildConfigFields.entrySet()) {
                    builder.append(entry.getKey())
                        .append(" = ")
                        .append(entry.getValue())
                        .append("\n");
                  }
                  ((TextView) findViewById(R.id.text)).setText(builder.toString());
                }
              });
        } catch (Exception e) {
          ((TextView) findViewById(R.id.text)).setText(e.getMessage());
        }
      }
    });
  }
}

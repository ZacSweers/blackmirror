package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.serjltt.moshi.adapters.Wrapped;
import com.squareup.moshi.Moshi;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.sweers.blackmirror.spy.Spies;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.Callable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class BorrowServiceActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_buttonwithtext);
    setUpHeader("Borrow");

    Button button = findViewById(R.id.button);
    button.setText("Borrow");
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          Class<?> serviceClass =
              Spies.service(BorrowServiceActivity.this, "io.sweers.blackmirror.neighbor",
                  "io.sweers.blackmirror.neighbor.NeighborApi");

          Moshi moshi = new Moshi.Builder().add(Wrapped.ADAPTER_FACTORY).build();
          final Object service = new Retrofit.Builder()
              .baseUrl("https://api.imgur.com/3/")
              .addConverterFactory(MoshiConverterFactory.create(moshi))
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()
              .create(serviceClass);

          final Method uploadMethod = serviceClass.getDeclaredMethod("postImage", MultipartBody.Part.class);

          Single<String> upload = Single.defer(new Callable<SingleSource<? extends String>>() {
            @Override public SingleSource<? extends String> call() throws Exception {
              // Defer because creating the Part opens an input stream we should defer till subscribe-time
              return (Single<String>) uploadMethod.invoke(service, assetsBody("reflection", "jpg"));
            }
          });

          final TextView textView = findViewById(R.id.text);
          upload.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .doOnSubscribe(new Consumer<Disposable>() {
                @Override public void accept(Disposable disposable) {
                  textView.setText("Uploading...");
                }
              })
              .as(AutoDispose.<String>autoDisposable(
                  AndroidLifecycleScopeProvider.from(BorrowServiceActivity.this)))
              .subscribe(new DisposableSingleObserver<String>() {
                @Override public void onSuccess(String url) {
                  textView.setText("Success: " + url);
                }

                @Override public void onError(Throwable e) {
                  textView.setText("Failure: " + e.getMessage());
                }
              });
        } catch (Exception e) {
          ((TextView) findViewById(R.id.text)).setText("Failure: " + e.getMessage());
        }
      }
    });
  }

  private MultipartBody.Part assetsBody(String name, String extension) throws IOException {
    final InputStream is = getAssets().open(name + "." + extension);
    return MultipartBody.Part.createFormData("image", name + new Random().nextInt(),
        // A RequestBody that can source from android assets, which only give an InputStream
        new RequestBody() {
          @Override public MediaType contentType() {
            return MediaType.parse("image/*");
          }

          @Override public long contentLength() throws IOException {
            return is.available();
          }

          @Override public void writeTo(BufferedSink sink) throws IOException {
            try (Source source = Okio.source(is)) {
              sink.writeAll(source);
            }
          }
        });
  }
}

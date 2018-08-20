/*
 * Copyright (c) 2018. Zac Sweers
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final RecyclerView recyclerView = findViewById(R.id.list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(layoutManager);
    final ClassLoaderDisplayAdapter adapter = new ClassLoaderDisplayAdapter();
    recyclerView.setAdapter(adapter);

    // Timber logs are going, but to a different tree! Can't rely on that
    Flowable.create(new FlowableOnSubscribe<String>() {
      @Override public void subscribe(final FlowableEmitter<String> emitter) {
        try {
          // Clear first
          //new ProcessBuilder().command("logcat", "-v", "time")
          //    .redirectErrorStream(true)
          //    .start()
          //    .waitFor();
          final Process process = new ProcessBuilder().command("logcat", "-v", "time")
              .redirectErrorStream(true)
              .start();
          emitter.setCancellable(new Cancellable() {
            @Override public void cancel() {
              process.destroy();
            }
          });
          BufferedReader bufferedReader =
              new BufferedReader(new InputStreamReader(process.getInputStream()));

          String line;
          while ((line = bufferedReader.readLine()) != null) {
            if (emitter.isCancelled()) {
              break;
            } else {
              emitter.onNext(line);
            }
          }
        } catch (Exception e) {
          emitter.onError(e);
        }
      }
    }, BackpressureStrategy.DROP)
        .subscribeOn(Schedulers.computation())
        .sample(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.from(Looper.getMainLooper(), true))
        .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) {
            adapter.append(s);
            //recyclerView.scrollToPosition(adapter.getItemCount() - 1);
          }
        });

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        // By the time we've gotten here, there's no _new_ class loading to show. But we can
        // Kick some off
        try {
          Single.just(5)
              .subscribe();
        } catch (Exception e) {
          Timber.e(e);
        }
      }
    });
  }

  static class ClassLoaderDisplayAdapter extends RecyclerView.Adapter<ItemView> {

    private final List<String> logs = new ArrayList<>();

    public ClassLoaderDisplayAdapter() {
      setHasStableIds(true);
    }

    @Override public int getItemViewType(int position) {
      return 0;
    }

    @Override public long getItemId(int position) {
      return logs.get(0)
          .hashCode();
    }

    @Override public ItemView onCreateViewHolder(ViewGroup viewGroup, int i) {
      return new ItemView((AppCompatTextView) LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_view, viewGroup, false));
    }

    @Override public void onBindViewHolder(ItemView itemView, int i) {
      String log = logs.get(0);
      // Pass text computation future to AppCompatTextView,
      // which awaits result before measuring.
      itemView.textView.setTextFuture(PrecomputedTextCompat.getTextFuture(log,
          TextViewCompat.getTextMetricsParams(itemView.textView),
          /*optional custom executor*/
          null));
    }

    @Override public int getItemCount() {
      return logs.size();
    }

    void append(String log) {
      logs.add(log);
      notifyDataSetChanged();
    }
  }

  static class ItemView extends RecyclerView.ViewHolder {

    private final AppCompatTextView textView;

    ItemView(AppCompatTextView textView) {
      super(textView);
      this.textView = textView;
    }
  }
}

package io.sweers.blackmirror.app;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class LogcatActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_logcat);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Logcat");
    setSupportActionBar(toolbar);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    final RecyclerView recyclerView = findViewById(R.id.list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(layoutManager);
    final ClassLoaderDisplayAdapter adapter = new ClassLoaderDisplayAdapter();
    recyclerView.setAdapter(adapter);

    DividerItemDecoration decoration =
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(decoration);

    Flowable<String> logcatDump = Flowable.create(new FlowableOnSubscribe<String>() {
      @Override public void subscribe(FlowableEmitter<String> emitter) {
        try {
          // Clear first
          new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start().waitFor();
          final Process process = new ProcessBuilder().command("logcat", "-d", "-v", "time")
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
    }, BackpressureStrategy.DROP).distinctUntilChanged().filter(new Predicate<String>() {
      @Override public boolean test(String s) {
        return s.contains("D/BlackMirror");
      }
    });

    Flowable<String> timberLogs = Flowable.create(new FlowableOnSubscribe<String>() {
      @Override public void subscribe(final FlowableEmitter<String> emitter) {
        final Timber.Tree tree = new Timber.Tree() {
          @Override protected void log(int priority, @Nullable String tag, @NotNull String message,
              @Nullable Throwable t) {
            if ("BlackMirror".equals(tag)) {
              emitter.onNext(message);
            }
          }
        };
        Timber.plant(tree);
        emitter.setCancellable(new Cancellable() {
          @Override public void cancel() {
            Timber.uproot(tree);
          }
        });
      }
    }, BackpressureStrategy.DROP);

    Flowable.concat(logcatDump, timberLogs)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.from(Looper.getMainLooper(), true))
        .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(new DisposableSubscriber<String>() {
          @Override public void onNext(String newLog) {
            adapter.append(newLog);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
          }

          @Override public void onError(Throwable t) {
            // Noop
          }

          @Override public void onComplete() {

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
      return logs.get(position).hashCode();
    }

    @Override public ItemView onCreateViewHolder(ViewGroup viewGroup, int i) {
      return new ItemView((AppCompatTextView) LayoutInflater.from(viewGroup.getContext())
          .inflate(R.layout.item_view, viewGroup, false));
    }

    @Override public void onBindViewHolder(ItemView itemView, int i) {
      String log = logs.get(i);
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

    void append(String newLog) {
      logs.add(newLog);
      notifyItemInserted(logs.size() - 1);
    }
  }

  static class ItemView extends RecyclerView.ViewHolder {

    private final AppCompatTextView textView;

    ItemView(AppCompatTextView textView) {
      super(textView);
      this.textView = textView;
      textView.setTextSize(12f);
    }
  }
}

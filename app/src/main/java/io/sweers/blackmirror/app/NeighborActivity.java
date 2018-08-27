package io.sweers.blackmirror.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NeighborActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_neighbor);
    setUpHeader("Neighbor");

    findViewById(R.id.sayhello_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(NeighborActivity.this, SayHelloActivity.class));
      }
    });
    findViewById(R.id.spy_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(NeighborActivity.this, SpyActivity.class));
      }
    });
    findViewById(R.id.service_spy_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(NeighborActivity.this, SpyServiceActivity.class));
      }
    });
    findViewById(R.id.service_borrow_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(NeighborActivity.this, BorrowServiceActivity.class));
      }
    });
  }
}

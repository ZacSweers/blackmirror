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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("BlackMirror");
    setSupportActionBar(toolbar);

    findViewById(R.id.logcat_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, LogcatActivity.class));
        //try {
        //  Timber.d("Clicked");
        //  Spy spy = new Spy(MainActivity.this);
        //  String hello = spy.sayHello();
        //
        //  String buildConfig = Spies.guessBuildConfig(MainActivity.this, "io.sweers.blackmirror.neighbor");
        //  Map<String, String> vals = Spies.buildConfigFields(MainActivity.this, "io.sweers.blackmirror.neighbor", buildConfig);
        //
        //  Toast.makeText(MainActivity.this, hello, Toast.LENGTH_SHORT).show();
        //  Class.forName("io.reactivex.Single")
        //      .getDeclaredMethod("just", Object.class)
        //      .invoke(null, 5);
        //  //Single.just(5).subscribe();
        //} catch (Exception e) {
        //  Timber.e(e);
        //}
      }
    });

    findViewById(R.id.assets_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, AssetsActivity.class));
      }
    });
  }
}

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

package io.sweers.blackmirror;

public class ClassResult {
  private final Class<?> clazz;
  private final String name;

  private ClassResult(Builder builder) {
    clazz = builder.clazz;
    name = builder.name;
  }

  public Class<?> clazz() {
    return clazz;
  }

  public String name() {
    return name;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override public String toString() {
    return "ClassResult{" + "clazz=" + clazz + ", name='" + name + '\'' + '}';
  }

  public static final class Builder {
    private Class<?> clazz;
    private String name;

    private Builder() {
    }

    private Builder(ClassResult result) {
      this.clazz = result.clazz;
      this.name = result.name;
    }

    public Builder clazz(Class<?> clazz) {
      this.clazz = clazz;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public ClassResult build() {
      if (name == null) {
        throw new IllegalStateException("name == null");
      }
      if (clazz != null && !name.equals(clazz.getName())) {
        throw new IllegalStateException("name ("
            + name
            + ") "
            + "must be the class's fully qualified name ("
            + clazz.getName()
            + ")");
      }
      return new ClassResult(this);
    }
  }
}

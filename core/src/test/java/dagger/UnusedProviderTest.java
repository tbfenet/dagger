/*
 * Copyright (C) 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger;

import javax.inject.Inject;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UnusedProviderTest {

  @Test public void unusedProvidesMethod_whenModuleNonStrict() throws Exception {
    class EntryPoint {
    }
    class BagOfMoney {
    }
    @Module(entryPoints = EntryPoint.class, library = true) class TestModule {
      @Provides BagOfMoney providesMoney() {
        return new BagOfMoney();
      }
    }

    ObjectGraph graph = ObjectGraph.create(new TestModule());
    graph.validate();
  }

  @Test public void unusedProviderMethod_whenModuleStrict() throws Exception {
    class EntryPoint {
    }
    class BagOfMoney {
    }

    @Module(entryPoints = EntryPoint.class) class TestModule {
      @Provides BagOfMoney providesMoney() {
        return new BagOfMoney();
      }
    }

    try {
      ObjectGraph graph = ObjectGraph.create(new TestModule());
      graph.validate();
      fail("Validation should have exploded!");
    } catch (IllegalStateException e) {
    }
  }

  @Test public void whenLibraryModulePlussedToNecessaryModule_shouldNotFailOnUnusedLibraryModule()
      throws Exception {
    class EntryPoint {
    }
    class BagOfMoney {
    }

    @Module(entryPoints = EntryPoint.class, library = true) class ExampleLibraryModule {
      @Provides BagOfMoney providesMoney() {
        return new BagOfMoney();
      }
    }

    @Module(entryPoints = EntryPoint.class) class TestModule {
    }

    ObjectGraph graph = ObjectGraph.create(new TestModule());
    graph = graph.plus(new ExampleLibraryModule());
    graph.validate();
  }

  @Test public void whenConstructorInjected_shouldNotFail() throws Exception {

    @Module(entryPoints = MyEntryPoint.class) class TestModule {
      @Provides PocketFullOfChange providesMoney() {
        return new PocketFullOfChange();
      }
    }

    ObjectGraph graph = ObjectGraph.create(new TestModule());
    graph.validate();
  }

  public static class PocketFullOfChange {
  }

  public static class MyEntryPoint {
    @Inject public MyEntryPoint(PocketFullOfChange pocket) {
    }
  }
}

BlackMirror
===========

Terrible reflection hacks around classloaders in Android. Sample app from a talk given at Droidcon NYC 2018.

Slides: https://speakerdeck.com/hzsweers/breaking-the-android-classloader

Core project is `blackmirror`, which has the `BlackMirror` classloader implementation and some other utilities for working with classloaders and dex files

Examples:
* `app` - The main sample app
* `neighbor` - A simple sibling app with some simple code, used as the target for the main app to run code out of.
* `initprovider:simple` - demonstrating automatic initialization via init provider (before `Application#onCreate()`)
* `initprovider:pluginized` - demonstrating automatic initialization via pluginized init provider (before `Application#onCreate()`)
* `samples:logging` - Simple logging of class loading via Timber
* `samples:timing` - Simple timing of class loading, reported via Timber
* `samples:swapper` - Different examples of trying to swap what classes are loaded. Does not work for different reasons
* `samples:hello` - Simple `Hello` interface with a `sayHello` method. Used in the next two samples
* `samples:assets` - Example loading dex files from assets, in this case `Hello` impls
* `samples:resources` - Example loading dex files from resources, in this case `Hello` impls
* `samples:spy` - Examples for reading other apps' dex files and running other apps' code


BlackMirror
===========

Terrible reflection hacks around classloaders in Android

Core project is `blackmirror`

Examples:
* `initprovider:simple` - demonstrating automatic initialization via init provider (before `Application#onCreate()`)
* `initprovider:pluginized` - demonstrating automatic initialization via pluginized init provider (before `Application#onCreate()`)
* `samples:logging` - Simple logging of class loading via Timber
* `samples:timing` - Simple timing of class loading, reported via Timber
* `samples:swapper` - Different examples of trying to swap what classes are loaded. Does not work for different reasons


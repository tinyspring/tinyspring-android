tinyspring-android
===================

This project allows you to use Spring-like injections in your Android projects. It uses the [Tinyspring](https://github.com/tinyspring/tinyspring) as an lightweight injection framework and add Android specific features on top of it.

It mix the bean configuration through standard spring xml and android configuration through annotations.

Main features list:

- spring-like xml configuration of beans and application context availability in your Android project 
- autowiring of xml defined spring beans in activities
- autowiring of android application layouts through @AndroidLayout annotations
- autowiring of android UI elements (Views etc.) through @View annotations
- autowiring of on-click events on defined UI elements through @OnClick annotations
- framework supports custom extensions/plugins (check [Zubhium plugin](http://github.com/tinyspring/tinyspring-android-plugin-zubhium/wiki))

Check out the [Wiki](http://github.com/tinyspring/tinyspring-android/wiki) page for more info.

# TaliBottomNavigation
 [![Platform](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)](https://www.android.com/) [![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
 [![Bintray](https://img.shields.io/bintray/v/alirezatali/TaliBottomNavigation/com.alitali%3Atalibottomnavigation.svg?logo=jfrog-bintray)](https://bintray.com/alirezatali/TaliBottomNavigation/com.alitali%3Atalibottomnavigation) 
 getting Started
------------------------
`TaliBottomNavigation` manage the various (Navigation) graphs needed for a [BottomNavigationView].

The actual features are:

 * Bottom Bar with multiple NavGraph .
 * Saving fragment state.
 This sample is a workaround until the Navigation Component supports multiple back stacks.
 Inspired by this [kotlin version](https://github.com/android/architecture-components-samples/blob/master/NavigationAdvancedSample/app/src/main/java/com/example/android/navigationadvancedsample/NavigationExtensions.kt)
 
 ### Gradle Dependency
* Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
       jcenter()
    }
}
```

* Add the dependency in your app's build.gradle file

```
dependencies {
	implementation 'com.alitali:talibottomnavigation:1.0.0'
}
```

## Usage
------------------------
### Step 1.  Add this view to your activity layout
```
<com.alitali.talibottomnavigation.TaliBottomNavigationView
        android:id="@+id/bottomNavigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:menu="@menu/bottom_menu"
        app:labelVisibilityMode="selected"
        android:animateLayoutChanges="true"
        android:splitMotionEvents="true"/>

```
### Step 2.  create your bottom_menu.xml to your menu directory
```
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/setting"
        android:icon="@drawable/ic_setting"
        android:title="Setting"
        android:contentDescription="Setting"/>
    <item
        android:id="@+id/home"
        android:icon="@drawable/ic_home"
        android:title="Home"
        android:contentDescription="Home" />
    <item
        android:id="@+id/about"
        android:icon="@drawable/ic_about"
        android:title="About"
        android:contentDescription="About"/>
</menu>

``` 
### Step 3.  Add your navigation resource file 
## Note: 
The navigation id for any item must same name. for example : Navigation for "home" button has "home" id
### Step 4.  Setting in your MainAcitivty
```
TaliBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        //bottomNavigationView.setSelectedItemId(R.id.home);
        List<Integer> navGraphIds = Arrays.asList(R.navigation.setting,R.navigation.home,R.navigation.about);
        LiveData<NavController> controller = bottomNavigationView.setupWithNavController(navGraphIds,
                getSupportFragmentManager(),
                R.id.nav_host_container,
                getIntent());
        controller.observe(this, this::setupActionBarWithNavController);
        
 ```
  #Note: This code must add too your activity 
 ```
  protected void setupActionBarWithNavController(NavController navController){
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
    }
 ```
 # For more information see the [Sample Project] (https://github.com/alirezatali/TaliBottomNavigation/tree/master/Sample)
 ### 
 

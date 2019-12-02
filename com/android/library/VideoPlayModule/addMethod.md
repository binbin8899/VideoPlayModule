https://blog.csdn.net/yanmantian/article/details/85780826
打包jar去github对外做引用
引用项目build.gradle中添加
      allprojects  maven {
            url "https://raw.githubusercontent.com/binbin8899/VideoPlayModule/master"
        }
 dependencies implementation 'com.android.library:VideoPlayModule:1.0.0'
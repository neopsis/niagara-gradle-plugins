@echo off

FOR /L %%G IN (7,1,13) DO (
   echo ----------------------------------
   echo Creating bundle for Niagara 4.%%G
   echo ----------------------------------
   call gradlew bundleRelease -Pbuild_release=%%G
)

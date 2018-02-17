** How to set up gradle wrapper

graddle wrapper

** How to run this example with auto-recompile

./gradlew vertxRun

** How to build a "fat jar"

./gradlew shadowJar

** How to run the fat jar

java -jar build/libs/simple-project-fat.jar,


** How to kill all running vertx when graddle barf and doesn't release them

pkill -9 -f io.vertx.core.Launcher


** Update Kotlin runtime

 - change kotlin version in build.gradle
 - run gradlew ?
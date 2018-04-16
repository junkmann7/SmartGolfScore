
VER_NAME=`echo $1 | sed -e "s/\./_/g"`

APP_NAME=SmartGolfScore_ver${VER_NAME}_release.apk

./gradlew build

cp ./app/build/outputs/apk/release/app-release.apk ~/Desktop/${APP_NAME}

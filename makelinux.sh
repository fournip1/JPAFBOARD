jpackage --input target/ \
  --name JPafBoard \
  --main-jar JPafBoard-2.1-shaded.jar \
  --main-class com.paf.jpafboard.Main \
  --app-version 2.1 \
  --linux-shortcut \
  --type deb \
  --java-options '--enable-preview'

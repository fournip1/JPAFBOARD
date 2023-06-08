# not working
jpackage --input target/ \
  --name JPafBoard \
  --main-jar JPafBoard-2.1-shaded.jar \
  --main-class com.paf.jpafboard.Main \
  --app-version 2.1 \
  --type dmg \
  --java-options '--enable-preview' \
  --mac-package-identifier JPafBoard \
  --mac-package-name JPafBoard 
#\
#  --mac-sign \
#  --mac-signing-key-user-name "Pierre-Alexandre Fournié"

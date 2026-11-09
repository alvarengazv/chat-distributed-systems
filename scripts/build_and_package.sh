#!/usr/bin/env bash
set -euo pipefail
ROOT=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT"
# O compilador de formulários da instalação do IntelliJ processa os arquivos .form.
IDEA_HOME=${IDEA_HOME:-$HOME/Programs/idea-IU-262.10968.63}
IDE_CP="$IDEA_HOME/lib/*:$IDEA_HOME/plugins/java/lib/*"
[[ -f "$IDEA_HOME/lib/forms_rt.jar" && -f "$IDEA_HOME/plugins/java/lib/intellij.java.guiForms.compiler.jar" ]] || {
    printf 'Defina IDEA_HOME para uma instalação do IntelliJ com UI Designer.\n' >&2; exit 1;
}
mkdir -p build/form-compiler
javac -cp "$IDE_CP" -d build/form-compiler scripts/InstrumentarFormularios.java
mkdir -p build/middleware build/desktop build/web/WEB-INF/classes build/figurinhas dist
find Chat_Middleware_Service/src/main/java -maxdepth 1 -name '*.java' -print > build/middleware-sources.txt
javac -encoding UTF-8 --release 21 -d build/middleware @build/middleware-sources.txt
java -cp "build/form-compiler:$IDE_CP" InstrumentarFormularios build/middleware Chat_Middleware_Service/src/main/java/FrmPainelDeControle.form
(cd build/middleware && jar xf "$IDEA_HOME/lib/forms_rt.jar" com/intellij/uiDesigner/core)
if [[ -d Chat_Middleware_Service/src/main/resources/images ]]; then
    cp -R Chat_Middleware_Service/src/main/resources/images build/middleware/
fi
jar --create --file dist/middleware.jar --main-class Run -C build/middleware .
find Chat_Cliente_Desktop/src -maxdepth 1 -name '*.java' -print > build/desktop-sources.txt
javac -encoding UTF-8 --release 21 -d build/desktop @build/desktop-sources.txt
cp -R Chat_Cliente_Desktop/src/images build/desktop/
java -cp "build/form-compiler:$IDE_CP" InstrumentarFormularios build/desktop Chat_Cliente_Desktop/src/FrmLogin.form Chat_Cliente_Desktop/src/FrmChat.form
(cd build/desktop && jar xf "$IDEA_HOME/lib/forms_rt.jar" com/intellij/uiDesigner/core)
jar --create --file dist/client-desktop.jar --main-class Run -C build/desktop .
cp -R Chat_Cliente_Web/src/main/webapp/. build/web/
# O Servlet existente precisa das APIs do contêiner; não são empacotadas no WAR.
GLASSFISH_HOME=${GLASSFISH_HOME:-$HOME/Programs/glassfish-8.0.5/glassfish8}
if [[ -d "$GLASSFISH_HOME/glassfish/modules" ]]; then
    MODULES="$GLASSFISH_HOME/glassfish/modules/*"
elif [[ -d "$GLASSFISH_HOME/modules" ]]; then
    MODULES="$GLASSFISH_HOME/modules/*"
else
    printf 'Defina GLASSFISH_HOME para gerar o WAR com as APIs Jakarta do contêiner.\n' >&2
    exit 1
fi
find Chat_Cliente_Web/src/main/java -name '*.java' -print > build/web-sources.txt
javac -encoding UTF-8 --release 21 -cp "$MODULES" -d build/web/WEB-INF/classes @build/web-sources.txt
jar --create --file dist/chat.war -C build/web .
# Repositório único editável no servidor; avatares e figurinhas dos dois clientes.
cp Chat_Cliente_Desktop/src/images/*.png build/figurinhas/
cp Chat_Cliente_Web/src/main/webapp/images/*.png build/figurinhas/
mkdir -p build/figurinhas/WEB-INF
printf '<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0"/>\n' > build/figurinhas/WEB-INF/web.xml
jar --create --file dist/figurinhas.war -C build/figurinhas .
printf 'Gerados: dist/middleware.jar dist/client-desktop.jar dist/chat.war dist/figurinhas.war\n'

#!/usr/bin/env bash
set -euo pipefail
ROOT=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT"
acao=${1:-help}
if [[ $# -gt 0 ]]; then shift; fi
case "$acao" in
    middleware)
        exec java -jar dist/middleware.jar --cli "$@"
        ;;
    middleware-gui)
        exec java -jar dist/middleware.jar "$@"
        ;;
    desktop)
        JAVA_OPTS=()
        [[ -n ${CHAT_HOST:-} ]] && JAVA_OPTS+=("-Dchat.host=$CHAT_HOST")
        [[ -n ${CHAT_ASSETS:-} ]] && JAVA_OPTS+=("-Dchat.assets=$CHAT_ASSETS")
        exec java "${JAVA_OPTS[@]}" -jar dist/client-desktop.jar "$@"
        ;;
    web)
        GLASSFISH_HOME=${GLASSFISH_HOME:-$HOME/Programs/glassfish-8.0.5/glassfish8}
        ASADMIN="$GLASSFISH_HOME/bin/asadmin"
        if [[ ! -x "$ASADMIN" ]]; then
            printf 'Defina GLASSFISH_HOME para a pasta que contém bin/asadmin.\n' >&2
            exit 1
        fi
        [[ -f dist/chat.war && -d build/figurinhas ]] || {
            printf 'Execute scripts/build_and_package.sh antes do deploy.\n' >&2; exit 1;
        }
        # A aplicação de figurinhas é uma pasta expandida, para que novos arquivos
        # possam ser acrescentados sem recompilar ou substituir os clientes.
        mkdir -p build/figurinhas/WEB-INF
        printf '<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0"/>\n' > build/figurinhas/WEB-INF/web.xml
        # Detectar domínio ativo evita ocultar erros reais de start-domain com || true.
        DOMINIO=${GLASSFISH_DOMAIN:-domain1}
        if ! "$ASADMIN" list-domains | grep -F "$DOMINIO running" >/dev/null; then
            "$ASADMIN" start-domain "$DOMINIO"
        fi
        "$ASADMIN" deploy --force=true --name figurinhas --contextroot /figurinhas "$ROOT/build/figurinhas"
        # Mesmo nome usado pelo deploy existente do projeto evita dois aplicativos
        # disputando /chat ao alternar entre a IDE e a execução autônoma.
        "$ASADMIN" deploy --force=true --name "${GLASSFISH_CHAT_APP:-Chat_Cliente_Web-1.0-SNAPSHOT}" --contextroot /chat "$ROOT/dist/chat.war"
        printf 'Chat: http://localhost:8080/chat/\nFigurinhas: %s/build/figurinhas (http://localhost:8080/figurinhas/)\n' "$ROOT"
        ;;
    *)
        printf 'Uso: %s {middleware|middleware-gui|desktop|web}\n' "$0"
        printf 'Configuração: GLASSFISH_HOME, GLASSFISH_DOMAIN, CHAT_HOST, CHAT_ASSETS; opções JVM adicionais via JAVA_TOOL_OPTIONS.\n'
        ;;
esac

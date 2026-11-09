# Executar sem IntelliJ/NetBeans

Execute na raiz do repositório. Requisitos: JDK21+ (java, javac, jar), GlassFish8 e uma instalação do IntelliJ para compilar os formulários do UI Designer. A IDE não precisa estar aberta. Se estiver em outro caminho, defina `IDEA_HOME` antes de gerar os artefatos. Os JARs gerados não precisam da IDE para executar. Os scripts não precisam de Maven nem da IDE; não execute servidores com sudo.

## 1. Gerar artefatos

```bash
export GLASSFISH_HOME="$HOME/Programs/glassfish-8.0.5/glassfish8"
bash scripts/build_and_package.sh
```

Gera dist/middleware.jar, dist/client-desktop.jar, dist/chat.war e dist/figurinhas.war. O WAR não se executa com java -jar: ele é publicado no GlassFish.

## 2. Middleware

Em um terminal:

```bash
bash scripts/run_standalone.sh middleware
```

Equivalente a `java -jar dist/middleware.jar --cli`. O coletor inicia automaticamente na6664. No console do middleware digite:

```text
start desktop
start web
```

Desktop:6661/6662; recepção Web:6663; envio Web:6665. Para parar um serviço, stop desktop ou stop web. `sair` encerra o middleware inteiro e logs. Se preferir GUI: `bash scripts/run_standalone.sh middleware-gui` ou `java -jar dist/middleware.jar`. Não inicie duas instâncias simultâneas nas mesmas portas.

## 3. Servidor Web e imagens

Em outro terminal, com GLASSFISH_HOME definido:

```bash
bash scripts/run_standalone.sh web
```

O script inicia domain1 se necessário, publica dist/chat.war em /chat e o diretório build/figurinhas em /figurinhas. O GlassFish continua rodando depois do comando. Abra http://localhost:8080/chat/. Verifique http://localhost:8080/figurinhas/homem.png antes de usar os clientes. Adicione novas imagens em build/figurinhas; não é necessário recompilar para servi-las. O seletor mantém as seis opções existentes.

Se já existir um deploy com outro nome em /chat, use GLASSFISH_CHAT_APP com o nome exibido por `"$GLASSFISH_HOME/bin/asadmin" list-applications`. O padrão é Chat_Cliente_Web-1.0-SNAPSHOT, igual à configuração existente. Confirme que nenhuma execução da IDE ainda gerencia o servidor.

Para parar o GlassFish:

```bash
"$GLASSFISH_HOME/bin/asadmin" stop-domain domain1
```

## 4. Desktop

Com as imagens publicadas e desktop ativado no middleware, em outro terminal:

```bash
bash scripts/run_standalone.sh desktop
```

Equivalente a `java -jar dist/client-desktop.jar`. A janela continua necessitando sessão gráfica; estar fora da IDE não significa execução headless.

## 5. Outra máquina

Exemplo desktop: `CHAT_HOST=192.168.1.10 bash scripts/run_standalone.sh desktop`. Imagens usam esse mesmo host na8080. O GlassFish e middleware devem ser acessíveis na rede, com as portas necessárias liberadas.

Para Web remoto, configure no JVM do GlassFish `-Dchat.assets=http://192.168.1.10:8080/figurinhas/`: quem carrega a imagem é o navegador. `chat.webService` aponta à recepção6663 e `chat.webEnvio` ao envio6665; localhost só serve se GlassFish/middleware estiverem juntos. `chat.collector` aponta ao coletor6664. Essas propriedades são definidas no GlassFish, não apenas no terminal do cliente.

Histórico e logs ficam por padrão em dados/repositorio.txt e dados/logs, relativos à raiz porque os scripts mudam para ela. Se precisar, passe opções com JAVA_TOOL_OPTIONS: `-Dchat.history=/caminho/repositorio.txt -Dchat.logs=/caminho/logs`.

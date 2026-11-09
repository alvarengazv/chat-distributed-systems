# Evolução do chat — requisito e implementação

Fonte: atividade_sistemas_distribuidos_enunciado_resumido.pdf, itens 1–7. As decisões abaixo preservam os nomes dos módulos, os JSPs, o protocolo desktop e a formatação existente. Não houve migração para framework.

| Comando da atividade | Código e explicação |
|---|---|
| 1. “Na inicialização, carregar o histórico do TXT ... RAM” | `Run.main` chama `Util.inicializar()`. A variável **Util.historico** é compartilhada por todos os serviços. Seu tipo `Historico` contém `ArrayList<String>`; `Files.readAllLines` ocorre somente no construtor. `chat.history` permite selecionar o TXT sem caminho absoluto de usuário. |
| 1. “consultas ... apenas pela memória” | `Historico.consultar()` adquire readLock e junta as linhas da lista. `DesktopEnvioThread` delega ao controlador TCP, que chama consultar; HTTP GET chama a mesma instância. Foram removidos os laços de leitura periódica do TXT. O laço de accept continua necessário para aceitar várias conexões. |
| 1. “falha na gravação não pode ... mensagem persistida” | `Historico.adicionar` adquire writeLock, grava o buffer UTF-8 completamente e chama force(true), depois adiciona à lista. Falha parcial tenta restaurar o tamanho anterior antes de propagar IOException. O lock impede leitores de observar uma publicação incompleta. Uma falha no próprio rollback deve ser investigada: o hardware pode falhar também ao restaurar o TXT. |
| 2. “serviços ... independentes” | `ServicoDesktop` mantém socket de escuta aberto com SO_REUSEADDR configurado antes de bind. Cada conexão é tratada separadamente; sockets ativos são fechados ao parar. `DesktopRecepcaoThread` e `DesktopEnvioThread` continuam como adaptadores dos nomes existentes. `ServicoHttp(false)` atende `/messages` na 6663; `ServicoHttp(true)` atende `/errors` na 6664. Cada instância possui listener/executor próprios. |
| 2. “ligar/desligar ... inclusive coletor” | `FrmPainelDeControle` conserva controles desktop/web e usa o antigo controle de terceiros para coletor. `ConsoleServidor` permite start/stop desktop, web, coletor em modo sem GUI. Parar um listener fecha apenas seus recursos. O controle de publicidade preexistente era apenas visual e continua assim. |
| 2. “Platform Threads ou Virtual Threads ... justificar” | Serviços TCP/HTTP e telemetria escolhem Virtual Threads porque predominam esperas de I/O curtas. Executors são independentes por serviço. Locks de escrita serializam a persistência; mais threads não aumentam a capacidade do disco. Swing continua no modelo de eventos da plataforma. O dispatcher HTTP interno é administrado pelo JDK. Não se promete ganho para tarefas de CPU. |
| 3. “servidor Web autonomamente” | `scripts/build_and_package.sh` usa javac/jar, gera `dist/chat.war` e executáveis. `scripts/run_standalone.sh` usa asadmin do GlassFish instalado fora da IDE. O servidor não é embutido no middleware. |
| 3. “Centralizar ... pasta ... URL” | O build reúne imagens dos dois clientes em `build/figurinhas`. Deploy desse diretório como aplicação estática `/figurinhas`. `chat.assets` configura a URL pública. Desktop carrega ImageIcon por URL e resolve o histórico legado; `InterfaceMiddlewareWeb` faz a resolução para o navegador e topo/login. Adicione novos arquivos diretamente na pasta publicada, sem recompilar para servi-los por URL. O seletor desktop conserva a lista atual de seis figurinhas; não descobre automaticamente novas opções. |
| 4. “serviço independente de logs ... por tipo” | Coletor HTTP POST recebe campos URL-encoded. `RegistroErros.cliente` valida o tipo para impedir caminhos arbitrários e escolhe `client_<tipo>_errors.log`. `Telemetria` desktop e `InterfaceMiddlewareWeb` enviam erros capturados e disponibilizam provocação intencional. No fluxo JSP, o remetente de rede é GlassFish: IP registrado pelo coletor é o desse servidor, não necessariamente o navegador remoto. |
| 4/5. “linha em branco ... acrescentar ... sincronizar” | `RegistroErros` usa ConcurrentHashMap<Path,ReentrantLock>, UTF-8, CREATE/APPEND e force(true). Campos de entrada são higienizados para não quebrar separadores/linhas. Arquivos distintos têm locks distintos. Formato cliente tem 5 campos e servidor 6. Falha do logger vai a stderr para evitar recursão. |
| 5. “um arquivo por serviço ... identificar cliente” | TCP registra endereço remoto e classe:método; HTTP registra endpoint remoto. Erros sem conexão usam INTERNO/NÃO APLICÁVEL. O nome do arquivo é derivado do serviço, incluindo internal e coletor. |
| 6. “JARs ... WAR ... sem IDE” | Dois módulos têm classe Run no pacote default, portanto compilam em pastas separadas e recebem manifestos próprios. `.form` é preservado como fonte visual, mas inicialização Swing explícita permite javac puro. A API Jakarta vem do GlassFish somente na compilação; o WAR usa a API provided do contêiner. |
| 7. “Demonstração ... erros provocados” | CLI: `disco off` simula erro de persistência, `disco on` restaura. `erro desktop`, `erro web`, `erro internal` geram logs locais. Botões desktop/web provocam telemetria. `tests/IntegrationCheck.java` testa boot, RAM sem releitura, TCP+HTTP simultâneos, falha disco e restart independente. `tests/LoggingCheck.java` audita registros concorrentes e formatos. |

## Configuração e execução

Java 21 ou superior. Informe `GLASSFISH_HOME` para build/deploy quando não encontrado automaticamente. `chat.history`, `chat.logs`, `chat.host`, `chat.assets`, `chat.webService` e `chat.collector` tornam os caminhos/endereços configuráveis. Para navegador em outra máquina, chat.assets deve usar IP/nome público do servidor (não localhost).

O histórico continua contendo HTML formatado pelo cliente, como antes. Esta implementação para exercício mantém a fronteira de confiança original: não expor as portas sem controle em uma rede não confiável. Protocolo TCP writeUTF mantém o limite original de 65535 bytes por histórico e não confirma persistência ao remetente; HTTP POST confirma após persistência. Essas limitações são preservadas para não mudar o cliente/protocolo além do necessário.

## Explicação complementar do cliente web

Veja `docs/CLIENTE_WEB.md` para os métodos do helper HTTP e os JSPs individualmente. A sessão continua apenas com identificação/preferências; mensagens são obtidas do serviço Web independente e nunca salvas na sessão.

## Ajuste para preservar a estrutura original

O Web segue agora a mesma divisão do Desktop: `WebRecepcaoThread` recebe POST na `Util.portaRecepcaoWeb` (6663), enquanto `WebEnvioThread` entrega GET na `Util.portaEnvioWeb` (6665). Cada thread possui seu próprio listener e executor HTTP; `ServicoHttp` é apenas o código de atendimento compartilhado. `Util.portaLogs` (6664) centraliza a configuração do coletor. Painel/console ligam e desligam as duas direções Web juntas, com rollback se a segunda porta falhar. Desktop mantém 6661/6662. O botão de terceiros foi restaurado; o coletor ganhou controle próprio sem ocupar essa opção. Cliente web configura `chat.webService` para recepção e `chat.webEnvio` para consulta. As descrições anteriores de GET na 6663 foram substituídas por esta divisão.

## Ciclo de vida centralizado em Util

Console e painel agora apenas chamam `Util.iniciarDesktop/pararDesktop`, `iniciarWeb/pararWeb` e `iniciarLogs/pararLogs`. Nenhum dos dois cria os serviços. Desktop/Web são instanciados apenas ao ativar; falhas na segunda porta fecham a primeira, sem publicar referências incompletas. Desativar fecha listeners, interrompe/aguarda executores e elimina referências. Somente logs iniciam automaticamente em `Util.inicializar`. O histórico permanece único em RAM desde o boot por exigência do item 1. Shutdown hook encerra os serviços ao fechar o processo. `LifecycleCheck` verificou portas livres no boot, logs ativos, três ciclos de ativação/desativação e rollback com porta ocupada; PASS. A classe cliente `InterfaceMiddlewareWeb` explicita no nome seu papel de interface de comunicação HTTP entre cliente web e middleware; não se trata do tipo Java `interface`.

## Logs de serviços futuros (item 5)

`RegistroErros.TERCEIROS` e `RegistroErros.PUBLICIDADE` identificam as opções já previstas no menu. O método genérico `servidor(tipo, identificacao, ip, origem, erro)` também aceita qualquer novo identificador válido. Exemplo no catch de um serviço futuro:

```java
RegistroErros.servidor(RegistroErros.TERCEIROS, cliente, ip,
        "TerceirosRecepcaoThread:run", erro);
```

Sem cliente, use null em identificação/IP. O arquivo `server_middleware_terceiros_errors.log` ou `server_middleware_publicidade_errors.log` só é criado no primeiro registro. Não há serviço/thread ativado nem arquivos vazios antecipados. O coletor de clientes já aceita `tipo=terceiros` e tipos futuros, criando `client_<tipo>_errors.log` independentemente dos logs de servidor. Para demonstrar pelo console, `erro terceiros` e `erro publicidade` geram ocorrências intencionais. `FutureServiceLogsCheck` passou para terceiros, publicidade e novo_servico e confirmou que não houve inicialização de serviços.

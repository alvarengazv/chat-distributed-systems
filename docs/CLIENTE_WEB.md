# Cliente Web: enunciado e implementação correspondente

| Comando da atividade | Código e explicação |
|---|---|
| §2: “criar atendimento próprio do cliente Web, com porta e threads independentes e protocolo compatível (HTTP/WebSocket)” | `InterfaceMiddlewareWeb.consultar()` e `enviar()` usam GET e POST em `http://127.0.0.1:6663/messages`. Sai o protocolo `ObjectInputStream`/`ObjectOutputStream` das páginas. O atendimento concorrente e o ciclo de vida pertencem ao middleware; o GlassFish executa os JSPs em suas threads de requisição. Cada conexão HTTP tem timeout de conexão e leitura de dois segundos e é fechada em `finally`. |
| §3: “Centralizar as imagens/figurinhas em uma pasta do servidor Web acessível por URL HTTP(S), utilizada tanto pelo desktop quanto pelo cliente Web” | `InterfaceMiddlewareWeb.imagem()` gera URL de `/figurinhas/`; `index.jsp`, `topo.jsp` e `conversa.jsp` usam essa URL. `imagens()` converte os identificadores legados `chatimg:` e caminhos `images/` recebidos no histórico para a URL central, preservando mensagens antigas. Os PNGs deixam de ser carregados da cópia local do WAR. |
| §4: “Enviar as exceções capturadas em try-catch e gravá-las no servidor, por tipo de cliente” | Os catches de envio/consulta chamam `InterfaceMiddlewareWeb.registrar()`. A função envia formulário HTTP para o coletor independente na porta 6664, com `tipo=web`, computador/origem/mensagem. Se o coletor estiver desligado, a falha vai ao stderr do GlassFish, evitando recursão de telemetria. A separação e sincronização dos arquivos são responsabilidade do coletor. |
| §6: “Gerar o WAR da aplicação Web e implantá-lo em um servidor/contêiner compatível” | O helper depende somente do JDK; os JSPs continuam sendo compilados pelo GlassFish. O pacote WAR deve conter `InterfaceMiddlewareWeb.class` em `WEB-INF/classes/com/mycompany/chat_cliente_web/`. |
| §7: “erros provocados intencionalmente em desktop, Web e serviços do middleware” | O botão `Demonstrar erro` provoca `IllegalStateException`, capturada e encaminhada como `conversa:demonstrarErro`. Esse formulário não exige mensagem preenchida. |

## Fluxo mantido

`chat.jsp` mantém os três iframes, nickname e preferências na sessão. `conversa.jsp` continua montando o HTML por scriptlet, com escape do texto do usuário, avatar, cor, Fala/Grita/Xinga e conversão de emoticons. Ao receber confirmação HTTP de envio, limpa o campo e solicita a atualização do quadro. Em caso de erro conserva o texto digitado. `mensagem.jsp` continua usando apenas meta refresh para consultas periódicas; o JavaScript da página controla somente a rolagem até o fim.

## Configuração no servidor GlassFish

As propriedades Java `chat.webService`, `chat.collector` e `chat.assets` definem, respectivamente, a URL interna do serviço Web, a URL interna do coletor e a URL pública das imagens. Os valores padrão são `http://127.0.0.1:6663`, `http://127.0.0.1:6664` e `http://127.0.0.1:8080/figurinhas/`. Para acesso de outra máquina, `chat.assets` precisa usar o IP/DNS do servidor acessível ao navegador, pois a URL é incorporada ao HTML. Os dois serviços internos são acessados pelo processo GlassFish.

## Verificação executada

- `javac --release 21 -encoding UTF-8` compilou o helper sem dependências externas.
- O compilador real `org.glassfish.wasp.JspC`, distribuído com GlassFish 8.0.5, compilou `chat.jsp`, `conversa.jsp`, `index.jsp`, `mensagem.jsp` e `topo.jsp` sem erros.
- Teste de navegação e integração de rede deve ser feito com middleware e GlassFish ativos; compilação sozinha não prova o funcionamento desses fluxos.

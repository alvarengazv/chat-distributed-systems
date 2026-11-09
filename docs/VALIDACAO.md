# Validação executada

## Rodada 1 — build CLI

`bash scripts/build_and_package.sh`: PASS. Gerados dist/middleware.jar, dist/client-desktop.jar, dist/chat.war e dist/figurinhas.war. Desktop javac --release21 passou. Cinco JSPs compilados com compilador real do GlassFish. Comentários em português presentes nos executores, persistência e carregamento de imagens.

## Rodada 2 — integração

`IntegrationCheck`: PASS duas vezes após correções. Hidratação boot, consulta RAM após alteração externa do TXT, 20 POSTs concorrentes, TCP readUTF, persistência de envio desktop, falha de disco sem publicação RAM, stop/restart independente desktop/web/coletor. Execução exigiu permissão para sockets locais do sandbox.

Domínio GlassFish temporário `/tmp/chat-qa-domains/chatqa`, portas18048/18080: deploy de chat.war e diretório expandido de figurinhas PASS. GET /chat/, /chat/mensagem.jsp e /figurinhas/coracao.png: HTTP200. Domínio encerrado ao concluir. Não alteramos o domain1 do usuário. A consulta JSP foi executada com middleware offline e cobriu o estado de manutenção, não uma conversa end-to-end no browser.

## Rodada 3 — logs

`LoggingCheck`: PASS em 240 registros concorrentes, três tipos extensíveis, formato 5/6 campos, UTF-8, append, campos internos e uma linha vazia entre registros. IntegrationCheck provocou logs de serviços e clientes por HTTP. Arquivos temporários de teste foram mantidos em /tmp/chat-logging-check-* e /tmp/chat-integration-*.

## Revisões independentes

Concorrência: 9,2/10. Logs: 9,3/10. Deploy: 8,8/10. Média 9,1/10 no escopo inspecionado. Essas notas NÃO certificam todas as demonstrações solicitadas: GUI desktop, fluxo completo de navegador (login/envio/refresh/scroll), captura de erros por botões reais e múltiplos clientes reais ainda precisam de validação visual. Os testes automatizados passaram no escopo descrito; a verificação completa do enunciado ainda não foi declarada concluída.

## Limitações preservadas

TCP writeUTF mantém limite 65535 bytes e não confirma a gravação. O seletor de figurinhas conserva lista fixa, embora arquivos novos sejam servidos por URL. Telemetria web vem do GlassFish; IP coletado é do servidor web. Em falhas muito longas de disco, stop registra prazo de encerramento excedido. HTML do histórico conserva a confiança original nos clientes. Não há autenticação/CORS para clientes de terceiros diretamente no navegador; JSP faz HTTP server-side.

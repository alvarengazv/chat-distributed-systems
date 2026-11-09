import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ServicoHttp {
    private final boolean logs;
    private final boolean recepcao;
    private HttpServer servidor;
    private ExecutorService executor;

    public ServicoHttp(boolean logs) {
        this(logs, true);
    }

    public ServicoHttp(boolean logs, boolean recepcao) {
        this.logs = logs;
        this.recepcao = recepcao;
    }

    public synchronized void iniciar() throws IOException {
        if (servidor != null) {
            return;
        }
        HttpServer novo = HttpServer.create(new InetSocketAddress(Util.enderecoBind,
                logs ? Util.portaLogs : (recepcao ? Util.portaRecepcaoWeb : Util.portaEnvioWeb)), 0);

        // Virtual Threads isoladas por serviço: bloqueio de HTTP e disco, sem cálculo intensivo.
        executor = Executors.newVirtualThreadPerTaskExecutor();
        novo.setExecutor(executor);
        novo.createContext(logs ? "/errors" : "/messages", this::atender);
        novo.start();
        servidor = novo;
    }

    private void atender(HttpExchange troca) throws IOException {
        String tipo = logs ? "coletor" : "web";
        try (troca) {
            try {
                if (logs) {
                    if (!"POST".equals(troca.getRequestMethod())) {
                        responder(troca, 405, "Use POST");
                        return;
                    }
                    Map<String, String> campos = new HashMap<>();
                    for (String par : new String(troca.getRequestBody().readNBytes(65537), StandardCharsets.UTF_8).split("&")) {
                        String[] partes = par.split("=", 2);
                        if (partes.length == 2)
                            campos.put(URLDecoder.decode(partes[0], StandardCharsets.UTF_8), URLDecoder.decode(partes[1], StandardCharsets.UTF_8));
                    }
                    RegistroErros.cliente(campos.get("tipo"), campos.get("computador"), troca.getRemoteAddress().getAddress().getHostAddress(), campos.get("origem"), campos.get("mensagem"));
                    responder(troca, 200, "Registrado");
                } else if (!recepcao && "GET".equals(troca.getRequestMethod())) {
                    responder(troca, 200, Util.historico.consultar());
                } else if (recepcao && "POST".equals(troca.getRequestMethod())) {
                    byte[] corpo = troca.getRequestBody().readNBytes(65537);

                    if (corpo.length > 65536) {
                        responder(troca, 413, "Mensagem muito grande");
                        return;
                    }

                    Util.historico.adicionar(new String(corpo, StandardCharsets.UTF_8));
                    responder(troca, 200, "Persistida");
                } else {
                    responder(troca, 405, "Método inválido");
                }
            } catch (IllegalArgumentException e) {
                RegistroErros.servidor(tipo, troca.getRemoteAddress().toString(), troca.getRemoteAddress().getAddress().getHostAddress(), "ServicoHttp:atender", e);
                responder(troca, 400, "Evento inválido");
            } catch (IOException e) {
                RegistroErros.servidor(tipo, troca.getRemoteAddress().toString(), troca.getRemoteAddress().getAddress().getHostAddress(), "ServicoHttp:atender", e);
                responder(troca, 503, "Serviço indisponível");
            }
        }
    }

    private void responder(HttpExchange troca, int status, String mensagem) throws IOException {
        byte[] corpo = mensagem.getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        troca.getResponseHeaders().set("Cache-Control", "no-store");
        troca.sendResponseHeaders(status, corpo.length);
        troca.getResponseBody().write(corpo);
    }

    public synchronized void parar() {
        if (servidor != null) {
            servidor.stop(0);
            servidor = null;
        }

        if (executor != null) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    RegistroErros.servidor(logs ? "coletor" : "web", null, null, "ServicoHttp:parar", new IOException("Tarefas não encerraram no prazo"));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized boolean isRunning() {
        return servidor != null;
    }
}

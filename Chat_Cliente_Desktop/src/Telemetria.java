import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Envia erros ao coletor independente; uma falha de telemetria nunca impede o chat. */
public final class Telemetria {
    private Telemetria() { }
    public static void erro(String origem, Throwable erro) {
        // Virtual Thread: HTTP bloqueante e pouco processamento; não bloquear Swing.
        Thread.ofVirtual().start(() -> {
            try {
                String host = InetAddress.getLocalHost().getHostName();
                String dados = "tipo=desktop&computador=" + codificar(host)
                        + "&origem=" + codificar(origem) + "&mensagem=" + codificar(erro.toString());
                HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

                cliente.send(HttpRequest.newBuilder(URI.create(System.getProperty("chat.logs",
                                "http://" + Util.serverIpAddress + ":6664/errors")))
                        .timeout(Duration.ofSeconds(3)).header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(dados)).build(), HttpResponse.BodyHandlers.discarding());
            } catch (Exception falha) {
                System.err.println("Coletor indisponível: " + falha.getMessage());
            }
        });
    }
    private static String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }
}

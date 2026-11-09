import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Registro centralizado: um arquivo por origem, preservando o histórico em UTF-8.
 */
public final class RegistroErros {
    public static final String INTERNO = "INTERNO/NÃO APLICÁVEL";
    // Identificadores dos serviços previstos no menu. São somente nomes:
    // não criam threads, arquivos ou instâncias dos serviços futuros.
    public static final String DESKTOP = "desktop";
    public static final String WEB = "web";
    public static final String COLETOR = "coletor";
    public static final String INTERNAL = "internal";
    public static final String TERCEIROS = "terceiros";
    public static final String PUBLICIDADE = "publicidade";
    private static final ConcurrentHashMap<Path, ReentrantLock> TRAVAS = new ConcurrentHashMap<>();

    private RegistroErros() {
    }

    public static void cliente(String tipo, String computador, String ip, String origem, String mensagem) throws IOException {
        gravar("client_" + tipoValido(tipo) + "_errors.log",
                data() + " | " + campo(computador) + " | " + campo(ip)
                        + " | " + campo(origem) + " | " + campo(mensagem));
    }

    /**
     * Extensão para qualquer serviço, inclusive ainda não implementado.
     * Ex.: servidor(TERCEIROS, cliente, ip, "TerceirosRecepcaoThread:run", erro).
     * O arquivo/lock só nasce no primeiro erro; não é preciso alterar o coletor.
     * Para falha sem cliente, identificação e IP devem ser null.
     */
    public static void servidor(String tipo, String identificacao, String ip, String origem, Throwable erro) {
        servidor(tipo, identificacao, ip, origem, erro == null ? "Erro não informado" : erro.toString());
    }

    public static void servidor(String tipo, String identificacao, String ip, String origem, String mensagem) {
        try {
            String servico = tipoValido(tipo);

            // Sem cliente associado, os campos de identificação e IP são explícitos.
            boolean interno = identificacao == null || identificacao.isBlank();
            gravar("server_middleware_" + servico + "_errors.log",
                    data() + " | " + servico + " | " + campo(interno ? INTERNO : identificacao)
                            + " | " + campo(interno ? INTERNO : ip)
                            + " | " + campo(origem) + " | " + campo(mensagem));
        } catch (IOException | IllegalArgumentException falha) {
            // Não enviar uma falha do próprio logger novamente ao coletor: evita recursão.
            System.err.println("Falha ao registrar erro do serviço " + campo(tipo) + ": " + falha);
            System.err.println(campo(origem) + " | " + campo(mensagem));
        }
    }

    private static String tipoValido(String tipo) {
        if (tipo == null || !tipo.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Tipo de log inválido: use [a-z0-9_-]+");
        }
        return tipo;
    }

    private static String campo(String texto) {
        // Uma ocorrência ocupa exatamente uma linha; o cliente não pode injetar campos.
        return texto == null || texto.isBlank() ? INTERNO : texto.replace('|', '/').replaceAll("[\\r\\n\\u0085\\u2028\\u2029]+", " ");
    }

    private static String data() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static void gravar(String nome, String registro) throws IOException {
        Path arquivo = Path.of(System.getProperty("chat.logs", "dados/logs"))
                .resolve(nome).toAbsolutePath().normalize();
        ReentrantLock trava = TRAVAS.computeIfAbsent(arquivo, chave -> new ReentrantLock());

        // O lock é por arquivo: serviços distintos não se bloqueiam mutuamente.
        // APPEND preserva o histórico;
        trava.lock();
        try {
            Files.createDirectories(arquivo.getParent());
            try (FileChannel canal = FileChannel.open(arquivo, StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {

                ByteBuffer bytes = StandardCharsets.UTF_8.encode(registro + "\n\n");
                while (bytes.hasRemaining()) {
                    canal.write(bytes);
                }
                canal.force(true);
            }
        } finally {
            trava.unlock();
        }
    }
}

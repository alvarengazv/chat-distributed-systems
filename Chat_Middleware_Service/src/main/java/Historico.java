import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Histórico único: hidratação apenas no boot e consultas exclusivamente em RAM.
 */
public final class Historico {
    private final ArrayList<String> mensagens = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Path arquivo;
    private volatile boolean falharGravacao;

    public Historico(Path arquivo) throws IOException {
        this.arquivo = arquivo.toAbsolutePath();
        Files.createDirectories(this.arquivo.getParent());
        if (Files.exists(this.arquivo)) mensagens.addAll(Files.readAllLines(this.arquivo, StandardCharsets.UTF_8));
    }

    public String consultar() {
        lock.readLock().lock();
        try {
            return String.join("", mensagens);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void falharGravacao(boolean valor) {
        falharGravacao = valor;
    }

    public void adicionar(String mensagem) throws IOException {
        lock.writeLock().lock();
        try {
            if (falharGravacao){
                throw new IOException("Falha de disco simulada");
            }
            // Disco primeiro: force(true) confirma a persistência antes de publicar em RAM.
            // Em escrita parcial, truncamos para o tamanho anterior sob o mesmo lock.
            try (FileChannel canal = FileChannel.open(arquivo, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                long anterior = canal.size();
                try {
                    canal.position(anterior);
                    ByteBuffer dados = StandardCharsets.UTF_8.encode(mensagem + System.lineSeparator());
                    while (dados.hasRemaining()) canal.write(dados);
                    canal.force(true);
                } catch (IOException e) {
                    canal.truncate(anterior);
                    canal.force(true);
                    throw e;
                }
            }
            mensagens.add(mensagem);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

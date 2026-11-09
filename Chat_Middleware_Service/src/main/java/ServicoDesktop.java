import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Um controlador por porta; fechar um serviço não afeta os irmãos.
 */
public final class ServicoDesktop {
    private final boolean recepcao;
    private ServerSocket servidor;
    private ExecutorService tarefas;
    private final Set<Socket> clientes = ConcurrentHashMap.newKeySet();

    public ServicoDesktop(boolean recepcao) {
        this.recepcao = recepcao;
    }

    public synchronized void iniciar() throws IOException {
        if (servidor != null && !servidor.isClosed()) {
            return;
        }

        ServerSocket novo = new ServerSocket();

        try {
            novo.setReuseAddress(true);
            novo.bind(new InetSocketAddress(Util.enderecoBind,
                    recepcao ? Util.portaRecepcaoDesktop : Util.portaEnvioDesktop));
        } catch (IOException e) {
            novo.close();
            throw e;
        }
        servidor = novo;

        // Virtual Threads: conexões curtas bloqueiam em socket/arquivo; não são trabalho de CPU.
        tarefas = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService executor = tarefas;
        executor.submit(() -> aceitar(novo, executor));
    }

    private void aceitar(ServerSocket listener, ExecutorService executor) {
        while (!listener.isClosed()) {
            try {
                Socket cliente = listener.accept();
                clientes.add(cliente);
                try {
                    executor.submit(() -> atender(cliente));
                } catch (RejectedExecutionException e) {
                    clientes.remove(cliente);
                    cliente.close();
                }
            } catch (IOException e) {
                if (!listener.isClosed()) {
                    RegistroErros.servidor("desktop", null, null, "ServicoDesktop:aceitar", e);
                }
            }
        }
    }

    private void atender(Socket cliente) {
        try (cliente) {
            cliente.setSoTimeout(3000);

            if (recepcao) {
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                Util.historico.adicionar(entrada.readUTF());

            } else {
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.writeUTF(Util.historico.consultar());
                saida.flush();
            }
        } catch (IOException e) {
            RegistroErros.servidor("desktop", cliente.toString(), cliente.getInetAddress().getHostAddress(), "ServicoDesktop:atender", e);
        } finally {
            clientes.remove(cliente);
        }
    }

    public synchronized void fecharServidor() {
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
            RegistroErros.servidor("desktop", null, null, "ServicoDesktop:fecharServidor", e);
        }

        // Primeiro rejeitamos novos atendimentos; um accept que acabou de retornar
        // fechará seu socket ao tentar submeter trabalho ao executor encerrado.
        if (tarefas != null) {
            tarefas.shutdownNow();
        }

        for (Socket cliente : clientes) {
            try {
                cliente.close();
            } catch (IOException ignored) {
            }
        }

        if (tarefas != null) {
            try {
                if (!tarefas.awaitTermination(5, TimeUnit.SECONDS)) {
                    RegistroErros.servidor("desktop", null, null, "ServicoDesktop:fecharServidor", new IOException("Atendimentos não finalizaram em cinco segundos"));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized boolean isRunning() {
        return servidor != null && !servidor.isClosed();
    }

}

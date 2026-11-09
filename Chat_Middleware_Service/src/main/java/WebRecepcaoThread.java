/**
 * Atendimento HTTP do cliente Web, no mesmo padrão das threads Desktop.
 * Cada direção possui listener, porta e executor próprios. Virtual Threads são
 * adequadas às esperas de rede/disco; o executor é encerrado ao desligar o serviço.
 */
public class WebRecepcaoThread implements Runnable {
    private final ServicoHttp servico = new ServicoHttp(false, true);

    public void iniciar() throws java.io.IOException {
        servico.iniciar();
    }

    public void run() {
        try {
            iniciar();
        } catch (java.io.IOException e) {
            RegistroErros.servidor(RegistroErros.WEB, null, null, "WebRecepcaoThread:run", e);
        }
    }

    public void fecharServidor() {
        servico.parar();
    }

    public boolean isRunning() {
        return servico.isRunning();
    }
}

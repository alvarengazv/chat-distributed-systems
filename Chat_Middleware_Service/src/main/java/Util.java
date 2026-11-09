public class Util {
    // O middleware aceita conexões em todas as interfaces por padrão. Use
    // -Dchat.bindAddress=127.0.0.1 para restringi-lo à própria máquina.
    public static String enderecoBind = System.getProperty("chat.bindAddress", "0.0.0.0");
    public static int portaRecepcaoDesktop = 6662;
    public static int portaEnvioDesktop = 6661;
    public static int portaRecepcaoWeb = 6663;
    public static int portaEnvioWeb = 6665;
    public static int portaLogs = 6664;
    public static String pathRepDesktop = System.getProperty("chat.history", "dados/repositorio.txt");

    // Um único cache para todos os serviços; exigido pelo enunciado, carregado no boot.
    // Acessos nunca fazem leitura de disco. Desligar um serviço não duplica o histórico.
    public static Historico historico;
    public static DesktopRecepcaoThread desktopRecepcaoThread;
    public static DesktopEnvioThread desktopEnvioThread;
    public static WebRecepcaoThread webRecepcaoThread;
    public static WebEnvioThread webEnvioThread;
    public static ServicoHttp coletor;

    public static synchronized void inicializar() throws java.io.IOException {
        if (historico == null){
            historico = new Historico(java.nio.file.Path.of(pathRepDesktop));
        }
        iniciarLogs();
    }

    public static synchronized void iniciarDesktop() throws java.io.IOException {
        if (desktopRecepcaoThread != null){
            return;
        }
        DesktopRecepcaoThread recepcao = new DesktopRecepcaoThread();
        DesktopEnvioThread envio = new DesktopEnvioThread();
        try {
            recepcao.iniciar();
            envio.iniciar();
        } catch (java.io.IOException e) {
            recepcao.fecharServidor();
            envio.fecharServidor();
            throw e;
        }
        desktopRecepcaoThread = recepcao;
        desktopEnvioThread = envio;
    }

    public static synchronized void pararDesktop() {
        if (desktopRecepcaoThread != null) {
            desktopRecepcaoThread.fecharServidor();
        }
        if (desktopEnvioThread != null){
            desktopEnvioThread.fecharServidor();
        }
        desktopRecepcaoThread = null;
        desktopEnvioThread = null;
    }

    public static synchronized void iniciarWeb() throws java.io.IOException {
        if (webRecepcaoThread != null){
            return;
        }

        WebRecepcaoThread recepcao = new WebRecepcaoThread();
        WebEnvioThread envio = new WebEnvioThread();

        try {
            recepcao.iniciar();
            envio.iniciar();
        } catch (java.io.IOException e) {
            recepcao.fecharServidor();
            envio.fecharServidor();
            throw e;
        }
        webRecepcaoThread = recepcao;
        webEnvioThread = envio;
    }

    public static synchronized void pararWeb() {
        if (webRecepcaoThread != null){
            webRecepcaoThread.fecharServidor();
        }

        if (webEnvioThread != null){
            webEnvioThread.fecharServidor();
        }
        webRecepcaoThread = null;
        webEnvioThread = null;
    }

    public static synchronized void iniciarLogs() throws java.io.IOException {
        if (coletor != null && coletor.isRunning()){
            return;
        }
        ServicoHttp novo = new ServicoHttp(true);
        novo.iniciar();
        coletor = novo;
    }

    private static synchronized void pararLogs() {
        if (coletor != null){
            coletor.parar();
        }
        coletor = null;
    }

    public static synchronized void encerrar() {
        pararDesktop();
        pararWeb();
        pararLogs();
    }
}

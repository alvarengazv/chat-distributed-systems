public class Util {
    public static String nickname = "";
    public static String cor = "";
    public static String avatar = "";

    // Host do middleware e do servidor de imagens. Em execução local, loopback
    // funciona; em máquinas diferentes, configure chat.host com o IP/nome do servidor.
    public static String serverIpAddress = System.getProperty("chat.host", "127.0.0.1");
    public static int portaEnvioMiddleware = 6662;
    public static int portaConsultaMiddleware = 6661;
    public static int portaImagens = 8080;

    // Todas as imagens agora vêm da pasta central do servidor Web; os clientes não
    // precisam ser recompilados quando o administrador troca o arquivo no servidor.
    public static String assetsUrl = System.getProperty("chat.assets",
            "http://" + serverIpAddress + ":" + portaImagens + "/figurinhas/");

    public static java.net.URL imagem(String nome) {
        try {
            return java.net.URI.create(assetsUrl + nome).toURL();
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("URL das figurinhas inválida", e);
        }
    }
}

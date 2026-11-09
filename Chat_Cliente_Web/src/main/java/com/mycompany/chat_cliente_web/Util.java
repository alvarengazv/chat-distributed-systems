package com.mycompany.chat_cliente_web;

/** Endereços centrais usados pelo cliente Web para falar com os serviços. */
public final class Util {
    private Util() { }

    // Em instalação local, o GlassFish e o middleware compartilham a máquina.
    // Em rede, informe o host acessível a partir do GlassFish.
        public static final String HOST_MIDDLEWARE = System.getProperty("chat.host", "127.0.0.1");
    // Este endereço aparece no HTML e precisa ser acessível pelo navegador do usuário.
    public static final String HOST_IMAGENS = System.getProperty("chat.assetsHost", HOST_MIDDLEWARE);
    public static final int PORTA_WEB_RECEPCAO = 6663;
    public static final int PORTA_WEB_ENVIO = 6665;
    public static final int PORTA_LOGS = 6664;
    public static final int PORTA_IMAGENS = 8080;

    public static final String URL_WEB_ENVIO = System.getProperty("chat.webEnvio",
            "http://" + HOST_MIDDLEWARE + ":" + PORTA_WEB_ENVIO);
    public static final String URL_WEB_RECEPCAO = System.getProperty("chat.webService",
            "http://" + HOST_MIDDLEWARE + ":" + PORTA_WEB_RECEPCAO);
    public static final String URL_LOGS = System.getProperty("chat.collector",
            "http://" + HOST_MIDDLEWARE + ":" + PORTA_LOGS);
    public static final String URL_IMAGENS = System.getProperty("chat.assets",
            "http://" + HOST_IMAGENS + ":" + PORTA_IMAGENS + "/figurinhas/");
}

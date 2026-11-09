import java.util.Scanner;

public final class ConsoleServidor {
    public static void executar() {
        System.out.println("Comandos: start|stop desktop|web; disco on|off; erro <servico> (desktop, web, internal, terceiros, publicidade ou futuro); sair");
        try (Scanner entrada = new Scanner(System.in)) {
            while (entrada.hasNextLine()) {
                String[] cmd = entrada.nextLine().trim().split("\\s+");
                if (cmd[0].equals("sair")){
                    break;
                }
                if (cmd.length < 2){
                    continue;
                }
                try {
                    if (cmd[0].equals("disco")) Util.historico.falharGravacao(cmd[1].equals("off"));
                    else if (cmd[0].equals("erro")) {
                        RegistroErros.servidor(cmd[1], null, null, "ConsoleServidor:executar", new java.io.IOException("Erro intencional"));
                    } else if (cmd[0].equals("start")) {
                        switch (cmd[1]) {
                            case "desktop" -> Util.iniciarDesktop();
                            case "web" -> Util.iniciarWeb();
                            default -> throw new IllegalArgumentException("Serviço desconhecido");
                        }
                    } else if (cmd[0].equals("stop")){
                        parar(cmd[1]);
                    }
                    System.out.println("OK " + String.join(" ", cmd));
                } catch (Exception e) {
                    RegistroErros.servidor(java.util.Set.of("desktop", "web", "coletor").contains(cmd[1]) ? cmd[1] : "internal", null, null, "ConsoleServidor:executar", e);
                    System.err.println(e);
                }
            }
        } finally {
            Util.encerrar();
        }
    }

    private static void parar(String servico) {
        switch (servico) {
            case "desktop" -> Util.pararDesktop();
            case "web" -> Util.pararWeb();
            default -> throw new IllegalArgumentException("Serviço não controlável: " + servico);
        }
    }
}

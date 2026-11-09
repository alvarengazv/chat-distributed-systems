import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MenuServidor {
    SystemTray Tray;
    TrayIcon Icon;
    PopupMenu Popup;
    MenuItem mnuPainelControle;
    MenuItem mnuSair;
    Menu mnuAcoes;
    CheckboxMenuItem mnuItDesktop;
    CheckboxMenuItem mnuItWeb;
    CheckboxMenuItem mnuItTerceiros;
    CheckboxMenuItem mnuItPublicidade;
    FrmPainelDeControle frmPainelControle;

    public MenuServidor() {
        try {
            frmPainelControle = new FrmPainelDeControle();

            if(!SystemTray.isSupported()){
                frmPainelControle.setVisible(true);
                return;
            } else {
                Tray = SystemTray.getSystemTray();

                Dimension tamanho = Tray.getTrayIconSize();
                // Alguns hosts XEmbed do GNOME reservam 16 px mesmo quando o AWT
                // informa 24. Uma imagem de 24 px sem autosize acaba recortada.
                // Permite ajustar outros painéis via -Dchat.traySize=24, por exemplo.
                if (System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("linux")) {
                    int lado = Math.max(8, Math.min(128, Integer.getInteger("chat.traySize", 16)));
                    tamanho = new Dimension(lado, lado);
                }

                BufferedImage original;

                try (InputStream recurso = getClass().getResourceAsStream("/images/icone-branco.png")) {
                    if (recurso == null) {
                        throw new java.io.IOException("Ícone da bandeja não encontrado");
                    }
                    original = ImageIO.read(recurso);
                }
                if (original == null) {
                    throw new java.io.IOException("Imagem do ícone inválida");
                }
                BufferedImage redimensionada = new BufferedImage(tamanho.width, tamanho.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D desenho = redimensionada.createGraphics();

                try {
                    // XEmbed/AWT pode substituir o alpha por cinza no Linux.
                    // Compor sobre a cor do painel evita o quadrado cinza. O PNG
                    // original continua transparente; ajuste via chat.trayBackground.
                    if (System.getProperty("os.name", "").toLowerCase(java.util.Locale.ROOT).contains("linux")) {
                        Color fundo = Color.decode(System.getProperty("chat.trayBackground", "#000000"));
                        desenho.setColor(fundo);
                        desenho.fillRect(0, 0, tamanho.width, tamanho.height);
                    }
                    desenho.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    desenho.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // O tamanho da bandeja varia por desktop. Centralizar sem deformar
                    // evita depender do redimensionamento automático do AWT no Linux.
                    double escala = Math.min((double) tamanho.width / original.getWidth(), (double) tamanho.height / original.getHeight());
                    int largura = Math.max(1, (int) Math.round(original.getWidth() * escala));
                    int altura = Math.max(1, (int) Math.round(original.getHeight() * escala));
                    desenho.drawImage(original, (tamanho.width - largura) / 2, (tamanho.height - altura) / 2, largura, altura, null);
                } finally {
                    desenho.dispose();
                }
                Icon = new TrayIcon(redimensionada, "Servidor chat");
                Icon.setImageAutoSize(false);



                Popup = new PopupMenu();
                mnuPainelControle = new MenuItem("Abrir Painel de Controle");
                mnuPainelControle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frmPainelControle.setVisible(true);
                    }
                });
                mnuAcoes = new Menu("Ações");

                mnuItDesktop = new CheckboxMenuItem("Servidor Desktop");
                mnuItWeb = new CheckboxMenuItem("Servidor Web");
                mnuItTerceiros = new CheckboxMenuItem("Servidor Terceiros");
                mnuItPublicidade = new CheckboxMenuItem("Enviar Publicidade");

                Popup.add(mnuPainelControle);
                Popup.addSeparator();

                mnuAcoes.add(mnuItDesktop);
                mnuAcoes.add(mnuItWeb);
                mnuAcoes.add(mnuItTerceiros);
                mnuAcoes.addSeparator();
                mnuAcoes.add(mnuItPublicidade);

                Popup.add(mnuAcoes);
                Popup.addSeparator();
                mnuSair = new MenuItem("Fechar Middleware");
                // O shutdown hook registrado em Run encerra os serviços e os logs.
                mnuSair.addActionListener(e -> System.exit(0));
                Popup.add(mnuSair);

                Icon.setPopupMenu(Popup);
                Tray.add(Icon);
            }
        } catch (Exception e){
            RegistroErros.servidor("internal", null, null, "MenuServidor:construir", e);
            JOptionPane.showMessageDialog(null, "Erro ao criar menu do servidor de chat: " + e.getMessage());
        }
    }
}

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
                throw new Exception("Sem suporte a system tray!");
            } else {
                Tray = SystemTray.getSystemTray();

//                java.net.URL imgURL = getClass().getResource("/images/icone-preto.png");
//                if (imgURL == null) {
//                    throw new Exception("Imagem do ícone não encontrada em /images/icone-preto.png");
//                }
//                Image image = Toolkit.getDefaultToolkit().getImage(imgURL);
//                Icon = new TrayIcon(image);
//                Icon.setImageAutoSize(false);

                ImageIcon imgIcone = new ImageIcon("src/main/resources/images/icone-preto.png", "Servidor chat");
                Icon = new TrayIcon(imgIcone.getImage());
                Icon.setImageAutoSize(true);



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

                Icon.setPopupMenu(Popup);
                Tray.add(Icon);
            }
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao criar menu do servidor de chat: " + e.getMessage());
        }
    }
}

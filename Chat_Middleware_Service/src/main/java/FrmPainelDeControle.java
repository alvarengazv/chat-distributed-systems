import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrmPainelDeControle extends JFrame {
    private JPanel contentPane;
    private JLabel lblSrvDesktop;
    private JLabel lblSrvDesktopLegenda;
    private JToggleButton btnAtivarDesktop;
    private JLabel lblSrvWeb;
    private JToggleButton btnAtivarWeb;
    private JLabel lblSrvWebLegenda;
    private JLabel lblSrvTerceiros;
    private JToggleButton btnAtivarTerceiros;
    private JLabel lblSrvTerceirosLegenda;
    private JLabel lblSrvPublicitario;
    private JLabel lblSrvPublicitarioLegenda;
    private JToggleButton btnAtivarPublicitario;


    public FrmPainelDeControle() {
        btnAtivarDesktop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAtivarDesktop.isSelected()) {
                    try {
                        Util.iniciarDesktop();
                        btnAtivarDesktop.setText("DESATIVAR");
                    } catch (java.io.IOException erro) {
                        btnAtivarDesktop.setSelected(false);
                        RegistroErros.servidor("desktop", null, null, "FrmPainelDeControle:ativar", erro);
                    }
                } else {
                    int resposta = JOptionPane.showConfirmDialog(null, "Você tem certeza de que deseja parar os serviços para clientes DESKTOP?", "Confirmação", JOptionPane.YES_NO_OPTION);

                    if (resposta == JOptionPane.YES_OPTION) {
                        Util.pararDesktop();
                        btnAtivarDesktop.setText("ATIVAR");
                    } else {
                        btnAtivarDesktop.setSelected(true);
                    }
                }
            }
        });

        btnAtivarWeb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAtivarWeb.isSelected()) {
                    try {
                        Util.iniciarWeb();
                        btnAtivarWeb.setText("DESATIVAR");
                    } catch (java.io.IOException erro) {
                        btnAtivarWeb.setSelected(false);
                        RegistroErros.servidor("web", null, null, "FrmPainelDeControle:ativar", erro);
                    }
                } else {
                    Util.pararWeb();
                    btnAtivarWeb.setText("ATIVAR");
                }
            }
        });

        // Terceiros e publicidade conservam suas opções originais; logs são automáticos.
        btnAtivarTerceiros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAtivarTerceiros.isSelected()) {
                    btnAtivarTerceiros.setText("DESATIVAR");
                } else {
                    btnAtivarTerceiros.setText("ATIVAR");
                }
            }
        });

        btnAtivarPublicitario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnAtivarPublicitario.isSelected()) {
                    btnAtivarPublicitario.setText("DESATIVAR");
                } else {
                    btnAtivarPublicitario.setText("ATIVAR");
                }
            }
        });
        setTitle("Painel de Controle");
        setContentPane(contentPane);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(350, 300));
        pack();
        setLocationRelativeTo(null);
//        setVisible(true);
    }
}

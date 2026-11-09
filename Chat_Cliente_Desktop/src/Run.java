import javax.swing.*;

public class Run {
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            Telemetria.erro("Run:main", e);
        }

        // Carregamento de imagens HTTP antes da EDT evita congelar eventos de janelas.
        FrmLogin frmLogin = new FrmLogin();
    }
}

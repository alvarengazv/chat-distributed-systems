import javax.swing.*;
import java.awt.*;

public class Run {
    static MenuServidor menuServidor;

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

        Color gtkMenuBg       = new Color(45, 45, 45);    // #2d2d2d
        Color gtkBorder       = new Color(30, 30, 30);    // #1e1e1e
        Color gtkTextPrimary  = new Color(238, 238, 238); // #eeeeee
        Color gtkAccentBlue   = new Color(53, 132, 228);  // #3584e4
        Color gtkAccentText   = Color.WHITE;
        Font gtkFont          = new Font("Cantarell", Font.PLAIN, 12);

        String[] menuKeys = {"PopupMenu", "Menu", "MenuItem", "CheckBoxMenuItem"};
        for (String key : menuKeys) {
            UIManager.put(key + ".background", gtkMenuBg);
            UIManager.put(key + ".foreground", gtkTextPrimary);
            UIManager.put(key + ".selectionBackground", gtkAccentBlue);
            UIManager.put(key + ".selectionForeground", gtkAccentText);
            UIManager.put(key + ".font", gtkFont);
        }
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(gtkBorder, 1));
        UIManager.put("Separator.background", gtkBorder);
        UIManager.put("Separator.foreground", new Color(60, 60, 60));

        menuServidor = new MenuServidor();
    }
}

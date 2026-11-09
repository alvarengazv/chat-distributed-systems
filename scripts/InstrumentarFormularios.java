import com.intellij.compiler.instrumentation.InstrumentationClassFinder;
import com.intellij.uiDesigner.compiler.AsmCodeGenerator;
import com.intellij.uiDesigner.compiler.FormErrorInfo;
import com.intellij.uiDesigner.compiler.Utils;
import com.intellij.uiDesigner.lw.CompiledClassPropertiesProvider;
import com.intellij.uiDesigner.lw.LwRootContainer;
import org.jetbrains.org.objectweb.asm.ClassWriter;
import java.nio.file.Path;
import java.net.URL;

public class InstrumentarFormularios {
    public static void main(String[] args) throws Exception {
        Path classes = Path.of(args[0]);
        InstrumentationClassFinder finder = new InstrumentationClassFinder(new URL[]{classes.toUri().toURL(), com.intellij.uiDesigner.core.Spacer.class.getProtectionDomain().getCodeSource().getLocation(), InstrumentationClassFinder.createJDKPlatformUrl(System.getProperty("java.home"))});
        try {
            for (int i = 1; i < args.length; i++) {
                LwRootContainer form = Utils.getRootContainer(Path.of(args[i]).toUri().toURL(),
                        new CompiledClassPropertiesProvider(finder.getLoader()));
                AsmCodeGenerator gerador = new AsmCodeGenerator(form, finder, null, false,
                        new ClassWriter(ClassWriter.COMPUTE_FRAMES));
                gerador.patchFile(classes.resolve(form.getClassToBind().replace('.', '/') + ".class").toFile());
                for (FormErrorInfo erro : gerador.getErrors()) {
                    throw new IllegalStateException(args[i] + ": " + erro.getErrorMessage());
                }
            }
        } finally {
            finder.releaseResources();
        }
    }
}

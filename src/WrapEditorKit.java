import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class WrapEditorKit extends StyledEditorKit {
    private ViewFactory defaultFactory = new WrapColumnFactory();

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}

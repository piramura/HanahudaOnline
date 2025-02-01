import javax.swing.text.*;

/**
 * 英小文字・大文字・数字以外の入力を制限し、条件付きで有効化する DocumentFilter
 */
public class ConditionalAlphabetOnlyFilter extends DocumentFilter {
    private boolean isSettingOpen;
    private boolean isPlayerIcon;

    public ConditionalAlphabetOnlyFilter(boolean isSettingOpen, boolean isPlayerIcon) {
        this.isSettingOpen = isSettingOpen;
        this.isPlayerIcon = isPlayerIcon;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isInputAllowed(fb.getDocument().getLength(), string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (isInputAllowed(fb.getDocument().getLength(), text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isInputAllowed(int currentLength, String input) {
        if (isSettingOpen || isPlayerIcon) {
            return false;
        }
        return input != null && input.matches("[a-zA-Z0-9]+") && currentLength + input.length() <= 8;
    }
}
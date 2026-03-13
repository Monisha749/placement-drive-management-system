import javax.swing.*;

public final class UITransition {

    private UITransition() {
    }

    public static void switchFrame(JFrame currentFrame, Runnable openNextScreen) {
        if (openNextScreen == null) {
            return;
        }

        try {
            openNextScreen.run();

            // Dispose on next event-cycle so next UI can render first.
            if (currentFrame != null && currentFrame.isDisplayable()) {
                SwingUtilities.invokeLater(currentFrame::dispose);
            }
        } catch (Throwable ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            cause.printStackTrace();

            JOptionPane.showMessageDialog(
                    currentFrame,
                    "Unable to open the next screen.\n" + cause.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
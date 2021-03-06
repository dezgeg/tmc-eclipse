package tmc.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import tmc.eclipse.tasks.TaskStarter;
import tmc.eclipse.ui.EclipseIdeUIInvoker;

/**
 * UI handler for code review check
 * 
 */
public class CheckForCodeReviewsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell();
        EclipseIdeUIInvoker invoker = new EclipseIdeUIInvoker(shell);
        TaskStarter.startFetchCodeReviewsTask(invoker, true);
        return null;
    }
}

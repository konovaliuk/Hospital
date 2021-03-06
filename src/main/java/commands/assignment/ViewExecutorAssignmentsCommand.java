package commands.assignment;

import commands.ActionDbCommand;
import model.entities.Assignment;
import model.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource_managers.PageManager;
import services.AssignmentService;
import services.UserService;
import utils.CommandResult;
import utils.PageContent;
import utils.SessionRequestContent;

public class ViewExecutorAssignmentsCommand implements ActionDbCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewExecutorAssignmentsCommand.class);
    private static ViewExecutorAssignmentsCommand instance;

    public static ViewExecutorAssignmentsCommand getInstance() {
        if (instance == null) {
            synchronized (ViewExecutorAssignmentsCommand.class) {
                if (instance == null)
                    instance = new ViewExecutorAssignmentsCommand();
            }
        }
        return instance;
    }

    private ViewExecutorAssignmentsCommand() {
        LOGGER.info(getClass().getSimpleName() + " instance created!");
    }

    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        LOGGER.trace("Entering the method");
        int page = Integer.parseInt(sessionRequestContent.getSingleRequestParameter("page"));
        int executorId = Integer.parseInt(sessionRequestContent.getSingleRequestParameter("executor_id"));
        User executor = UserService.getUserById(executorId);
        try {
            User currentUser = (User)sessionRequestContent.getSessionAttribute("current_user");
            if (!currentUser.getRoleMap().containsKey(3L)) {
                sessionRequestContent.addRequestAttribute("error_message", "error.access");
                LOGGER.trace("Leaving the method");
                return new CommandResult(PageManager.getProperty("page.error"));
            }
            PageContent<Assignment> pageContent =
                    AssignmentService.getAssignmentsForPageByExecutorId(executorId, page, currentUser.getItemsPerPage());
            sessionRequestContent.addRequestAttribute("title", "assignments.assignments_executor");
            sessionRequestContent.addRequestAttribute("page_content", pageContent);
            sessionRequestContent.addRequestAttribute("url_pattern",
                    "/serv?action=view_doctor_assignments&executor_id=" + executorId + "&page=");
            sessionRequestContent.addRequestAttribute("user", executor);
            LOGGER.trace("Leaving the method");
            return new CommandResult(PageManager.getProperty("page.view_assignments"));
        } catch (RuntimeException e) {
            LOGGER.error("Error caught while executing the method:", e);
            LOGGER.trace("Leaving the method");
            return new CommandResult(PageManager.getProperty("page.error"));
        }
    }
}

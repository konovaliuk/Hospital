package commands.settings;

import commands.ActionDbCommand;
import exceptions.ErrorMessageKeysContainedException;
import model.entities.User;
import resource_managers.MessageManager;
import resource_managers.PageManager;
import services.AssignmentTypeService;
import utils.CommandResult;
import utils.SessionRequestContent;
import utils.json.ErrorResponseCreator;
import utils.json.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DeleteAssignmentTypeCommand implements ActionDbCommand {

    private static DeleteAssignmentTypeCommand instance;

    public static DeleteAssignmentTypeCommand getInstance() {
        if (instance == null) {
            synchronized (DeleteAssignmentTypeCommand.class) {
                if (instance == null)
                    instance = new DeleteAssignmentTypeCommand();
            }
        }
        return instance;
    }


    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        User currentUser = (User)sessionRequestContent.getSessionAttribute("current_user");
        long assignmentTypeId = Long.parseLong(sessionRequestContent.getSingleRequestParameter("id"));
        List<String> validationFails = new ArrayList<>();
        String jsonString = null;

        try {
            AssignmentTypeService.deleteAssignmentType(assignmentTypeId);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", MessageManager.getProperty("assignment_type.deleted", currentUser.getLanguage()));
            jsonString = JsonSerializer.serialize(responseMap);
            sessionRequestContent.addRequestAttribute("jsonString", jsonString);
            return new CommandResult(PageManager.getProperty("page.json"));
        } catch (ErrorMessageKeysContainedException e) {
            String generalError = e.getErrorMesageKeys().get(0);
            jsonString = ErrorResponseCreator.createResponseWithErrors(validationFails,
                    generalError, currentUser.getLanguage());
            sessionRequestContent.addRequestAttribute("jsonString", jsonString);
            return new CommandResult(PageManager.getProperty("page.json"));
        }
    }
}

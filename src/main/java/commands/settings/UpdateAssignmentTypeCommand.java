package commands.settings;

import commands.ActionDbCommand;
import exceptions.ErrorMessageKeysContainedException;
import model.entities.AssignmentType;
import model.entities.User;
import resource_managers.MessageManager;
import resource_managers.PageManager;
import services.AssignmentTypeService;
import utils.CommandResult;
import utils.SessionRequestContent;
import utils.json.ErrorResponseCreator;
import utils.json.JsonSerializer;
import utils.parsers.AssignmentTypeParser;
import validation.EntityValidatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateAssignmentTypeCommand implements ActionDbCommand {

    private static UpdateAssignmentTypeCommand instance;

    public static UpdateAssignmentTypeCommand getInstance() {
        if (instance == null) {
            synchronized (UpdateAssignmentTypeCommand.class) {
                if (instance == null)
                    instance = new UpdateAssignmentTypeCommand();
            }
        }
        return instance;
    }


    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        User currentUser = (User)sessionRequestContent.getSessionAttribute("current_user");
        AssignmentType assignmentType = AssignmentTypeParser.parseAssignmentType(sessionRequestContent);
        List<String> validationFails = EntityValidatorFactory.getValidatorFor(AssignmentType.class).validate(assignmentType);
        String jsonString = null;

        if (!validationFails.isEmpty()) {
            jsonString = ErrorResponseCreator.createResponseWithErrors(validationFails, null, currentUser.getLanguage());
            sessionRequestContent.addRequestAttribute("jsonString", jsonString);
            return new CommandResult(PageManager.getProperty("page.json"));
        }

        try {
            AssignmentTypeService.updateAssignmentType(assignmentType);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", MessageManager.getProperty("assignment_type.updated", currentUser.getLanguage()));
            jsonString = JsonSerializer.serialize(responseMap);
            sessionRequestContent.addRequestAttribute("jsonString", jsonString);
            return new CommandResult(PageManager.getProperty("page.json"));
        } catch (ErrorMessageKeysContainedException e) {
            validationFails.addAll(e.getErrorMesageKeys());
            jsonString = ErrorResponseCreator.createResponseWithErrors(validationFails, null, currentUser.getLanguage());
            sessionRequestContent.addRequestAttribute("jsonString", jsonString);
            return new CommandResult(PageManager.getProperty("page.json"));
        }
    }


}

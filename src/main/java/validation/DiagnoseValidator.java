package validation;

import model.entities.Diagnose;
import model.entities.Examination;
import resource_managers.RegexManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiagnoseValidator implements EntityValidator<Diagnose> {

    private static DiagnoseValidator instance;

    public static DiagnoseValidator getInstance() {
        if (instance == null) {
            synchronized (DiagnoseValidator.class) {
                if (instance == null)
                    instance = new DiagnoseValidator();
            }
        }
        return instance;
    }

    private DiagnoseValidator() {

    }

    @Override
    public List<String> validate(Diagnose diagnose) {
        List<String> errorMessageKeys = new ArrayList<>(1);
        if (diagnose.getCode() == null || !diagnose.getCode().matches(RegexManager.getProperty("regex.diagnose.code")))
            errorMessageKeys.add("validation.diagnose.code");
            return errorMessageKeys;
    }
}

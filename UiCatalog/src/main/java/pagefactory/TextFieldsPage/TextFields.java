package pagefactory.TextFieldsPage;

import base.MobileAPI;

/**
 * Created by mrahman on 1/15/17.
 */
public class TextFields extends MobileAPI{

    public void typeOnUiTextField(){
        typeByXpath("//UIAApplication[1]/UIAWindow[2]/UIATableView[1]/UIATableCell[1]", "Architecture");
    }
    public void typeOnUiTextFieldRounded(){
        typeByXpath("//UIAApplication[1]/UIAWindow[2]/UIATableView[1]/UIATableCell[3]", "Rounded");
    }
    public void typeOnUiTextFieldSecure(){
        typeByXpath("//UIAApplication[1]/UIAWindow[2]/UIATableView[1]/UIATableCell[5]", "test123");
    }
    public void typeOnUiTextFieldLeftView(){
        typeByXpath("//UIAApplication[1]/UIAWindow[2]/UIATableView[1]/UIATableCell[7]", "Secure");
    }

    public void writeTextToFields()throws InterruptedException{
        typeOnUiTextField();
        typeOnUiTextFieldRounded();
        typeOnUiTextFieldSecure();
        typeOnUiTextFieldLeftView();
    }
}

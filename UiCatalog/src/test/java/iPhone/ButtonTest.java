package iPhone;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;
import pagefactory.NavigateUi;
import pagefactory.UiCatalogPage.UiCatalog;

/**
 * Created by mrahman on 9/5/15.
 */
public class ButtonTest extends NavigateUi {

    @Test
    public void navigate()throws InterruptedException{
        UiCatalog ui = PageFactory.initElements(ad, UiCatalog.class);
        ui.getButtonPage();

    }

}

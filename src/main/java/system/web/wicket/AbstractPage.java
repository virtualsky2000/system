package system.web.wicket;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.FormComponent;

import system.logging.Logger;

public class AbstractPage extends WebPage {

    private Logger log;


	/**
	 * クリア処理
	 *
	 * @param parentId
	 *            親のID
	 */
	@SuppressWarnings("rawtypes")
	protected void clear(String parentId) {
		Component parent = WicketUtils.findComponent(this, parentId);
		if (parent instanceof MarkupContainer) {
			List<FormComponent> lstComponent = WicketUtils.getChilds((MarkupContainer)parent, FormComponent.class);

			for (FormComponent component : lstComponent) {
				component.getValue();
//				component.setModelValue(value);
			}
		}
	}

}

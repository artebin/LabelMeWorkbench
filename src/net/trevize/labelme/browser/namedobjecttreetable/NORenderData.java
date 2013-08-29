package net.trevize.labelme.browser.namedobjecttreetable;

import org.netbeans.swing.outline.CheckRenderDataProvider;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * NORenderData.java - Apr 13, 2010
 */

public class NORenderData implements CheckRenderDataProvider {

	private NOTreeModel model;

	public NORenderData(NOTreeModel model) {
		this.model = model;
	}

	/***************************************************************************
	 * implementation of CheckRenderDataProvider.
	 **************************************************************************/

	@Override
	public java.awt.Color getBackground(Object o) {
		return null;
	}

	@Override
	public String getDisplayName(Object o) {
		return ((NOTreeNode) o).getUserObject().toString();
	}

	@Override
	public java.awt.Color getForeground(Object o) {
		NOTreeNode node = (NOTreeNode) o;
		return null;
	}

	@Override
	public javax.swing.Icon getIcon(Object o) {
		return null;

	}

	@Override
	public String getTooltipText(Object o) {
		return null;
	}

	@Override
	public boolean isHtmlDisplayName(Object o) {
		return false;
	}

	@Override
	public boolean isCheckEnabled(Object arg0) {
		if (arg0 == model.getRoot()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isCheckable(Object arg0) {
		if (arg0 == model.getRoot()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Boolean isSelected(Object arg0) {
		NOTreeNode node = (NOTreeNode) arg0;
		return node.isChecked();
	}

	@Override
	public void setSelected(Object arg0, Boolean arg1) {
		NOTreeNode node = (NOTreeNode) arg0;
		node.setChecked(arg1);
	}
	
}

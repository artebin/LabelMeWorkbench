package net.trevize.labelme.browser.namedobjecttreetable;

import org.netbeans.swing.outline.RowModel;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * NORowModel.java - Apr 13, 2010
 */

public class NORowModel implements RowModel {

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		default:
			assert false;
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return 0;
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? "Element value" : null;
	}

	@Override
	public Object getValueFor(Object node, int column) {
		NOTreeNode n = (NOTreeNode) node;
		switch (column) {
		case 0:
			if (n.getTableRowValues().size() == 0) {
				return "";
			} else {
				return n.getTableRowValues().get(0);
			}
		default:
			assert false;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return false;
	}

	@Override
	public void setValueFor(Object node, int column, Object value) {
		//do nothing for now
	}

}
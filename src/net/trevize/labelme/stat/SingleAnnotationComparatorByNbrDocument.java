package net.trevize.labelme.stat;

import java.util.Comparator;

/**
 * This class is a SingleAnnotation comparator on the multiplicity.
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 */

public class SingleAnnotationComparatorByNbrDocument implements
		Comparator<SingleAnnotation> {

	@Override
	public int compare(SingleAnnotation o1, SingleAnnotation o2) {
		int df = (Integer) (o1.nbrDocument.getValue())
				- (Integer) (o2.nbrDocument.getValue());
		if (df > 0) {
			return 1;
		} else if (df == 0) {
			return 0;
		} else {
			return -1;
		}
	}
}

package net.sf.latexdraw.view.svg;

import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IEllipse;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.parsers.svg.SVGAttributes;
import net.sf.latexdraw.parsers.svg.SVGDocument;
import net.sf.latexdraw.parsers.svg.SVGElement;
import net.sf.latexdraw.parsers.svg.SVGEllipseElement;
import net.sf.latexdraw.parsers.svg.SVGGElement;
import net.sf.latexdraw.util.LNamespace;
import net.sf.latexdraw.view.pst.PSTricksConstants;

/**
 * An SVG generator for an ellipse.
 * @author Arnaud BLOUIN
 */
class SVGEllipse extends SVGShape<IEllipse> {
	/**
	 * Creates a generator of SVG ellipse.
	 * @param ellipse The ellipse shape used for the generation.
	 * @throws IllegalArgumentException If ellipse is null.
	 * @since 2.0
	 */
	protected SVGEllipse(final IEllipse ellipse) {
		super(ellipse);
	}



	/**
	 * Creates an ellipse from an SVG ellipse element.
	 * @param elt The source element.
	 * @since 2.0.0
	 */
	protected SVGEllipse(final SVGEllipseElement elt) {
		this(ShapeFactory.INST.createEllipse());

		setSVGParameters(elt);
		setEllipseParameters(elt, 0.);
		applyTransformations(elt);
	}


	/**
	 * Creates an ellipse from a latexdraw-SVG element.
	 * @param elt The source element.
	 * @since 2.0.0
	 */
	protected SVGEllipse(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createEllipse());

		final SVGElement elt2 = getLaTeXDrawElement(elt, null);

		if(elt==null || !(elt2 instanceof SVGEllipseElement))
			throw new IllegalArgumentException();

		setSVGLatexdrawParameters(elt);
		setSVGParameters(elt2);
		setEllipseParameters((SVGEllipseElement)elt2, getPositionGap());

		setSVGShadowParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_SHADOW));
		setSVGDbleBordersParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_DBLE_BORDERS));

		if(withTransformation)
			applyTransformations(elt);
	}



	/**
	 * Sets the ellipse parameters.
	 * @param ellipseElt The source SVG ellipse element.
	 * @param gap The gap used to define the latexdraw ellipse.
	 * @since 3.0
	 */
	protected void setEllipseParameters(final SVGEllipseElement ellipseElt, final double gap) {
		final double width  = 2.*ellipseElt.getRx()-gap;
		final double height	= 2.*ellipseElt.getRy()-gap;

		shape.setPosition(ellipseElt.getCx()-width/2., ellipseElt.getCy()+height/2.);
		shape.setWidth(width);
		shape.setHeight(height);
	}



	@Override
	public SVGElement toSVG(final SVGDocument doc) {
		if(doc==null || doc.getFirstChild().getDefs()==null)
			throw new IllegalArgumentException();

		final IPoint tl = shape.getTopLeftPoint();
		final IPoint br = shape.getBottomRightPoint();
		final double tlx = tl.getX();
		final double tly = tl.getY();
		final double brx = br.getX();
		final double bry = br.getY();
		SVGElement elt;
		final SVGElement root = new SVGGElement(doc);
        root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE+':'+LNamespace.XML_TYPE, LNamespace.XML_TYPE_ELLIPSE);
        root.setAttribute(SVGAttributes.SVG_ID, getSVGID());
        final double gap 	= getPositionGap();
        final double width  = Math.max(1., (brx-tlx+gap)/2.);
        final double height = Math.max(1., (bry-tly+gap)/2.);
        final double x		= (brx+tlx)/2.;
        final double y		= (bry+tly)/2.;

        if(shape.hasShadow()) {
        	elt = new SVGEllipseElement(x, y, width, height, doc);
        	setSVGShadowAttributes(elt, true);
        	root.appendChild(elt);
        }

        if(shape.hasShadow() && !shape.getLineStyle().getLatexToken().equals(PSTricksConstants.LINE_NONE_STYLE)) {
        	// The background of the borders must be filled is there is a shadow.
        	elt = new SVGEllipseElement(x, y, width, height, doc);
        	setSVGBorderBackground(elt, root);
        }

        elt = new SVGEllipseElement(x, y, width, height, doc);
        setSVGAttributes(doc, elt, true);
        root.appendChild(elt);

        if(shape.hasDbleBord())  {
        	elt = new SVGEllipseElement(x, y, width, height, doc);
        	setSVGDoubleBordersAttributes(elt);
        	root.appendChild(elt);
        }

        setSVGRotationAttribute(root);

		return root;
	}
}

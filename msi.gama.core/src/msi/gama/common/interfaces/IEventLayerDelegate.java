/*********************************************************************************************
 *
 * 'ICreateDelegate.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;
import java.util.Map;

import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.IScope;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;

/**
 * Class ICreateDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface IEventLayerDelegate {

	/**
	 * Returns whether or not this delegate accepts to create agents from this
	 * source.
	 * @param scope TODO
	 * @param source
	 * 
	 * @return
	 */

	boolean acceptSource(IScope scope, Object source);

	/**
	 * Fills the list of maps with the initial values read from the source.
	 * Returns true if all the inits have been correctly filled
	 * 
	 * @param scope
	 * @param inits
	 * @param max
	 *            can be null (in that case, the maximum number of agents to
	 *            create is ignored)
	 * @param source
	 * @return
	 */

	boolean createFrom(IScope scope, List<Map<String, Object>> inits, Integer max, Object source, Arguments init,
			EventLayerStatement statement);

	/**
	 * Returns the type expected in the 'from:' facet of 'create' statement.
	 * Should not be null and should be different from IType.NO_TYPE (in order
	 * to be able to check the validity of create statements at compile time-
	 * 
	 * @return a GAML type representing the type of the source expected by this
	 *         ICreateDelegate
	 */
	IType<?> fromFacetType();

}

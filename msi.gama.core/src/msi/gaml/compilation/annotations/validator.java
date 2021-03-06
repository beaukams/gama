package msi.gaml.compilation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import msi.gaml.compilation.IValidator;

/**
 * Allows to declare a custom validator for Symbols. This validator, if declared on subclasses of Symbol, will be called
 * after the standard validation is done. The validator must be subclass of IDescriptionValidator
 *
 * @author drogoul
 * @since 11 nov. 2014
 *
 */

@Retention (RetentionPolicy.RUNTIME)
@Target ({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@SuppressWarnings ({ "rawtypes" })
public @interface validator {

	Class<? extends IValidator> value();
}
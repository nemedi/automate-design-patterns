_package:begin
package $packageName;
_package:end

import $builderClassName;
import $className;

// Testing...
public class $thisBuilderClassSimpleName {

	private $builderClassSimpleName<$classSimpleName> builder = new $builderClassSimpleName<$classSimpleName>($classSimpleName.class);
	
_setter:begin	
	public $thisBuilderClassSimpleName $fieldName($fieldType value) {
		builder.set(value);
		return this;
	}
_setter:end

_getter:begin
	public $fieldType $fieldName() {
		return builder.get();
	}
_getter:end
	
	public $classSimpleName build() {
		return builder.build();
	}

}

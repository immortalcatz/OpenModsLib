package openmods.calc.parsing;

import java.util.EnumSet;
import java.util.Set;

enum TokenProperties {
	VALUE,
	SYMBOL,
	NEXT_OP_UNARY,
	INSERT_DEFAULT_OP
}

public enum TokenType {
	DEC_NUMBER(TokenProperties.VALUE),
	HEX_NUMBER(TokenProperties.VALUE),
	OCT_NUMBER(TokenProperties.VALUE),
	BIN_NUMBER(TokenProperties.VALUE),
	QUOTED_NUMBER(TokenProperties.VALUE),

	SYMBOL(TokenProperties.SYMBOL, TokenProperties.INSERT_DEFAULT_OP),
	SYMBOL_WITH_ARGS(TokenProperties.SYMBOL),

	OPERATOR(TokenProperties.NEXT_OP_UNARY),
	LEFT_BRACKET(TokenProperties.NEXT_OP_UNARY, TokenProperties.INSERT_DEFAULT_OP),
	RIGHT_BRACKET(),
	SEPARATOR(TokenProperties.NEXT_OP_UNARY);

	public boolean isValue() {
		return properties.contains(TokenProperties.VALUE);
	}

	public final boolean isSymbol() {
		return properties.contains(TokenProperties.SYMBOL);
	}

	public final boolean isNextOpUnary() {
		return properties.contains(TokenProperties.NEXT_OP_UNARY);
	}

	public final boolean canInsertDefaultOp() {
		return properties.contains(TokenProperties.INSERT_DEFAULT_OP);
	}

	private final Set<TokenProperties> properties;

	private TokenType(TokenProperties property, TokenProperties... properties) {
		this.properties = EnumSet.of(property, properties);
	}

	private TokenType() {
		this.properties = EnumSet.noneOf(TokenProperties.class);
	}
}
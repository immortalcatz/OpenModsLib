package openmods.calc;

import openmods.calc.CalcTestUtils.CalcCheck;
import openmods.calc.Calculator.ExprType;
import openmods.calc.types.fp.DoubleCalculator;
import org.junit.Test;

public class DoubleCalculatorTest {

	private final DoubleCalculator sut = new DoubleCalculator();

	public CalcCheck<Double> infix(String value) {
		final IExecutable<Double> expr = sut.compile(ExprType.INFIX, value);
		return new CalcCheck<Double>(sut, expr);
	}

	public CalcCheck<Double> postfix(String value) {
		final IExecutable<Double> expr = sut.compile(ExprType.POSTFIX, value);
		return new CalcCheck<Double>(sut, expr);
	}

	@Test
	public void testBasicPostfix() {
		postfix("1 2 +").expectResult(3.0).expectEmptyStack();
		postfix("0.5 0.5 +").expectResult(1.0).expectEmptyStack();
		postfix("0.25 0.25 +").expectResult(0.5).expectEmptyStack();
		postfix("2 3 *").expectResult(6.0).expectEmptyStack();
		postfix("10 2 /").expectResult(5.0).expectEmptyStack();
		postfix("2 5 ^").expectResult(32.0).expectEmptyStack();
	}

	@Test
	public void testPostfixStackOperations() {
		postfix("2 dup +").expectResult(4.0).expectEmptyStack();
		postfix("1 2 3 pop +").expectResult(3.0).expectEmptyStack();
		postfix("2 3 swap -").expectResult(1.0).expectEmptyStack();
	}

	@Test
	public void testPostfixDupWithArgs() {
		postfix("0 1 2 dup@2").execute().expectStack(0.0, 1.0, 2.0, 1.0, 2.0);
	}

	@Test
	public void testPostfixDupWithReturns() {
		postfix("1 2 dup@,4").execute().expectStack(1.0, 2.0, 2.0, 2.0, 2.0);
	}

	@Test
	public void testPostfixDupWithArgsAndReturns() {
		postfix("1 2 3 4 dup@3,5").execute().expectStack(1.0, 2.0, 3.0, 4.0, 2.0, 3.0);
	}

	@Test
	public void testPostfixPopWithArgs() {
		postfix("1 2 3 4 pop@3").execute().expectStack(1.0);
	}

	@Test
	public void testVariadicPostfixFunctions() {
		postfix("max@0").expectResult(0.0).expectEmptyStack();
		postfix("1 max@1").expectResult(1.0).expectEmptyStack();
		postfix("1 2 max@2").expectResult(2.0).expectEmptyStack();
		postfix("3 2 1 max@3").expectResult(3.0).expectEmptyStack();
		postfix("2 4 INF 1 max@4").expectResult(Double.POSITIVE_INFINITY).expectEmptyStack();
	}

	@Test
	public void testBasicInfix() {
		infix("1 + 2").expectResult(3.0).expectEmptyStack();
		infix("2 * 3").expectResult(6.0).expectEmptyStack();
		infix("10 / 2").expectResult(5.0).expectEmptyStack();
		infix("2 ^ 5").expectResult(32.0).expectEmptyStack();
		infix("-PI").expectResult(-Math.PI).expectEmptyStack();
		infix("2*E").expectResult(2 * Math.E).expectEmptyStack();
		infix("2*-E").expectResult(2 * -Math.E).expectEmptyStack();
		infix("2E").expectResult(2 * Math.E).expectEmptyStack();
		infix("2(E)").expectResult(2 * Math.E).expectEmptyStack();
		infix("-2*-3*10^3").expectResult(6e3).expectEmptyStack();
		infix("-2*-3*10^+3").expectResult(6e3).expectEmptyStack();
		infix("-2*-3*10^-3").expectResult(6e-3).expectEmptyStack();
		infix("2*10^2+3*10^3").expectResult(3200.0).expectEmptyStack();
		infix("2*10^2*3*10^3").expectResult(6e5).expectEmptyStack();
	}

	@Test
	public void testBasicOrdering() {
		infix("1 + 2 - 3").expectResult(0.0).expectEmptyStack();

		infix("1 + 2 * 3").expectResult(7.0).expectEmptyStack();
		infix("1 + (2 * 3)").expectResult(7.0).expectEmptyStack();
		infix("(1 + 2) * 3").expectResult(9.0).expectEmptyStack();
		infix("-(1 + 2) * 3").expectResult(-9.0).expectEmptyStack();
		infix("(1 + 2) * -3").expectResult(-9.0).expectEmptyStack();
		infix("--3").expectResult(3.0).expectEmptyStack();

		infix("2 * 2 ^ 2").expectResult(8.0).expectEmptyStack();
		infix("2 * (2 ^ 2)").expectResult(8.0).expectEmptyStack();
		infix("(2 * 2) ^ 2").expectResult(16.0).expectEmptyStack();
	}

	@Test
	public void testBasicInfixFunctions() {
		infix("sin(0)").expectResult(0.0).expectEmptyStack();
		infix("cos(0)").expectResult(1.0).expectEmptyStack();
		infix("1+cos(0)").expectResult(2.0).expectEmptyStack();
		infix("5cos(0)").expectResult(5.0).expectEmptyStack();
		infix("min(2,3)").expectResult(2.0).expectEmptyStack();
		infix("max(2,3)").expectResult(3.0).expectEmptyStack();
		infix("2-max(2,3)").expectResult(-1.0).expectEmptyStack();
	}

	@Test
	public void testVariadicInfixFunctions() {
		infix("max()").expectResult(0.0).expectEmptyStack();
		infix("max(1)").expectResult(1.0).expectEmptyStack();
		infix("max(1,2)").expectResult(2.0).expectEmptyStack();
		infix("max(3,2,1)").expectResult(3.0).expectEmptyStack();
		infix("max(2,4,INF,1)").expectResult(Double.POSITIVE_INFINITY).expectEmptyStack();
	}

	@Test(expected = Exception.class)
	public void testTooManyParameters() {
		infix("sin(0, 1)").execute();
	}

	@Test(expected = Exception.class)
	public void testTooFewParameters() {
		infix("atan2(0)").execute();
	}
}

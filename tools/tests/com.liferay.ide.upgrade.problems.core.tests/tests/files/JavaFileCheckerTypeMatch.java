package blade.migrate.liferay70;

public class JavaFileCheckerTypeMatch {

	public static void main(String[] args) {
		Foo foo = new Foo();

		String s1 = "";
		java.lang.String s2 = "";

		foo.barForString(s1);
		foo.barForString(s2);

		foo.barForStringFull(s1);
		foo.barForStringFull(s2);

		String[] sa1 = null;
		java.lang.String[] sa2 = null;

		foo.barForStringArray(sa1);
		foo.barForStringArray(sa2);

		foo.barForStringArrayFull(sa1);
		foo.barForStringArrayFull(sa2);

		AnyClass a1 = null;
		blade.migrate.liferay70.AnyClass a2 = null;

		foo.barForClass(a1);
		foo.barForClass(a2);

		foo.barForClassFull(a1);
		foo.barForClassFull(a2);

		AnyClass[] aa1 = null;
		blade.migrate.liferay70.AnyClass[] aa2 = null;

		foo.barForClassArray(aa1);
		foo.barForClassArray(aa2);

		foo.barForClassArrayFull(aa1);
		foo.barForClassArrayFull(aa2);

		Object o1 = null;
		java.lang.Object o2 = null;

		Object[] oa1 = null;
		java.lang.Object[] oa2 = null;

		foo.barForObject(o1);
		foo.barForObject(o2);
		foo.barForObject(a1);
		foo.barForObject(a2);
		foo.barForObject(s1);
		foo.barForObject(s2);

		// Should ignore and couldn't find
		foo.barForObject(oa1);
		foo.barForObject(oa2);

		// Should ignore and couldn't find
		foo.barForObjectFull(oa1);
		foo.barForObjectFull(oa2);

		foo.barForObjectFull(o1);
		foo.barForObjectFull(o2);
		foo.barForObjectFull(a1);
		foo.barForObjectFull(a2);
		foo.barForObjectFull(s1);
		foo.barForObjectFull(s2);

		long l1 = 0;
		Long l2 = 0L;
		java.lang.Long l3 = 0L;

		long[] la1 = null;
		Long[] la2 = null;
		java.lang.Long[] la3 = null;

		int i1 = 0;
		Integer i2 = 0;
		java.lang.Integer i3 = 0;

		int[] ia1 = null;
		Integer[] ia2 = null;
		java.lang.Integer[] ia3 = null;

		short short1 = 0;
		Short short2 = 0;
		java.lang.Short short3 = 0;

		short[] shortArray1 = null;
		Short[] shortArray2 = null;
		java.lang.Short[] shortArray3 = null;

		byte b1 = 0;
		Byte b2 = 0;
		java.lang.Byte b3 = 0;

		byte[] ba1 = null;
		Byte[] ba2 = null;
		java.lang.Byte[] ba3 = null;

		foo.barForLong(l1);
		foo.barForLong(l2);
		foo.barForLong(l3);

		foo.barForLong(i1);
		foo.barForLong(i2);
		foo.barForLong(i3);

		foo.barForLong(short1);
		foo.barForLong(short2);
		foo.barForLong(short3);

		foo.barForLong(b1);
		foo.barForLong(b2);
		foo.barForLong(b3);

		foo.barForLongClass(l1);
		foo.barForLongClass(l2);
		foo.barForLongClass(l3);

		foo.barForLongClassFull(l1);
		foo.barForLongClassFull(l2);
		foo.barForLongClassFull(l3);

		foo.barForLongArray(la1);

		foo.barForLongArrayClass(la2);
		foo.barForLongArrayClass(la3);

		foo.barForLongArrayClassFull(la2);
		foo.barForLongArrayClassFull(la3);

		foo.barForInt(i1);
		foo.barForInt(i2);
		foo.barForInt(i3);

		foo.barForInt(short1);
		foo.barForInt(short2);
		foo.barForInt(short3);

		foo.barForInt(b1);
		foo.barForInt(b2);
		foo.barForInt(b3);

		foo.barForIntClass(i1);
		foo.barForIntClass(i2);
		foo.barForIntClass(i3);

		foo.barForIntClassFull(i1);
		foo.barForIntClassFull(i2);
		foo.barForIntClassFull(i3);

		foo.barForIntArray(ia1);

		foo.barForIntArrayClass(ia2);
		foo.barForIntArrayClass(ia3);

		foo.barForIntArrayClassFull(ia2);
		foo.barForIntArrayClassFull(ia3);

		foo.barForShort(short1);
		foo.barForShort(short2);
		foo.barForShort(short3);

		foo.barForShort(b1);
		foo.barForShort(b2);
		foo.barForShort(b3);

		foo.barForShortClass(short1);
		foo.barForShortClass(short2);
		foo.barForShortClass(short3);

		foo.barForShortClassFull(short1);
		foo.barForShortClassFull(short2);
		foo.barForShortClassFull(short3);

		foo.barForShortArray(shortArray1);

		foo.barForShortArrayClass(shortArray2);
		foo.barForShortArrayClass(shortArray3);

		foo.barForShortArrayClassFull(shortArray2);
		foo.barForShortArrayClassFull(shortArray3);

		foo.barForByte(b1);
		foo.barForByte(b2);
		foo.barForByte(b3);

		foo.barForByteClass(b1);
		foo.barForByteClass(b2);
		foo.barForByteClass(b3);

		foo.barForByteClassFull(b1);
		foo.barForByteClassFull(b2);
		foo.barForByteClassFull(b3);

		foo.barForByteArray(ba1);

		foo.barForByteArrayClass(ba2);
		foo.barForByteArrayClass(ba3);

		foo.barForByteArrayClassFull(ba2);
		foo.barForByteArrayClassFull(ba3);

		double d1 = 0;
		Double d2 = 0d;
		java.lang.Double d3 = 0d;

		double[] da1 = null;
		Double[] da2 = null;
		java.lang.Double[] da3 = null;

		float f1 = 0;
		Float f2 = 0f;
		java.lang.Float f3 = 0f;

		float[] fa1 = null;
		Float[] fa2 = null;
		java.lang.Float[] fa3 = null;

		foo.barForDouble(d1);
		foo.barForDouble(d2);
		foo.barForDouble(d3);

		foo.barForDouble(f1);
		foo.barForDouble(f2);
		foo.barForDouble(f3);

		foo.barForDoubleClass(d1);
		foo.barForDoubleClass(d2);
		foo.barForDoubleClass(d3);

		foo.barForDoubleClassFull(d1);
		foo.barForDoubleClassFull(d2);
		foo.barForDoubleClassFull(d3);

		foo.barForDoubleArray(da1);

		foo.barForDoubleArrayClass(da2);
		foo.barForDoubleArrayClass(da3);

		foo.barForDoubleArrayClassFull(da2);
		foo.barForDoubleArrayClassFull(da3);

		foo.barForFloat(f1);
		foo.barForFloat(f2);
		foo.barForFloat(f3);

		foo.barForFloatClass(f1);
		foo.barForFloatClass(f2);
		foo.barForFloatClass(f3);

		foo.barForFloatClassFull(f1);
		foo.barForFloatClassFull(f2);
		foo.barForFloatClassFull(f3);

		foo.barForFloatArray(fa1);

		foo.barForFloatArrayClass(fa2);
		foo.barForFloatArrayClass(fa3);

		foo.barForFloatArrayClassFull(fa2);
		foo.barForFloatArrayClassFull(fa3);

		char c1 = 'a';
		Character c2 = 'a';
		java.lang.Character c3 = 'a';

		char[] ca1 = null;
		Character[] ca2 = null;
		java.lang.Character[] ca3 = null;

		foo.barForChar(c1);
		foo.barForChar(c2);
		foo.barForChar(c3);

		foo.barForCharClass(c1);
		foo.barForCharClass(c2);
		foo.barForCharClass(c3);

		foo.barForCharClassFull(c1);
		foo.barForCharClassFull(c2);
		foo.barForCharClassFull(c3);

		foo.barForCharArray(ca1);

		foo.barForCharArrayClass(ca2);
		foo.barForCharArrayClass(ca3);

		foo.barForCharArrayClassFull(ca2);
		foo.barForCharArrayClassFull(ca3);

		foo.barForObjectArray(oa1);
		foo.barForObjectArray(oa2);

		foo.barForObjectArray(aa1);
		foo.barForObjectArray(aa2);
		foo.barForObjectArray(sa1);
		foo.barForObjectArray(sa2);
		foo.barForObjectArray(la2);
		foo.barForObjectArray(la3);
		foo.barForObjectArray(ia2);
		foo.barForObjectArray(ia3);
		foo.barForObjectArray(shortArray2);
		foo.barForObjectArray(shortArray3);
		foo.barForObjectArray(ba2);
		foo.barForObjectArray(ba3);
		foo.barForObjectArray(da2);
		foo.barForObjectArray(da3);
		foo.barForObjectArray(fa2);
		foo.barForObjectArray(fa3);
		foo.barForObjectArray(ca2);
		foo.barForObjectArray(ca3);

		foo.barForObjectArrayFull(oa1);
		foo.barForObjectArrayFull(oa2);

		foo.barForObjectArrayFull(aa1);
		foo.barForObjectArrayFull(aa2);
		foo.barForObjectArrayFull(sa1);
		foo.barForObjectArrayFull(sa2);
		foo.barForObjectArrayFull(la2);
		foo.barForObjectArrayFull(la3);
		foo.barForObjectArrayFull(ia2);
		foo.barForObjectArrayFull(ia3);
		foo.barForObjectArrayFull(shortArray2);
		foo.barForObjectArrayFull(shortArray3);
		foo.barForObjectArrayFull(ba2);
		foo.barForObjectArrayFull(ba3);
		foo.barForObjectArrayFull(da2);
		foo.barForObjectArrayFull(da3);
		foo.barForObjectArrayFull(fa2);
		foo.barForObjectArrayFull(fa3);
		foo.barForObjectArrayFull(ca2);
		foo.barForObjectArrayFull(ca3);

		TFoo<String> tfoo = new TFoo<String>();

		tfoo.bar(s1);
		tfoo.bar(s2);
	}

}

class Foo {
	public Foo() {
	}

	void barForString(String s) {
	}

	void barForStringFull(java.lang.String s) {
	}

	void barForStringArray(String[] s) {
	}

	void barForStringArrayFull(java.lang.String[] s) {
	}

	void barForClass(AnyClass a) {
	}

	void barForClassFull(blade.migrate.liferay70.AnyClass a) {
	}

	void barForClassArray(AnyClass[] a) {
	}

	void barForClassArrayFull(blade.migrate.liferay70.AnyClass[] a) {
	}

	void barForObject(Object o) {
	}

	void barForObjectFull(java.lang.Object o) {
	}

	void barForLong(long l) {
	}

	void barForLongClass(Long l) {
	}

	void barForLongClassFull(java.lang.Long l) {
	}

	void barForLongArray(long[] l) {
	}

	void barForLongArrayClass(Long[] l) {
	}

	void barForLongArrayClassFull(java.lang.Long[] l) {
	}

	void barForInt(int i) {
	}

	void barForIntClass(Integer i) {
	}

	void barForIntClassFull(java.lang.Integer i) {
	}

	void barForIntArray(int[] i) {
	}

	void barForIntArrayClass(Integer[] i) {
	}

	void barForIntArrayClassFull(java.lang.Integer[] i) {
	}

	void barForShort(short s) {
	}

	void barForShortClass(Short s) {
	}

	void barForShortClassFull(java.lang.Short s) {
	}

	void barForShortArray(short[] s) {
	}

	void barForShortArrayClass(Short[] s) {
	}

	void barForShortArrayClassFull(java.lang.Short[] s) {
	}

	void barForByte(byte b) {
	}

	void barForByteClass(Byte b) {
	}

	void barForByteClassFull(java.lang.Byte b) {
	}

	void barForByteArray(byte[] b) {
	}

	void barForByteArrayClass(Byte[] b) {
	}

	void barForByteArrayClassFull(java.lang.Byte[] b) {
	}

	void barForDouble(double d) {
	}

	void barForDoubleClass(Double d) {
	}

	void barForDoubleClassFull(java.lang.Double d) {
	}

	void barForDoubleArray(double[] d) {
	}

	void barForDoubleArrayClass(Double[] d) {
	}

	void barForDoubleArrayClassFull(java.lang.Double[] d) {
	}

	void barForFloat(float f) {
	}

	void barForFloatClass(Float f) {
	}

	void barForFloatClassFull(java.lang.Float f) {
	}

	void barForFloatArray(float[] f) {
	}

	void barForFloatArrayClass(Float[] f) {
	}

	void barForFloatArrayClassFull(java.lang.Float[] f) {
	}

	void barForObjectArray(Object[] o) {
	}

	void barForObjectArrayFull(java.lang.Object[] o) {
	}

	void barForChar(char f) {
	}

	void barForCharClass(Character f) {
	}

	void barForCharClassFull(java.lang.Character f) {
	}

	void barForCharArray(char[] f) {
	}

	void barForCharArrayClass(Character[] f) {
	}

	void barForCharArrayClassFull(java.lang.Character[] f) {
	}
}

class AnyClass {
}

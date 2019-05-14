package blade.migrate.liferay70;

public class JavaFileChecker {
	boolean value = false;

	public static void main(String[] args) {
		Foo foo = new Foo();

		System.out.println(
			foo.
				bar(value));

		System.out.println(
			String.
				valueOf(1));

		foo.bar("1");
		JavaFileChecker andyTest = new Test();
		String str = null;
		andyTest.call(str , str , str);
		andyTest.call(str , new String() , str);
		andyTest.call(str , new String() , andyTest.getString());
		andyTest.call(str , new String() , strange.getString());
		andyTest.call2(str,str,str);
		JavaFileChecker.staticCall(str+"ss", str, str);
		JavaFileChecker.staticCall(str , new String() , str);
		JavaFileChecker.staticCall(str , new String() , andyTest.getString());
		JavaFileChecker.staticCall(str , new String() , strange.getString());
	}

	Foo foo = new Foo();

	public void anotherMethod() {
		foo.bar("2");
	}

	public void typeTest() {
		NotFoo foo = new NotFoo();
		foo.bar(false);
	}

	public String getString(){
		return new String();
	}


	public static void staticCall(String str1 , String str2 , String str3){

	}
}

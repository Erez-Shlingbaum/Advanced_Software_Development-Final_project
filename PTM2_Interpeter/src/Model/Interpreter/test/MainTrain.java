package Model.Interpreter.test;

class MainTrain
{
/*
	public static void main(String[] args) {
		Random r=new Random();
		int port=r.nextInt(1001)+5000;
		Simulator sim=new Simulator(port); // sim_client on port+1, sim_server on port
		
		int rand=r.nextInt(1000);
		
		String[] test1={
				"return "+rand+" * 5 - (8+2)"	
		};
		
		if(MyInterpreter.interpret(test1)!=rand*5-(8+2))
			System.out.println("failed test1 (-20)");

		String[] test2={
				"var x",	
				"x="+rand,	
				"var y=x+3",	
				"return y"	
		};
		
		if(MyInterpreter.interpret(test2)!=rand+3)
			System.out.println("failed test2 (-20)");

		String[] test3={
				"connect 127.0.0.1 "+port,
				"var x",
				"x = bind simX",
				"var y = bind simX",
				"x = "+rand*2,
				"disconnect",
				"return y"	
		};
		
		if(MyInterpreter.interpret(test3)!=rand*2)
			System.out.println("failed test3 (-20)");

		String[] test4={
				"openDataServer "+ (port+1)+" 10",
				"connect 127.0.0.1 "+port,
				"var x = bind simX",
				"var y = bind simY",	
				"var z = bind simZ",	
				"x = "+rand*2,
				"disconnect",
				"return x+y*z"	
		};
		
		if(MyInterpreter.interpret(test4)!=sim.simX+sim.simY*sim.simZ)
			System.out.println("failed test4 (-20)");
				
		String[] test5={
				"var x = 0",
				"var y = "+rand,
				"while x < 5 {",
				"	y = y + 2",
				"	x = x + 1",
				"}",
				"return y"	
		};
		
		if(MyInterpreter.interpret(test5)!=rand+2*5)
			System.out.println("failed test5 (-20)");
		
		sim.close();
		System.out.println("done");
	}
*/
}

import java.util.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Assignment 2
 * File: Inspector.java
 * @author Daniel Van Leusen
 * Student id: 10064708
 * E-mail: danvanleusen@yahoo.co.uk

 * <p>
 * Purpose: This file is adopted from ObjectInspector.java, which was created
 * by Jordan Kidney on October 23, 2005. It will introspect on the object
 * specified, and print what it finds to standard output.
 */

public class Inspector
{
    public Inspector() { }

    //-----------------------------------------------------------
    public void inspect(Object obj, boolean recursive)
    {
	Vector objectsToInspect = new Vector();
	Class ObjClass = obj.getClass();

	System.out.println("inside inspector: " + obj + " (recursive = "+recursive+")");
	
	//inspect the current class
	inspectFields(obj, ObjClass,objectsToInspect);
	
	if(recursive)
	    inspectFieldClasses( obj, ObjClass, objectsToInspect, recursive);
    }
    //-----------------------------------------------------------
    private void inspectFieldClasses(Object obj,Class ObjClass,
				     Vector objectsToInspect,boolean recursive)
    {
	
	if(objectsToInspect.size() > 0 )
	    System.out.println("---- Inspecting Field Classes ----");
	
	Enumeration e = objectsToInspect.elements();
	while(e.hasMoreElements())
	    {
		Field f = (Field) e.nextElement();
		System.out.println("Inspecting Field: " + f.getName() );
		
		try
		    {
			System.out.println("******************");
			inspect( f.get(obj) , recursive);
			System.out.println("******************");
		    }
		catch(Exception exp) { exp.printStackTrace(); }
	    }
    }
//==============================================================================================   
    // this method finds declaring class name and prints it
    // it also returns the name for unit tests
    public String inspectDeclaringClass(Method ObjClass){
         Class<?> dClass = ObjClass.getDeclaringClass();
         if (dClass != null){
             System.out.println("Declared Classes: " +dClass.getName());
             return dClass.getName();
         }
         return "";
     }
    
    // this method finds name of immediate superclass and prints it
    // it also returns the name for unit tests
    public String inspectSuperClass(Class ObjClass){
        if (ObjClass.getSuperclass()!=null){
            System.out.println("SuperClass: " + ObjClass.getSuperclass().getName() );
            return ObjClass.getSuperclass().getName() ;
        }
        else
            return "";
    }
    
    // this method finds the names of the interfaces the class implements and prints them
    // it also returns the names for unit tests
    public String inspectInterfaces(Class objClass){
    	if (objClass.getInterfaces()!= null){
    		String printString = "";
    		Class<?>[] interfacesClasses = objClass.getInterfaces();
    		for (int i = 0; i < interfacesClasses.length; i++){
    			if (printString.equals(""))
    				printString += interfacesClasses[i].getName();
    			else
    				printString += ", " + interfacesClasses[i].getName();
    		}
    		System.out.println("Interface(s): " + printString);
    		return printString;
    	}
    	else
    		return "";
    }
    

    
    // helps with printing methods information
    private String addedNewLine(String s1, String s2){
    	if (s2.equals(""))
    		return s1+s2;
    	else
    		return s1+s2+"\n";
    }
    
    // this method finds the methods the class declares, which also include
    // exceptions thrown, the parameter types, the return type, the modifiers
    // it also returns them for unit tests
    public String inspectMethods(Class objClass){
    	if (objClass.getDeclaredMethods()!= null){
    		String printString = "";
    		Method[] methodNames = objClass.getDeclaredMethods();
    		for (int i = 0; i < methodNames.length; i++){
    			printString += "Method: " + methodNames[i].getName() + "\n";
    			
    			// get the types of exceptions thrown under method
    			Class<?>[] exceptionTypes = methodNames[i].getExceptionTypes();
    			String printString2 = "";
    			for (int j = 0; j < exceptionTypes.length; j++){
    				if (printString2.equals(""))
    						printString2 += "\tExceptions thrown: " + exceptionTypes[j].getName();
    				else
    						printString2 += ", "+exceptionTypes[j].getName();
    			}
    			printString = addedNewLine(printString, printString2);
    			
    			// get the parameter types under method
    			printString = addedNewLine(printString, getParameters(methodNames[i]));
    			
    			// get the return type under method
    			printString += "\tReturn type: " +  methodNames[i].getReturnType().getName() + "\n";
    			
    			// get the modifiers under method
    			printString += getModifiers(methodNames[i]);
    		}    		
	    	System.out.println(printString);
    		return printString;
    	}
    	else
    		return "";
    }
    
    // this method finds the constructors the class declares, which also include
    // the parameter types and the modifiers; it also returns them for unit tests
    public String inspectConstructors(Class objClass){
    	if (objClass.getConstructors()!= null){
    		String printString = "";
    		String printString2 = "";
    		Constructor[] constructorNames = objClass.getConstructors();
    		for (int i = 0; i < constructorNames.length; i++){
    			printString += "Constructor: " + constructorNames[i].getName() + "\n";
    			
    			// get the parameter types under constructor
    			printString = addedNewLine(printString, getParameters(constructorNames[i]));
    			
    			// get the modifiers under constructor
    			printString += getModifiers(constructorNames[i]);
    		}    		
	    	System.out.println(printString);
    		return printString;
    	}
    	else
    		return "";
    }

    // gets parameter types for objects
    private String getParameters(Object obj){
    	String printString2 = "";
    	Class<?>[] parameterTypes = null;
    	if (obj.getClass().getSimpleName().equals("Method")){
    		Method m = (Method) obj;
    		parameterTypes = m.getParameterTypes();
    	}
    	
    	else if (obj.getClass().getSimpleName().equals("Constructor")){
    		Constructor c = (Constructor) obj;
    		parameterTypes = c.getParameterTypes();
    	}
    	
		for (int j = 0; j < parameterTypes.length; j++){
			if (printString2.equals(""))
					printString2 += "\tParameter type(s): " + parameterTypes[j].getName();
			else
					printString2 += ", "+parameterTypes[j].getName();
		}
    	return printString2;
    }
    
    // gets modifiers for objects
    private String getModifiers(Object obj){
        String printString2 = "";
        int modifiers = 0;
        if (obj.getClass().getSimpleName().equals("Method")){
        	Method m = (Method) obj;
        	modifiers = m.getModifiers();
        }
        	
        else if (obj.getClass().getSimpleName().equals("Constructor")){
        	Constructor c = (Constructor) obj;
        	modifiers = c.getModifiers();
    	}
        
        else if (obj.getClass().getSimpleName().equals("Field")){
        	Field c = (Field) obj;
        	modifiers = c.getModifiers();
    	}
        if (modifiers != 0)
        	return "\tModifier: " + Modifier.toString(modifiers) + "\n";
        
        return "\tModifier: Package-private (Default)\n";
    }

    // gets names of fields the class declares and their type and modifiers
    // if the field is an object reference and recursive is set to false, print out reference value
    // if it's an array, name, component type, length, and all its contents are printed where valid
    public String inspectFields(Object obj,Class ObjClass,Vector objectsToInspect){
    	String printString = "";
		if(ObjClass.getDeclaredFields().length >= 1){
			Field[] fields = ObjClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++ ){
				printString += "Field: " + fields[i].getName() + "\n";
				printString += "\tType: " + fields[i].getType().getName() + "\n";
				printString += getModifiers(fields[i]);
				
				fields[i].setAccessible(true);
				
				// if the field is not of primitive type
				if(! fields[i].getType().isPrimitive()) {
					objectsToInspect.addElement(fields[i]);
					// checks if it is an array
					if (fields[i].getType().isArray()){
						printString += "\tComponent type: " + fields[i].getType().getComponentType().getName() + "\n";
						
						// tries to get array length and its contents
						try{
							int l = Array.getLength(fields[i].get(obj));
							printString += "\tLength: " + l + "\n";
							String printString2 = "";
							for (int j = 0; j < l; j++){								
								if (printString2.equals("")){
									printString2 += "\tValues: " + Array.get(fields[i].get(obj), j).toString();		
								}
								else
									printString2 += ", " + Array.get(fields[i].get(obj), j).toString();
							}
							printString = addedNewLine(printString, printString2);	
						}
						catch (Exception e){}
					}
					
					// not an array and tries to get reference value
					else{
						try{
							printString += "\tReference value: " + fields[i].getType().getName() + " " + System.identityHashCode(fields[i].get(obj)) +"\n";
						}
						catch (Exception e){}
					}
				}
				
				// if the field is of primitive type
				else{		
					try {
						printString += "\tValue: " + fields[i].get(obj) + "\n";
					}
					catch(Exception e) {}
				}
			}
		}
		
		// prints result
		System.out.println(printString);
		// checks if superclass is not null
		if(ObjClass.getSuperclass() != null){
			System.out.println("---- Inspects superclass: " + ObjClass.getSuperclass().getName() + " ----");
		    inspectFields(obj, ObjClass.getSuperclass() , objectsToInspect);
		}
		return printString;
    }
}

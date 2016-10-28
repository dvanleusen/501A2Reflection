import java.util.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.print.DocFlavor.STRING;


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
	// initializes variables and arrayList to check if an object is already inspected
    public Inspector() { }
    private ArrayList inspectedList = new ArrayList();
    boolean chkInspectedList;

    // takes in recursive boolean and the object and inspects specific object
    public void inspect(Object obj, boolean recursive){
		chkInspectedList=false;
		System.out.println("inside inspector: " + obj + " (recursive = "+recursive+")");
		this.inspectDeclaringClass(obj);
		this.inspectSuperClass(obj.getClass());
		this.inspectInterfaces(obj.getClass());
		this.inspectMethods(obj.getClass()); 
		this.inspectConstructors(obj.getClass()) 		;   		
		
		inspectObject(obj,  recursive);
    }
    
    // inspects all fields under the object
    private void inspectObject(Object obj, boolean recursive){
		Vector objectsToInspect = new Vector();
		Class ObjClass = obj.getClass();
		
		// inspects the current object
		inspectFields(obj, ObjClass,objectsToInspect);
		
		//if recursive is true, inspects recrusively
		if(recursive)
		    inspectFieldClasses( obj, ObjClass, objectsToInspect, recursive);
    }
    
    
    // adds elements to the "to be inspected" vector
    private void addObjectsToInspect(Object obj,Vector objectsToInspect){
    	
    	if (obj.getClass().isArray() &&(!obj.getClass().getComponentType().isPrimitive())){
    		try{
    			Class<?> cls=obj.getClass().getComponentType();
    			Object newInstance=cls.newInstance();
    			if(!inspectedList.contains(cls.getName()))
    				objectsToInspect.addElement(newInstance);
    		}
    		catch(Exception e){}
    	}
    	else if (! obj.getClass().isPrimitive()){
    		if(!inspectedList.contains(obj.getClass().getName()))
    			objectsToInspect.addElement(obj);	
    	}
    }
    
    // gets names of fields the class declares and their type and modifiers
    // if the field is an object reference and recursive is set to false, print out reference value
    // if it's an array, name, component type, length, and all its contents are printed where valid
    public String inspectFields(Object obj,Class ObjClass,Vector objectsToInspect){
    	
    	// checks if the object is already inspected
    	if (chkInspectedList && inspectedList.contains(ObjClass.getName())){
    		return "";
    	}
    	
    	chkInspectedList=true;
    	String printString = "";
    	// if not, check if the specific object is an array type (for example, classB[13])
    	// if it is, prints relevant information and puts it into "to be inspected" vector
    	if (obj.getClass().isArray()){
    		try{
	    		printString+="\tType: "+ ObjClass.getTypeName()
	    			+"\n\tComponent type: "+ObjClass.getComponentType().getName()
	    			+"\n\tLength: "+Array.getLength(obj);
	    		addObjectsToInspect( obj,objectsToInspect);
    		}
    		
    		catch(Exception ex){}
    	}
    	
    	// if it is not, inspects fields underneath
    	else{ 
    		inspectedList.add(ObjClass.getName());
    		Field[] fields =ObjClass.getDeclaredFields();
			if(ObjClass.getDeclaredFields().length >= 1){
				// prints fields name, type, and modifiers
				for (int i = 0; i < fields.length; i++ ){
					inspectedList.add(fields[i].getName());
					addObjectsToInspect( fields[i],objectsToInspect);
					printString += "Field: " + fields[i].getName() + "\n";
					printString += "\tType: " + fields[i].getType().getName() + "\n";
					printString += getModifiers(fields[i]);
					
					fields[i].setAccessible(true);
					
					// if the field is not of primitive type
					if(! fields[i].getType().isPrimitive()) {
						// checks if it is an array
						if (fields[i].getType().isArray()){
							printString = addedNewLine(printString, inspectArray(fields[i], obj));	
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
    	}
		
		// prints result
		System.out.println(printString);
		
		// checks if superclass is not null; if not, inspects its immediate superclass
		if(ObjClass.getSuperclass() != null){
			System.out.println("---- Inspecting superclass: " + ObjClass.getSuperclass().getName() + " ----");
		    inspectFields(obj, ObjClass.getSuperclass() , objectsToInspect);
		}
		return printString;
    }
    
    // when recursive boolean is true, inspects objects in the vector
    private void inspectFieldClasses(Object obj,Class ObjClass, Vector objectsToInspect,boolean recursive) {
		if(objectsToInspect.size() > 0 )
		    System.out.println("---- Inspecting Field Classes ----");
		
		Enumeration e = objectsToInspect.elements();
		while(e.hasMoreElements())
		    {
			Object o = (Object) e.nextElement();
			System.out.println("Inspecting Field: " + o.getClass().getName() );
			try
			    {
				System.out.println("******************");
				inspectObject( o , recursive);
				System.out.println("******************");
			    }
			catch(Exception exp) { exp.printStackTrace(); }
		    }
    }

    // this method finds declaring class name and prints it
    // it also returns the name for unit tests
    public String inspectDeclaringClass(Object obj){
  	         Class<?> dClass = obj.getClass();
	         if (dClass != null){
	             System.out.println("Declared Class: " +dClass.getName());
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

    // inspects array and prints its length and contents
    private String inspectArray(Field field, Object obj){
    	String printString = "\tComponent type: " + field.getType().getComponentType().getName() + "\n";
		
		// tries to get array length and its contents
		try{
			int l = Array.getLength(field.get(obj));
			printString += "\tLength: " + l + "\n";
			String printString2 = "";
			for (int j = 0; j < l; j++){								
				if (printString2.equals("")){
					printString2 += "\tValues: " + Array.get(field.get(obj), j).toString();		
				}
				else
					printString2 += ", " + Array.get(field.get(obj), j).toString();
			}
			printString = printString + printString2;	
		}
		catch (Exception e){}
    	return printString;
    }
}

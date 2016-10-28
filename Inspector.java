import java.util.*;
import java.lang.reflect.*;

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
//==============================================================================================  
    private void inspectFields(Object obj,Class ObjClass,Vector objectsToInspect){
		if(ObjClass.getDeclaredFields().length >= 1){ 
			Field f = ObjClass.getDeclaredFields()[0];
			
			f.setAccessible(true);
			
			if(! f.getType().isPrimitive() ) 
			    objectsToInspect.addElement( f );
			
			try {
				System.out.println("Field: " + f.getName() + " = " + f.get(obj));
			}
			catch(Exception e) {}    
		}
	
		if(ObjClass.getSuperclass() != null)
		    inspectFields(obj, ObjClass.getSuperclass() , objectsToInspect);
    }
}

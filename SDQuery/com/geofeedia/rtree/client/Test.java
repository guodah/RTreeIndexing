package com.geofeedia.rtree.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Test {
	public static void main(String args[]) throws IOException{
		String input = "1,fish,2,fish,red,fish,blue,fish";
	     Scanner s = new Scanner(input).useDelimiter(",|\n");
	     System.out.println(s.nextInt());
	     
	     System.out.println(s.next());
	     System.out.println(s.next());
	     s.close(); 
	}
}

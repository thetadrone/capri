package utils.io;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SimpleLogger {

	protected HashMap<String, Vector<Float>> map;

	public SimpleLogger() {
		super();
		map = new HashMap<String, Vector<Float>>();
	}
	
	public void log(String name, float value) {
		Float f = new Float(value);
		Vector<Float> v;
		if (map.containsKey(name)) {
			v = map.get(name);
			v.addElement(f);
		}
		else {
			v = new Vector<Float>();
			v.addElement(f);
			map.put(name, v);
		}
	}
	
	public void print() {
		Set<String> key = map.keySet();
		for (Iterator<String> i = key.iterator(); i.hasNext();) {
			String name = i.next();
			System.out.print(name + " ");
			Vector<Float> v = map.get(name);
			for (Iterator<Float> j = v.iterator(); j.hasNext();) {
				Float f = j.next();
				System.out.print(f.floatValue() + " ");
			}
			System.out.print("\n");
		}
	}
	
}

package in.principal.adapter;

public class Capitalize {

	public static String capitalThis(String s){
		int pos = 0;
		boolean capitalize = true;
		StringBuilder sb = new StringBuilder(s);
		while (pos < sb.length()) {
			if (sb.charAt(pos) == '.' || Character.isWhitespace(sb.charAt(pos))) {
				capitalize = true;
			}else if(Character.isUpperCase(sb.charAt(pos)) && !capitalize && pos!=0){
				sb.setCharAt(pos, Character.toLowerCase(sb.charAt(pos)));	    	
			}else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
				sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
				capitalize = false;
			}
			pos++;
		}
		return sb.toString();
	}

}

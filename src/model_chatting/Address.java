package model_chatting;

public class Address {

    public static String checkAddress(String add1, String add2) {
        String result = "";
        switch (add1) {
            case "195.192.150.10":
                add1 = "A";
                break;
            case "193.24.56.11":
                add1 = "B";
                break;
            case "196.192.6.12":
                add1 = "C";
                break;
            case "200.100.10.13":
                add1 = "D";
                break;
            default:
                break;
        }

        switch (add2) {
            case "195.192.150.10":
                add2 = "A";
                break;
            case "193.24.56.11":
                add2 = "B";
                break;
            case "196.192.6.12":
                add2 = "C";
                break;
            case "200.100.10.13":
                add2 = "D";
                break;
            default:
                break;
        }
        result = add1 + " sang " + add2;
        return result;
    }
    
    public static String check_source(String source) {
        return source.substring(0, source.indexOf("-", 1));
    }
    
    public static String check_destination(String des) {
        return des.substring(des.indexOf("-", 1) + 1, des.indexOf("*", 1));
    }
    
    public static String check_content(String content) {
        return content.substring(content.indexOf("*", 1) + 1, content.length());
    }

    public static void main(String[] args) {
        System.out.println(Address.checkAddress("195.192.150.10", "200.100.10.13"));
        String a = "196.192.6.12-193.24.56.11*anh yeu em";
//        System.out.println(a.indexOf("*", 2));
        String source = a.substring(0, a.indexOf("-", 1));
        String destination = a.substring(a.indexOf("-", 1) + 1, a.indexOf("*", 1));
        String content = a.substring(a.indexOf("*", 1) + 1, a.length());
        System.out.println(source);
        System.out.println(destination);
        System.out.println(content);
    }
}

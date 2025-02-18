package Server;



import  java.util.Date; // Import date to append date to header

public class Utitlity {

    static String get200Header(String fileName) {
        return String.format("HTTP/1.0 200 OK\r\n" +
                             "Content-Type: %s\r\n" +
                             "Date: %s\r\n" +
                             "\r\n", contentType(fileName), new Date());
    }

    static String get404Header() {
        return String.format("HTTP/1.0 404 Not Found\r\n" +
                             "Content-Type: text/html\r\n" +
                             "Date: %s\r\n" +
                             "\r\n", new Date());
    }

    static String get403Header() {
        return String.format("HTTP/1.0 403 Bad Request\r\n" +
                             "Content-Type: text/html\r\n" +
                             "Date: %s\r\n" +
                             "\r\n", new Date());
    }

    static String contentType(String fileName) {
        int locationOfLastDot = fileName.lastIndexOf('.'); // trying to find location of last dot to seprate the extension
        if (locationOfLastDot == -1)
            return "text/plain";

        String fileType = fileName.substring(locationOfLastDot + 1).trim();

        return switch (fileType) {
            case ".html", ".htm" -> "text/html";
            case ".gif" -> "image/gif";
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            default -> "text/plain";
        };
    }
}

package payload;


import org.json.JSONObject;

    public class BookPayload {

        public static String createBookPayload(int id, String name, String author, int year, String summary) {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("name", name);
            json.put("author", author);
            json.put("published_year", year);
            json.put("book_summary", summary);
            return json.toString();
        }

        public static String updateBookPayload(String name, String author, int year, String summary) {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("author", author);
            json.put("published_year", year);
            json.put("book_summary", summary);
            return json.toString();
        }
    }



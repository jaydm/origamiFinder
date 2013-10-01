
package net.jnwd.origamiData;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private List<Model> contents;

    public Book() {
        super();

        isbn = "";
        title = "";
        contents = new ArrayList<Model>();
    }

    public Book(String ISBN) {
        this();

        isbn = ISBN;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Model> getContents() {
        return contents;
    }

    public void setContents(List<Model> contents) {
        this.contents = contents;
    }
}

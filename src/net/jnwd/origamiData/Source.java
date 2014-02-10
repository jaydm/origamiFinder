
package net.jnwd.origamiData;

public class Source {
    public Long id;
    public String title;
    public String isbn;

    public Source(String title, String isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    public Source(Model model) {
        title = model.getBookTitle();
        isbn = model.getISBN();
    }

    public boolean equals(Object that) {
        Source cThat;

        if (that instanceof Source) {
            cThat = (Source) that;
        } else {
            return false;
        }

        if (!cThat.title.equals(title)) {
            return false;
        }

        if (!cThat.isbn.equals(isbn)) {
            return false;
        }

        return true;
    }
}

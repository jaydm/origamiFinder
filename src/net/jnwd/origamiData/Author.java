
package net.jnwd.origamiData;

public class Author {
    public Long id;
    public String name;

    public Author(String name) {
        this.name = name;
    }

    public Author(Model model) {
        // don't have authors :(
        name = "";
    }

    public boolean equals(Object that) {
        Author cThat;

        if (that instanceof Author) {
            cThat = (Author) that;
        } else {
            return false;
        }

        if (!cThat.name.equals(name)) {
            return false;
        }

        return true;
    }
}
